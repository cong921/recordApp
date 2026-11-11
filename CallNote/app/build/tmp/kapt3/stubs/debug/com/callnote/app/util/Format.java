package com.callnote.app.util;

/**
 * 展示层格式化工具（纯 Kotlin，可单元测试）
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0007J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\rJ\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\rR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/callnote/app/util/Format;", "", "<init>", "()V", "duration", "", "totalSeconds", "", "fileSize", "bytes", "date", "millis", "zone", "Ljava/time/ZoneId;", "dateTime", "SAME_YEAR", "Ljava/time/format/DateTimeFormatter;", "OTHER_YEAR", "FULL", "app_debug"})
public final class Format {
    @org.jetbrains.annotations.NotNull()
    private static final java.time.format.DateTimeFormatter SAME_YEAR = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.time.format.DateTimeFormatter OTHER_YEAR = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.time.format.DateTimeFormatter FULL = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.callnote.app.util.Format INSTANCE = null;
    
    private Format() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String duration(long totalSeconds) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fileSize(long bytes) {
        return null;
    }
    
    /**
     * 同年显示 "11月10日 14:30"，跨年显示 "2025年11月10日"
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String date(long millis, @org.jetbrains.annotations.NotNull()
    java.time.ZoneId zone) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String dateTime(long millis, @org.jetbrains.annotations.NotNull()
    java.time.ZoneId zone) {
        return null;
    }
}