package com.callnote.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.pojo.CategoryStats
import com.callnote.app.data.repository.CallCategoryRepository
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    recordRepository: CallRecordRepository,
    categoryRepository: CallCategoryRepository
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
}
