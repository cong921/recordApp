package com.callnote.app.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.importer.AudioImporter
import com.callnote.app.data.repository.CallRecordRepository
import com.callnote.app.data.whisper.WhisperModelManager
import com.callnote.app.work.TranscriptionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val recordRepository: CallRecordRepository,
    private val audioImporter: AudioImporter,
    private val modelManager: WhisperModelManager
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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** 导入用户通过 SAF 选中的音频文件；导入中防重复触发 */
    fun importUris(uris: List<Uri>) {
        if (uris.isEmpty() || _importing.value) return
        viewModelScope.launch {
            _importing.value = true
            val result = audioImporter.import(uris)
            _importing.value = false
            _message.value = buildString {
                append("成功导入 ${result.successCount} 条录音")
                if (result.failedFiles.isNotEmpty()) {
                    append("，${result.failedFiles.size} 个文件导入失败")
                }
            }
        }
    }

    /** 发起批量转写：校验模型与待转写数量后调度前台 Worker */
    fun startBatchTranscription() {
        viewModelScope.launch {
            val model = modelManager.selected.value
            if (model == null) {
                _message.value = "请先在「我的 → 语音转写引擎」下载并选择模型"
                return@launch
            }
            val pendingCount = recordRepository.getPendingRecords().size
            if (pendingCount == 0) {
                _message.value = "没有待转写的录音"
                return@launch
            }
            WorkManager.getInstance(context).enqueueUniqueWork(
                TranscriptionWorker.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<TranscriptionWorker>().build()
            )
            _message.value = "已开始本地转写 $pendingCount 条录音，进度见通知栏"
        }
    }

    /** Snackbar 展示完毕后清除消息 */
    fun consumeMessage() {
        _message.value = null
    }
}
