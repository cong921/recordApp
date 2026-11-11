package com.callnote.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callnote.app.data.whisper.DownloadState
import com.callnote.app.data.whisper.ModelStatus
import com.callnote.app.data.whisper.WhisperModel
import com.callnote.app.util.Format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modelStatuses by viewModel.modelStatuses.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard()
            StorageCard(uiState)
            EngineCard(
                statuses = modelStatuses,
                selected = selectedModel,
                onDownload = viewModel::downloadModel,
                onCancel = viewModel::cancelDownload,
                onDelete = viewModel::deleteModel,
                onSelect = viewModel::selectModel
            )
            RoadmapCard()
            PrivacyCard()
            Text(
                text = "听记 CallNote · v1.0.0 全部八阶段完成",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeaderCard() {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "听记 CallNote",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "免费 · 离线 · 隐私优先的通话录音管理",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StorageCard(uiState: ProfileUiState) {
    SectionCard(title = "存储与记录") {
        InfoRow(
            icon = Icons.Outlined.Folder,
            label = "录音总数",
            value = "${uiState.stats.totalCount} 条"
        )
        InfoRow(
            icon = Icons.Outlined.Verified,
            label = "已转写",
            value = "${uiState.stats.completedCount} 条"
        )
        InfoRow(
            icon = Icons.Outlined.GraphicEq,
            label = "待转写 / 失败",
            value = "${uiState.stats.pendingCount} / ${uiState.stats.failedCount} 条"
        )
        InfoRow(
            icon = Icons.Outlined.Folder,
            label = "占用空间",
            value = Format.fileSize(uiState.stats.totalSize)
        )
    }
}

@Composable
private fun EngineCard(
    statuses: List<ModelStatus>,
    selected: WhisperModel?,
    onDownload: (WhisperModel) -> Unit,
    onCancel: (WhisperModel) -> Unit,
    onDelete: (WhisperModel) -> Unit,
    onSelect: (WhisperModel) -> Unit
) {
    var pendingDelete by remember { mutableStateOf<WhisperModel?>(null) }

    SectionCard(title = "语音转写引擎") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.GraphicEq,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = "Whisper.cpp（本地离线）", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "模型下载后完全离线运行，删除模型即可释放空间",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusTag(text = selected?.id?.replaceFirstChar { it.uppercase() } ?: "未选择")
        }
        if (statuses.isEmpty()) {
            Text(
                text = "正在加载模型清单…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            statuses.forEach { status ->
                ModelRow(
                    status = status,
                    isSelected = status.model == selected,
                    onDownload = onDownload,
                    onCancel = onCancel,
                    onDelete = { pendingDelete = status.model },
                    onSelect = onSelect
                )
            }
        }
    }

    pendingDelete?.let { model ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除模型") },
            text = {
                Text("将删除 ${model.displayName}（${Format.fileSize(model.sizeBytes)}），删除后可重新下载。")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(model)
                    pendingDelete = null
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun ModelRow(
    status: ModelStatus,
    isSelected: Boolean,
    onDownload: (WhisperModel) -> Unit,
    onCancel: (WhisperModel) -> Unit,
    onDelete: (WhisperModel) -> Unit,
    onSelect: (WhisperModel) -> Unit
) {
    val model = status.model
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                enabled = status.downloaded,
                onClick = { if (!isSelected) onSelect(model) }
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${model.displayName} · ${Format.fileSize(model.sizeBytes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = model.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when (val state = status.state) {
                    is DownloadState.Downloading -> {
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = {
                                if (state.totalBytes > 0) {
                                    (state.receivedBytes.toFloat() / state.totalBytes).coerceIn(0f, 1f)
                                } else 0f
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = buildString {
                                if (state.totalBytes > 0) {
                                    append("${state.receivedBytes * 100 / state.totalBytes}% · ")
                                }
                                append("${Format.fileSize(state.receivedBytes)} / ${Format.fileSize(state.totalBytes)}")
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is DownloadState.Error -> {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "下载失败：${state.message}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
            }
            when {
                status.state is DownloadState.Downloading -> {
                    TextButton(onClick = { onCancel(model) }) { Text("取消") }
                }
                status.downloaded -> {
                    TextButton(
                        onClick = { onSelect(model) },
                        enabled = !isSelected
                    ) { Text(if (isSelected) "使用中" else "使用") }
                    IconButton(onClick = { onDelete(model) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "删除模型",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    Button(onClick = { onDownload(model) }) { Text("下载") }
                }
            }
        }
    }
}

@Composable
private fun RoadmapCard() {
    SectionCard(title = "功能规划") {
        RoadmapRow(label = "工程基础 · 首页 / 录音 / 分类", phase = "第一阶段", done = true)
        RoadmapRow(label = "录音导入与管理（SAF）", phase = "第二阶段", done = true)
        RoadmapRow(label = "Whisper.cpp 本地集成", phase = "第三阶段", done = true)
        RoadmapRow(label = "批量转写与任务管理", phase = "第四阶段", done = true)
        RoadmapRow(label = "本地智能分类", phase = "第五阶段", done = true)
        RoadmapRow(label = "分类管理与批量清理", phase = "第六阶段", done = true)
        RoadmapRow(label = "全文搜索与导出 TXT", phase = "第七阶段", done = true)
        RoadmapRow(label = "性能优化与真机测试", phase = "第八阶段", done = true)
    }
}

@Composable
private fun PrivacyCard() {
    SectionCard(title = "隐私承诺") {
        InfoRow(icon = Icons.Outlined.CloudOff, label = "不上传录音与转写文本", value = "")
        InfoRow(icon = Icons.Outlined.Lock, label = "无需注册账号", value = "")
        InfoRow(icon = Icons.Outlined.Security, label = "不依赖云端服务器", value = "")
        InfoRow(icon = Icons.Outlined.Person, label = "所有数据仅保存在本机", value = "")
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RoadmapRow(label: String, phase: String, done: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        StatusTag(text = if (done) "$phase ✓" else phase, done = done)
    }
}

@Composable
private fun StatusTag(text: String, done: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (done) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        contentColor = if (done) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
