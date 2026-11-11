package com.callnote.app.data.model;

/**
 * 转写任务状态（第四阶段批量转写使用）
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/callnote/app/data/model/TaskStatus;", "", "<init>", "(Ljava/lang/String;I)V", "PENDING", "RUNNING", "PAUSED", "COMPLETED", "FAILED", "app_debug"})
public enum TaskStatus {
    /*public static final*/ PENDING /* = new PENDING() */,
    /*public static final*/ RUNNING /* = new RUNNING() */,
    /*public static final*/ PAUSED /* = new PAUSED() */,
    /*public static final*/ COMPLETED /* = new COMPLETED() */,
    /*public static final*/ FAILED /* = new FAILED() */;
    
    TaskStatus() {
    }
    
    /**
     * 转写任务状态（第四阶段批量转写使用）
     */
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.callnote.app.data.model.TaskStatus> getEntries() {
        return null;
    }
}