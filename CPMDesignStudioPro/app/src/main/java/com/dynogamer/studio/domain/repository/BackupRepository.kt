package com.dynogamer.studio.domain.repository

import com.dynogamer.studio.domain.model.Backup
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    fun getAllBackups(): Flow<List<Backup>>
    fun getBackupsForProject(projectId: String): Flow<List<Backup>>
    suspend fun getBackupById(id: String): Backup?
    suspend fun saveBackup(backup: Backup)
    suspend fun deleteBackup(id: String)
    suspend fun deleteBackupsForProject(projectId: String)
    suspend fun getBackupCount(): Int
    suspend fun getTotalBackupSize(): Long
}
