package com.dynogamer.studio.ui.screens.settings

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
import com.dynogamer.studio.ui.navigation.Screen
import com.dynogamer.studio.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
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
            Icon(Icons.Default.Settings, null, tint = AccentRed, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Text("Settings", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        // Appearance
        SectionHeader("Appearance")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            SettingsToggle("Dark Mode", "Always on (required)", true, {})
            StudioDivider()
            SettingsToggle("Red Glow Effects", "Subtle accent glow on cards", uiState.glowEnabled, { viewModel.toggleGlow() })
        }

        Spacer(Modifier.height(16.dp))

        // Storage
        SectionHeader("Storage")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            SettingsRow("Projects Directory", uiState.projectsDir, Icons.Default.Folder)
            StudioDivider()
            SettingsRow("Backups Directory", uiState.backupsDir, Icons.Default.Backup)
            StudioDivider()
            SettingsRow("Exports Directory", uiState.exportsDir, Icons.Default.Upload)
        }

        Spacer(Modifier.height(16.dp))

        // Projects
        SectionHeader("Projects")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            SettingsToggle("Auto-backup on Import", "Create backup when importing", uiState.autoBackupImport, { viewModel.toggleAutoBackupImport() })
            StudioDivider()
            SettingsToggle("Auto-backup on Edit", "Create backup before editing", uiState.autoBackupEdit, { viewModel.toggleAutoBackupEdit() })
        }

        Spacer(Modifier.height(16.dp))

        // Shizuku
        SectionHeader("Shizuku")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            NavigationRow(Icons.Default.Security, "Shizuku Dashboard", "Status, setup, and troubleshooting", { onNavigate(Screen.Shizuku.route) })
        }

        Spacer(Modifier.height(16.dp))

        // Diagnostics
        SectionHeader("Diagnostics & Logs")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            NavigationRow(Icons.Default.BugReport, "Diagnostics Center", "Device and app diagnostics", { onNavigate(Screen.Diagnostics.route) })
            StudioDivider()
            NavigationRow(Icons.Default.List, "Log Viewer", "Browse and export logs", { onNavigate(Screen.Logs.route) })
        }

        Spacer(Modifier.height(16.dp))

        // Advanced
        SectionHeader("Advanced")
        Spacer(Modifier.height(8.dp))
        StudioCard {
            SettingsToggle("Developer Mode", "Show extra debug info", uiState.developerMode, { viewModel.toggleDeveloperMode() })
            StudioDivider()
            NavigationRow(Icons.Default.DeleteSweep, "Clear All Logs", "Permanently delete all log entries", { viewModel.clearLogs() })
        }

        Spacer(Modifier.height(16.dp))

        // App info
        StudioCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("CPM Design Studio Pro", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text("v1.0.0", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Spacer(Modifier.height(4.dp))
            Text("Built on TNNR Project Final v5.1", style = MaterialTheme.typography.bodySmall, color = TextDisabled)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = TextOnAccent, checkedTrackColor = AccentRed, uncheckedTrackColor = SurfaceCard)
        )
    }
}

@Composable
private fun SettingsRow(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AccentRed, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(value, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
