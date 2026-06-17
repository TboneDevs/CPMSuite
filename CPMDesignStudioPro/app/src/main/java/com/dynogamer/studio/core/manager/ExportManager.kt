package com.dynogamer.studio.core.manager

import com.dynogamer.studio.domain.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ExportManager
 *
 * Handles project, backup, archive, and report exports.
 */
@Singleton
class ExportManager @Inject constructor(
    private val fileManager: FileManager,
    private val backupManager: BackupManager,
    private val logger: Logger
) {

    sealed class ExportResult {
        data class Success(val exportedFile: File) : ExportResult()
        data class Error(val message: String) : ExportResult()
    }

    suspend fun exportProject(project: Project): ExportResult = withContext(Dispatchers.IO) {
        try {
            // Auto-backup before export
            backupManager.createBackup(project, "export")

            val sourceFile = File(project.workingCopyPath)
            if (!sourceFile.exists()) return@withContext ExportResult.Error("Working copy not found")

            val exportFileName = "${project.vehicleName.replace(" ", "_")}_${System.currentTimeMillis()}.es3"
            val exportFile = File(fileManager.getExportsDir(), exportFileName)
            fileManager.copyFile(sourceFile, exportFile)

            logger.info("ExportManager", "Exported project ${project.id} to ${exportFile.name}", project.id)
            ExportResult.Success(exportFile)
        } catch (e: Exception) {
            Timber.e(e, "Export failed")
            ExportResult.Error(e.message ?: "Export failed")
        }
    }

    suspend fun exportReport(content: String, reportName: String): ExportResult =
        withContext(Dispatchers.IO) {
            try {
                val reportFile = File(fileManager.getExportsDir(), "$reportName.txt")
                reportFile.writeText(content)
                ExportResult.Success(reportFile)
            } catch (e: Exception) {
                ExportResult.Error(e.message ?: "Report export failed")
            }
        }
}
