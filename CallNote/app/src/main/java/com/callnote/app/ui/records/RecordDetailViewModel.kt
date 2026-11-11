package com.callnote.app.ui.records

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
import com.callnote.app.data.repository.CallCategoryRepository
import com.callnote.app.data.repository.CallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 录音详情页 ViewModel：单条记录 + 分类列表 + 最近转写任务 */
@HiltViewModel
class RecordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recordRepository: CallRecordRepository,
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

    fun consumeMessage() {
        _message.value = null
    }
}
