package com.callnote.app.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.importer.AudioImporter
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

data class HomeUiState(
    val loading: Boolean = true,
    val stats: RecordStats = RecordStats(),
    val recentRecords: List<RecordWithCategory> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    recordRepository: CallRecordRepository,
    private val audioImporter: AudioImporter
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

    private val _importing = MutableStateFlow(false)
    val importing: StateFlow<Boolean> = _importing.asStateFlow()

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    /** 导入用户通过 SAF 选中的音频文件；导入中防重复触发 */
    fun importUris(uris: List<Uri>) {
        if (uris.isEmpty() || _importing.value) return
        viewModelScope.launch {
            _importing.value = true
            val result = audioImporter.import(uris)
            _importing.value = false
            _importMessage.value = buildString {
                append("成功导入 ${result.successCount} 条录音")
                if (result.failedFiles.isNotEmpty()) {
                    append("，${result.failedFiles.size} 个文件导入失败")
                }
            }
        }
    }

    /** Snackbar 展示完毕后清除消息 */
    fun consumeImportMessage() {
        _importMessage.value = null
    }
}
