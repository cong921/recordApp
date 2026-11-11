package com.callnote.app.data.model;

/**
 * 录音转写状态
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\b\u001a\u00020\t8F\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\f"}, d2 = {"Lcom/callnote/app/data/model/TranscriptionStatus;", "", "<init>", "(Ljava/lang/String;I)V", "PENDING", "PROCESSING", "COMPLETED", "FAILED", "label", "", "getLabel", "()Ljava/lang/String;", "app_debug"})
public enum TranscriptionStatus {
    /*public static final*/ PENDING /* = new PENDING() */,
    /*public static final*/ PROCESSING /* = new PROCESSING() */,
    /*public static final*/ COMPLETED /* = new COMPLETED() */,
    /*public static final*/ FAILED /* = new FAILED() */;
    
    TranscriptionStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLabel() {
        return null;
    }
    
    /**
     * 录音转写状态
     */
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.callnote.app.data.model.TranscriptionStatus> getEntries() {
        return null;
    }
}