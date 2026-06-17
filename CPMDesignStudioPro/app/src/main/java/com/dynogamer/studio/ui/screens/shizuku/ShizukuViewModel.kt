package com.dynogamer.studio.ui.screens.shizuku

import android.os.Build
import androidx.lifecycle.ViewModel
import com.dynogamer.studio.core.manager.ShizukuManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ShizukuUiState(
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false,
    val isAuthorized: Boolean = false,
    val version: Int = -1,
    val androidVersion: String = Build.VERSION.RELEASE
)

@HiltViewModel
class ShizukuViewModel @Inject constructor(
    private val shizukuManager: ShizukuManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShizukuUiState())
    val uiState: StateFlow<ShizukuUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        _uiState.value = ShizukuUiState(
            isInstalled = shizukuManager.isInstalled(),
            isRunning = shizukuManager.isRunning(),
            isAuthorized = shizukuManager.isAuthorized(),
            version = shizukuManager.getVersion(),
            androidVersion = Build.VERSION.RELEASE
        )
    }

    fun requestPermission() {
        shizukuManager.requestPermission()
    }
}
