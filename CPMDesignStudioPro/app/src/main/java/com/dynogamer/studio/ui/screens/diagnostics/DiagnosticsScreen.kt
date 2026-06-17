package com.dynogamer.studio.ui.screens.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*

@Composable
fun DiagnosticsScreen(
    onBack: () -> Unit,
    viewModel: DiagnosticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.BugReport, null, tint = AccentRed, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Diagnostics Center", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            AccentButton("Refresh", { viewModel.refresh() })
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(5) { SkeletonBox(Modifier.fillMaxWidth(), 60.dp) }
            }
        } else {
            val report = uiState.report

            // Device
            SectionHeader("Device")
            Spacer(Modifier.height(8.dp))
            StudioCard {
                DiagRow("Manufacturer", report?.deviceManufacturer ?: "—")
                StudioDivider()
                DiagRow("Model", report?.deviceModel ?: "—")
                StudioDivider()
                DiagRow("Android Version", "${report?.androidVersion ?: "—"} (API ${report?.apiLevel ?: "—"})")
            }

            Spacer(Modifier.height(16.dp))

            // Storage
            SectionHeader("Storage")
            Spacer(Modifier.height(8.dp))
            StudioCard {
                DiagRow("Internal Free", "${(report?.internalStorageFreeBytes ?: 0) / 1_048_576} MB / ${(report?.internalStorageTotalBytes ?: 0) / 1_048_576} MB")
                StudioDivider()
                DiagRow("External Free", "${(report?.externalStorageFreeBytes ?: 0) / 1_048_576} MB / ${(report?.externalStorageTotalBytes ?: 0) / 1_048_576} MB")
                StudioDivider()
                DiagRow("App Storage Free", "${(report?.appStorageFreeBytes ?: 0) / 1_048_576} MB")
            }

            Spacer(Modifier.height(16.dp))

            // Permissions
            SectionHeader("Permissions")
            Spacer(Modifier.height(8.dp))
            StudioCard {
                DiagRowStatus("All Files Access", report?.allFilesPermission ?: false)
            }

            Spacer(Modifier.height(16.dp))

            // Shizuku
            SectionHeader("Shizuku")
            Spacer(Modifier.height(8.dp))
            StudioCard {
                DiagRowStatus("Installed", report?.shizukuInstalled ?: false)
                StudioDivider()
                DiagRowStatus("Running", report?.shizukuRunning ?: false)
                StudioDivider()
                DiagRowStatus("Authorized", report?.shizukuAuthorized ?: false)
                StudioDivider()
                DiagRow("Version", report?.shizukuVersion?.toString() ?: "—")
            }

            Spacer(Modifier.height(16.dp))

            AccentButton(
                "Export Diagnostics Report",
                { viewModel.exportReport() },
                Modifier.fillMaxWidth(),
                icon = Icons.Default.Download
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DiagRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DiagRowStatus(label: String, ok: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
        StatusChip(if (ok) "OK" else "FAIL", if (ok) StatusSuccess else StatusError)
    }
}
