package com.callnote.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 录音分类，对应表 call_category。系统分类不可删除，用户可自定义新分类。 */
@Entity(tableName = "call_category")
data class CallCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val categoryName: String,
    /** WORK / DELIVERY / ESTATE / SPAM / FINANCE / FAMILY / LIFE / OTHER，自定义分类使用 CUSTOM_ 前缀 */
    val categoryCode: String,
    val description: String,
    val isSystem: Boolean = true,
    val sort: Int = 0
)
