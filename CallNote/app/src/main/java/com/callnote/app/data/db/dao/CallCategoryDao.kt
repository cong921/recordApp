package com.callnote.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.callnote.app.data.db.entity.CallCategoryEntity
import com.callnote.app.data.db.pojo.CategoryStats
import kotlinx.coroutines.flow.Flow

@Dao
interface CallCategoryDao {

    @Insert
    suspend fun insertAll(categories: List<CallCategoryEntity>): List<Long>

    @Insert
    suspend fun insert(category: CallCategoryEntity): Long

    @Update
    suspend fun update(category: CallCategoryEntity)

    @Query("SELECT * FROM call_category ORDER BY sort ASC")
    fun observeAll(): Flow<List<CallCategoryEntity>>

    @Query("SELECT * FROM call_category WHERE id = :id")
    suspend fun getById(id: Long): CallCategoryEntity?

    @Query("SELECT categoryName FROM call_category WHERE id = :id")
    fun observeCategoryName(id: Long): Flow<String?>

    @Query("SELECT COUNT(*) FROM call_category")
    suspend fun count(): Int

    @Query(
        """
        SELECT c.id AS id,
               c.categoryName AS categoryName,
               c.categoryCode AS categoryCode,
               COUNT(r.id) AS recordCount,
               COALESCE(SUM(r.fileSize), 0) AS totalSize,
               COALESCE(SUM(r.duration), 0) AS totalDuration,
               MAX(r.callTime) AS lastCallTime
        FROM call_category c
        LEFT JOIN call_record r ON r.categoryId = c.id
        GROUP BY c.id
        ORDER BY c.sort ASC
        """
    )
    fun observeCategoryStats(): Flow<List<CategoryStats>>
}
