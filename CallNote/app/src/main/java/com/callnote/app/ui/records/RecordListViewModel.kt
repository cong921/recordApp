package com.callnote.app.ui.records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.RecordWithCategory
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

/** 录音列表筛选状态 */
enum class RecordFilter(val label: String) {
    ALL("全部"), COMPLETED("已转写"), PENDING("待转写"), FAILED("失败")
}

data class RecordListUiState(
    val loading: Boolean = true,
    val records: List<RecordWithCategory> = emptyList(),
    val query: String = "",
    val filter: RecordFilter = RecordFilter.ALL,
    val categoryFilterName: String? = null,
    val totalCount: Int = 0
)

@HiltViewModel
class RecordListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordRepository: CallRecordRepository,
    categoryRepository: CallCategoryRepository
) : ViewModel() {

    private val categoryId: Long = savedStateHandle.get<String>("categoryId")?.toLongOrNull() ?: -1L

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(RecordFilter.ALL)

    /** 全部分类（手动归类底部弹窗用） */
    val categories: StateFlow<List<CallCategoryEntity>> = categoryRepository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<RecordListUiState> = combine(
        recordRepository.observeRecords(categoryId),
        query,
        filter,
        recordRepository.observeCategoryName(categoryId)
    ) { records, query, filter, categoryName ->
        val filtered = records.filter { record ->
            val statusMatch = when (filter) {
                RecordFilter.ALL -> true
                RecordFilter.COMPLETED -> record.record.transcriptionStatus == "COMPLETED"
                RecordFilter.PENDING -> record.record.transcriptionStatus == "PENDING"
                RecordFilter.FAILED -> record.record.transcriptionStatus == "FAILED"
            }
            val queryMatch = query.isBlank() || record.record.fileName.contains(
                query.trim(), ignoreCase = true
            ) || (record.record.transcriptionText?.contains(query.trim()) == true)
            statusMatch && queryMatch
        }
        RecordListUiState(
            loading = false,
            records = filtered,
            query = query,
            filter = filter,
            categoryFilterName = categoryName,
            totalCount = filtered.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecordListUiState()
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onFilterChange(newFilter: RecordFilter) {
        filter.value = newFilter
    }

    /** 手动归类录音；categoryId 为 null 表示移出分类 */
    fun assignCategory(recordId: Long, categoryId: Long?) {
        viewModelScope.launch {
            recordRepository.updateCategory(recordId, categoryId)
            _message.value = if (categoryId == null) {
                "已移出分类"
            } else {
                "归类成功"
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
