cmake_minimum_required(VERSION 3.22)
project(callnote LANGUAGES CXX)

# 内存受限构建环境（4GiB cgroup）：限制 ninja 并行编译/链接任务数，
# 避免 Gradle 守护进程 + 多个 clang 并发导致 OOM
set_property(GLOBAL PROPERTY JOB_POOLS "compile_pool=2;link_pool=1")
set(CMAKE_JOB_POOL_COMPILE compile_pool)
set(CMAKE_JOB_POOL_LINK link_pool)

# whisper.cpp 作为子项目编译（自动引入 ggml 后端与 whisper 核心静态库）
add_subdirectory(whisper.cpp)

# JNI 封装：模型加载 / 释放 / 16kHz mono float PCM 转写
add_library(whisper_jni SHARED whisper_jni.cpp)

target_link_libraries(whisper_jni
    PRIVATE
    whisper
    log
)
