package com.dynogamer.studio.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynogamer.studio.domain.model.LogEntry
import com.dynogamer.studio.domain.repository.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogsUiState(val logs: List<LogEntry> = emptyList())

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val logRepository: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init { loadLogs() }

    private fun loadLogs() {
        viewModelScope.launch {
            logRepository.getRecentLogs(500).collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                logRepository.getRecentLogs(500).collect { logs -> _uiState.update { it.copy(logs = logs) } }
            } else {
                logRepository.searchLogs(query).collect { logs -> _uiState.update { it.copy(logs = logs) } }
            }
        }
    }

    fun filterByLevel(level: String) {
        viewModelScope.launch {
            if (level == "ALL") {
                logRepository.getRecentLogs(500).collect { logs -> _uiState.update { it.copy(logs = logs) } }
            } else {
                logRepository.getLogsByLevel(level).collect { logs -> _uiState.update { it.copy(logs = logs) } }
            }
        }
    }

    fun clearLogs() {
        viewModelScope.launch { logRepository.clearAllLogs() }
    }
}
