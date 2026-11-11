package com.callnote.app.ui.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.ui.categories.categoryVisual
import com.callnote.app.ui.components.EmptyState
import com.callnote.app.ui.components.RecordListItem
import com.callnote.app.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 待归类的录音（点击列表项后弹底部选择器）
    var assignTarget by remember { mutableStateOf<RecordWithCategory?>(null) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    assignTarget?.let { target ->
        CategoryPickerSheet(
            categories = categories,
            currentCategoryId = target.record.categoryId,
            onPick = { categoryId ->
                viewModel.assignCategory(target.record.id, categoryId)
                assignTarget = null
            },
            onDismiss = { assignTarget = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = uiState.categoryFilterName?.let { "分类 · $it" } ?: "录音")
                        Text(
                            text = "共 ${uiState.totalCount} 条",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("搜索文件名或转写内容") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Outlined.Close, contentDescription = "清空")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecordFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.filter == filter,
                        onClick = { viewModel.onFilterChange(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            if (uiState.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.records.isEmpty()) {
                EmptyState(
                    icon = Icons.AutoMirrored.Outlined.Notes,
                    title = if (uiState.query.isBlank()) "暂无录音" else "未找到匹配的录音",
                    subtitle = if (uiState.query.isBlank()) {
                        "导入电话录音后，将在这里统一管理"
                    } else {
                        "换个关键词试试，或清空筛选条件"
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.records, key = { it.record.id }) { item ->
                        RecordListItem(
                            item = item,
                            onClick = { assignTarget = item },
                            trailing = {
                                StatusBadge(statusName = item.record.transcriptionStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 手动归类底部弹窗：选择分类或移出分类 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerSheet(
    categories: List<CallCategoryEntity>,
    currentCategoryId: Long?,
    onPick: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            text = "移动到分类",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("未分类") },
                    leadingContent = {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = null)
                    },
                    modifier = Modifier.let { m ->
                        if (currentCategoryId == null) {
                            m.background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        } else {
                            m.clickable { onPick(null) }
                        }
                    }
                )
            }
            items(categories, key = { it.id }) { category ->
                val visual = categoryVisual(category.categoryCode)
                ListItem(
                    headlineContent = { Text(category.categoryName) },
                    leadingContent = {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = category.categoryName,
                            tint = visual.color
                        )
                    },
                    modifier = if (currentCategoryId == category.id) {
                        Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    } else {
                        Modifier.clickable { onPick(category.id) }
                    }
                )
            }
        }
    }
}
