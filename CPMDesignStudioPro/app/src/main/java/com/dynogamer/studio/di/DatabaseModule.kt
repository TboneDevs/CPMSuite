package com.dynogamer.studio.di

import android.content.Context
import androidx.room.Room
import com.dynogamer.studio.data.local.AppDatabase
import com.dynogamer.studio.data.local.dao.BackupDao
import com.dynogamer.studio.data.local.dao.LogDao
import com.dynogamer.studio.data.local.dao.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cpm_studio_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideBackupDao(db: AppDatabase): BackupDao = db.backupDao()

    @Provides
    fun provideLogDao(db: AppDatabase): LogDao = db.logDao()
}
