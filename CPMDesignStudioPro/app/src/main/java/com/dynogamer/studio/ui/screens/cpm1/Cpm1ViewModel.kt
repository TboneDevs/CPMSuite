package com.dynogamer.studio.ui.screens.cpm1

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.FileManager
import com.dynogamer.studio.core.manager.ImportManager
import com.dynogamer.studio.core.manager.ShizukuManager
import com.dynogamer.studio.domain.model.Es3File
import com.dynogamer.studio.domain.model.Project
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Cpm1UiState(
    val files: List<Es3File> = emptyList(),
    val projects: List<Project> = emptyList(),
    val activePath: String = ShizukuManager.CPM1_ES3_PATH,
    val shizukuAuthorized: Boolean = false,
    val importStatus: String? = null,
    val importSuccess: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class Cpm1ViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val importManager: ImportManager,
    private val fileManager: FileManager,
    private val shizukuManager: ShizukuManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(Cpm1UiState())
    val uiState: StateFlow<Cpm1UiState> = _uiState.asStateFlow()

    init {
        shizukuManager.setPathForCpm("CPM1")
        _uiState.update {
            it.copy(
                shizukuAuthorized = shizukuManager.isAuthorized(),
                activePath = shizukuManager.getActivePath()
            )
        }
        observeProjects()
        if (shizukuManager.isAuthorized()) loadShizukuFiles()
    }

    private fun observeProjects() {
        viewModelScope.launch {
            projectRepository.getProjectsByType("CPM1").collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
    }

    private fun loadShizukuFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val files = shizukuManager.listFiles(ShizukuManager.CPM1_ES3_PATH)
            _uiState.update { it.copy(files = files, isLoading = false) }
        }
    }

    fun setFolder(uri: Uri) {
        viewModelScope.launch {
            val files = fileManager.listEs3Files(uri)
            _uiState.update { it.copy(files = files) }
        }
    }

    fun selectFile(file: Es3File) {
        // Navigate to import or preview
    }

    fun importFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, importStatus = "Importing...") }
            when (val result = importManager.importFile(uri, "CPM1")) {
                is ImportManager.ImportResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            importStatus = "Import successful: ${result.project.name}",
                            importSuccess = true
                        )
                    }
                }
                is ImportManager.ImportResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, importStatus = "Error: ${result.message}", importSuccess = false)
                    }
                }
            }
        }
    }
}
