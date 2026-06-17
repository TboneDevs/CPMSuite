package com.dynogamer.studio.ui.screens.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.ExportManager
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExportUiState(
    val projects: List<Project> = emptyList(),
    val exportStatus: String? = null,
    val exportSuccess: Boolean = false
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val exportManager: ExportManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepository.getAllProjects().collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
    }

    fun exportProject(project: Project) {
        viewModelScope.launch {
            when (val result = exportManager.exportProject(project)) {
                is ExportManager.ExportResult.Success ->
                    _uiState.update { it.copy(exportStatus = "Exported: ${result.exportedFile.name}", exportSuccess = true) }
                is ExportManager.ExportResult.Error ->
                    _uiState.update { it.copy(exportStatus = "Export failed: ${result.message}", exportSuccess = false) }
            }
        }
    }
}
