package com.dynogamer.studio.core.manager

import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ProjectManager
 *
 * High-level project lifecycle operations: duplicate, delete, update metadata.
 */
@Singleton
class ProjectManager @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val backupManager: BackupManager,
    private val fileManager: FileManager,
    private val logger: Logger
) {

    suspend fun updateMetadata(
        project: Project,
        name: String? = null,
        vehicleName: String? = null,
        notes: String? = null,
        tags: List<String>? = null,
        category: String? = null
    ): Project = withContext(Dispatchers.IO) {
        // Auto-backup before edit
        backupManager.createBackup(project, "edit")

        val updated = project.copy(
            name = name ?: project.name,
            vehicleName = vehicleName ?: project.vehicleName,
            notes = notes ?: project.notes,
            tags = tags ?: project.tags,
            category = category ?: project.category,
            modifiedAt = System.currentTimeMillis()
        )
        projectRepository.saveProject(updated)
        logger.info("ProjectManager", "Metadata updated for project ${project.id}", project.id)
        updated
    }

    suspend fun duplicateProject(project: Project): Project = withContext(Dispatchers.IO) {
        val newId = UUID.randomUUID().toString()
        val newWorkingCopy = File(fileManager.getProjectsDir(), "${newId}_working.es3")
        fileManager.copyFile(File(project.workingCopyPath), newWorkingCopy)

        val duplicate = project.copy(
            id = newId,
            name = "${project.name} (Copy)",
            workingCopyPath = newWorkingCopy.absolutePath,
            backupPath = null,
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        projectRepository.saveProject(duplicate)
        logger.info("ProjectManager", "Duplicated project ${project.id} -> $newId", newId)
        duplicate
    }

    suspend fun deleteProject(project: Project) = withContext(Dispatchers.IO) {
        // Delete working copy
        File(project.workingCopyPath).delete()
        File(project.originalFilePath).delete()
        project.backupPath?.let { File(it).delete() }
        projectRepository.deleteProject(project.id)
        logger.info("ProjectManager", "Deleted project ${project.id}", project.id)
    }
}
