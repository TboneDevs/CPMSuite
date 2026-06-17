package com.dynogamer.studio.core.manager

import android.net.Uri
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ImportManager
 *
 * Handles the full import workflow:
 * 1. Select File  2. Validate  3. Analyze  4. Summary  5. Create Project  6. Load Workspace
 */
@Singleton
class ImportManager @Inject constructor(
    private val fileManager: FileManager,
    private val backupManager: BackupManager,
    private val projectRepository: ProjectRepository,
    private val logger: Logger
) {

    sealed class ImportResult {
        data class Success(val project: Project) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }

    data class ImportSummary(
        val fileName: String,
        val fileSizeBytes: Long,
        val vehicleName: String,
        val isValid: Boolean,
        val warnings: List<String>
    )

    suspend fun analyzeFile(uri: Uri, cpmType: String): ImportSummary =
        withContext(Dispatchers.IO) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "unknown.es3"
            val warnings = mutableListOf<String>()

            // Basic validation
            if (!fileName.endsWith(".es3")) warnings.add("File does not have .es3 extension")

            val vehicleName = fileName.removeSuffix(".es3")
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }

            // Estimate size via content resolver
            val sizeBytes = try {
                val fd = fileManager.getFileSizeFromUri(uri)
                fd
            } catch (e: Exception) { 0L }

            if (sizeBytes < 100) warnings.add("File appears very small — may be corrupted")

            ImportSummary(
                fileName = fileName,
                fileSizeBytes = sizeBytes,
                vehicleName = vehicleName,
                isValid = warnings.isEmpty(),
                warnings = warnings
            )
        }

    suspend fun importFile(uri: Uri, cpmType: String, vehicleName: String? = null): ImportResult =
        withContext(Dispatchers.IO) {
            try {
                val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "unknown_${System.currentTimeMillis()}.es3"
                val projectId = UUID.randomUUID().toString()
                val destFileName = "${projectId}_${fileName}"

                // Copy to internal storage
                val internalFile = fileManager.copyToInternal(uri, destFileName)
                    ?: return@withContext ImportResult.Error("Failed to copy file to internal storage")

                // Create working copy
                val workingCopy = File(fileManager.getProjectsDir(), "${projectId}_working.es3")
                fileManager.copyFile(internalFile, workingCopy)

                val resolvedVehicleName = vehicleName
                    ?: fileName.removeSuffix(".es3").replace("_", " ").replaceFirstChar { it.uppercase() }

                val project = Project(
                    id = projectId,
                    name = resolvedVehicleName,
                    vehicleName = resolvedVehicleName,
                    cpmType = cpmType,
                    originalFilePath = internalFile.absolutePath,
                    workingCopyPath = workingCopy.absolutePath,
                    backupPath = null,
                    notes = null,
                    tags = emptyList(),
                    category = null,
                    isFavorite = false,
                    isArchived = false,
                    status = "active",
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                )

                // Auto-backup on import
                backupManager.createBackup(project, "import")

                projectRepository.saveProject(project)
                logger.info("ImportManager", "Imported $fileName as project $projectId", projectId)

                ImportResult.Success(project)
            } catch (e: Exception) {
                Timber.e(e, "Import failed")
                logger.error("ImportManager", "Import failed: ${e.message}")
                ImportResult.Error(e.message ?: "Unknown import error")
            }
        }
}
