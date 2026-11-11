package com.callnote.app

import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.ui.records.RecordListViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

/** 导出 TXT 文本生成测试（第七阶段） */
class ExportTextTest {

    private val zone = ZoneId.of("Asia/Shanghai")

    private fun record(
        id: Long,
        name: String = "通话录音.amr",
        text: String? = "您好，您的快递已到小区门口。",
        summary: String? = "快递送达通知",
        keywords: String? = "快递,送达",
        status: String = "COMPLETED"
    ) = CallRecordEntity(
        id = id,
        fileName = name,
        fileUri = "content://test/$id",
        fileSize = 1024L,
        duration = 95L,
        format = "AMR",
        callTime = 1_762_000_000_000L,
        importTime = 1_762_000_000_000L,
        transcriptionStatus = status,
        transcriptionText = text,
        summary = summary,
        keywords = keywords,
        createdAt = 1_762_000_000_000L,
        updatedAt = 1_762_000_000_000L
    )

    private val category = CallCategoryEntity(
        id = 2L, categoryName = "快递外卖", categoryCode = "DELIVERY",
        description = "", isSystem = true, sort = 2
    )

    @Test
    fun `导出文本包含头信息与统计`() {
        val output = RecordListViewModel.buildExportText(
            listOf(RecordWithCategory(record(1), category)),
            exportTime = LocalDateTime.of(2026, 11, 11, 14, 30),
            zone = zone
        )
        assertTrue(output.contains("听记录音导出"))
        assertTrue(output.contains("导出时间：2026年11月11日 14:30"))
        assertTrue(output.contains("共 1 条录音"))
    }

    @Test
    fun `导出文本包含文件名 分类 摘要 关键词与转写全文`() {
        val output = RecordListViewModel.buildExportText(
            listOf(RecordWithCategory(record(1, "通话录音_138.amr"), category)),
            exportTime = LocalDateTime.of(2026, 11, 11, 14, 30),
            zone = zone
        )
        assertTrue(output.contains("【1】通话录音_138.amr"))
        assertTrue(output.contains("分类：快递外卖"))
        assertTrue(output.contains("状态：已转写"))
        assertTrue(output.contains("摘要：快递送达通知"))
        assertTrue(output.contains("关键词：快递,送达"))
        assertTrue(output.contains("您好，您的快递已到小区门口。"))
    }

    @Test
    fun `未分类显示未分类 空摘要不输出行`() {
        val output = RecordListViewModel.buildExportText(
            listOf(RecordWithCategory(record(1, "a.amr", text = null, summary = null, keywords = null), null)),
            exportTime = LocalDateTime.of(2026, 11, 11, 14, 30),
            zone = zone
        )
        assertTrue(output.contains("分类：未分类"))
        assertFalse(output.contains("摘要"))
        assertFalse(output.contains("关键词"))
        assertFalse(output.contains("转写内容"))
    }

    @Test
    fun `多条录音按序号编排`() {
        val items = listOf(
            RecordWithCategory(record(1, "a.amr"), category),
            RecordWithCategory(record(2, "b.amr"), null)
        )
        val output = RecordListViewModel.buildExportText(
            items, exportTime = LocalDateTime.of(2026, 11, 11, 14, 30), zone = zone
        )
        assertTrue(output.contains("共 2 条录音"))
        assertTrue(output.contains("【1】a.amr"))
        assertTrue(output.contains("【2】b.amr"))
    }

    @Test
    fun `空列表导出头仍合法`() {
        val output = RecordListViewModel.buildExportText(
            emptyList(), exportTime = LocalDateTime.of(2026, 11, 11, 14, 30), zone = zone
        )
        assertTrue(output.contains("共 0 条录音"))
        assertEquals("听记录音导出", output.lineSequence().first())
    }
}
