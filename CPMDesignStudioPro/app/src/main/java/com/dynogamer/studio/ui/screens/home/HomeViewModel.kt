package com.dynogamer.studio.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.CPMScanner
import com.dynogamer.studio.core.manager.ShizukuManager
import com.dynogamer.studio.domain.repository.BackupRepository
import com.dynogamer.studio.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val cpm1Count: Int = 0,
    val cpm2Count: Int = 0,
    val backupCount: Int = 0,
    val shizukuAuthorized: Boolean = false,
    val scanResult: CPMScanner.ScanResult? = null,
    val isScanning: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val backupRepository: BackupRepository,
    private val cpmScanner: CPMScanner,
    private val shizukuManager: ShizukuManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeStats()
        checkShizuku()
    }

    private fun observeStats() {
        viewModelScope.launch {
            combine(
                projectRepository.getProjectsByType("CPM1"),
                projectRepository.getProjectsByType("CPM2"),
                backupRepository.getAllBackups()
            ) { cpm1, cpm2, backups ->
                _uiState.update { it.copy(cpm1Count = cpm1.size, cpm2Count = cpm2.size, backupCount = backups.size) }
            }.collect()
        }
    }

    private fun checkShizuku() {
        _uiState.update { it.copy(shizukuAuthorized = shizukuManager.isAuthorized()) }
    }

    fun scanDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            val result = cpmScanner.scanDevice()
            _uiState.update { it.copy(scanResult = result, isScanning = false, shizukuAuthorized = result.shizukuAuthorized) }
        }
    }
}
