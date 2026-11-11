package com.callnote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.ui.categories.categoryVisual

/** 手动归类底部弹窗：选择分类或移出分类（录音列表 / 详情页共用） */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
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
