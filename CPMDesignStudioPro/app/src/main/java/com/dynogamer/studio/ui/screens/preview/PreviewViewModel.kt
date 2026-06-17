package com.dynogamer.studio.ui.screens.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class PreviewUiState(
    val projectId: String = "",
    val projectName: String = "Loading...",
    val vehicleName: String = "",
    val cpmType: String = "",
    val status: String = "",
    val fileSize: String = "Unknown",
    val layerCount: String = "N/A",
    val vinylCount: String = "N/A",
    val validationWarnings: List<String> = emptyList()
)

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    fun loadProject(projectId: String) {
        if (projectId == "none") {
            _uiState.update { it.copy(projectName = "No Project Selected", vehicleName = "—", cpmType = "—", status = "—") }
            return
        }
        viewModelScope.launch {
            val project = projectRepository.getProjectById(projectId)
            if (project != null) {
                val file = File(project.workingCopyPath)
                val warnings = mutableListOf<String>()
                if (!file.exists()) warnings.add("Working copy file not found")
                if (file.length() < 100) warnings.add("File may be corrupted (very small)")

                _uiState.update {
                    it.copy(
                        projectId = project.id,
                        projectName = project.name,
                        vehicleName = project.vehicleName,
                        cpmType = project.cpmType,
                        status = project.status,
                        fileSize = if (file.exists()) "${file.length() / 1024} KB" else "Not found",
                        layerCount = "—",
                        vinylCount = "—",
                        validationWarnings = warnings
                    )
                }
            } else {
                _uiState.update { it.copy(projectName = "Project Not Found") }
            }
        }
    }
}
