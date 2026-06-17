package com.dynogamer.studio.di

import com.dynogamer.studio.data.repository.BackupRepositoryImpl
import com.dynogamer.studio.data.repository.LogRepositoryImpl
import com.dynogamer.studio.data.repository.ProjectRepositoryImpl
import com.dynogamer.studio.domain.repository.BackupRepository
import com.dynogamer.studio.domain.repository.LogRepository
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

    @Binds
    @Singleton
    abstract fun bindLogRepository(impl: LogRepositoryImpl): LogRepository
}
