package com.callnote.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.pojo.CategoryStats
import com.callnote.app.data.repository.CallCategoryRepository
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val loading: Boolean = true,
    val categories: List<CategoryStats> = emptyList(),
    val uncategorizedCount: Int = 0,
    val totalCount: Int = 0,
    val totalDuration: Long = 0,
    val totalSize: Long = 0
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val recordRepository: CallRecordRepository,
    private val categoryRepository: CallCategoryRepository
) : ViewModel() {

    val uiState: StateFlow<CategoryUiState> = combine(
        recordRepository.observeStats(),
        categoryRepository.observeCategoryStats()
    ) { stats, categories ->
        val categorizedCount = categories.sumOf { it.recordCount }
        CategoryUiState(
            loading = false,
            categories = categories,
            uncategorizedCount = (stats.totalCount - categorizedCount).coerceAtLeast(0),
            totalCount = stats.totalCount,
            totalDuration = stats.totalDuration,
            totalSize = stats.totalSize
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryUiState()
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** 待删除的分类（UI 弹确认对话框用） */
    private val _pendingDelete = MutableStateFlow<CategoryStats?>(null)
    val pendingDelete: StateFlow<CategoryStats?> = _pendingDelete.asStateFlow()

    /** 新建自定义分类 */
    fun createCategory(name: String) {
        viewModelScope.launch {
            val result = categoryRepository.createCategory(name)
            _message.value = if (result == null) {
                "分类已存在或名称为空"
            } else {
                "已创建分类「${result.categoryName}」"
            }
        }
    }

    fun requestDelete(category: CategoryStats) {
        if (category.isSystem) {
            _message.value = "系统分类不可删除"
        } else {
            _pendingDelete.value = category
        }
    }

    fun cancelDelete() {
        _pendingDelete.value = null
    }

    /** 确认删除：其下录音转为未分类（外键 SET_NULL） */
    fun confirmDelete() {
        val category = _pendingDelete.value ?: return
        viewModelScope.launch {
            val ok = categoryRepository.deleteCategory(category.id)
            _pendingDelete.value = null
            _message.value = if (ok) {
                "已删除分类「${category.categoryName}」，其录音已转为未分类"
            } else {
                "系统分类不可删除"
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
