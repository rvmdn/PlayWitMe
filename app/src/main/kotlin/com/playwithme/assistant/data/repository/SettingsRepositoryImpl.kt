package com.playwithme.assistant.data.repository

import com.playwithme.assistant.data.local.datastore.SettingsDataStore
import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.domain.model.AiProvider
import com.playwithme.assistant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = dataStore.settings

    override suspend fun updateSettings(settings: AppSettings) = dataStore.updateAll(settings)
    override suspend fun setDarkMode(enabled: Boolean) = dataStore.setDarkMode(enabled)
    override suspend fun setOverlayAlpha(alpha: Float) = dataStore.setOverlayAlpha(alpha)
    override suspend fun setOverlaySize(widthDp: Int, heightDp: Int) = dataStore.setOverlaySize(widthDp, heightDp)
    override suspend fun setOverlayPosition(x: Int, y: Int) = dataStore.setOverlayPosition(x, y)
    override suspend fun setOverlayLocked(locked: Boolean) = dataStore.setOverlayLocked(locked)
    override suspend fun setOcrEnabled(enabled: Boolean) = dataStore.setOcrEnabled(enabled)
    override suspend fun setOcrFrameRate(fps: Int) = dataStore.setOcrFrameRate(fps)
    override suspend fun setAiEnabled(enabled: Boolean) = dataStore.setAiEnabled(enabled)
    override suspend fun setAiProvider(provider: AiProvider) = dataStore.setAiProvider(provider)
    override suspend fun setGankDetectorEnabled(enabled: Boolean) = dataStore.setGankDetectorEnabled(enabled)
    override suspend fun resetToDefaults() = dataStore.reset()
}
