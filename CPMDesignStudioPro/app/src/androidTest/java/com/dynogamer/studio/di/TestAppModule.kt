package com.dynogamer.studio.di

import android.content.Context
import androidx.room.Room
import com.dynogamer.studio.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideProjectDao(db: AppDatabase) = db.projectDao()

    @Provides
    fun provideBackupDao(db: AppDatabase) = db.backupDao()

    @Provides
    fun provideLogDao(db: AppDatabase) = db.logDao()
}
