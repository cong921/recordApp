package com.callnote.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionTaskDao {

    @Insert
    suspend fun insert(task: TranscriptionTaskEntity): Long

    @Insert
    suspend fun insertAll(tasks: List<TranscriptionTaskEntity>)

    @Update
    suspend fun update(task: TranscriptionTaskEntity)

    @Query("SELECT * FROM transcription_task WHERE recordId = :recordId ORDER BY createdAt DESC")
    fun observeByRecord(recordId: Long): Flow<List<TranscriptionTaskEntity>>

    @Query("SELECT * FROM transcription_task ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TranscriptionTaskEntity>>

    @Query("SELECT * FROM transcription_task WHERE recordId = :recordId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestByRecord(recordId: Long): TranscriptionTaskEntity?

    @Query(
        """
        UPDATE transcription_task
        SET status = 'PENDING', errorMessage = NULL, progress = 0, updatedAt = :updatedAt
        WHERE recordId = :recordId
        """
    )
    suspend fun resetByRecord(recordId: Long, updatedAt: Long)

    @Query("DELETE FROM transcription_task WHERE recordId = :recordId")
    suspend fun deleteByRecord(recordId: Long)
}
