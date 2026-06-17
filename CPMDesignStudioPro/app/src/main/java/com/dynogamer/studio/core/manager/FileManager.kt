package com.dynogamer.studio.core.manager

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.dynogamer.studio.core.util.Es3FileUtils
import com.dynogamer.studio.domain.model.Es3File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FileManager
 *
 * Unified file access layer. Routes to SAF (DocumentFile) on Android 11–13,
 * or Shizuku shell commands on Android 14+.
 */
@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuManager: ShizukuManager
) {

    private val TAG = "FileManager"

    val isAndroid14Plus: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    // ---- SAF Directory ----

    fun getDocumentDirectory(uri: Uri): DocumentFile? =
        DocumentFile.fromTreeUri(context, uri)

    fun listEs3Files(uri: Uri): List<Es3File> {
        val dir = getDocumentDirectory(uri) ?: return emptyList()
        return Es3FileUtils.listAllFiles(dir)
    }

    // ---- App-Internal Storage ----

    fun getProjectsDir(): File {
        val dir = File(context.filesDir, "projects")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getBackupsDir(): File {
        val dir = File(context.filesDir, "backups")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getExportsDir(): File {
        val dir = File(context.getExternalFilesDir(null), "exports")
        if (dir != null && !dir.exists()) dir.mkdirs()
        return dir ?: File(context.filesDir, "exports").also { it.mkdirs() }
    }

    // ---- File size from URI ----

    fun getFileSizeFromUri(uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                pfd.statSize
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    // ---- Copy from SAF to internal storage ----

    suspend fun copyToInternal(sourceUri: Uri, destFileName: String): File? =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
                val destFile = File(getProjectsDir(), destFileName)
                FileOutputStream(destFile).use { out -> inputStream.copyTo(out) }
                inputStream.close()
                destFile
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to copy to internal storage")
                null
            }
        }

    // ---- Internal file operations ----

    suspend fun copyFile(source: File, dest: File): Boolean = withContext(Dispatchers.IO) {
        try {
            source.copyTo(dest, overwrite = true)
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Copy failed: ${source.path} -> ${dest.path}")
            false
        }
    }

    suspend fun deleteFile(file: File): Boolean = withContext(Dispatchers.IO) {
        try { file.delete() } catch (e: Exception) { false }
    }

    fun getFileSizeBytes(file: File): Long = file.length()

    fun fileExists(path: String): Boolean = File(path).exists()
}
