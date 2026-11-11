package com.callnote.app;

@dagger.hilt.android.HiltAndroidApp()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u000bH\u0016R\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t\u00a8\u0006\f"}, d2 = {"Lcom/callnote/app/CallNoteApplication;", "Landroid/app/Application;", "<init>", "()V", "databaseSeeder", "Lcom/callnote/app/data/seed/DatabaseSeeder;", "getDatabaseSeeder", "()Lcom/callnote/app/data/seed/DatabaseSeeder;", "setDatabaseSeeder", "(Lcom/callnote/app/data/seed/DatabaseSeeder;)V", "onCreate", "", "app_debug"})
public final class CallNoteApplication extends android.app.Application {
    @javax.inject.Inject()
    public com.callnote.app.data.seed.DatabaseSeeder databaseSeeder;
    
    public CallNoteApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.callnote.app.data.seed.DatabaseSeeder getDatabaseSeeder() {
        return null;
    }
    
    public final void setDatabaseSeeder(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.seed.DatabaseSeeder p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}