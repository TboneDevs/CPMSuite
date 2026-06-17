package com.dynogamer.studio.ui.screens.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.core.manager.DiagnosticsManager
import com.dynogamer.studio.core.manager.ExportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiagnosticsUiState(
    val report: DiagnosticsManager.DiagnosticsReport? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val diagnosticsManager: DiagnosticsManager,
    private val exportManager: ExportManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticsUiState())
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val report = diagnosticsManager.generateReport()
            _uiState.update { it.copy(report = report, isLoading = false) }
        }
    }

    fun exportReport() {
        viewModelScope.launch {
            val report = _uiState.value.report ?: return@launch
            val content = diagnosticsManager.formatReport(report)
            exportManager.exportReport(content, "diagnostics_${System.currentTimeMillis()}")
        }
    }
}
