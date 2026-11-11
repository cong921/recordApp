package com.callnote.app.ui.records

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 录音详情页 ViewModel：单条记录 + 分类列表 + 最近转写任务 */
@HiltViewModel
class RecordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
    private val recordRepository: CallRecordRepository,
    private val modelManager: WhisperModelManager,
    categoryRepository: CallCategoryRepository
) : ViewModel() {

    // 路由参数类型为 NavType.LongType，必须按 Long 读取（按 String 读取会 ClassCastException）
    private val recordId: Long = savedStateHandle.get<Long>("recordId") ?: -1L

    val record: StateFlow<RecordWithCategory?> = recordRepository.observeRecord(recordId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /** 最近一次转写任务（失败原因展示） */
    val latestTask: StateFlow<TranscriptionTaskEntity?> = recordRepository.observeLatestTask(recordId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /** 全部分类（更改分类弹窗用） */
    val categories: StateFlow<List<CallCategoryEntity>> = categoryRepository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** 手动归类；categoryId 为 null 表示移出分类 */
    fun assignCategory(categoryId: Long?) {
        viewModelScope.launch {
            recordRepository.updateCategory(recordId, categoryId)
            _message.value = if (categoryId == null) "已移出分类" else "归类成功"
        }
    }

    /**
     * 发起转写：PENDING 直接开始 / FAILED 重试 / COMPLETED 重新转写（覆盖旧文本）。
     * 重新转写前清空旧结果并回到待转写状态，换模型后重转即用新模型。
     * APPEND_OR_REPLACE：批量正在跑时把本条追加到队尾（运行中的 Worker 持有旧快照，
     * KEEP 会丢弃新请求导致这条记录无人处理）；链已失败则重开。
     */
    fun startTranscription() {
        val current = record.value?.record ?: return
        when (current.transcriptionStatus) {
            "PROCESSING" -> {
                _message.value = "正在转写中，请稍候"
                return
            }
            "PENDING" -> Unit
            "FAILED", "COMPLETED" -> Unit
            else -> {
                _message.value = "当前状态不支持转写"
                return
            }
        }
        if (modelManager.selected.value == null) {
            _message.value = "请先在「我的 → 语音转写引擎」下载并选择模型"
            return
        }
        val isRetranscribe = current.transcriptionStatus == "COMPLETED"
        viewModelScope.launch {
            if (current.transcriptionStatus != "PENDING") {
                recordRepository.resetForRetry(recordId)
            }
            WorkManager.getInstance(appContext).enqueueUniqueWork(
                TranscriptionWorker.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequestBuilder<TranscriptionWorker>().build()
            )
            _message.value = if (isRetranscribe) {
                "正在重新转写，完成后自动更新"
            } else {
                "已加入转写队列，进度见通知栏"
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
