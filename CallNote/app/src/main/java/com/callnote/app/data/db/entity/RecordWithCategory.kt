package com.callnote.app.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/** 录音及其分类（未分类时为 null） */
data class RecordWithCategory(
    @Embedded val record: CallRecordEntity,
    @Relation(parentColumn = "categoryId", entityColumn = "id")
    val category: CallCategoryEntity?
)
