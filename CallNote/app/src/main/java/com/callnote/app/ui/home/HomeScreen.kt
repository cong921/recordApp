package com.callnote.app.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callnote.app.data.db.pojo.RecordStats
import com.callnote.app.ui.components.RecordListItem
import com.callnote.app.ui.components.StatusBadge
import com.callnote.app.util.Format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategories: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val importing by viewModel.importing.collectAsStateWithLifecycle()
    val classifying by viewModel.classifying.collectAsStateWithLifecycle()
    val cleaning by viewModel.cleaning.collectAsStateWithLifecycle()
    val cleanPreview by viewModel.cleanPreview.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) viewModel.importUris(uris)
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    // 批量清理确认对话框
    cleanPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = viewModel::cancelClean,
            title = { Text("批量清理") },
            text = {
                Text(
                    "将删除 ${preview.count} 条已转写录音的音频文件，" +
                        "预计释放 ${Format.fileSize(preview.sizeBytes)}。\n\n" +
                        "转写文本、摘要与分类会完整保留。"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::executeClean,
                    enabled = !cleaning
                ) { Text(if (cleaning) "清理中…" else "清理") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelClean) { Text("取消") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "听记",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "免费 · 离线 · 智能通话录音管理",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { StatsCard(uiState.stats) }
                item {
                    QuickActions(
                        importing = importing,
                        classifying = classifying,
                        onImport = { importLauncher.launch(arrayOf("audio/*")) },
                        onTranscribe = viewModel::startBatchTranscription,
                        onClassify = viewModel::startBatchClassification,
                        onClean = viewModel::prepareClean
                    )
                }
                item {
                    Text(
                        text = "最近录音",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.recentRecords, key = { it.record.id }) { item ->
                    RecordListItem(item = item, trailing = {
                        StatusBadge(statusName = item.record.transcriptionStatus)
                    })
                }
            }
        }
    }
}

@Composable
private fun StatsCard(stats: RecordStats) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.LibraryMusic,
                    label = "录音总数",
                    value = stats.totalCount.toString()
                )
                StatItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Timer,
                    label = "录音总时长",
                    value = Format.duration(stats.totalDuration)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CheckCircle,
                    label = "已转写",
                    value = stats.completedCount.toString()
                )
                StatItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Schedule,
                    label = "待转写",
                    value = stats.pendingCount.toString()
                )
                StatItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.ErrorOutline,
                    label = "失败",
                    value = stats.failedCount.toString()
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            StatItem(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Folder,
                label = "占用空间",
                value = Format.fileSize(stats.totalSize)
            )
        }
    }
}

@Composable
private fun StatItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActions(
    importing: Boolean,
    classifying: Boolean,
    onImport: () -> Unit,
    onTranscribe: () -> Unit,
    onClassify: () -> Unit,
    onClean: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "快捷操作",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.FileUpload,
                label = if (importing) "导入中…" else "导入录音",
                loading = importing,
                onClick = onImport
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.GraphicEq,
                label = "批量转写",
                onClick = onTranscribe
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.AutoAwesome,
                label = if (classifying) "分类中…" else "智能分类",
                loading = classifying,
                onClick = onClassify
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.CleaningServices,
                label = "批量清理",
                onClick = onClean
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (loading) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (loading) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onPrimaryContainer,
        onClick = { if (!loading) onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(imageVector = icon, contentDescription = label)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}
