package com.dynogamer.studio.ui.screens.projects

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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectsScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Folder, null, tint = AccentRed, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Text("Project Library", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("${uiState.projects.size} projects", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Spacer(Modifier.height(14.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search projects...", color = TextDisabled) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = ""; viewModel.search("") }) {
                        Icon(Icons.Default.Clear, null, tint = TextSecondary)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = Divider,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentRed
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "CPM1", "CPM2", "Favorites", "Archived").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter; viewModel.setFilter(filter) },
                    label = { Text(filter, style = MaterialTheme.typography.labelLarge) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = TextOnAccent,
                        containerColor = SurfaceElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (uiState.isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { SkeletonBox(Modifier.fillMaxWidth(), 72.dp) }
            }
        } else if (uiState.projects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FolderOff, null, tint = TextDisabled, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No projects found", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Text("Import a CPM1 or CPM2 file to get started", style = MaterialTheme.typography.bodySmall, color = TextDisabled)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.projects, key = { it.id }) { project ->
                    ProjectCard(
                        project = project,
                        onPreview = { onNavigate("preview/${project.id}") },
                        onFavorite = { viewModel.toggleFavorite(project) },
                        onArchive = { viewModel.toggleArchive(project) },
                        onDelete = { viewModel.deleteProject(project) },
                        onDuplicate = { viewModel.duplicateProject(project) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onPreview: () -> Unit,
    onFavorite: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    var showMenu by remember { mutableStateOf(false) }

    StudioCard(onClick = onPreview) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(AccentRedGlow, androidx.compose.foundation.shape.RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    project.vehicleName.take(2).uppercase(),
                    color = AccentRed,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(project.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    if (project.isFavorite) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Star, null, tint = StatusWarning, modifier = Modifier.size(14.dp))
                    }
                }
                Text(project.vehicleName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    StatusChip(project.cpmType, AccentRed)
                    StatusChip(dateFormat.format(Date(project.modifiedAt)), TextSecondary)
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, null, tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = { Text(if (project.isFavorite) "Remove Favorite" else "Add Favorite", color = TextPrimary) },
                        onClick = { showMenu = false; onFavorite() },
                        leadingIcon = { Icon(Icons.Default.Star, null, tint = StatusWarning) }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate", color = TextPrimary) },
                        onClick = { showMenu = false; onDuplicate() },
                        leadingIcon = { Icon(Icons.Default.CopyAll, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text(if (project.isArchived) "Unarchive" else "Archive", color = TextPrimary) },
                        onClick = { showMenu = false; onArchive() },
                        leadingIcon = { Icon(Icons.Default.Archive, null, tint = TextSecondary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = StatusError) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = StatusError) }
                    )
                }
            }
        }
    }
}
