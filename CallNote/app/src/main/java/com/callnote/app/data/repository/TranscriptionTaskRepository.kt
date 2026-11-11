package com.callnote.app.data.repository

import com.callnote.app.data.db.dao.TranscriptionTaskDao
import com.callnote.app.data.db.entity.TranscriptionTaskEntity
import com.callnote.app.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptionTaskRepository @Inject constructor(
    private val dao: TranscriptionTaskDao
) {

    fun observeByRecord(recordId: Long): Flow<List<TranscriptionTaskEntity>> =
        dao.observeByRecord(recordId)

    fun observeAll(): Flow<List<TranscriptionTaskEntity>> = dao.observeAll()

    suspend fun getLatestByRecord(recordId: Long): TranscriptionTaskEntity? =
        dao.getLatestByRecord(recordId)

    suspend fun createTask(recordId: Long): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            TranscriptionTaskEntity(
                recordId = recordId,
                status = TaskStatus.PENDING.name,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    /** 更新任务状态与错误信息 */
    suspend fun updateTaskStatus(
        recordId: Long,
        status: TaskStatus,
        progress: Int = 0,
        errorMessage: String? = null
    ) {
        val task = dao.getLatestByRecord(recordId) ?: return
        dao.update(
            task.copy(
                status = status.name,
                progress = progress,
                errorMessage = errorMessage ?: task.errorMessage,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
