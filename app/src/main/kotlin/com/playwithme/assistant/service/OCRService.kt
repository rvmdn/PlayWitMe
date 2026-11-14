package com.playwithme.assistant.service

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.ocr.GameTextParser
import com.playwithme.assistant.core.ocr.ImagePreprocessor
import com.playwithme.assistant.core.ocr.MLKitOCREngine
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.service.notification.ServiceNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service for processing captured frames through the OCR pipeline.
 *
 * **Pipeline:**
 * [CaptureService.frameFlow] → [ImagePreprocessor] → [MLKitOCREngine] → [GameTextParser]
 *                                                                      → [ocrResultFlow]
 *
 * Runs processing on [Dispatchers.Default] to avoid UI jank.
 * Frame processing is serial — drops frames if pipeline is busy.
 */
@AndroidEntryPoint
class OCRService : LifecycleService() {

    @Inject lateinit var notificationManager: ServiceNotificationManager
    @Inject lateinit var imagePreprocessor: ImagePreprocessor
    @Inject lateinit var mlKitOCREngine: MLKitOCREngine
    @Inject lateinit var gameTextParser: GameTextParser
    @Inject lateinit var settingsRepository: com.playwithme.assistant.domain.repository.SettingsRepository
    @Inject lateinit var gameStateManager: com.playwithme.assistant.core.state.GameStateManager
    @Inject lateinit var heroRepository: com.playwithme.assistant.domain.repository.HeroRepository

    private var isProcessing = false
    private var isOcrEnabled = true

    companion object {
        private val _ocrResultFlow = MutableSharedFlow<OcrResult>(extraBufferCapacity = 5)
        val ocrResultFlow = _ocrResultFlow.asSharedFlow()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("OCRService: onCreate")
        startForeground(
            Constants.OCR_SERVICE_NOTIFICATION_ID,
            notificationManager.buildOcrNotification()
        )
        startProcessingFrames()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        Timber.d("OCRService: onDestroy")
        mlKitOCREngine.close()
        super.onDestroy()
    }

    /**
     * Subscribes to [CaptureService.frameFlow] and processes each frame
     * through the full OCR pipeline.
     *
     * Frames are dropped if a previous frame is still processing
     * (backpressure: last-value semantics).
     */
    private var pendingScanCommand: com.playwithme.assistant.core.state.ScanCommand? = null

    private fun startProcessingFrames() {
        lifecycleScope.launch {
            settingsRepository.getSettings().collect { settings ->
                isOcrEnabled = settings.isOcrEnabled
            }
        }
        
        lifecycleScope.launch {
            gameStateManager.scanCommandFlow.collect { command ->
                pendingScanCommand = command
                Timber.d("OCRService: Received ScanCommand, waiting for next frame")
            }
        }

        lifecycleScope.launch {
            CaptureService.frameFlow.collect { frame ->
                if (!isOcrEnabled && pendingScanCommand == null) return@collect

                if (isProcessing) {
                    Timber.v("OCRService: dropping frame (pipeline busy)")
                    return@collect
                }

                isProcessing = true
                withContext(Dispatchers.Default) {
                    val commandToProcess = pendingScanCommand
                    if (commandToProcess != null) {
                        pendingScanCommand = null
                        processScanCommand(frame, commandToProcess)
                    } else {
                        processFrame(frame)
                    }
                }
                isProcessing = false
            }
        }
    }

    private suspend fun processFrame(frame: CapturedFrame) {
        try {
            // Step 1: Preprocess
            val bitmap = imagePreprocessor.process(
                bytes = frame.bytes,
                width = frame.width,
                height = frame.height,
                pixelStride = frame.pixelStride,
                rowStride = frame.rowStride
            ) ?: run {
                Timber.v("OCRService: preprocessing returned null")
                return
            }

            // Step 2: OCR
            val ocrResult = when (val result = mlKitOCREngine.recognize(bitmap)) {
                is com.playwithme.assistant.core.common.Result.Success -> result.data
                is com.playwithme.assistant.core.common.Result.Error -> {
                    Timber.e(result.exception, "OCR failed")
                    return
                }
                else -> return
            }

            // Step 3: Emit result (subscribers handle parsing)
            _ocrResultFlow.tryEmit(ocrResult)

            Timber.v("OCRService: OCR done in ${ocrResult.processingTimeMs}ms — '${ocrResult.rawText.take(50)}'")
        } catch (e: Exception) {
            Timber.e(e, "OCRService: pipeline error")
        } finally {
            // Bitmap recycled by GC — no explicit recycle needed for processed bitmaps
        }
    }
    
    private val scannedBitmaps = mutableMapOf<com.playwithme.assistant.core.state.ScanStep, Bitmap>()

    private suspend fun processScanCommand(frame: CapturedFrame, command: com.playwithme.assistant.core.state.ScanCommand) {
        try {
            val bitmap = imagePreprocessor.process(
                bytes = frame.bytes,
                width = frame.width,
                height = frame.height,
                pixelStride = frame.pixelStride,
                rowStride = frame.rowStride
            ) ?: return

            // Ensure coordinates are within bitmap bounds
            val safeRect = android.graphics.Rect(
                command.captureRect.left.coerceAtLeast(0),
                command.captureRect.top.coerceAtLeast(0),
                command.captureRect.right.coerceAtMost(bitmap.width),
                command.captureRect.bottom.coerceAtMost(bitmap.height)
            )

            // Crop current step
            val stepBitmap = Bitmap.createBitmap(
                bitmap,
                safeRect.left,
                safeRect.top,
                safeRect.width(),
                safeRect.height()
            )
            
            scannedBitmaps[command.step] = stepBitmap
            
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                android.widget.Toast.makeText(this, "Captured ${command.step.name}", android.widget.Toast.LENGTH_SHORT).show()
            }

            if (command.step == com.playwithme.assistant.core.state.ScanStep.ULTIMATE) {
                // Finalize and save
                finalizeScan()
            }
        } catch (e: Exception) {
            Timber.e(e, "OCRService: Error processing scan command")
        }
    }

    private suspend fun finalizeScan() {
        try {
            val mainBitmap = scannedBitmaps[com.playwithme.assistant.core.state.ScanStep.MAIN_INFO] ?: return
            
            // For now, let's just OCR the whole main info box to find a generic name
            val heroName = when (val result = mlKitOCREngine.recognize(mainBitmap)) {
                is com.playwithme.assistant.core.common.Result.Success -> result.data.rawText.split("\n").firstOrNull()?.trim() ?: "Unknown"
                else -> "Unknown_Hero"
            }
            
            val cleanHeroName = heroName.replace(Regex("[^A-Za-z0-9]"), "_").lowercase()
            val heroesDir = java.io.File(this.filesDir, "heroes")
            if (!heroesDir.exists()) heroesDir.mkdirs()

            // Save all bitmaps to files
            val iconUrls = mutableMapOf<com.playwithme.assistant.core.state.ScanStep, String>()
            scannedBitmaps.forEach { (step, bmp) ->
                val fileName = "${cleanHeroName}_${step.name}.png"
                val file = java.io.File(heroesDir, fileName)
                java.io.FileOutputStream(file).use { out ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                iconUrls[step] = file.absolutePath
            }

            val newHero = com.playwithme.assistant.domain.model.Hero(
                id = "scanned_$cleanHeroName",
                name = heroName,
                title = "Scanned Hero",
                lane = listOf(com.playwithme.assistant.domain.model.HeroLane.EXP),
                difficulty = com.playwithme.assistant.domain.model.HeroDifficulty.MODERATE,
                description = "Hero baru hasil scan 5 langkah.",
                iconUrl = iconUrls[com.playwithme.assistant.core.state.ScanStep.MAIN_INFO] ?: "",
                portrait = iconUrls[com.playwithme.assistant.core.state.ScanStep.MAIN_INFO] ?: "",
                skills = listOf(
                    com.playwithme.assistant.domain.model.HeroSkill(
                        key = com.playwithme.assistant.domain.model.SkillKey.SKILL_1,
                        name = "Skill 1",
                        description = "Scanned",
                        type = com.playwithme.assistant.domain.model.SkillType.ACTIVE,
                        iconUrl = iconUrls[com.playwithme.assistant.core.state.ScanStep.SKILL_1] ?: ""
                    ),
                    com.playwithme.assistant.domain.model.HeroSkill(
                        key = com.playwithme.assistant.domain.model.SkillKey.SKILL_2,
                        name = "Skill 2",
                        description = "Scanned",
                        type = com.playwithme.assistant.domain.model.SkillType.ACTIVE,
                        iconUrl = iconUrls[com.playwithme.assistant.core.state.ScanStep.SKILL_2] ?: ""
                    ),
                    com.playwithme.assistant.domain.model.HeroSkill(
                        key = com.playwithme.assistant.domain.model.SkillKey.SKILL_3,
                        name = "Skill 3",
                        description = "Scanned",
                        type = com.playwithme.assistant.domain.model.SkillType.ACTIVE,
                        iconUrl = iconUrls[com.playwithme.assistant.core.state.ScanStep.SKILL_3] ?: ""
                    ),
                    com.playwithme.assistant.domain.model.HeroSkill(
                        key = com.playwithme.assistant.domain.model.SkillKey.ULTIMATE,
                        name = "Ultimate",
                        description = "Scanned",
                        type = com.playwithme.assistant.domain.model.SkillType.ACTIVE,
                        iconUrl = iconUrls[com.playwithme.assistant.core.state.ScanStep.ULTIMATE] ?: ""
                    )
                ),
                recommendedBuilds = emptyList(),
                recommendedEmblem = com.playwithme.assistant.domain.model.HeroEmblem(
                    name = "Default", tier = 1, talents = emptyList()
                ),
                recommendedSpells = emptyList(),
                counters = emptyList(),
                strongAgainst = emptyList(),
                tips = emptyList(),
                combos = emptyList()
            )
            
            heroRepository.insertHero(newHero)
            
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                android.widget.Toast.makeText(this, "Hero $heroName Saved Completely!", android.widget.Toast.LENGTH_LONG).show()
            }
            
            // Clear cache
            scannedBitmaps.clear()
            
        } catch (e: Exception) {
            Timber.e(e, "OCRService: Error finalizing scan")
        }
    }
}
