package com.callnote.app.data.importer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.entity.CallRecordEntity
import com.callnote.app.data.model.TranscriptionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** 批量导入结果：成功条数、失败文件名列表、顺带清除的演示数据条数 */
data class ImportResult(
    val successCount: Int,
    val failedFiles: List<String>,
    val demoPurged: Int = 0
)

/**
 * SAF 导入处理器：将用户选择的音频复制到应用私有目录（避免 URI 授权失效后无法访问），
 * 解析时长/大小/格式后写入数据库，初始状态为待转写。
 */
@Singleton
class AudioImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordDao: CallRecordDao
) {

    /** 逐个导入，单文件失败不中断整批；首次导入真实录音时顺带清除预置演示数据 */
    suspend fun import(uris: List<Uri>): ImportResult = withContext(Dispatchers.IO) {
        val demoPurged = if (recordDao.count() > 0) recordDao.deleteDemoRecords() else 0
        var success = 0
        val failed = mutableListOf<String>()
        for (uri in uris) {
            try {
                importOne(uri)
                success++
            } catch (e: Exception) {
                failed += resolveDisplayName(uri)
            }
        }
        ImportResult(success, failed, demoPurged)
    }

    private suspend fun importOne(uri: Uri) {
        val displayName = resolveDisplayName(uri).substringAfterLast('/')
        val dir = File(context.filesDir, RECORDINGS_DIR).apply { mkdirs() }
        val dest = File(dir, "${System.currentTimeMillis()}_$displayName")

        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IOException("无法读取所选文件")

        val now = System.currentTimeMillis()
        recordDao.insert(
            CallRecordEntity(
                fileName = displayName,
                fileUri = uri.toString(),
                localPath = dest.absolutePath,
                fileSize = dest.length(),
                duration = probeDurationSeconds(dest),
                format = audioFormatFromName(displayName),
                callTime = now,
                importTime = now,
                transcriptionStatus = TranscriptionStatus.PENDING.name,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    /** 查询 SAF 文件的显示名，失败时退化为 lastPathSegment */
    private fun resolveDisplayName(uri: Uri): String {
        context.contentResolver
            .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst() && !cursor.isNull(idx)) {
                    return cursor.getString(idx)
                }
            }
        return uri.lastPathSegment ?: "unknown_${System.currentTimeMillis()}"
    }

    /** 用本地副本解析时长（秒）；解析失败返回 0，不阻塞导入 */
    private fun probeDurationSeconds(file: File): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val ms = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            ms / 1000
        } catch (e: Exception) {
            0L
        } finally {
            retriever.release()
        }
    }

    companion object {
        private const val RECORDINGS_DIR = "recordings"

        /** 从文件名解析音频格式编码；无法识别时返回 AUDIO */
        fun audioFormatFromName(fileName: String): String {
            val ext = fileName.substringAfterLast('.', "").lowercase()
            return when (ext) {
                "mp3" -> "MP3"
                "wav" -> "WAV"
                "m4a" -> "M4A"
                "aac" -> "AAC"
                "amr", "3gp", "3gpp" -> "AMR"
                "ogg", "oga" -> "OGG"
                "flac" -> "FLAC"
                "opus" -> "OPUS"
                "wma" -> "WMA"
                else -> "AUDIO"
            }
        }
    }
}
