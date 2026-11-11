package com.callnote.app.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\n\u001a\u00020\u000bJ\u0012\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007J\u0016\u0010\r\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/callnote/app/data/repository/TranscriptionTaskRepository;", "", "dao", "Lcom/callnote/app/data/db/dao/TranscriptionTaskDao;", "<init>", "(Lcom/callnote/app/data/db/dao/TranscriptionTaskDao;)V", "observeByRecord", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/callnote/app/data/db/entity/TranscriptionTaskEntity;", "recordId", "", "observeAll", "createTask", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class TranscriptionTaskRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.dao.TranscriptionTaskDao dao = null;
    
    @javax.inject.Inject()
    public TranscriptionTaskRepository(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.dao.TranscriptionTaskDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.TranscriptionTaskEntity>> observeByRecord(long recordId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.TranscriptionTaskEntity>> observeAll() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createTask(long recordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
}