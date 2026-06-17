package com.dynogamer.studio.core.manager

import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ShizukuManager
 *
 * Manages Shizuku lifecycle, permission requests, and shell command execution.
 * Ported and expanded from the original ShizukuFileManager in TNNR v5.1.
 */
@Singleton
class ShizukuManager @Inject constructor() {

    companion object {
        private const val TAG = "ShizukuManager"
        private const val REQUEST_CODE = 1001

        // CPM1 package and save path
        const val CPM1_PACKAGE = "com.olzhas.carparking.multiplayer"
        const val CPM1_ES3_PATH = "/sdcard/Android/data/$CPM1_PACKAGE/files/es3"

        // CPM2 package and save path
        const val CPM2_PACKAGE = "com.olzhas.carparking.multiplayer2"
        const val CPM2_ES3_PATH = "/sdcard/Android/data/$CPM2_PACKAGE/files/es3"
    }

    private var activePath: String = CPM2_ES3_PATH

    // ---- Status ----

    fun isInstalled(): Boolean = try {
        Shizuku.pingBinder()
        true
    } catch (e: Exception) {
        false
    }

    fun isRunning(): Boolean = try {
        Shizuku.pingBinder() && Shizuku.getVersion() >= 0
    } catch (e: Exception) {
        false
    }

    fun isAuthorized(): Boolean = try {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    } catch (e: Exception) {
        false
    }

    fun getVersion(): Int = try {
        Shizuku.getVersion()
    } catch (e: Exception) {
        -1
    }

    fun requestPermission() {
        try {
            Shizuku.requestPermission(REQUEST_CODE)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to request Shizuku permission")
        }
    }

    // ---- Path Management ----

    fun setActivePath(path: String) {
        activePath = path
    }

    fun getActivePath(): String = activePath

    fun setPathForCpm(cpmType: String) {
        activePath = if (cpmType == "CPM1") CPM1_ES3_PATH else CPM2_ES3_PATH
    }

    // ---- Shell Commands ----

    suspend fun runCommand(cmd: String): String = withContext(Dispatchers.IO) {
        if (!isAuthorized()) return@withContext "SYSTEM_ERR: NOT_AUTHORIZED"
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            val output = process.inputStream.bufferedReader().readText().trim()
            val error = process.errorStream.bufferedReader().readText().trim()
            process.waitFor()
            if (output.isNotEmpty()) output else if (error.isNotEmpty()) "SYSTEM_ERR: $error" else "SYSTEM_EMPTY"
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Shell command failed: $cmd")
            "INTERNAL_ERR: ${e.message}"
        }
    }

    // ---- File Operations ----

    suspend fun listFiles(path: String = activePath): List<com.dynogamer.studio.domain.model.Es3File> =
        withContext(Dispatchers.IO) {
            val output = runCommand("ls -F -t -a -L $path")
            val files = mutableListOf<com.dynogamer.studio.domain.model.Es3File>()
            if (output.isBlank() || output.startsWith("SYSTEM_ERR:") || output.startsWith("INTERNAL_ERR:") || output == "SYSTEM_EMPTY") return@withContext files
            val newestName = findMostRecentFile(path)
            for (line in output.split("\n")) {
                if (line.isBlank() || line == "." || line == "..") continue
                val name = line.trim()
                val isDir = name.endsWith("/") || name.endsWith("@")
                val cleanName = name.removeSuffix("/").removeSuffix("@")
                val detailOutput = runCommand("stat -c '%s %y' $path/$cleanName")
                val parts = if (detailOutput.startsWith("SYSTEM_ERR:")) emptyList() else detailOutput.split(" ")
                val size = if (parts.isNotEmpty()) "${(parts[0].toLongOrNull() ?: 0L) / 1024} KB" else "0 KB"
                val date = if (parts.size >= 2) parts[1] else "Unknown"
                files.add(
                    com.dynogamer.studio.domain.model.Es3File(
                        name = cleanName, size = size, lastModified = date,
                        isDirectory = isDir, isNewest = !isDir && cleanName == newestName,
                        fullPath = "$path/$cleanName"
                    )
                )
            }
            files
        }

    suspend fun findMostRecentFile(path: String = activePath): String? =
        withContext(Dispatchers.IO) {
            val output = runCommand("ls -t $path/*.es3 2>/dev/null | head -1")
            if (output.isBlank() || output.startsWith("SYSTEM_ERR:") || output.startsWith("INTERNAL_ERR:")) null
            else output.substringAfterLast("/").trim()
        }

    suspend fun copyFile(sourcePath: String, destPath: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = runCommand("cp -f $sourcePath $destPath")
            !result.startsWith("SYSTEM_ERR:") && !result.startsWith("INTERNAL_ERR:")
        }

    suspend fun moveFile(sourcePath: String, destPath: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = runCommand("mv -f $sourcePath $destPath")
            !result.startsWith("SYSTEM_ERR:") && !result.startsWith("INTERNAL_ERR:")
        }

    suspend fun deleteFile(path: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = runCommand("rm -f $path")
            !result.startsWith("SYSTEM_ERR:") && !result.startsWith("INTERNAL_ERR:")
        }

    suspend fun fileExists(path: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = runCommand("[ -f $path ] && echo exists")
            result.contains("exists")
        }

    suspend fun swapFiles(sourceFile: String, targetFile: String, path: String = activePath): Boolean =
        withContext(Dispatchers.IO) {
            val src = "$path/$sourceFile"
            val dst = "$path/$targetFile"
            val tmpPath = "$path/_tmp_swap_${System.currentTimeMillis()}.es3"
            // Atomic swap: copy source → tmp, copy target → source, copy tmp → target
            val step1 = runCommand("cp -f $src $tmpPath")
            val step2 = runCommand("cp -f $dst $src")
            val step3 = runCommand("cp -f $tmpPath $dst")
            runCommand("rm -f $tmpPath")
            !step1.startsWith("SYSTEM_ERR:") && !step2.startsWith("SYSTEM_ERR:") && !step3.startsWith("SYSTEM_ERR:")
        }

    suspend fun forceStopApp(packageName: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = runCommand("am force-stop $packageName")
            !result.startsWith("SYSTEM_ERR:")
        }

    suspend fun clearAppCache(packageName: String): Boolean =
        withContext(Dispatchers.IO) {
            val cachePath = "/sdcard/Android/data/$packageName/cache"
            val result = runCommand("rm -rf $cachePath/*")
            !result.startsWith("SYSTEM_ERR:")
        }

    suspend fun getFileSizeBytes(path: String): Long =
        withContext(Dispatchers.IO) {
            val output = runCommand("stat -c '%s' $path")
            output.trim().toLongOrNull() ?: 0L
        }
}
