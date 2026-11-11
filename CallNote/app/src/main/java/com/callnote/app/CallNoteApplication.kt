package com.callnote.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.callnote.app.data.seed.DatabaseSeeder
import com.callnote.app.work.TranscriptionWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CallNoteApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // 第一阶段：仅在建库时写入模拟数据；第二阶段起由导入流程替代
        databaseSeeder.seedIfEmpty()
        // 批量转写的前台进度通知渠道
        TranscriptionWorker.ensureChannel(this)
    }
}
