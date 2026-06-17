package com.dynogamer.studio.ui.screens.conversion

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
fun ConversionScreen(
    onBack: () -> Unit,
    viewModel: ConversionViewModel = hiltViewModel()
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Transform, null, tint = AccentRed, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Conversion Studio", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("CPM1 → CPM2", style = MaterialTheme.typography.bodySmall, color = AccentRed)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Workflow Steps
        SectionHeader("Conversion Workflow")
        Spacer(Modifier.height(12.dp))

        ConversionStepCard(
            step = 1,
            title = "Select Source Project",
            description = "Choose a CPM1 project to convert",
            isActive = uiState.currentStep == 1,
            isComplete = uiState.currentStep > 1
        ) {
            if (uiState.currentStep == 1) {
                AccentButton("Select CPM1 Project", { viewModel.selectSource() }, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 2,
            title = "Analyze",
            description = "Analyze project structure and contents",
            isActive = uiState.currentStep == 2,
            isComplete = uiState.currentStep > 2
        ) {
            if (uiState.currentStep == 2) {
                if (uiState.isProcessing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AccentRed, trackColor = SurfaceCard)
                } else {
                    AccentButton("Run Analysis", { viewModel.analyze() }, Modifier.fillMaxWidth())
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 3,
            title = "Compatibility Check",
            description = "Validate CPM2 compatibility",
            isActive = uiState.currentStep == 3,
            isComplete = uiState.currentStep > 3
        ) {
            if (uiState.currentStep == 3) {
                uiState.compatibilityReport?.let { report ->
                    StudioCard {
                        Text(report, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Spacer(Modifier.height(8.dp))
                }
                AccentButton("Proceed", { viewModel.proceedToConversion() }, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 4,
            title = "Build Conversion Plan",
            description = "Generate conversion strategy",
            isActive = uiState.currentStep == 4,
            isComplete = uiState.currentStep > 4
        ) {
            if (uiState.currentStep == 4) {
                AccentButton("Build Plan", { viewModel.buildPlan() }, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 5,
            title = "Validate",
            description = "Validate the conversion plan",
            isActive = uiState.currentStep == 5,
            isComplete = uiState.currentStep > 5
        ) {
            if (uiState.currentStep == 5) {
                AccentButton("Validate", { viewModel.validate() }, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 6,
            title = "Preview",
            description = "Preview the converted project",
            isActive = uiState.currentStep == 6,
            isComplete = uiState.currentStep > 6
        ) {
            if (uiState.currentStep == 6) {
                AccentButton("Preview Result", { viewModel.preview() }, Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(8.dp))

        ConversionStepCard(
            step = 7,
            title = "Export",
            description = "Export the converted CPM2 project",
            isActive = uiState.currentStep == 7,
            isComplete = uiState.currentStep > 7
        ) {
            if (uiState.currentStep == 7) {
                AccentButton("Export CPM2 Project", { viewModel.export() }, Modifier.fillMaxWidth(), icon = Icons.Default.Upload)
            }
        }

        // Result
        uiState.resultMessage?.let { msg ->
            Spacer(Modifier.height(16.dp))
            StudioCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (uiState.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        null,
                        tint = if (uiState.isSuccess) StatusSuccess else StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(msg, style = MaterialTheme.typography.bodyMedium, color = if (uiState.isSuccess) StatusSuccess else StatusError)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ConversionStepCard(
    step: Int,
    title: String,
    description: String,
    isActive: Boolean,
    isComplete: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = when {
        isComplete -> StatusSuccess
        isActive -> AccentRed
        else -> Divider
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActive) SurfaceElevated else SurfaceSecondary),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (isComplete) StatusSuccess else if (isActive) AccentRed else SurfaceCard,
                            androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isComplete) Icon(Icons.Default.Check, null, tint = TextOnAccent, modifier = Modifier.size(16.dp))
                    else Text(step.toString(), color = if (isActive) TextOnAccent else TextDisabled, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = if (isActive || isComplete) TextPrimary else TextDisabled, fontWeight = FontWeight.SemiBold)
                    Text(description, style = MaterialTheme.typography.bodySmall, color = if (isActive) TextSecondary else TextDisabled)
                }
            }
            if (isActive) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}
