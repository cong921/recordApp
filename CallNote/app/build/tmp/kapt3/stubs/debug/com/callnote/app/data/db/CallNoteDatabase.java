package com.callnote.app.data.db;

/**
 * CallNote 主数据库。
 * schema JSON 导出到 app/schemas，为后续版本迁移保留依据。
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/callnote/app/data/db/CallNoteDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "callRecordDao", "Lcom/callnote/app/data/db/dao/CallRecordDao;", "callCategoryDao", "Lcom/callnote/app/data/db/dao/CallCategoryDao;", "transcriptionTaskDao", "Lcom/callnote/app/data/db/dao/TranscriptionTaskDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.callnote.app.data.db.entity.CallRecordEntity.class, com.callnote.app.data.db.entity.CallCategoryEntity.class, com.callnote.app.data.db.entity.TranscriptionTaskEntity.class}, version = 1, exportSchema = true)
public abstract class CallNoteDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String NAME = "callnote.db";
    @org.jetbrains.annotations.NotNull()
    public static final com.callnote.app.data.db.CallNoteDatabase.Companion Companion = null;
    
    public CallNoteDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.callnote.app.data.db.dao.CallRecordDao callRecordDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.callnote.app.data.db.dao.CallCategoryDao callCategoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.callnote.app.data.db.dao.TranscriptionTaskDao transcriptionTaskDao();
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/callnote/app/data/db/CallNoteDatabase$Companion;", "", "<init>", "()V", "NAME", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}