package com.playwithme.assistant.service

import android.content.Intent
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.overlay.OverlayConfig
import com.playwithme.assistant.core.overlay.OverlayEngine
import com.playwithme.assistant.core.permission.PermissionManager
import com.playwithme.assistant.service.notification.ServiceNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

/**
 * Foreground service that hosts all overlay windows.
 *
 * **Responsibilities:**
 * - Start as a foreground service with a persistent notification
 * - Initialize and manage the [OverlayEngine]
 * - Show/hide the floating assistant panel
 * - Handle start/stop commands via Intent actions
 *
 * **What it does NOT do:**
 * - No auto-tap, no macro, no automated gameplay interaction
 * - No accessibility service usage
 * - No input injection of any kind
 *
 * The service is annotated with [@AndroidEntryPoint] allowing Hilt
 * to inject dependencies directly.
 */
@AndroidEntryPoint
class OverlayService : LifecycleService() {

    @Inject lateinit var overlayEngine: OverlayEngine
    @Inject lateinit var notificationManager: ServiceNotificationManager
    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var heroRepository: com.playwithme.assistant.domain.repository.HeroRepository
    @Inject lateinit var gameStateManager: com.playwithme.assistant.core.state.GameStateManager
    @Inject lateinit var settingsRepository: com.playwithme.assistant.domain.repository.SettingsRepository

    private lateinit var draftViewModel: com.playwithme.assistant.feature.draft.DraftViewModel

    override fun onCreate() {
        super.onCreate()
        Timber.d("OverlayService: onCreate")
        draftViewModel = com.playwithme.assistant.feature.draft.DraftViewModel(heroRepository)
        
        startForeground(
            Constants.OVERLAY_SERVICE_NOTIFICATION_ID,
            notificationManager.buildOverlayNotification()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            Constants.ACTION_START_OVERLAY -> {
                Timber.d("OverlayService: START_OVERLAY")
                startOverlay()
            }
            Constants.ACTION_STOP_OVERLAY -> {
                Timber.d("OverlayService: STOP_OVERLAY")
                stopSelf()
            }
            else -> {
                // Default start — show overlay
                startOverlay()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Timber.d("OverlayService: onDestroy")
        overlayEngine.removeAll()
        super.onDestroy()
    }

    private fun startOverlay() {
        if (!permissionManager.hasOverlayPermission()) {
            Timber.w("OverlayService: Overlay permission not granted")
            stopSelf()
            return
        }

        lifecycleScope.launch {
            showMainAssistantOverlay()
        }
    }

    private fun showMainAssistantOverlay() {
        val density = resources.displayMetrics.density
        var currentX = 0
        var currentY = (100 * density).toInt()
        var currentWidth = (Constants.OVERLAY_DEFAULT_WIDTH_DP * density).toInt()
        var currentHeight = (Constants.OVERLAY_DEFAULT_HEIGHT_DP * density).toInt()
        var currentOpacity = 0.92f

        overlayEngine.addWindow(
            id = "main_assistant",
            config = OverlayConfig(
                widthPx = currentWidth,
                heightPx = currentHeight,
                xPosition = currentX,
                yPosition = currentY,
                alpha = currentOpacity,
                isDraggable = true,
                isSnapEnabled = true,
            ),
            autoShow = true
        ) {
            var opacity by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(currentOpacity) }
            var fontScale by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1.0f) }
            val draftState by draftViewModel.state.collectAsState()
            val screenState by gameStateManager.screenState.collectAsState()
            val appSettings by settingsRepository.getSettings().collectAsState(initial = com.playwithme.assistant.domain.model.AppSettings())
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

            // For now, use MainAssistantOverlay which has a switcher for Phase 13 testing
            com.playwithme.assistant.core.overlay.components.MainAssistantOverlay(
                draftState = draftState,
                screenState = screenState,
                onCloseClick = { stopSelf() },
                onDrag = { dx, dy ->
                    currentX += dx.toInt()
                    currentY += dy.toInt()
                    overlayEngine.updatePosition("main_assistant", currentX, currentY)
                },
                onResize = { dw, dh ->
                    currentWidth = (currentWidth + dw.toInt()).coerceAtLeast((150 * density).toInt())
                    currentHeight = (currentHeight + dh.toInt()).coerceAtLeast((44 * density).toInt())
                    overlayEngine.updateSize("main_assistant", currentWidth, currentHeight)
                },
                opacity = opacity,
                onOpacityChange = { newOpacity ->
                    opacity = newOpacity
                    overlayEngine.updateAlpha("main_assistant", newOpacity)
                },
                fontScale = fontScale,
                onFontScaleChange = { newScale -> fontScale = newScale },
                appSettings = appSettings,
                onToggleOcr = { enabled -> coroutineScope.launch { settingsRepository.setOcrEnabled(enabled) } },
                onToggleGankDetector = { enabled -> coroutineScope.launch { settingsRepository.setGankDetectorEnabled(enabled) } },
                isAutoDetectEnabled = draftState.isAutoDetectEnabled,
                onToggleAutoDetect = { draftViewModel.toggleAutoDetect() },
                onHeroSlotClick = { isAlly, index, isBan ->
                    // For manual testing, we could open a hero picker here.
                    // Currently acts as a placeholder callback.
                },
                onScanClick = { command ->
                    gameStateManager.triggerScan(command)
                }
            )
        }

        Timber.d("OverlayService: main_assistant overlay shown")
    }
}
