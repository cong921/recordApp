package com.callnote.app.transcription

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 转写引擎：管理 whisper 原生上下文的生命周期。
 * 模型上下文创建开销大（需完整加载模型文件），
 * 批量转写时应 open 一次、逐条 transcribe、最后 close。
 * Worker 以 UniqueWork 串行执行，因此单例持有原生上下文是安全的。
 */
@Singleton
class TranscriptionEngine @Inject constructor() {

    @Volatile
    var nativeContext: Long = 0L
        private set

    val isOpen: Boolean get() = nativeContext != 0L

    /** 加载模型；成功返回 true */
    fun open(modelPath: String): Boolean {
        close()
        nativeContext = WhisperNative.createContext(modelPath)
        return nativeContext != 0L
    }

    fun close() {
        if (nativeContext != 0L) {
            WhisperNative.freeContext(nativeContext)
            nativeContext = 0L
        }
    }

    /**
     * 转写一条录音。
     * @throws IllegalStateException 引擎未打开
     * @throws java.io.IOException 音频解码失败
     */
    fun transcribe(audioPath: String): String {
        check(nativeContext != 0L) { "转写引擎未初始化，请先调用 open()" }
        val pcm = AudioDecoder.decodeTo16kMonoFloat(audioPath)
        if (pcm.isEmpty()) return ""
        return WhisperNative.transcribePcm(nativeContext, pcm, pcm.size)
    }
}
