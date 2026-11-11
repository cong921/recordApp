package com.callnote.app.transcription

/**
 * whisper.cpp JNI 绑定。native 实现见 app/src/main/cpp/whisper_jni.cpp。
 * 上下文句柄为非托管资源，必须与 freeContext 成对使用。
 */
object WhisperNative {

    init {
        System.loadLibrary("whisper_jni")
    }

    /** 从 ggml 模型文件创建 whisper 上下文；失败返回 0 */
    external fun createContext(modelPath: String): Long

    /** 释放上下文 */
    external fun freeContext(contextPtr: Long)

    /**
     * 转写 16kHz 单声道 float PCM。
     * @return 拼接后的转写文本；失败返回空字符串
     */
    external fun transcribePcm(contextPtr: Long, pcmData: FloatArray, pcmLength: Int): String
}
