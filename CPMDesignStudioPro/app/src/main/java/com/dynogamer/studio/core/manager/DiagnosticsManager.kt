package com.dynogamer.studio.core.manager

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DiagnosticsManager
 *
 * Collects device, storage, permission, and Shizuku diagnostics.
 */
@Singleton
class DiagnosticsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuManager: ShizukuManager,
    private val fileManager: FileManager
) {

    data class DiagnosticsReport(
        val androidVersion: String,
        val apiLevel: Int,
        val deviceModel: String,
        val deviceManufacturer: String,
        val internalStorageFreeBytes: Long,
        val internalStorageTotalBytes: Long,
        val externalStorageFreeBytes: Long,
        val externalStorageTotalBytes: Long,
        val appStorageFreeBytes: Long,
        val shizukuInstalled: Boolean,
        val shizukuRunning: Boolean,
        val shizukuAuthorized: Boolean,
        val shizukuVersion: Int,
        val allFilesPermission: Boolean,
        val generatedAt: Long = System.currentTimeMillis()
    )

    suspend fun generateReport(): DiagnosticsReport = withContext(Dispatchers.IO) {
        val internalStat = StatFs(Environment.getDataDirectory().path)
        val externalStat = try { StatFs(Environment.getExternalStorageDirectory().path) } catch (e: Exception) { null }
        val appStat = StatFs(context.filesDir.path)

        DiagnosticsReport(
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            deviceModel = Build.MODEL,
            deviceManufacturer = Build.MANUFACTURER,
            internalStorageFreeBytes = internalStat.availableBytes,
            internalStorageTotalBytes = internalStat.totalBytes,
            externalStorageFreeBytes = externalStat?.availableBytes ?: 0L,
            externalStorageTotalBytes = externalStat?.totalBytes ?: 0L,
            appStorageFreeBytes = appStat.availableBytes,
            shizukuInstalled = shizukuManager.isInstalled(),
            shizukuRunning = shizukuManager.isRunning(),
            shizukuAuthorized = shizukuManager.isAuthorized(),
            shizukuVersion = shizukuManager.getVersion(),
            allFilesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Environment.isExternalStorageManager() else true
        )
    }

    fun formatReport(report: DiagnosticsReport): String = buildString {
        appendLine("=== CPM Design Studio Pro — Diagnostics Report ===")
        appendLine("Generated: ${java.util.Date(report.generatedAt)}")
        appendLine()
        appendLine("--- Device ---")
        appendLine("Manufacturer : ${report.deviceManufacturer}")
        appendLine("Model        : ${report.deviceModel}")
        appendLine("Android      : ${report.androidVersion} (API ${report.apiLevel})")
        appendLine()
        appendLine("--- Storage ---")
        appendLine("Internal Free : ${report.internalStorageFreeBytes / 1_048_576} MB / ${report.internalStorageTotalBytes / 1_048_576} MB")
        appendLine("External Free : ${report.externalStorageFreeBytes / 1_048_576} MB / ${report.externalStorageTotalBytes / 1_048_576} MB")
        appendLine("App Free      : ${report.appStorageFreeBytes / 1_048_576} MB")
        appendLine()
        appendLine("--- Permissions ---")
        appendLine("All Files Access : ${if (report.allFilesPermission) "GRANTED" else "DENIED"}")
        appendLine()
        appendLine("--- Shizuku ---")
        appendLine("Installed  : ${report.shizukuInstalled}")
        appendLine("Running    : ${report.shizukuRunning}")
        appendLine("Authorized : ${report.shizukuAuthorized}")
        appendLine("Version    : ${report.shizukuVersion}")
    }
}
