package com.dynogamer.studio.ui.screens.shizuku

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
fun ShizukuScreen(
    onBack: () -> Unit,
    viewModel: ShizukuViewModel = hiltViewModel()
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
            Icon(Icons.Default.Security, null, tint = AccentRed, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Text("Shizuku Dashboard", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        // Status overview
        StudioCard {
            SectionHeader("Connection Status")
            Spacer(Modifier.height(10.dp))
            ShizukuRow("Installed", uiState.isInstalled)
            StudioDivider()
            ShizukuRow("Running", uiState.isRunning)
            StudioDivider()
            ShizukuRow("Authorized", uiState.isAuthorized)
            StudioDivider()
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Version", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(if (uiState.version >= 0) "v${uiState.version}" else "N/A", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }
            StudioDivider()
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Android Version", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(uiState.androidVersion, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Actions
        SectionHeader("Actions")
        Spacer(Modifier.height(10.dp))

        if (!uiState.isAuthorized) {
            AccentButton(
                "Request Shizuku Permission",
                { viewModel.requestPermission() },
                Modifier.fillMaxWidth(),
                icon = Icons.Default.Lock
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlineButton("Refresh Status", { viewModel.refresh() }, Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))

        // Setup Guide
        SectionHeader("Setup Guide")
        Spacer(Modifier.height(10.dp))
        StudioCard {
            SetupStep(1, "Install Shizuku", "Download from Google Play Store or GitHub")
            StudioDivider()
            SetupStep(2, "Activate Shizuku", "Open Shizuku and start the service via ADB or wireless debugging")
            StudioDivider()
            SetupStep(3, "Authorize App", "Tap 'Request Shizuku Permission' above and confirm in Shizuku")
            StudioDivider()
            SetupStep(4, "Verify", "All status indicators above should show green")
        }

        Spacer(Modifier.height(16.dp))

        // Troubleshooting
        SectionHeader("Troubleshooting")
        Spacer(Modifier.height(10.dp))
        StudioCard {
            Text("If Shizuku is not working:", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            TroubleshootRow("Shizuku not found", "Install from Play Store and activate")
            TroubleshootRow("Not authorized", "Open Shizuku app and tap Authorize next to this app")
            TroubleshootRow("Service stopped", "Re-activate Shizuku service (requires ADB or wireless debugging)")
            TroubleshootRow("Android 14+ issue", "Shizuku is required for /Android/data/ access on Android 14+")
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShizukuRow(label: String, ok: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (ok) Icons.Default.CheckCircle else Icons.Default.Cancel,
            null,
            tint = if (ok) StatusSuccess else StatusError,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        StatusChip(if (ok) "OK" else "FAIL", if (ok) StatusSuccess else StatusError)
    }
}

@Composable
private fun SetupStep(step: Int, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier.size(24.dp).background(AccentRed, androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step.toString(), color = TextOnAccent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun TroubleshootRow(problem: String, solution: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("• $problem", style = MaterialTheme.typography.bodySmall, color = StatusWarning, fontWeight = FontWeight.Medium)
        Text("  → $solution", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}
