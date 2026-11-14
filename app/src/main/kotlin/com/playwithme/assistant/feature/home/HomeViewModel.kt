package com.playwithme.assistant.feature.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.permission.PermissionManager
import com.playwithme.assistant.domain.usecase.GetAllHeroesUseCase
import com.playwithme.assistant.service.OverlayService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager,
    private val getAllHeroesUseCase: GetAllHeroesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshPermissions()
        observeHeroCount()
    }

    fun refreshPermissions() {
        val state = permissionManager.getPermissionState()
        _uiState.update { it.copy(permissionState = state) }
    }

    private fun observeHeroCount() {
        getAllHeroesUseCase()
            .onEach { heroes ->
                _uiState.update { it.copy(heroCount = heroes.size) }
            }
            .launchIn(viewModelScope)
    }

    fun startOverlay() {
        if (!permissionManager.hasOverlayPermission()) {
            Timber.w("HomeViewModel: overlay permission missing")
            return
        }
        val intent = Intent(context, OverlayService::class.java).apply {
            action = Constants.ACTION_START_OVERLAY
        }
        context.startForegroundService(intent)
        _uiState.update { it.copy(isOverlayRunning = true) }
        Timber.d("HomeViewModel: OverlayService started")
    }

    fun stopOverlay() {
        val intent = Intent(context, OverlayService::class.java).apply {
            action = Constants.ACTION_STOP_OVERLAY
        }
        context.startService(intent)
        _uiState.update { it.copy(isOverlayRunning = false) }
    }
}
