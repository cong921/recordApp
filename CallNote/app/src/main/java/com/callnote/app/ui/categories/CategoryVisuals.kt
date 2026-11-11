package com.callnote.app.ui.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** 分类的视觉映射：图标 + 品牌色（用于列表、卡片与分类页） */
data class CategoryVisual(val icon: ImageVector, val color: Color)

fun categoryVisual(categoryCode: String): CategoryVisual = when (categoryCode) {
    "WORK" -> CategoryVisual(Icons.Outlined.Work, Color(0xFF4C6FFF))
    "DELIVERY" -> CategoryVisual(Icons.Outlined.LocalShipping, Color(0xFF00A870))
    "ESTATE" -> CategoryVisual(Icons.Outlined.HomeWork, Color(0xFFB25E00))
    "SPAM" -> CategoryVisual(Icons.Outlined.Campaign, Color(0xFFD93025))
    "FINANCE" -> CategoryVisual(Icons.Outlined.AccountBalance, Color(0xFF7B1FA2))
    "FAMILY" -> CategoryVisual(Icons.Outlined.Favorite, Color(0xFFE91E63))
    "LIFE" -> CategoryVisual(Icons.Outlined.HomeRepairService, Color(0xFF00838F))
    "OTHER" -> CategoryVisual(Icons.Outlined.Label, Color(0xFF607D8B))
    else -> CategoryVisual(Icons.Outlined.Label, Color(0xFF607D8B))
}
