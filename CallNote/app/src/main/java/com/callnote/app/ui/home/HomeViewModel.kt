package com.callnote.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val loading: Boolean = true,
    val stats: RecordStats = RecordStats(),
    val recentRecords: List<RecordWithCategory> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    recordRepository: CallRecordRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        recordRepository.observeStats(),
        recordRepository.observeRecent(limit = 3)
    ) { stats, recent ->
        HomeUiState(
            loading = false,
            stats = stats,
            recentRecords = recent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
