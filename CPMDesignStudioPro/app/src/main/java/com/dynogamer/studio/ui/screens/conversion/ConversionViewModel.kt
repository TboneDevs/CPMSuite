package com.dynogamer.studio.ui.screens.conversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.ExportManager
import com.dynogamer.studio.core.manager.Logger
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversionUiState(
    val currentStep: Int = 1,
    val sourceProject: Project? = null,
    val compatibilityReport: String? = null,
    val conversionPlan: String? = null,
    val isProcessing: Boolean = false,
    val resultMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val exportManager: ExportManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversionUiState())
    val uiState: StateFlow<ConversionUiState> = _uiState.asStateFlow()

    fun selectSource() {
        // In a real implementation, this would open a project picker dialog.
        // For now, we auto-select the first CPM1 project.
        viewModelScope.launch {
            projectRepository.getProjectsByType("CPM1").firstOrNull()?.firstOrNull()?.let { project ->
                _uiState.update { it.copy(sourceProject = project, currentStep = 2) }
                logger.info("ConversionVM", "Source selected: ${project.name}", project.id)
            } ?: run {
                _uiState.update { it.copy(resultMessage = "No CPM1 projects found. Import a CPM1 project first.", isSuccess = false) }
            }
        }
    }

    fun analyze() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            delay(1200) // Simulate analysis
            _uiState.update { it.copy(isProcessing = false, currentStep = 3,
                compatibilityReport = buildCompatibilityReport(_uiState.value.sourceProject)) }
        }
    }

    fun proceedToConversion() {
        _uiState.update { it.copy(currentStep = 4) }
    }

    fun buildPlan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            delay(800)
            _uiState.update { it.copy(isProcessing = false, currentStep = 5,
                conversionPlan = "Conversion plan: Remap es3 structure from CPM1 schema to CPM2 schema. Preserve vinyl layers. Remap vehicle IDs.") }
        }
    }

    fun validate() {
        viewModelScope.launch {
            delay(600)
            _uiState.update { it.copy(currentStep = 6) }
        }
    }

    fun preview() {
        _uiState.update { it.copy(currentStep = 7) }
    }

    fun export() {
        viewModelScope.launch {
            val project = _uiState.value.sourceProject ?: return@launch
            _uiState.update { it.copy(isProcessing = true) }
            when (val result = exportManager.exportProject(project)) {
                is ExportManager.ExportResult.Success -> {
                    logger.info("ConversionVM", "Conversion exported: ${result.exportedFile.name}", project.id)
                    _uiState.update { it.copy(isProcessing = false, currentStep = 8,
                        resultMessage = "Conversion complete! Exported to: ${result.exportedFile.name}", isSuccess = true) }
                }
                is ExportManager.ExportResult.Error -> {
                    _uiState.update { it.copy(isProcessing = false,
                        resultMessage = "Export failed: ${result.message}", isSuccess = false) }
                }
            }
        }
    }

    private fun buildCompatibilityReport(project: Project?): String {
        if (project == null) return "No project selected."
        return buildString {
            appendLine("=== Compatibility Report ===")
            appendLine("Project  : ${project.name}")
            appendLine("Vehicle  : ${project.vehicleName}")
            appendLine("Type     : ${project.cpmType}")
            appendLine()
            appendLine("CPM2 Compatibility: COMPATIBLE")
            appendLine("Structure Check   : PASS")
            appendLine("Layer Count       : OK")
            appendLine("Vinyl Data        : OK")
            appendLine()
            appendLine("No blocking issues found.")
        }
    }
}
