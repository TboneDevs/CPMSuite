package com.dynogamer.studio.ui.screens.home

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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.navigation.Screen
import com.dynogamer.studio.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CPM Design Studio",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pro Edition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentRed,
                    letterSpacing = 2.sp
                )
            }
            // Shizuku status badge
            StatusChip(
                label = if (uiState.shizukuAuthorized) "SHIZUKU OK" else "SHIZUKU OFF",
                color = if (uiState.shizukuAuthorized) StatusSuccess else StatusWarning
            )
        }

        Spacer(Modifier.height(20.dp))

        // Stats Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(modifier = Modifier.weight(1f), label = "CPM1 Projects", value = uiState.cpm1Count.toString())
            StatCard(modifier = Modifier.weight(1f), label = "CPM2 Projects", value = uiState.cpm2Count.toString())
            StatCard(modifier = Modifier.weight(1f), label = "Backups", value = uiState.backupCount.toString())
        }

        Spacer(Modifier.height(20.dp))

        // Quick Actions
        SectionHeader(title = "Quick Actions")
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionTile(
                icon = Icons.Default.Search,
                label = "Scan Device",
                onClick = { viewModel.scanDevice() },
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.FileDownload,
                label = "Import CPM1",
                onClick = { onNavigate(Screen.Cpm1.route) },
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.FileDownload,
                label = "Import CPM2",
                onClick = { onNavigate(Screen.Cpm2.route) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionTile(
                icon = Icons.Default.FolderOpen,
                label = "Last Project",
                onClick = { onNavigate(Screen.Projects.route) },
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.Restore,
                label = "Restore Backup",
                onClick = { onNavigate(Screen.Backup.route) },
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.Transform,
                label = "Convert",
                onClick = { onNavigate(Screen.Conversion.route) },
                modifier = Modifier.weight(1f),
                accent = true
            )
        }

        Spacer(Modifier.height(20.dp))

        // Scan Result
        if (uiState.scanResult != null) {
            SectionHeader(title = "Device Scan")
            Spacer(Modifier.height(8.dp))
            ScanResultCard(result = uiState.scanResult!!, onNavigateShizuku = { onNavigate(Screen.Shizuku.route) })
            Spacer(Modifier.height(20.dp))
        }

        // Tools Grid
        SectionHeader(title = "Tools")
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            NavigationRow(Icons.Default.DirectionsCar, "CPM1 Workspace", "Import, browse, inspect CPM1 files", { onNavigate(Screen.Cpm1.route) })
            StudioDivider()
            NavigationRow(Icons.Default.DirectionsCarFilled, "CPM2 Workspace", "Import, browse, inspect CPM2 files", { onNavigate(Screen.Cpm2.route) })
            StudioDivider()
            NavigationRow(Icons.Default.Transform, "Conversion Studio", "Convert CPM1 → CPM2 projects", { onNavigate(Screen.Conversion.route) })
            StudioDivider()
            NavigationRow(Icons.Default.Folder, "Project Library", "All imported projects", { onNavigate(Screen.Projects.route) })
            StudioDivider()
            NavigationRow(Icons.Default.Visibility, "Preview Studio", "Visual design preview", { onNavigate("preview/none") })
            StudioDivider()
            NavigationRow(Icons.Default.Upload, "Export Center", "Export projects and reports", { onNavigate(Screen.Export.route) })
            StudioDivider()
            NavigationRow(Icons.Default.Backup, "Backup Manager", "Manage all backups", { onNavigate(Screen.Backup.route) })
            StudioDivider()
            NavigationRow(Icons.Default.List, "Logs", "Activity and error logs", { onNavigate(Screen.Logs.route) })
            StudioDivider()
            NavigationRow(Icons.Default.BugReport, "Diagnostics", "Device and app diagnostics", { onNavigate(Screen.Diagnostics.route) })
            StudioDivider()
            NavigationRow(Icons.Default.Security, "Shizuku", "Shizuku setup and status", { onNavigate(Screen.Shizuku.route) })
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String) {
    StudioCard(modifier = modifier) {
        Text(text = value, style = MaterialTheme.typography.headlineLarge, color = AccentRed, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun ScanResultCard(
    result: com.dynogamer.studio.core.manager.CPMScanner.ScanResult,
    onNavigateShizuku: () -> Unit
) {
    StudioCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = AccentRed)
            Spacer(Modifier.width(8.dp))
            Text("Scan Results", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        }
        Spacer(Modifier.height(10.dp))
        ScanRow("CPM1 Installed", result.cpm1Installed)
        ScanRow("CPM2 Installed", result.cpm2Installed)
        ScanRow("CPM1 Path Accessible", result.cpm1Es3PathAccessible)
        ScanRow("CPM2 Path Accessible", result.cpm2Es3PathAccessible)
        ScanRow("Shizuku Running", result.shizukuAvailable)
        ScanRow("Shizuku Authorized", result.shizukuAuthorized)
        if (result.issues.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            result.issues.forEach { issue ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(issue, style = MaterialTheme.typography.bodySmall, color = StatusWarning)
                }
            }
            if (!result.shizukuAuthorized) {
                Spacer(Modifier.height(8.dp))
                OutlineButton("Fix: Open Shizuku Setup", onClick = onNavigateShizuku, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ScanRow(label: String, ok: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (ok) StatusSuccess else StatusError,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}
