package com.playwithme.assistant.service

import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.service.notification.ServiceNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service for screen capture using [MediaProjection].
 *
 * **Flow:**
 * 1. Activity requests screen capture permission via MediaProjectionManager.
 * 2. Activity starts this service with result code + data intent.
 * 3. Service creates MediaProjection → VirtualDisplay → ImageReader.
 * 4. Frames are emitted via [frameFlow] for OCR processing.
 * 5. Service stops when [ACTION_STOP_CAPTURE] is received or stopSelf() called.
 *
 * **NOT doing any input or automation** — read-only capture.
 */
@AndroidEntryPoint
class CaptureService : LifecycleService() {

    @Inject lateinit var notificationManager: ServiceNotificationManager

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var frameJob: Job? = null

    companion object {
        /** Shared flow of captured frames (ARGB_8888 bitmap bytes). */
        private val _frameFlow = MutableSharedFlow<CapturedFrame>(
            extraBufferCapacity = 2 // Allow 2 frames to buffer
        )
        val frameFlow = _frameFlow.asSharedFlow()
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        Timber.d("CaptureService: onCreate")
        startForeground(
            Constants.CAPTURE_SERVICE_NOTIFICATION_ID,
            notificationManager.buildCaptureNotification()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            Constants.ACTION_STOP_CAPTURE -> {
                stopCapture()
                stopSelf()
            }
            Constants.ACTION_START_CAPTURE -> {
                val resultCode = intent.getIntExtra(Constants.EXTRA_MEDIA_PROJECTION_RESULT_CODE, -1)
                val data = intent.getParcelableExtra<Intent>(Constants.EXTRA_MEDIA_PROJECTION_DATA)

                if (resultCode != -1 && data != null) {
                    startCapture(resultCode, data)
                } else {
                    Timber.e("CaptureService: Invalid MediaProjection data")
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Timber.d("CaptureService: onDestroy")
        stopCapture()
        super.onDestroy()
    }

    private fun startCapture(resultCode: Int, data: Intent) {
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        mediaProjection = projectionManager.getMediaProjection(resultCode, data).also { projection ->
            projection.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    Timber.d("CaptureService: MediaProjection stopped")
                    stopCapture()
                    stopSelf()
                }
            }, mainHandler)
        }

        // Downscaled capture resolution for OCR efficiency
        val captureWidth = Constants.CAPTURE_WIDTH_PX
        val captureHeight = Constants.CAPTURE_HEIGHT_PX
        val density = resources.displayMetrics.densityDpi

        imageReader = ImageReader.newInstance(
            captureWidth, captureHeight,
            android.graphics.PixelFormat.RGBA_8888,
            2 // Max images in queue
        )

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "PlayWitMeCapture",
            captureWidth, captureHeight, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, mainHandler
        )

        startFrameCapture()
        Timber.d("CaptureService: capture started ($captureWidth×$captureHeight)")
    }

    private fun startFrameCapture() {
        frameJob = lifecycleScope.launch {
            val throttleMs = Constants.OCR_FRAME_THROTTLE_MS
            while (isActive) {
                try {
                    val image = imageReader?.acquireLatestImage()
                    if (image != null) {
                        val plane = image.planes[0]
                        val buffer = plane.buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)

                        _frameFlow.tryEmit(
                            CapturedFrame(
                                bytes = bytes,
                                width = image.width,
                                height = image.height,
                                pixelStride = plane.pixelStride,
                                rowStride = plane.rowStride,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        image.close()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "CaptureService: Frame capture error")
                }
                delay(throttleMs)
            }
        }
    }

    private fun stopCapture() {
        frameJob?.cancel()
        frameJob = null
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection?.stop()
        mediaProjection = null
        Timber.d("CaptureService: capture stopped, resources released")
    }
}

/**
 * A captured screen frame from [MediaProjection].
 */
data class CapturedFrame(
    val bytes: ByteArray,
    val width: Int,
    val height: Int,
    val pixelStride: Int,
    val rowStride: Int,
    val timestamp: Long
) {
    override fun equals(other: Any?) = false // Always unique
    override fun hashCode() = timestamp.hashCode()
}
