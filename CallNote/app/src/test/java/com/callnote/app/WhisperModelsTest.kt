package com.callnote.app

import com.callnote.app.data.whisper.WhisperModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhisperModelsTest {

    @Test
    fun `模型 id 与文件名一一对应且不重复`() {
        val ids = WhisperModel.entries.map { it.id }
        val fileNames = WhisperModel.entries.map { it.fileName }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(fileNames.size, fileNames.toSet().size)
        WhisperModel.entries.forEach {
            assertEquals("ggml-${it.id}.bin", it.fileName)
        }
    }

    @Test
    fun `下载地址含国内镜像与官方源`() {
        WhisperModel.entries.forEach {
            assertEquals(2, it.downloadUrls.size)
            assertTrue(
                it.downloadUrls[0].startsWith(
                    "https://hf-mirror.com/ggerganov/whisper.cpp/resolve/main/"
                )
            )
            assertTrue(
                it.downloadUrls[1].startsWith(
                    "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/"
                )
            )
            it.downloadUrls.forEach { url ->
                assertTrue(url.endsWith(it.fileName))
            }
        }
    }

    @Test
    fun `fromId 解析已知与未知 id`() {
        assertEquals(WhisperModel.BASE, WhisperModel.fromId("base"))
        assertEquals(WhisperModel.TINY, WhisperModel.fromId("tiny"))
        assertNull(WhisperModel.fromId(null))
        assertNull(WhisperModel.fromId(""))
        assertNull(WhisperModel.fromId("gpt-4"))
    }

    @Test
    fun `模型大小按 tiny base small 递增`() {
        val tiny = WhisperModel.TINY.sizeBytes
        val base = WhisperModel.BASE.sizeBytes
        val small = WhisperModel.SMALL.sizeBytes
        assertTrue(tiny < base && base < small)
        assertFalse(small < tiny)
    }
}
