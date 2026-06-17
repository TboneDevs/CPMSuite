package com.dynogamer.studio.core.manager

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CPMScanner
 *
 * Scans the device for CPM1 and CPM2 installations, save directories,
 * accessible paths, and existing backups.
 */
@Singleton
class CPMScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuManager: ShizukuManager
) {

    data class ScanResult(
        val cpm1Installed: Boolean,
        val cpm2Installed: Boolean,
        val cpm1Es3PathAccessible: Boolean,
        val cpm2Es3PathAccessible: Boolean,
        val cpm1Es3FileCount: Int,
        val cpm2Es3FileCount: Int,
        val shizukuAvailable: Boolean,
        val shizukuAuthorized: Boolean,
        val issues: List<String>
    )

    suspend fun scanDevice(): ScanResult = withContext(Dispatchers.IO) {
        val issues = mutableListOf<String>()

        val cpm1Installed = isPackageInstalled(ShizukuManager.CPM1_PACKAGE)
        val cpm2Installed = isPackageInstalled(ShizukuManager.CPM2_PACKAGE)

        if (!cpm1Installed) issues.add("CPM1 is not installed")
        if (!cpm2Installed) issues.add("CPM2 is not installed")

        val shizukuAvailable = shizukuManager.isRunning()
        val shizukuAuthorized = shizukuManager.isAuthorized()

        var cpm1Accessible = false
        var cpm2Accessible = false
        var cpm1Count = 0
        var cpm2Count = 0

        if (shizukuAuthorized) {
            val cpm1Check = shizukuManager.runCommand("ls ${ShizukuManager.CPM1_ES3_PATH} 2>/dev/null | wc -l")
            cpm1Count = cpm1Check.trim().toIntOrNull() ?: 0
            cpm1Accessible = !cpm1Check.startsWith("SYSTEM_ERR:") && cpm1Count >= 0

            val cpm2Check = shizukuManager.runCommand("ls ${ShizukuManager.CPM2_ES3_PATH} 2>/dev/null | wc -l")
            cpm2Count = cpm2Check.trim().toIntOrNull() ?: 0
            cpm2Accessible = !cpm2Check.startsWith("SYSTEM_ERR:") && cpm2Count >= 0
        } else {
            if (!shizukuAvailable) issues.add("Shizuku not running — Android 14+ paths may be inaccessible")
            else issues.add("Shizuku not authorized — tap Authorize in Shizuku app")
        }

        ScanResult(
            cpm1Installed = cpm1Installed,
            cpm2Installed = cpm2Installed,
            cpm1Es3PathAccessible = cpm1Accessible,
            cpm2Es3PathAccessible = cpm2Accessible,
            cpm1Es3FileCount = cpm1Count,
            cpm2Es3FileCount = cpm2Count,
            shizukuAvailable = shizukuAvailable,
            shizukuAuthorized = shizukuAuthorized,
            issues = issues
        )
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
