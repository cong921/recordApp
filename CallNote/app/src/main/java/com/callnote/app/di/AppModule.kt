package com.callnote.app.di

import android.content.Context
import androidx.room.Room
import com.callnote.app.data.db.CallNoteDatabase
import com.callnote.app.data.db.dao.CallCategoryDao
import com.callnote.app.data.db.dao.CallRecordDao
import com.callnote.app.data.db.dao.TranscriptionTaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CallNoteDatabase =
        Room.databaseBuilder(context, CallNoteDatabase::class.java, CallNoteDatabase.NAME)
            .build()

    @Provides
    fun provideCallRecordDao(database: CallNoteDatabase): CallRecordDao =
        database.callRecordDao()

    @Provides
    fun provideCallCategoryDao(database: CallNoteDatabase): CallCategoryDao =
        database.callCategoryDao()

    @Provides
    fun provideTranscriptionTaskDao(database: CallNoteDatabase): TranscriptionTaskDao =
        database.transcriptionTaskDao()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
