package com.callnote.app.data.repository

import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.pojo.CategoryStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallCategoryRepository @Inject constructor(
    private val dao: CallCategoryDao
) {

    fun observeCategoryStats(): Flow<List<CategoryStats>> = dao.observeCategoryStats()

    fun observeAll(): Flow<List<CallCategoryEntity>> = dao.observeAll()

    suspend fun getById(id: Long): CallCategoryEntity? = dao.getById(id)

    /** 按分类编码查询（第五阶段智能分类回填 categoryId 用） */
    suspend fun getByCode(code: String): CallCategoryEntity? = dao.getByCode(code)
}
