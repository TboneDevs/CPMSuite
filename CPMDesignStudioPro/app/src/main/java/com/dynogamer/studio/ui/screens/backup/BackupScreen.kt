package com.dynogamer.studio.ui.screens.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BackupScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Backup, null, tint = AccentRed, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Backup Manager", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("${uiState.backups.size} backups • ${uiState.totalSizeMb} MB total", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatMini("Total", uiState.backups.size.toString(), Modifier.weight(1f))
            StatMini("Size", "${uiState.totalSizeMb} MB", Modifier.weight(1f))
            StatMini("Valid", uiState.backups.count { it.isValid }.toString(), Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.backups.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.BackupTable, null, tint = TextDisabled, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No backups yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Text("Backups are created automatically before import, edit, conversion, and export.", style = MaterialTheme.typography.bodySmall, color = TextDisabled)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.backups, key = { it.id }) { backup ->
                    BackupCard(
                        backup = backup,
                        dateFormat = dateFormat,
                        onRestore = { viewModel.restore(backup) },
                        onDelete = { viewModel.delete(backup) },
                        onValidate = { viewModel.validate(backup) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMini(label: String, value: String, modifier: Modifier = Modifier) {
    StudioCard(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = AccentRed, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun BackupCard(
    backup: Backup,
    dateFormat: SimpleDateFormat,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    onValidate: () -> Unit
) {
    StudioCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(AccentRedGlow, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Backup, null, tint = AccentRed, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(backup.projectName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("Trigger: ${backup.triggerEvent.uppercase()} • ${backup.sizeBytes / 1024} KB", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(dateFormat.format(Date(backup.createdAt)), style = MaterialTheme.typography.labelSmall, color = TextDisabled)
            }
            StatusChip(if (backup.isValid) "VALID" else "INVALID", if (backup.isValid) StatusSuccess else StatusError)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlineButton("Restore", onRestore, Modifier.weight(1f))
            OutlineButton("Validate", onValidate, Modifier.weight(1f))
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = StatusError) }
        }
    }
}
