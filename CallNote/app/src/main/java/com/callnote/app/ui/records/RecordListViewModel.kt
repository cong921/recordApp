package com.callnote.app.ui.records

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.repository.CallRecordRepository
import com.callnote.app.util.Format
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    @ApplicationContext private val appContext: Context,
    private val recordRepository: CallRecordRepository
) : ViewModel() {

    // 路由参数类型为 NavType.LongType，必须按 Long 读取（按 String 读取会 ClassCastException）
    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: -1L

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(RecordFilter.ALL)

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
            val queryMatch = query.isBlank() || run {
                val q = query.trim()
                record.record.fileName.contains(q, ignoreCase = true)
                    || record.record.transcriptionText?.contains(q) == true
                    || record.record.summary?.contains(q) == true
                    || record.record.keywords?.contains(q) == true
            }
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

    private val _exporting = MutableStateFlow(false)
    val exporting: StateFlow<Boolean> = _exporting.asStateFlow()

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onFilterChange(newFilter: RecordFilter) {
        filter.value = newFilter
    }

    /** 将当前列表（应用搜索/筛选/分类后的结果）导出到指定 SAF Uri */
    fun exportTo(uri: Uri) {
        val records = uiState.value.records
        if (records.isEmpty()) {
            _message.value = "当前列表为空，没有可导出的录音"
            return
        }
        _exporting.value = true
        viewModelScope.launch {
            val written = withContext(Dispatchers.IO) {
                runCatching {
                    appContext.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(buildExportText(records).toByteArray(Charsets.UTF_8))
                    } != null
                }.getOrDefault(false)
            }
            _exporting.value = false
            _message.value = if (written) {
                "已导出 ${records.size} 条录音到 TXT 文件"
            } else {
                "导出失败，请重试"
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    companion object {
        private val EXPORT_HEADER_TIME = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm")

        /** 生成导出文本（纯函数，可单元测试）；exportTime 用于测试注入 */
        fun buildExportText(
            records: List<RecordWithCategory>,
            exportTime: LocalDateTime = LocalDateTime.now(),
            zone: ZoneId = ZoneId.systemDefault()
        ): String = buildString {
            appendLine("听记录音导出")
            appendLine("导出时间：${EXPORT_HEADER_TIME.format(exportTime)}")
            appendLine("共 ${records.size} 条录音")
            appendLine()
            records.forEachIndexed { index, item ->
                val r = item.record
                appendLine("=".repeat(46))
                appendLine("【${index + 1}】${r.fileName}")
                appendLine("时间：${Format.dateTime(r.callTime, zone)}")
                appendLine("时长：${Format.duration(r.duration)}")
                appendLine("分类：${item.category?.categoryName ?: "未分类"}")
                appendLine(
                    "状态：" + when (r.transcriptionStatus) {
                        "COMPLETED" -> "已转写"
                        "PROCESSING" -> "转写中"
                        "PENDING" -> "待转写"
                        "FAILED" -> "转写失败"
                        else -> r.transcriptionStatus
                    }
                )
                r.summary?.takeIf { it.isNotBlank() }?.let { appendLine("摘要：$it") }
                r.keywords?.takeIf { it.isNotBlank() }?.let { appendLine("关键词：$it") }
                r.transcriptionText?.takeIf { it.isNotBlank() }?.let {
                    appendLine("转写内容：")
                    appendLine(it)
                }
            }
            appendLine("=".repeat(46))
        }
    }
}
