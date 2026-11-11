package com.callnote.app.data.whisper

/**
 * 可选的 Whisper.cpp ggml 模型清单（多语言版，来自 whisper.cpp 官方发布）。
 * 模型下载后完全离线使用，转写不产生任何网络请求。
 */
enum class WhisperModel(
    val id: String,
    val displayName: String,
    val fileName: String,
    val sizeBytes: Long,
    val description: String
) {
    TINY(
        id = "tiny",
        displayName = "Tiny 极速版",
        fileName = "ggml-tiny.bin",
        sizeBytes = 77_685_000L,
        description = "约 75MB，转写最快，准确率较低，适合先试水"
    ),
    BASE(
        id = "base",
        displayName = "Base 均衡版",
        fileName = "ggml-base.bin",
        sizeBytes = 148_350_000L,
        description = "约 142MB，速度与准确率均衡，中文效果推荐"
    ),
    SMALL(
        id = "small",
        displayName = "Small 精准版",
        fileName = "ggml-small.bin",
        sizeBytes = 487_570_000L,
        description = "约 466MB，准确率最高，速度较慢、占用更大"
    );

    val downloadUrl: String get() = "$DOWNLOAD_BASE/$fileName"

    companion object {
        private const val DOWNLOAD_BASE =
            "https://huggingface.co/ggerganov/whisper.cpp/resolve/main"

        fun fromId(id: String?): WhisperModel? =
            if (id.isNullOrBlank()) null else entries.find { it.id == id }
    }
}
