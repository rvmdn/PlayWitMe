package com.playwithme.assistant.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.playwithme.assistant.app.navigation.AppNavGraph
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.permission.PermissionManager
import com.playwithme.assistant.core.ui.theme.PlayWitMeTheme
import com.playwithme.assistant.domain.usecase.GetSettingsUseCase
import com.playwithme.assistant.service.CaptureService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main entry point for PlayWitMe.
 *
 * Responsibilities:
 * - Install splash screen
 * - Set up Compose UI with theme
 * - Handle MediaProjection permission request (must come from Activity)
 * - Start overlay/capture services with proper permissions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var getSettingsUseCase: GetSettingsUseCase

    // MediaProjection permission launcher
    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            Timber.d("MainActivity: MediaProjection permission granted")
            startCaptureService(result.resultCode, result.data!!)
        } else {
            Timber.w("MainActivity: MediaProjection permission denied")
        }
    }

    // Notification permission launcher (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("Notification permission: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settings by getSettingsUseCase().collectAsState(
                initial = com.playwithme.assistant.domain.model.AppSettings()
            )

            PlayWitMeTheme(darkTheme = settings.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    /**
     * Requests MediaProjection permission from the user.
     * Must be called from the Activity — not from a Service.
     */
    fun requestMediaProjectionPermission() {
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
        mediaProjectionLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    private fun startCaptureService(resultCode: Int, data: Intent) {
        val serviceIntent = Intent(this, CaptureService::class.java).apply {
            action = Constants.ACTION_START_CAPTURE
            putExtra(Constants.EXTRA_MEDIA_PROJECTION_RESULT_CODE, resultCode)
            putExtra(Constants.EXTRA_MEDIA_PROJECTION_DATA, data)
        }
        startForegroundService(serviceIntent)
    }
}
