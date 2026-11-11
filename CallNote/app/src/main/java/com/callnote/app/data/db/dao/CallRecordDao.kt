package com.callnote.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.db.entity.RecordWithCategory
import com.callnote.app.data.db.pojo.RecordStats
import kotlinx.coroutines.flow.Flow

@Dao
interface CallRecordDao {

    @Insert
    suspend fun insert(record: CallRecordEntity): Long

    @Insert
    suspend fun insertAll(records: List<CallRecordEntity>): List<Long>

    @Update
    suspend fun update(record: CallRecordEntity)

    @Delete
    suspend fun delete(record: CallRecordEntity)

    @Query("SELECT COUNT(*) FROM call_record")
    suspend fun count(): Int

    @Query("SELECT * FROM call_record WHERE transcriptionStatus = 'PENDING' ORDER BY callTime ASC")
    suspend fun getPendingRecords(): List<CallRecordEntity>

    @Query(
        """
        SELECT * FROM call_record
        WHERE transcriptionStatus = 'COMPLETED' AND categoryId IS NULL
        ORDER BY callTime ASC
        """
    )
    suspend fun getUnclassifiedCompleted(): List<CallRecordEntity>

    @Query("UPDATE call_record SET transcriptionStatus = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)

    @Transaction
    @Query("SELECT * FROM call_record ORDER BY callTime DESC")
    fun observeAllWithCategory(): Flow<List<RecordWithCategory>>

    @Transaction
    @Query("SELECT * FROM call_record WHERE categoryId = :categoryId ORDER BY callTime DESC")
    fun observeByCategoryWithCategory(categoryId: Long): Flow<List<RecordWithCategory>>

    @Transaction
    @Query("SELECT * FROM call_record ORDER BY callTime DESC LIMIT :limit")
    fun observeRecentWithCategory(limit: Int): Flow<List<RecordWithCategory>>

    @Query("SELECT * FROM call_record WHERE id = :id")
    fun observeById(id: Long): Flow<CallRecordEntity?>

    @Query(
        """
        SELECT COUNT(*) AS totalCount,
               COALESCE(SUM(CASE WHEN transcriptionStatus = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completedCount,
               COALESCE(SUM(CASE WHEN transcriptionStatus IN ('PENDING', 'PROCESSING') THEN 1 ELSE 0 END), 0) AS pendingCount,
               COALESCE(SUM(CASE WHEN transcriptionStatus = 'FAILED' THEN 1 ELSE 0 END), 0) AS failedCount,
               COALESCE(SUM(duration), 0) AS totalDuration,
               COALESCE(SUM(fileSize), 0) AS totalSize
        FROM call_record
        """
    )
    fun observeStats(): Flow<RecordStats>

    @Query(
        """
        SELECT * FROM call_record
        WHERE fileName LIKE '%' || :query || '%'
           OR transcriptionText LIKE '%' || :query || '%'
           OR summary LIKE '%' || :query || '%'
           OR keywords LIKE '%' || :query || '%'
        ORDER BY callTime DESC
        """
    )
    fun search(query: String): Flow<List<CallRecordEntity>>

    @Query("UPDATE call_record SET categoryId = :categoryId, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCategory(id: Long, categoryId: Long?, updatedAt: Long)

    @Query("UPDATE call_record SET classificationScore = :score, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateClassificationScore(id: Long, score: Double, updatedAt: Long)

    @Query("UPDATE call_record SET summary = :summary, keywords = :keywords, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSummaryAndKeywords(id: Long, summary: String, keywords: String, updatedAt: Long)

    @Query(
        """
        UPDATE call_record
        SET transcriptionText = :text, summary = :summary, keywords = :keywords,
            transcriptionStatus = :status, updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateTranscription(
        id: Long,
        text: String,
        summary: String,
        keywords: String,
        status: String,
        updatedAt: Long
    )

    @Query("UPDATE call_record SET localPath = NULL, fileUri = :placeholderUri, fileSize = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markAudioDeleted(id: Long, placeholderUri: String, updatedAt: Long)
}
