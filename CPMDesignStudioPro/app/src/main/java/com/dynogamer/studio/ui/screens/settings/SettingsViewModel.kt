package com.dynogamer.studio.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.FileManager
import com.dynogamer.studio.domain.repository.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val glowEnabled: Boolean = true,
    val autoBackupImport: Boolean = true,
    val autoBackupEdit: Boolean = true,
    val developerMode: Boolean = false,
    val projectsDir: String = "",
    val backupsDir: String = "",
    val exportsDir: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fileManager: FileManager,
    private val logRepository: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                projectsDir = fileManager.getProjectsDir().absolutePath,
                backupsDir = fileManager.getBackupsDir().absolutePath,
                exportsDir = fileManager.getExportsDir().absolutePath
            )
        }
    }

    fun toggleGlow() = _uiState.update { it.copy(glowEnabled = !it.glowEnabled) }
    fun toggleAutoBackupImport() = _uiState.update { it.copy(autoBackupImport = !it.autoBackupImport) }
    fun toggleAutoBackupEdit() = _uiState.update { it.copy(autoBackupEdit = !it.autoBackupEdit) }
    fun toggleDeveloperMode() = _uiState.update { it.copy(developerMode = !it.developerMode) }

    fun clearLogs() {
        viewModelScope.launch { logRepository.clearAllLogs() }
    }
}
