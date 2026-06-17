package com.dynogamer.studio.ui.screens.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*

@Composable
fun PreviewScreen(
    projectId: String,
    onBack: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(projectId) { viewModel.loadProject(projectId) }

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Visibility, null, tint = AccentRed, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(uiState.projectName, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(uiState.vehicleName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            StatusChip(uiState.cpmType, AccentRed)
        }

        StudioDivider()

        // Canvas area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(SurfaceSecondary)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offsetX, translationY = offsetY)
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceElevated)
                    .border(2.dp, AccentRed, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DirectionsCar, null, tint = AccentRed, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.vehicleName.take(12), color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(uiState.cpmType, color = AccentRed, style = MaterialTheme.typography.labelSmall)
                }
            }

            // Zoom controls
            Column(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SmallIconButton(Icons.Default.ZoomIn) { scale = (scale * 1.25f).coerceAtMost(5f) }
                SmallIconButton(Icons.Default.ZoomOut) { scale = (scale / 1.25f).coerceAtLeast(0.5f) }
                SmallIconButton(Icons.Default.CenterFocusStrong) { scale = 1f; offsetX = 0f; offsetY = 0f }
            }
        }

        // Inspector panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader("Design Inspector")
            Spacer(Modifier.height(10.dp))

            StudioCard {
                InspectorRow("Vehicle Name", uiState.vehicleName)
                StudioDivider()
                InspectorRow("Project ID", uiState.projectId.take(16) + "...")
                StudioDivider()
                InspectorRow("CPM Type", uiState.cpmType)
                StudioDivider()
                InspectorRow("Status", uiState.status)
                StudioDivider()
                InspectorRow("File Size", uiState.fileSize)
                StudioDivider()
                InspectorRow("Layer Count", uiState.layerCount)
                StudioDivider()
                InspectorRow("Vinyl Count", uiState.vinylCount)
            }

            Spacer(Modifier.height(16.dp))

            // Validation
            SectionHeader("Validation")
            Spacer(Modifier.height(8.dp))
            StudioCard {
                if (uiState.validationWarnings.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = StatusSuccess, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("No issues found", color = StatusSuccess, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    uiState.validationWarnings.forEach { warning ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.Warning, null, tint = StatusWarning, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(warning, color = StatusWarning, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SmallIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated.copy(alpha = 0.9f))
            .border(1.dp, Divider, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(16.dp))
        }
    }
}
