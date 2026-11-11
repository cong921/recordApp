package com.callnote.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val stats: RecordStats = RecordStats()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    recordRepository: CallRecordRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = recordRepository.observeStats()
        .map { ProfileUiState(loading = false, stats = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )
}
