package com.callnote.app.data.repository

import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
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

    companion object {
        const val ALL = -1L
    }
}
