package com.dynogamer.studio.core.manager

import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BackupManager
 *
 * Creates, validates, restores, and manages project backups.
 * Automatically triggered before import, edit, conversion, and export.
 */
@Singleton
class BackupManager @Inject constructor(
    private val fileManager: FileManager,
    private val backupRepository: BackupRepository
) {

    private val TAG = "BackupManager"

    suspend fun createBackup(project: Project, triggerEvent: String): Backup? =
        withContext(Dispatchers.IO) {
            try {
                val sourceFile = File(project.workingCopyPath)
                if (!sourceFile.exists()) {
                    Timber.tag(TAG).w("Working copy not found for backup: ${project.id}")
                    return@withContext null
                }

                val backupId = UUID.randomUUID().toString()
                val backupFileName = "${project.id}_${triggerEvent}_${System.currentTimeMillis()}.es3.bak"
                val backupFile = File(fileManager.getBackupsDir(), backupFileName)

                fileManager.copyFile(sourceFile, backupFile)

                val backup = Backup(
                    id = backupId,
                    projectId = project.id,
                    projectName = project.name,
                    backupPath = backupFile.absolutePath,
                    triggerEvent = triggerEvent,
                    sizeBytes = backupFile.length(),
                    isValid = backupFile.exists() && backupFile.length() > 0,
                    createdAt = System.currentTimeMillis()
                )

                backupRepository.saveBackup(backup)
                Timber.tag(TAG).d("Backup created: $backupFileName")
                backup
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Backup failed for project ${project.id}")
                null
            }
        }

    suspend fun restoreBackup(backup: Backup, targetProject: Project): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val backupFile = File(backup.backupPath)
                if (!backupFile.exists()) return@withContext false
                val workingCopy = File(targetProject.workingCopyPath)
                fileManager.copyFile(backupFile, workingCopy)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Restore failed")
                false
            }
        }

    suspend fun validateBackup(backup: Backup): Boolean = withContext(Dispatchers.IO) {
        val file = File(backup.backupPath)
        file.exists() && file.length() > 0
    }

    suspend fun deleteBackup(backup: Backup): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(backup.backupPath)
            val deleted = file.delete()
            if (deleted) backupRepository.deleteBackup(backup.id)
            deleted
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Delete backup failed")
            false
        }
    }
}
