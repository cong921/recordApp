package com.callnote.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 通话录音记录，对应表 call_record。
 * fileUri 为 SAF 授权 URI；localPath 为后续阶段复制到应用私有目录的副本路径。
 */
@Entity(
    tableName = "call_record",
    foreignKeys = [
        ForeignKey(
            entity = CallCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("transcriptionStatus")]
)
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fileName: String,
    val fileUri: String,
    val localPath: String? = null,
    val fileSize: Long,
    /** 通话时长，秒 */
    val duration: Long,
    /** 音频格式：MP3 / WAV / M4A / AAC / AMR / OGG */
    val format: String,
    val callTime: Long,
    val importTime: Long,
    val transcriptionStatus: String,
    val transcriptionText: String? = null,
    val categoryId: Long? = null,
    val summary: String? = null,
    /** 逗号分隔的关键词 */
    val keywords: String? = null,
    val classificationScore: Float? = null,
    val createdAt: Long,
    val updatedAt: Long
)
