package com.playwithme.assistant.core.ai

import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Minimap Analyzer Skeleton (Phase 14)
 * Future home of OpenCV and Object Detection for minimap awareness.
 */
@Singleton
class MinimapAnalyzer @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private var isGankDetectorEnabled = false

    init {
        settingsRepository.getSettings()
            .onEach { settings ->
                isGankDetectorEnabled = settings.isGankDetectorEnabled
                if (isGankDetectorEnabled) {
                    Timber.d("MinimapAnalyzer: Gank Detector is enabled.")
                } else {
                    Timber.d("MinimapAnalyzer: Gank Detector is disabled.")
                }
            }
            .launchIn(CoroutineScope(Dispatchers.Default))
    }

    /**
     * Called by a VisionService/OCRService when a frame is ready for analysis.
     * Note: Should only process frames at a low frame rate (e.g. 2 FPS).
     */
    fun analyzeFrame(frameBytes: ByteArray, width: Int, height: Int) {
        if (!isGankDetectorEnabled) return

        // TODO: Phase 14 - OpenCV integration
        // 1. Crop frame based on user calibration box.
        // 2. Convert to HSV.
        // 3. Mask red pixels to find enemy icons.
        // 4. Calculate bounding boxes & missing enemy time.
        // 5. Emit Gank Warning event.
    }
}
