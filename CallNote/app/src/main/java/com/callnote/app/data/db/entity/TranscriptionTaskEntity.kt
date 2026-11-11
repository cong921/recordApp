package com.callnote.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** 转写任务，对应表 transcription_task。第四阶段批量转写使用，随录音删除而级联删除。 */
@Entity(
    tableName = "transcription_task",
    foreignKeys = [
        ForeignKey(
            entity = CallRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recordId")]
)
data class TranscriptionTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val recordId: Long,
    val status: String,
    /** 0-100 */
    val progress: Int = 0,
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
