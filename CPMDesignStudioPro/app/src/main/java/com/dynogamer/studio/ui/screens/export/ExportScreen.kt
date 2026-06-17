package com.dynogamer.studio.ui.screens.export

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
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*

@Composable
fun ExportScreen(
    onBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Upload, null, tint = AccentRed, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Text("Export Center", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        uiState.exportStatus?.let { status ->
            StudioCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (uiState.exportSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        null,
                        tint = if (uiState.exportSuccess) StatusSuccess else StatusError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(status, style = MaterialTheme.typography.bodySmall, color = if (uiState.exportSuccess) StatusSuccess else StatusError)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        SectionHeader("Select Project to Export")
        Spacer(Modifier.height(10.dp))

        if (uiState.projects.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No projects available to export.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.projects, key = { it.id }) { project ->
                    ExportProjectCard(project = project, onExport = { viewModel.exportProject(project) })
                }
            }
        }
    }
}

@Composable
private fun ExportProjectCard(project: Project, onExport: () -> Unit) {
    StudioCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(AccentRedGlow, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(project.vehicleName.take(2).uppercase(), color = AccentRed, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("${project.cpmType} • ${project.vehicleName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            AccentButton("Export", onExport, icon = Icons.Default.Upload)
        }
    }
}
