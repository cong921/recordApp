package com.callnote.app.data.db.pojo

/** 首页 / 我的页统计 */
data class RecordStats(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val failedCount: Int = 0,
    val totalDuration: Long = 0,
    val totalSize: Long = 0
)

/** 分类页统计：每个分类的录音数量、大小、时长与最近通话时间 */
data class CategoryStats(
    val id: Long,
    val categoryName: String,
    val categoryCode: String,
    val isSystem: Boolean,
    val recordCount: Int,
    val totalSize: Long,
    val totalDuration: Long,
    val lastCallTime: Long?
)
