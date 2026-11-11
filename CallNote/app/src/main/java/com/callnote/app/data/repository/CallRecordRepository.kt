package com.callnote.app.data.repository

import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.dao.TranscriptionTaskDao
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.model.TranscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** 录音数据仓库：UI 层唯一的数据入口 */
@Singleton
class CallRecordRepository @Inject constructor(
    private val dao: CallRecordDao,
    private val categoryDao: CallCategoryDao,
    private val taskDao: TranscriptionTaskDao
) {

    fun observeStats(): Flow<RecordStats> = dao.observeStats()

    /** categoryId <= 0 表示查询全部 */
    fun observeRecords(categoryId: Long = ALL): Flow<List<RecordWithCategory>> =
        if (categoryId > 0) dao.observeByCategoryWithCategory(categoryId)
        else dao.observeAllWithCategory()

    fun observeRecent(limit: Int = 3): Flow<List<RecordWithCategory>> =
        dao.observeRecentWithCategory(limit)

    /** 单条录音详情（含分类），详情页使用 */
    fun observeRecord(recordId: Long): Flow<RecordWithCategory?> =
        dao.observeByIdWithCategory(recordId)

    /** 该录音最近一次转写任务（详情页失败原因展示） */
    fun observeLatestTask(recordId: Long): Flow<TranscriptionTaskEntity?> =
        taskDao.observeByRecord(recordId).map { it.firstOrNull() }

    fun observeCategoryName(categoryId: Long): Flow<String?> = categoryDao.observeCategoryName(categoryId)

    fun search(query: String) = dao.search(query)

    suspend fun updateCategory(recordId: Long, categoryId: Long?) =
        dao.updateCategory(recordId, categoryId, System.currentTimeMillis())

    /** 待转写记录（第四阶段批量转写入口） */
    suspend fun getPendingRecords(): List<CallRecordEntity> = dao.getPendingRecords()

    /** 已转写但未分类的记录（第五阶段智能分类入口） */
    suspend fun getUnclassifiedCompleted(): List<CallRecordEntity> = dao.getUnclassifiedCompleted()

    /** 已转写且音频文件仍在的记录（第六阶段批量清理入口） */
    suspend fun getCleanableCompleted(): List<CallRecordEntity> = dao.getCleanableCompleted()

    suspend fun updateRecordStatus(recordId: Long, status: TranscriptionStatus) =
        dao.updateStatus(recordId, status.name, System.currentTimeMillis())

    /** 转写成功：写入文本、摘要、关键词并标记完成；命中分类时一并写入 */
    suspend fun updateRecordTranscription(
        recordId: Long,
        text: String,
        summary: String,
        keywords: String,
        categoryId: Long?,
        classificationScore: Double?
    ) {
        val category = categoryId?.let { categoryDao.getById(it) }
        dao.updateTranscription(
            id = recordId,
            text = text,
            summary = summary,
            keywords = keywords,
            status = TranscriptionStatus.COMPLETED.name,
            updatedAt = System.currentTimeMillis()
        )
        if (category != null && classificationScore != null) {
            dao.updateCategory(recordId, category.id, System.currentTimeMillis())
            dao.updateClassificationScore(recordId, classificationScore, System.currentTimeMillis())
        }
    }

    /** 仅更新分类与置信度（批量智能分类用，不动转写文本） */
    suspend fun applyClassification(recordId: Long, categoryId: Long?, score: Double?) {
        dao.updateCategory(recordId, categoryId, System.currentTimeMillis())
        if (score != null) {
            dao.updateClassificationScore(recordId, score, System.currentTimeMillis())
        }
    }

    /** 补齐摘要与关键词 */
    suspend fun updateSummaryAndKeywords(recordId: Long, summary: String, keywords: String) =
        dao.updateSummaryAndKeywords(recordId, summary, keywords, System.currentTimeMillis())

    /**
     * 批量清理：删除已转写录音的本地音频文件（转写文本保留）。
     * @return (清理条数, 释放字节数)
     */
    suspend fun cleanTranscribedAudio(): Pair<Int, Long> {
        val records = dao.getCleanableCompleted()
        var freedBytes = 0L
        var cleaned = 0
        for (record in records) {
            val path = record.localPath ?: continue
            val file = java.io.File(path)
            val size = if (file.exists()) file.length() else 0L
            val deleted = !file.exists() || file.delete()
            if (deleted) {
                freedBytes += size
                dao.markAudioDeleted(record.id, "", System.currentTimeMillis())
                cleaned++
            }
        }
        return cleaned to freedBytes
    }

    companion object {
        const val ALL = -1L
    }
}
