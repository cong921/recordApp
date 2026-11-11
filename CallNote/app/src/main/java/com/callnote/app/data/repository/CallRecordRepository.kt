package com.callnote.app.data.repository

import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.model.TranscriptionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** 录音数据仓库：UI 层唯一的数据入口 */
@Singleton
class CallRecordRepository @Inject constructor(
    private val dao: CallRecordDao,
    private val categoryDao: CallCategoryDao
) {

    fun observeStats(): Flow<RecordStats> = dao.observeStats()

    /** categoryId <= 0 表示查询全部 */
    fun observeRecords(categoryId: Long = ALL): Flow<List<RecordWithCategory>> =
        if (categoryId > 0) dao.observeByCategoryWithCategory(categoryId)
        else dao.observeAllWithCategory()

    fun observeRecent(limit: Int = 3): Flow<List<RecordWithCategory>> =
        dao.observeRecentWithCategory(limit)

    fun observeCategoryName(categoryId: Long): Flow<String?> = categoryDao.observeCategoryName(categoryId)

    fun search(query: String) = dao.search(query)

    suspend fun updateCategory(recordId: Long, categoryId: Long?) =
        dao.updateCategory(recordId, categoryId, System.currentTimeMillis())

    /** 待转写记录（第四阶段批量转写入口） */
    suspend fun getPendingRecords(): List<CallRecordEntity> = dao.getPendingRecords()

    /** 已转写但未分类的记录（第五阶段智能分类入口） */
    suspend fun getUnclassifiedCompleted(): List<CallRecordEntity> = dao.getUnclassifiedCompleted()

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

    companion object {
        const val ALL = -1L
    }
}
