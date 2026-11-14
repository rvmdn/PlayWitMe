package com.playwithme.assistant.domain.usecase

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.domain.model.AiProvider
import com.playwithme.assistant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case: Observe settings as a live Flow. */
class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.getSettings()
}

/** Use case: Update settings. */
class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(settings: AppSettings) = repository.updateSettings(settings)
}

/** Use case: Toggle dark mode. */
class SetDarkModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkMode(enabled)
}

/** Use case: Update overlay alpha. */
class SetOverlayAlphaUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(alpha: Float) = repository.setOverlayAlpha(alpha.coerceIn(0.1f, 1.0f))
}

/** Use case: Toggle OCR. */
class SetOcrEnabledUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setOcrEnabled(enabled)
}

/** Use case: Toggle AI analysis. */
class SetAiEnabledUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setAiEnabled(enabled)
}

/** Use case: Set AI provider. */
class SetAiProviderUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(provider: AiProvider): Result<Unit> {
        return try {
            repository.setAiProvider(provider)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/** Use case: Reset settings to defaults. */
class ResetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.resetToDefaults()
}
