package com.dynogamer.studio.ui.screens.cpm2

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.dynogamer.studio.domain.model.Es3File
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*

@Composable
fun Cpm2Screen(
    onNavigate: (String) -> Unit,
    viewModel: Cpm2ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importFile(it) }
    }
    val folderLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let { viewModel.setFolder(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsCarFilled, contentDescription = null, tint = AccentRed, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("CPM2 Workspace", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Car Parking Multiplayer 2", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AccentButton("Import File", { importLauncher.launch("*/*") }, Modifier.weight(1f), icon = Icons.Default.FileDownload)
            OutlineButton("Browse Folder", { folderLauncher.launch(null) }, Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Shizuku status
        if (uiState.shizukuAuthorized) {
            StudioCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Shizuku Active", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                    Spacer(Modifier.weight(1f))
                    Text(uiState.activePath, style = MaterialTheme.typography.labelSmall, color = TextDisabled)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        uiState.importStatus?.let { status ->
            StudioCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (uiState.importSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        null,
                        tint = if (uiState.importSuccess) StatusSuccess else StatusError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(status, style = MaterialTheme.typography.bodySmall, color = if (uiState.importSuccess) StatusSuccess else StatusError)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        if (uiState.files.isNotEmpty()) {
            SectionHeader("Files (${uiState.files.size})")
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(uiState.files) { file ->
                    FileRow(file, { viewModel.selectFile(file) })
                }
            }
        }

        if (uiState.projects.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SectionHeader("CPM2 Projects (${uiState.projects.size})")
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(uiState.projects) { project ->
                    ProjectRow(project, onNavigate)
                }
            }
        }
    }
}

@Composable
private fun FileRow(file: Es3File, onSelect: () -> Unit) {
    StudioCard(onClick = onSelect) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                null,
                tint = if (file.isNewest) AccentRed else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("${file.size} • ${file.lastModified}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (file.isNewest) StatusChip("NEWEST", AccentRed)
        }
    }
}

@Composable
private fun ProjectRow(project: Project, onNavigate: (String) -> Unit) {
    StudioCard(onClick = { onNavigate("preview/${project.id}") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(AccentRedGlow, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(project.vehicleName.take(2).uppercase(), color = AccentRed, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(project.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(project.vehicleName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            StatusChip(project.status, StatusSuccess)
        }
    }
}
