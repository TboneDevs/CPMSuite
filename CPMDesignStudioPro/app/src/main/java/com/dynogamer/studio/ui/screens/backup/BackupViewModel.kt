package com.dynogamer.studio.ui.screens.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.BackupManager
import com.dynogamer.studio.domain.model.Backup
import com.dynogamer.studio.domain.repository.BackupRepository
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val backups: List<Backup> = emptyList(),
    val totalSizeMb: Long = 0L
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val projectRepository: ProjectRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            backupRepository.getAllBackups().collect { backups ->
                val totalMb = backups.sumOf { it.sizeBytes } / 1_048_576
                _uiState.update { it.copy(backups = backups, totalSizeMb = totalMb) }
            }
        }
    }

    fun restore(backup: Backup) {
        viewModelScope.launch {
            val project = projectRepository.getProjectById(backup.projectId) ?: return@launch
            backupManager.restoreBackup(backup, project)
        }
    }

    fun delete(backup: Backup) {
        viewModelScope.launch { backupManager.deleteBackup(backup) }
    }

    fun validate(backup: Backup) {
        viewModelScope.launch {
            val isValid = backupManager.validateBackup(backup)
            // Update the backup's validity in the repository
            backupRepository.saveBackup(backup.copy(isValid = isValid))
        }
    }
}
