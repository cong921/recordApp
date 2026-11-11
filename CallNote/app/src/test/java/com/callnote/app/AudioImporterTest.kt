package com.callnote.app

import com.callnote.app.data.importer.AudioImporter
import org.junit.Assert.assertEquals
import org.junit.Test

class AudioImporterTest {

    @Test
    fun `常见音频扩展名解析为对应格式编码`() {
        assertEquals("MP3", AudioImporter.audioFormatFromName("通话录音_13800138000.amr.mp3"))
        assertEquals("WAV", AudioImporter.audioFormatFromName("call.wav"))
        assertEquals("M4A", AudioImporter.audioFormatFromName("录音.M4A"))
        assertEquals("AMR", AudioImporter.audioFormatFromName("record.amr"))
    }

    @Test
    fun `3gp 通话录音归入 AMR`() {
        assertEquals("AMR", AudioImporter.audioFormatFromName("voice_001.3gp"))
        assertEquals("AMR", AudioImporter.audioFormatFromName("voice_001.3gpp"))
    }

    @Test
    fun `无扩展名或未知扩展名回退为 AUDIO`() {
        assertEquals("AUDIO", AudioImporter.audioFormatFromName("no_extension"))
        assertEquals("AUDIO", AudioImporter.audioFormatFromName("file.xyz"))
        assertEquals("AUDIO", AudioImporter.audioFormatFromName(""))
    }
}
