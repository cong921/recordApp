package com.callnote.app.data.whisper

import android.content.Context
import com.callnote.app.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/** 单个模型的下载状态 */
sealed interface DownloadState {
    /** 本地未下载 */
    data object NotDownloaded : DownloadState

    /** 下载中：已接收字节 / 总字节（total 为 -1 表示服务器未返回长度） */
    data class Downloading(val receivedBytes: Long, val totalBytes: Long) : DownloadState

    /** 已下载完成 */
    data object Completed : DownloadState

    /** 下载失败 */
    data class Error(val message: String) : DownloadState
}

/** UI 直接可用的模型状态：清单 + 本地文件 + 下载进度合成 */
data class ModelStatus(
    val model: WhisperModel,
    val state: DownloadState,
    val downloaded: Boolean
)

/**
 * Whisper 模型管理：下载（支持进度与断点续传）、取消、删除、选中。
 * 下载在应用级作用域执行，退出页面不会中断。
 */
@Singleton
class WhisperModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope
) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    private val selectedId = MutableStateFlow(prefs.getString(KEY_SELECTED, null))
    private val fileVersion = MutableStateFlow(0)
    private val jobs = mutableMapOf<String, Job>()

    val selected: StateFlow<WhisperModel?> = selectedId
        .map { WhisperModel.fromId(it) }
        .stateIn(scope, SharingStarted.Eagerly, null)

    val statuses: StateFlow<List<ModelStatus>> = combine(
        downloadStates, selectedId, fileVersion
    ) { states, _, _ ->
        WhisperModel.entries.map { model ->
            val downloaded = isDownloaded(model)
            ModelStatus(
                model = model,
                state = states[model.id]
                    ?: if (downloaded) DownloadState.Completed else DownloadState.NotDownloaded,
                downloaded = downloaded
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    /** 开始下载；重复调用不会产生并发任务。 */
    fun download(model: WhisperModel) {
        synchronized(jobs) {
            if (jobs[model.id]?.isActive == true) return
            jobs[model.id] = scope.launch {
                try {
                    withContext(Dispatchers.IO) { downloadInternal(model) }
                } catch (e: CancellationException) {
                    // 用户取消：.part 保留供下次续传
                    refreshFiles()
                    throw e
                } catch (e: Exception) {
                    downloadStates.update {
                        it + (model.id to DownloadState.Error(e.message ?: "下载失败"))
                    }
                    refreshFiles()
                    return@launch
                }
                refreshFiles()
            }
        }
    }

    fun cancel(model: WhisperModel) {
        synchronized(jobs) { jobs.remove(model.id)?.cancel() }
        downloadStates.update { it - model.id }
        refreshFiles()
    }

    fun delete(model: WhisperModel) {
        cancel(model)
        val dir = modelsDir()
        File(dir, model.fileName).delete()
        File(dir, model.fileName + PART_SUFFIX).delete()
        if (selectedId.value == model.id) {
            select(null)
        }
        refreshFiles()
    }

    /** 设为转写引擎使用的模型；传 null 表示取消选择 */
    fun select(model: WhisperModel?) {
        prefs.edit().putString(KEY_SELECTED, model?.id).apply()
        selectedId.value = model?.id
        refreshFiles()
    }

    /** 模型本地文件（第四阶段转写引擎加载用）；文件可能尚未下载 */
    fun modelFileFor(model: WhisperModel): File = File(modelsDir(), model.fileName)

    private suspend fun downloadInternal(model: WhisperModel) {
        val dir = modelsDir()
        val dest = File(dir, model.fileName)
        val part = File(dir, model.fileName + PART_SUFFIX)

        val connection = URL(model.downloadUrl).openConnection() as HttpURLConnection
        connection.connectTimeout = CONNECT_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        val resumeFrom = if (part.exists()) part.length() else 0L
        if (resumeFrom > 0) {
            connection.setRequestProperty("Range", "bytes=$resumeFrom-")
        }
        try {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_PARTIAL) {
                throw IOException("服务器返回 HTTP $code")
            }
            val resumable = code == HttpURLConnection.HTTP_PARTIAL
            val contentLength = connection.contentLengthLong
            val total = if (resumable && contentLength >= 0) resumeFrom + contentLength else contentLength

            connection.inputStream.use { input ->
                FileOutputStream(part, resumable).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var received = if (resumable) resumeFrom else 0L
                    var lastReport = received
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        received += read
                        if (received - lastReport >= REPORT_INTERVAL ||
                            (total > 0 && received >= total)
                        ) {
                            lastReport = received
                            downloadStates.update {
                                it + (model.id to DownloadState.Downloading(received, total))
                            }
                        }
                    }
                    output.flush()
                }
            }
            // 完整性校验：大小达到期望值的绝大部分才视为有效
            if (part.length() < model.sizeBytes * MIN_SIZE_RATIO) {
                throw IOException("文件大小校验失败（预期约 ${model.sizeBytes}，实际 ${part.length()}）")
            }
            if (dest.exists()) dest.delete()
            if (!part.renameTo(dest)) throw IOException("无法保存模型文件")
        } finally {
            connection.disconnect()
        }
    }

    private fun isDownloaded(model: WhisperModel): Boolean =
        File(modelsDir(), model.fileName).length() >= model.sizeBytes * MIN_SIZE_RATIO

    private fun modelsDir(): File =
        File(context.filesDir, MODELS_DIR).apply { mkdirs() }

    private fun refreshFiles() {
        fileVersion.update { it + 1 }
    }

    companion object {
        private const val PREFS_NAME = "whisper_models"
        private const val KEY_SELECTED = "selected_model_id"
        private const val MODELS_DIR = "whisper-models"
        private const val PART_SUFFIX = ".part"
        private const val BUFFER_SIZE = 64 * 1024
        private const val REPORT_INTERVAL = 512L * 1024
        private const val MIN_SIZE_RATIO = 0.95
        private const val CONNECT_TIMEOUT = 15_000
        private const val READ_TIMEOUT = 30_000
    }
}
