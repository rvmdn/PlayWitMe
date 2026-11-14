package com.playwithme.assistant.feature.home

import com.playwithme.assistant.core.common.UiState
import com.playwithme.assistant.core.permission.PermissionState

data class HomeUiState(
    val permissionState: PermissionState = PermissionState(false, false),
    val isOverlayRunning: Boolean = false,
    val isCaptureRunning: Boolean = false,
    val heroCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
