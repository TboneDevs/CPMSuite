package com.dynogamer.studio.core.util

import androidx.documentfile.provider.DocumentFile
import com.dynogamer.studio.domain.model.Es3File
import timber.log.Timber

/**
 * Es3FileUtils
 *
 * Utility functions for working with CPM .es3 design files via the
 * Android DocumentFile API (SAF — Storage Access Framework).
 * Works on Android 11–13. For Android 14+, use ShizukuManager.
 *
 * Ported and expanded from the original Es3FileUtils in TNNR v5.1.
 */
object Es3FileUtils {

    private const val TAG = "Es3FileUtils"

    fun listEs3Files(directory: DocumentFile): List<DocumentFile> {
        return directory.listFiles()
            .filter { it.isFile && it.name?.endsWith(".es3") == true }
            .sortedByDescending { it.lastModified() }
    }

    fun listAllFiles(directory: DocumentFile): List<Es3File> {
        val all = directory.listFiles().sortedByDescending { it.lastModified() }
        val newestName = all.firstOrNull { it.isFile && it.name?.endsWith(".es3") == true }?.name
        return all.map { doc ->
            Es3File(
                name = doc.name ?: "unknown",
                size = "${doc.length() / 1024} KB",
                lastModified = doc.lastModified().toString(),
                isDirectory = doc.isDirectory,
                isNewest = doc.name == newestName,
                fullPath = doc.uri.toString()
            )
        }
    }

    fun findSourceFile(directory: DocumentFile): DocumentFile? =
        listEs3Files(directory).firstOrNull()

    fun findFileByName(directory: DocumentFile, name: String): DocumentFile? =
        directory.listFiles().firstOrNull { it.name == name }

    fun performSwap(directory: DocumentFile, targetFileName: String): SwapResult {
        val sourceFile = findSourceFile(directory)
            ?: return SwapResult(false, "No source .es3 file found in the selected folder.")

        val sourceName = sourceFile.name ?: "unknown"
        if (sourceName == targetFileName) {
            return SwapResult(false, "Source and target filenames are identical. No swap needed.")
        }

        val existingTarget = findFileByName(directory, targetFileName)
        existingTarget?.delete()

        return if (sourceFile.renameTo(targetFileName)) {
            SwapResult(true, "Swap successful!\n\nSource: $sourceName\nTarget: $targetFileName")
        } else {
            SwapResult(false, "Rename failed. Make sure the app is fully closed and try again.")
        }
    }

    fun validateEs3File(file: DocumentFile): ValidationResult {
        if (!file.isFile) return ValidationResult(false, "Not a file.")
        if (file.length() == 0L) return ValidationResult(false, "File is empty.")
        if (file.name?.endsWith(".es3") != true) return ValidationResult(false, "Not an .es3 file.")
        if (file.length() < 100) return ValidationResult(false, "File too small — may be corrupted.")
        return ValidationResult(true, "File appears valid.")
    }

    data class SwapResult(val success: Boolean, val message: String)
    data class ValidationResult(val isValid: Boolean, val message: String)
}
