package com.callnote.app.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.callnote.app.MainActivity
import com.callnote.app.R
import com.callnote.app.data.classification.LocalClassifier
import com.callnote.app.data.model.TaskStatus
import com.callnote.app.data.model.TranscriptionStatus
import com.callnote.app.data.repository.CallCategoryRepository
import com.callnote.app.data.repository.CallRecordRepository
import com.callnote.app.data.repository.TranscriptionTaskRepository
import com.callnote.app.data.whisper.WhisperModelManager
import com.callnote.app.transcription.TranscriptionEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.IOException

/**
 * 批量转写 Worker：以前台服务逐条转写所有待转写录音。
 * 进度通过通知与 WorkManager progress 双通道暴露，录音列表经 Room Flow 自动刷新。
 */
@HiltWorker
class TranscriptionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val recordRepository: CallRecordRepository,
    private val categoryRepository: CallCategoryRepository,
    private val taskRepository: TranscriptionTaskRepository,
    private val modelManager: WhisperModelManager,
    private val classifier: LocalClassifier,
    private val engine: TranscriptionEngine
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val model = modelManager.selected.value
            ?: return Result.failure(workDataOf(KEY_ERROR to "尚未选择转写模型，请到「我的 → 语音转写引擎」下载并选择"))

        val modelFile = modelManager.modelFileFor(model)
        if (!modelFile.exists()) {
            return Result.failure(workDataOf(KEY_ERROR to "模型文件缺失（${model.displayName}），请重新下载"))
        }

        val pending = recordRepository.getPendingRecords()
        if (pending.isEmpty()) {
            return Result.success(workDataOf(KEY_DONE to 0))
        }

        setForeground(createForegroundInfo(0, pending.size))

        if (!engine.open(modelFile.absolutePath)) {
            return Result.failure(workDataOf(KEY_ERROR to "模型加载失败：${model.displayName}"))
        }

        var successCount = 0
        var failedCount = 0
        try {
            pending.forEachIndexed { index, record ->
                setProgress(
                    workDataOf(
                        KEY_CURRENT to index + 1,
                        KEY_TOTAL to pending.size,
                        KEY_DONE to successCount
                    )
                )
                setForeground(createForegroundInfo(index + 1, pending.size))
                if (transcribeOne(record.id, record.localPath)) successCount++ else failedCount++
            }
        } finally {
            engine.close()
        }

        notifyResult(successCount, failedCount)

        return Result.success(
            workDataOf(
                KEY_DONE to successCount,
                KEY_FAILED to failedCount
            )
        )
    }

    /** 转写单条录音；返回是否成功 */
    private suspend fun transcribeOne(recordId: Long, localPath: String?): Boolean {
        recordRepository.updateRecordStatus(recordId, TranscriptionStatus.PROCESSING)
        taskRepository.updateTaskStatus(recordId, TaskStatus.RUNNING)
        try {
            val path = localPath
            if (path.isNullOrBlank() || !File(path).exists()) {
                throw IOException("音频文件不存在或已被清理")
            }
            val text = engine.transcribe(path)
            // 第五阶段：转写完成后本地规则分类（摘要/关键词/置信度一并生成）
            val classification = classifier.classify(text)
            val category = classification.categoryCode
                ?.let { categoryRepository.getByCode(it) }
            recordRepository.updateRecordTranscription(
                recordId = recordId,
                text = text,
                summary = classification.summary,
                keywords = classification.keywords.joinToString("，"),
                categoryId = category?.id,
                classificationScore = if (classification.categoryCode != null) classification.score else null
            )
            taskRepository.updateTaskStatus(recordId, TaskStatus.COMPLETED, progress = 100)
            return true
        } catch (e: Exception) {
            recordRepository.updateRecordStatus(recordId, TranscriptionStatus.FAILED)
            taskRepository.updateTaskStatus(
                recordId,
                TaskStatus.FAILED,
                errorMessage = e.message ?: "未知错误"
            )
            return false
        }
    }

    /** 结束后发一条可点击的结果通知（前台进度通知会随 Worker 结束自动撤下） */
    private fun notifyResult(successCount: Int, failedCount: Int) {
        val text = if (failedCount == 0) {
            "成功 $successCount 条，可到录音页查看全文"
        } else {
            "成功 $successCount 条，失败 $failedCount 条（详情页可查看原因并重试）"
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("转写完成")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(mainIntent())
            .setAutoCancel(true)
            .build()
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(RESULT_NOTIFICATION_ID, notification)
    }

    private fun mainIntent(): PendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        Intent(applicationContext, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun createForegroundInfo(current: Int, total: Int): ForegroundInfo {
        val title = if (total == 0) "正在准备转写" else "正在本地转写（$current/$total）"
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("录音在设备本地处理，不会上传")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(mainIntent())
            .setProgress(total.coerceAtLeast(1), current, total == 0)
            .build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "batch_transcription"
        const val CHANNEL_ID = "transcription"
        const val NOTIFICATION_ID = 1001
        const val RESULT_NOTIFICATION_ID = 1002
        const val KEY_ERROR = "error"
        const val KEY_DONE = "done"
        const val KEY_FAILED = "failed"
        const val KEY_CURRENT = "current"
        const val KEY_TOTAL = "total"

        /** 创建通知渠道（应用启动时调用一次） */
        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) != null) return
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "批量转写",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "本地批量转写进度通知"
                    setShowBadge(false)
                }
            )
        }
    }
}
