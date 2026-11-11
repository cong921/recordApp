package com.callnote.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.dao.TranscriptionTaskDao
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.TranscriptionTaskEntity

/**
 * CallNote 主数据库。
 * schema JSON 导出到 app/schemas，为后续版本迁移保留依据。
 */
@Database(
    entities = [
        CallRecordEntity::class,
        CallCategoryEntity::class,
        TranscriptionTaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class CallNoteDatabase : RoomDatabase() {

    abstract fun callRecordDao(): CallRecordDao

    abstract fun callCategoryDao(): CallCategoryDao

    abstract fun transcriptionTaskDao(): TranscriptionTaskDao

    companion object {
        const val NAME = "callnote.db"
    }
}
