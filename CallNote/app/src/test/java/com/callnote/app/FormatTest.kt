package com.callnote.app

import com.callnote.app.util.Format
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class FormatTest {

    private val zone: ZoneId = ZoneId.of("Asia/Shanghai")

    @Test
    fun duration_formats() {
        assertEquals("0秒", Format.duration(0))
        assertEquals("45秒", Format.duration(45))
        assertEquals("1分02秒", Format.duration(62))
        assertEquals("5分35秒", Format.duration(335))
        assertEquals("1小时02分", Format.duration(3720))
        assertEquals("2小时30分", Format.duration(9000))
    }

    @Test
    fun fileSize_formats() {
        assertEquals("512 B", Format.fileSize(512))
        assertEquals("1.0 KB", Format.fileSize(1024))
        assertEquals("756.8 KB", Format.fileSize(775_000))
        assertEquals("15.3 MB", Format.fileSize(16_000_000))
        assertEquals("1.50 GB", Format.fileSize(1_610_612_736L))
    }

    @Test
    fun date_sameYear_showsMonthDayAndTime() {
        val today = LocalDate.now(zone)
        val millis = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val expected = "${today.monthValue}月${today.dayOfMonth}日 00:00"
        assertEquals(expected, Format.date(millis, zone))
    }

    @Test
    fun date_otherYear_showsFullYear() {
        val millis = LocalDateTime.of(2020, 5, 20, 9, 15)
            .atZone(zone).toInstant().toEpochMilli()
        assertEquals("2020年5月20日", Format.date(millis, zone))
    }

    @Test
    fun dateTime_fullFormat() {
        val millis = LocalDateTime.of(2026, 11, 11, 10, 30)
            .atZone(zone).toInstant().toEpochMilli()
        assertEquals("2026年11月11日 10:30", Format.dateTime(millis, zone))
    }
}
