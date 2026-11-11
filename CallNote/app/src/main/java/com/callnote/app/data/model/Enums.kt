package com.callnote.app.data.model

/** 录音转写状态 */
enum class TranscriptionStatus {
    PENDING, PROCESSING, COMPLETED, FAILED;

    val label: String
        get() = when (this) {
            PENDING -> "待转写"
            PROCESSING -> "转写中"
            COMPLETED -> "已转写"
            FAILED -> "转写失败"
        }
}

/** 转写任务状态（第四阶段批量转写使用） */
enum class TaskStatus {
    PENDING, RUNNING, PAUSED, COMPLETED, FAILED
}
