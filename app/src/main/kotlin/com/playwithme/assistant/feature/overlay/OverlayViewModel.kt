package com.playwithme.assistant.feature.overlay

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.permission.PermissionManager
import com.playwithme.assistant.domain.usecase.*
import com.playwithme.assistant.service.OverlayService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OverlayControlUiState(
    val isOverlayRunning: Boolean = false,
    val hasPermission: Boolean = false,
    val alpha: Float = 0.9f,
    val snapEnabled: Boolean = true,
    val isLocked: Boolean = false
)

@HiltViewModel
class OverlayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setOverlayAlphaUseCase: SetOverlayAlphaUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverlayControlUiState())
    val uiState: StateFlow<OverlayControlUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(hasPermission = permissionManager.hasOverlayPermission()) }
        observeSettings()
    }

    private fun observeSettings() {
        getSettingsUseCase()
            .onEach { settings ->
                _uiState.update {
                    it.copy(
                        alpha = settings.overlayAlpha,
                        snapEnabled = settings.overlaySnapEnabled,
                        isLocked = settings.isOverlayLocked
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun startOverlay() {
        context.startForegroundService(Intent(context, OverlayService::class.java).apply {
            action = Constants.ACTION_START_OVERLAY
        })
        _uiState.update { it.copy(isOverlayRunning = true) }
    }

    fun stopOverlay() {
        context.startService(Intent(context, OverlayService::class.java).apply {
            action = Constants.ACTION_STOP_OVERLAY
        })
        _uiState.update { it.copy(isOverlayRunning = false) }
    }

    fun setAlpha(alpha: Float) = viewModelScope.launch { setOverlayAlphaUseCase(alpha) }
    fun setSnap(enabled: Boolean) = viewModelScope.launch {
        updateSettingsUseCase(getSettingsUseCase().first().copy(overlaySnapEnabled = enabled))
    }
    fun setLocked(locked: Boolean) = viewModelScope.launch {
        updateSettingsUseCase(getSettingsUseCase().first().copy(isOverlayLocked = locked))
    }
}
