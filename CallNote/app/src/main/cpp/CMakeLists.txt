cmake_minimum_required(VERSION 3.22)
project(callnote LANGUAGES CXX)

# whisper.cpp 作为子项目编译（自动引入 ggml 后端与 whisper 核心静态库）
add_subdirectory(whisper.cpp)

# JNI 封装：模型加载 / 释放 / 16kHz mono float PCM 转写
add_library(whisper_jni SHARED whisper_jni.cpp)

target_link_libraries(whisper_jni
    PRIVATE
    whisper
    log
)
