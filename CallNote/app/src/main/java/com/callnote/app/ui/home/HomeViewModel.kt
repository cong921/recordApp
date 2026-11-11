package com.callnote.app.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.callnote.app.data.classification.LocalClassifier
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.data.importer.AudioImporter
import com.callnote.app.data.repository.CallCategoryRepository
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
    private val categoryRepository: CallCategoryRepository,
    private val audioImporter: AudioImporter,
    private val modelManager: WhisperModelManager,
    private val classifier: LocalClassifier
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

    private val _classifying = MutableStateFlow(false)
    val classifying: StateFlow<Boolean> = _classifying.asStateFlow()

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

    /**
     * 批量智能分类：对已转写但未分类的记录执行本地规则分类。
     * 词典扫描毫秒级完成，直接在协程中执行，无需 Worker。
     */
    fun startBatchClassification() {
        if (_classifying.value) return
        viewModelScope.launch {
            _classifying.value = true
            try {
                val records = recordRepository.getUnclassifiedCompleted()
                if (records.isEmpty()) {
                    _message.value = "没有待分类的录音（需先完成转写）"
                    return@launch
                }
                var classified = 0
                for (record in records) {
                    val text = record.transcriptionText ?: continue
                    val result = classifier.classify(text)
                    if (result.categoryCode == null) continue
                    val category = categoryRepository.getByCode(result.categoryCode) ?: continue
                    recordRepository.applyClassification(record.id, category.id, result.score)
                    // 摘要与关键词在转写时已生成，未生成的补齐
                    if (record.summary.isNullOrBlank() && result.summary.isNotEmpty()) {
                        recordRepository.updateSummaryAndKeywords(
                            record.id, result.summary, result.keywords.joinToString("，")
                        )
                    }
                    classified++
                }
                _message.value = "智能分类完成：$classified / ${records.size} 条命中分类"
            } finally {
                _classifying.value = false
            }
        }
    }

    /** Snackbar 展示完毕后清除消息 */
    fun consumeMessage() {
        _message.value = null
    }
}
