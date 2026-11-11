package com.callnote.app.ui.records

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.ui.categories.categoryVisual
import com.callnote.app.ui.components.CategoryPickerSheet
import com.callnote.app.ui.components.EmptyState
import com.callnote.app.ui.components.StatusBadge
import com.callnote.app.util.Format
import kotlinx.coroutines.launch

/** 录音详情页：文件信息 + 分类管理 + 摘要 / 关键词 / 转写全文 / 失败原因 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    onBack: () -> Unit,
    viewModel: RecordDetailViewModel = hiltViewModel()
) {
    val record by viewModel.record.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val latestTask by viewModel.latestTask.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    if (showPicker && record != null) {
        CategoryPickerSheet(
            categories = categories,
            currentCategoryId = record?.record?.categoryId,
            onPick = { categoryId ->
                viewModel.assignCategory(categoryId)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("录音详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val loaded = record
        if (loaded == null) {
            // Flow 初值即为 null，加载完成后仍未 null 说明记录不存在
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "加载中…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val r = loaded.record
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---- 文件信息 ----
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = r.fileName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(statusName = r.transcriptionStatus)
                            }
                            Spacer(Modifier.height(12.dp))
                            DetailRow("通话时间", Format.dateTime(r.callTime))
                            DetailRow("时长", Format.duration(r.duration))
                            DetailRow("大小", Format.fileSize(r.fileSize))
                            DetailRow("格式", r.format)
                        }
                    }
                }

                // ---- 分类管理 ----
                item {
                    val category = loaded.category
                    val visual = category?.let { categoryVisual(it.categoryCode) }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        onClick = { showPicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = visual?.icon ?: Icons.Outlined.HelpOutline,
                                contentDescription = null,
                                tint = visual?.color ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("分类", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = category?.categoryName ?: "未分类",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            TextButton(onClick = { showPicker = true }) {
                                Icon(Icons.Outlined.Label, contentDescription = null,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("更改")
                            }
                        }
                    }
                }

                // ---- 失败原因 / 状态提示 ----
                when (r.transcriptionStatus) {
                    "FAILED" -> item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = "转写失败",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    latestTask?.errorMessage?.takeIf { it.isNotBlank() }?.let {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                                TextButton(onClick = viewModel::startTranscription) {
                                    Icon(
                                        Icons.Outlined.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("重试", color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                    }

                    "PENDING", "PROCESSING" -> item {
                        val isPending = r.transcriptionStatus == "PENDING"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isPending) {
                                        Icon(
                                            Icons.Outlined.HourglassTop,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = if (isPending) {
                                            "等待转写"
                                        } else {
                                            "正在本地转写中，完成后此页自动更新"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isPending) {
                                        TextButton(onClick = viewModel::startTranscription) {
                                            Icon(
                                                Icons.Outlined.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("开始转写")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ---- 摘要 ----
                r.summary?.takeIf { it.isNotBlank() }?.let { summary ->
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "摘要",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // ---- 关键词 ----
                r.keywords?.takeIf { it.isNotBlank() }?.let { keywords ->
                    item {
                        Column {
                            Text(
                                "关键词",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                keywords.split("，", ",").forEach { keyword ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(keyword.trim()) }
                                    )
                                }
                            }
                        }
                    }
                }

                // ---- 转写全文 ----
                r.transcriptionText?.takeIf { it.isNotBlank() }?.let { text ->
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "转写内容",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = {
                                        clipboard.setPrimaryClip(
                                            ClipData.newPlainText("转写内容", text)
                                        )
                                        scope.launch { snackbarHostState.showSnackbar("已复制全文") }
                                    }) {
                                        Icon(
                                            Icons.Outlined.ContentCopy,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("复制")
                                    }
                                    TextButton(onClick = {
                                        // 复用列表页导出格式：头信息 + 分类 + 摘要 + 关键词 + 全文
                                        val shareContent = RecordListViewModel.buildExportText(
                                            listOf(loaded)
                                        )
                                        val send = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, shareContent)
                                            putExtra(Intent.EXTRA_TITLE, r.fileName)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(send, "分享转写内容").apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        )
                                    }) {
                                        Icon(
                                            Icons.Outlined.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("分享")
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                SelectionContainer {
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
                                    )
                                }
                            }
                        }
                    }
                }

                // 已转写但没有任何文本时的兜底提示（失败/待转写已有各自的卡片）
                if (r.transcriptionStatus == "COMPLETED" &&
                    r.transcriptionText.isNullOrBlank() && r.summary.isNullOrBlank()
                ) {
                    item {
                        EmptyState(
                            icon = Icons.Outlined.Notes,
                            title = "暂无转写内容",
                            subtitle = "转写完成后，文字将显示在这里"
                        )
                    }
                }
            }
        }
    }
}

/** 信息行：标签 + 值 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
