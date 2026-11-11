package com.callnote.app.transcription

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 音频解码器：任意 Android 支持的录音格式（AMR/3GP/M4A/AAC/MP3/WAV/OGG...）
 * 解码为 whisper 需要的 16kHz 单声道 float PCM。
 * 解码用系统 MediaCodec，重采样用线性插值。
 */
object AudioDecoder {

    /** 单条录音最长处理时长（秒），防止异常文件耗尽内存 */
    private const val MAX_DURATION_SECONDS = 3600
    private const val TARGET_SAMPLE_RATE = 16_000
    private const val TIMEOUT_US = 10_000L

    /**
     * @param audioPath 本地音频文件路径
     * @return 16kHz 单声道 float PCM（幅度 -1..1）
     * @throws IOException 无音频轨道 / 解码失败
     */
    fun decodeTo16kMonoFloat(audioPath: String): FloatArray {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(audioPath)
            val trackIndex = selectAudioTrack(extractor)
                ?: throw IOException("音频文件中没有可解码的音频轨道")
            extractor.selectTrack(trackIndex)

            var format = extractor.getTrackFormat(trackIndex)
            val mime = format.getString(MediaFormat.KEY_MIME)
                ?: throw IOException("无法识别音频编码格式")

            val codec = MediaCodec.createDecoderByType(mime)
            try {
                codec.configure(format, null, null, 0)
                codec.start()
                val (pcm16, sampleRate, channels) = decodeLoop(codec, extractor)
                if (pcm16.isEmpty()) throw IOException("解码结果为空")
                return resampleTo16kMonoFloat(pcm16, sampleRate, channels)
            } finally {
                codec.stop()
                codec.release()
            }
        } finally {
            extractor.release()
        }
    }

    private fun selectAudioTrack(extractor: MediaExtractor): Int? {
        for (i in 0 until extractor.trackCount) {
            val mime = extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("audio/")) return i
        }
        return null
    }

    /** 同步解码循环，返回 (16bit PCM 字节, 采样率, 声道数) */
    private fun decodeLoop(
        codec: MediaCodec,
        extractor: MediaExtractor
    ): Triple<ByteArray, Int, Int> {
        val out = ByteArrayOutputStream()
        var sampleRate = -1
        var channels = -1
        val info = MediaCodec.BufferInfo()
        var sawInputEos = false
        var sawOutputEos = false
        var totalBytes = 0
        val maxBytes = MAX_DURATION_SECONDS * TARGET_SAMPLE_RATE * 2 // s16 单声道上限

        while (!sawOutputEos) {
            if (!sawInputEos) {
                val inIndex = codec.dequeueInputBuffer(TIMEOUT_US)
                if (inIndex >= 0) {
                    val buffer = codec.getInputBuffer(inIndex)!!
                    val size = extractor.readSampleData(buffer, 0)
                    if (size < 0) {
                        codec.queueInputBuffer(
                            inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        sawInputEos = true
                    } else {
                        codec.queueInputBuffer(inIndex, 0, size, extractor.sampleTime, 0)
                        extractor.advance()
                    }
                }
            }
            val outIndex = codec.dequeueOutputBuffer(info, TIMEOUT_US)
            when {
                outIndex >= 0 -> {
                    // 跳过个别解码器输出的负时间戳垃圾帧
                    if (info.size > 0 && info.presentationTimeUs >= 0) {
                        val buffer = codec.getOutputBuffer(outIndex)!!
                        val chunk = ByteArray(info.size)
                        buffer.get(chunk)
                        out.write(chunk)
                        totalBytes += chunk.size
                        if (totalBytes > maxBytes) {
                            sawOutputEos = true // 达到时长上限，提前截断
                        }
                    }
                    codec.releaseOutputBuffer(outIndex, false)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        sawOutputEos = true
                    }
                }
                outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    val fmt = codec.outputFormat
                    sampleRate = fmt.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    channels = fmt.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                }
            }
        }
        if (sampleRate <= 0) sampleRate = TARGET_SAMPLE_RATE
        if (channels <= 0) channels = 1
        return Triple(out.toByteArray(), sampleRate, channels)
    }

    /** s16 交错 PCM → float 单声道 → 线性插值重采样至 16kHz */
    private fun resampleTo16kMonoFloat(
        pcmBytes: ByteArray,
        sampleRate: Int,
        channels: Int
    ): FloatArray {
        val shorts = ShortArray(pcmBytes.size / 2)
        ByteBuffer.wrap(pcmBytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()
            .get(shorts)

        val frames = shorts.size / channels
        if (frames == 0) return FloatArray(0)

        // 交错多声道混合为单声道
        val mono = FloatArray(frames)
        for (f in 0 until frames) {
            var sum = 0
            for (c in 0 until channels) {
                sum += shorts[f * channels + c]
            }
            mono[f] = sum.toFloat() / channels / 32768f
        }

        if (sampleRate == TARGET_SAMPLE_RATE) return mono

        // 线性插值重采样
        val ratio = sampleRate.toDouble() / TARGET_SAMPLE_RATE
        val outLength = (frames / ratio).toInt().coerceAtLeast(1)
        val result = FloatArray(outLength)
        for (i in 0 until outLength) {
            val srcPos = i * ratio
            val i0 = srcPos.toInt().coerceAtMost(frames - 1)
            val i1 = (i0 + 1).coerceAtMost(frames - 1)
            val frac = (srcPos - i0).toFloat()
            result[i] = mono[i0] + (mono[i1] - mono[i0]) * frac
        }
        return result
    }
}
