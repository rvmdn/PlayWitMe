package com.playwithme.assistant.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.domain.model.AiProvider
import com.playwithme.assistant.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val setOcrEnabledUseCase: SetOcrEnabledUseCase,
    private val setAiEnabledUseCase: SetAiEnabledUseCase,
    private val setAiProviderUseCase: SetAiProviderUseCase,
    private val setOverlayAlphaUseCase: SetOverlayAlphaUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { setDarkModeUseCase(enabled) }
    fun setOcrEnabled(enabled: Boolean) = viewModelScope.launch { setOcrEnabledUseCase(enabled) }
    fun setAiEnabled(enabled: Boolean) = viewModelScope.launch { setAiEnabledUseCase(enabled) }
    fun setAiProvider(provider: AiProvider) = viewModelScope.launch { setAiProviderUseCase(provider) }
    fun setOverlayAlpha(alpha: Float) = viewModelScope.launch { setOverlayAlphaUseCase(alpha) }
    fun setOcrFrameRate(fps: Int) = viewModelScope.launch { updateSettingsUseCase(settings.value.copy(ocrFrameRateFps = fps)) }
    fun setCaptureScale(scale: Float) = viewModelScope.launch { updateSettingsUseCase(settings.value.copy(captureResolutionScale = scale)) }
    fun setOverlaySnap(enabled: Boolean) = viewModelScope.launch { updateSettingsUseCase(settings.value.copy(overlaySnapEnabled = enabled)) }
    fun setBatteryOptimization(enabled: Boolean) = viewModelScope.launch { updateSettingsUseCase(settings.value.copy(isBatteryOptimizationEnabled = enabled)) }
    fun setGankDetectorEnabled(enabled: Boolean) = viewModelScope.launch { updateSettingsUseCase(settings.value.copy(isGankDetectorEnabled = enabled)) }
    fun resetSettings() = viewModelScope.launch { resetSettingsUseCase() }
}
