package com.callnote.app.ui.records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.model.TranscriptionStatus
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class RecordFilter(val label: String, val statuses: List<String>?) {
    ALL("全部", null),
    COMPLETED("已转写", listOf(TranscriptionStatus.COMPLETED.name)),
    PENDING("待转写", listOf(TranscriptionStatus.PENDING.name, TranscriptionStatus.PROCESSING.name)),
    FAILED("失败", listOf(TranscriptionStatus.FAILED.name))
}

data class RecordListUiState(
    val loading: Boolean = true,
    val records: List<RecordWithCategory> = emptyList(),
    val totalCount: Int = 0,
    val query: String = "",
    val filter: RecordFilter = RecordFilter.ALL,
    val categoryFilterName: String? = null
)

@HiltViewModel
class RecordListViewModel @Inject constructor(
    private val recordRepository: CallRecordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long =
        savedStateHandle.get<Long>("categoryId") ?: CallRecordRepository.ALL

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(RecordFilter.ALL)

    val uiState: StateFlow<RecordListUiState> = combine(
        recordRepository.observeRecords(categoryId),
        query,
        filter,
        if (categoryId > 0) recordRepository.observeCategoryName(categoryId) else flowOf(null)
    ) { records, query, filter, categoryFilterName ->
        val filtered = records.filter { item ->
            val record = item.record
            val matchesQuery = query.isBlank() ||
                record.fileName.contains(query, ignoreCase = true) ||
                (!record.summary.isNullOrBlank() && record.summary.contains(query, ignoreCase = true)) ||
                (!record.transcriptionText.isNullOrBlank() && record.transcriptionText.contains(query, ignoreCase = true))
            val matchesFilter = filter.statuses == null || record.transcriptionStatus in filter.statuses
            matchesQuery && matchesFilter
        }
        RecordListUiState(
            loading = false,
            records = filtered,
            totalCount = records.size,
            query = query,
            filter = filter,
            categoryFilterName = if (categoryId > 0) categoryFilterName else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecordListUiState()
    )

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: RecordFilter) {
        filter.value = value
    }
}
