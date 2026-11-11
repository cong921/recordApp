package com.callnote.app

import android.app.Application
import com.callnote.app.data.seed.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CallNoteApplication : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        // 第一阶段：仅在建库时写入模拟数据；第二阶段起由导入流程替代
        databaseSeeder.seedIfEmpty()
    }
}
