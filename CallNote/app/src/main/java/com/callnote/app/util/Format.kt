package com.callnote.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** 展示层格式化工具（纯 Kotlin，可单元测试） */
object Format {

    fun duration(totalSeconds: Long): String {
        if (totalSeconds <= 0) return "0秒"
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
            hours > 0 -> String.format(Locale.US, "%d小时%02d分", hours, minutes)
            minutes > 0 -> String.format(Locale.US, "%d分%02d秒", minutes, seconds)
            else -> String.format(Locale.US, "%d秒", seconds)
        }
    }

    fun fileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
        return String.format(Locale.US, "%.2f GB", mb / 1024.0)
    }

    /** 同年显示 "11月10日 14:30"，跨年显示 "2025年11月10日" */
    fun date(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val dateTime = Instant.ofEpochMilli(millis).atZone(zone)
        val now = LocalDate.now(zone)
        return if (dateTime.year == now.year) {
            dateTime.format(SAME_YEAR)
        } else {
            dateTime.format(OTHER_YEAR)
        }
    }

    fun dateTime(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(FULL)

    private val SAME_YEAR: DateTimeFormatter =
        DateTimeFormatter.ofPattern("M月d日 HH:mm", Locale.CHINA)

    private val OTHER_YEAR: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy年M月d日", Locale.CHINA)

    private val FULL: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm", Locale.CHINA)
}
