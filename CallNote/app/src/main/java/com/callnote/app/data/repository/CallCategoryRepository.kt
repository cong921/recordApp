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

    /** 新建自定义分类；重名返回 null */
    suspend fun createCategory(name: String): CallCategoryEntity? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        val existing = dao.getAll()
        if (existing.any { it.categoryName == trimmed }) return null
        val entity = CallCategoryEntity(
            categoryName = trimmed,
            categoryCode = generateCategoryCode(trimmed),
            description = "自定义分类",
            isSystem = false,
            sort = (existing.size + 1) * 10
        )
        val id = dao.insert(entity)
        return entity.copy(id = id)
    }

    /** 删除自定义分类；系统分类在 SQL 层拒绝删除，其下录音自动转为未分类（外键 SET_NULL） */
    suspend fun deleteCategory(categoryId: Long): Boolean =
        dao.deleteById(categoryId) > 0

    companion object {
        private val codeCounter = java.util.concurrent.atomic.AtomicLong(0)

        /** 自定义分类编码：CUSTOM_ + 名称哈希 + 自增序号，保证稳定唯一（可测） */
        fun generateCategoryCode(name: String): String {
            val hash = (name.hashCode().toLong() and 0xFFFF).toString(16)
            return "CUSTOM_${hash}_${codeCounter.incrementAndGet()}"
        }
    }
}
