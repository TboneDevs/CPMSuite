package com.dynogamer.studio.data.repository

import com.dynogamer.studio.data.local.dao.BackupDao
import com.dynogamer.studio.data.local.entity.BackupEntity
import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.domain.repository.BackupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val dao: BackupDao
) : BackupRepository {

    override fun getAllBackups(): Flow<List<Backup>> =
        dao.getAllBackups().map { list -> list.map { it.toDomain() } }

    override fun getBackupsForProject(projectId: String): Flow<List<Backup>> =
        dao.getBackupsForProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun getBackupById(id: String): Backup? =
        dao.getBackupById(id)?.toDomain()

    override suspend fun saveBackup(backup: Backup) =
        dao.insertBackup(backup.toEntity())

    override suspend fun deleteBackup(id: String) =
        dao.deleteBackup(id)

    override suspend fun deleteBackupsForProject(projectId: String) =
        dao.deleteBackupsForProject(projectId)

    override suspend fun getBackupCount(): Int =
        dao.getBackupCount()

    override suspend fun getTotalBackupSize(): Long =
        dao.getTotalBackupSize() ?: 0L

    private fun BackupEntity.toDomain() = Backup(
        id = id, projectId = projectId, projectName = projectName,
        backupPath = backupPath, triggerEvent = triggerEvent,
        sizeBytes = sizeBytes, isValid = isValid, createdAt = createdAt
    )

    private fun Backup.toEntity() = BackupEntity(
        id = id, projectId = projectId, projectName = projectName,
        backupPath = backupPath, triggerEvent = triggerEvent,
        sizeBytes = sizeBytes, isValid = isValid, createdAt = createdAt
    )
}
