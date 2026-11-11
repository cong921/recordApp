#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <algorithm>
#include <chrono>
#include <string>
#include <vector>

#include "whisper.h"

#define LOG_TAG "CallNote/Whisper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 线程数按设备在线核数自适应，上限 8（tiny/base 模型线程过多收益递减）
static int pickThreadCount() {
    long cores = sysconf(_SC_NPROCESSORS_ONLN);
    if (cores < 1) cores = 1;
    return static_cast<int>(std::min(cores, 8L));
}

extern "C" {

// 加载 ggml 模型并创建 whisper 上下文；失败返回 0
JNIEXPORT jlong JNICALL
Java_com_callnote_app_transcription_WhisperNative_createContext(
        JNIEnv *env, jobject /*thiz*/, jstring modelPath) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    if (path == nullptr) {
        return 0;
    }
    struct whisper_context_params cparams = whisper_context_default_params();
    cparams.use_gpu = false;
    struct whisper_context *ctx = whisper_init_from_file_with_params(path, cparams);
    env->ReleaseStringUTFChars(modelPath, path);
    if (ctx == nullptr) {
        LOGE("whisper_init_from_file_with_params failed");
        return 0;
    }
    LOGI("whisper context created");
    return reinterpret_cast<jlong>(ctx);
}

// 释放 whisper 上下文
JNIEXPORT void JNICALL
Java_com_callnote_app_transcription_WhisperNative_freeContext(
        JNIEnv * /*env*/, jobject /*thiz*/, jlong contextPtr) {
    auto *ctx = reinterpret_cast<struct whisper_context *>(contextPtr);
    if (ctx != nullptr) {
        whisper_free(ctx);
        LOGI("whisper context freed");
    }
}

// 转写 16kHz 单声道 float PCM，返回拼接后的文本；失败返回空字符串
JNIEXPORT jstring JNICALL
Java_com_callnote_app_transcription_WhisperNative_transcribePcm(
        JNIEnv *env, jobject /*thiz*/, jlong contextPtr, jfloatArray pcmData, jint pcmLength) {
    auto *ctx = reinterpret_cast<struct whisper_context *>(contextPtr);
    if (ctx == nullptr || pcmData == nullptr || pcmLength <= 0) {
        return env->NewStringUTF("");
    }

    jfloat *elements = env->GetFloatArrayElements(pcmData, nullptr);
    std::vector<float> samples(elements, elements + pcmLength);
    env->ReleaseFloatArrayElements(pcmData, elements, JNI_ABORT);

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.language = "zh";
    params.translate = false;
    params.print_progress = false;
    params.print_special = false;
    params.print_realtime = false;
    params.print_timestamps = false;
    params.no_context = true;
    params.suppress_blank = true;
    params.single_segment = false;
    const int threads = pickThreadCount();
    params.n_threads = threads;
    const auto startTime = std::chrono::steady_clock::now();
    LOGI("whisper_full start: %d samples, %d threads", pcmLength, threads);

    if (whisper_full(ctx, params, samples.data(), static_cast<int>(samples.size())) != 0) {
        LOGE("whisper_full failed");
        return env->NewStringUTF("");
    }
    {
        const double seconds =
                std::chrono::duration<double>(std::chrono::steady_clock::now() - startTime).count();
        const double audioSeconds = static_cast<double>(pcmLength) / 16000.0;
        LOGI("whisper_full done: %.1fs audio in %.1fs (RTF %.2f, %d threads)",
             audioSeconds, seconds, seconds / audioSeconds, threads);
    }

    std::string result;
    const int nSegments = whisper_full_n_segments(ctx);
    for (int i = 0; i < nSegments; i++) {
        const char *text = whisper_full_get_segment_text(ctx, i);
        if (text != nullptr) {
            result += text;
        }
    }
    return env->NewStringUTF(result.c_str());
}

} // extern "C"
