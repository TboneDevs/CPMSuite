package com.dynogamer.studio.ui.screens.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dynogamer.studio.domain.model.LogEntry
import com.dynogamer.studio.ui.components.*
import com.dynogamer.studio.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogsScreen(
    onBack: () -> Unit,
    viewModel: LogsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("ALL") }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.List, null, tint = AccentRed, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Logs", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("${uiState.logs.size} entries", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            IconButton(onClick = { viewModel.clearLogs() }) {
                Icon(Icons.Default.DeleteSweep, null, tint = StatusError)
            }
        }

        Spacer(Modifier.height(10.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; viewModel.search(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search logs...", color = TextDisabled) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed, unfocusedBorderColor = Divider,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = AccentRed
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Level filter
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ALL", "INFO", "WARN", "ERROR", "DEBUG").forEach { level ->
                FilterChip(
                    selected = selectedLevel == level,
                    onClick = { selectedLevel = level; viewModel.filterByLevel(level) },
                    label = { Text(level, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = levelColor(level),
                        selectedLabelColor = TextOnAccent,
                        containerColor = SurfaceElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (uiState.logs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No log entries", color = TextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.logs, key = { it.id }) { log ->
                    LogRow(log = log, dateFormat = dateFormat)
                }
            }
        }
    }
}

@Composable
private fun LogRow(log: LogEntry, dateFormat: SimpleDateFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceSecondary, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = log.level.take(1),
            color = levelColor(log.level),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.width(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text("[${log.tag}]", color = AccentRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.width(4.dp))
                Text(log.message, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
        Text(dateFormat.format(Date(log.timestamp)), color = TextDisabled, fontSize = 9.sp)
    }
}

private fun levelColor(level: String): Color = when (level) {
    "INFO" -> StatusInfo
    "WARN" -> StatusWarning
    "ERROR" -> StatusError
    "DEBUG" -> TextSecondary
    else -> TextSecondary
}

private val StatusInfo = Color(0xFF2196F3)
