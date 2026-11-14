package com.playwithme.assistant.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.playwithme.assistant.R
import com.playwithme.assistant.app.MainActivity
import com.playwithme.assistant.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages notification channels and foreground service notifications.
 *
 * Must be called before starting any foreground service.
 * The channel is created once — subsequent calls are no-ops.
 */
@Singleton
class ServiceNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /** Creates the persistent notification channel (idempotent). */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW // Silent, no sound/vibration
        ).apply {
            description = "PlayWitMe game assistant service"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    /** Builds the overlay service notification. */
    fun buildOverlayNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            context,
            1,
            Intent(context, com.playwithme.assistant.service.OverlayService::class.java).apply {
                action = Constants.ACTION_STOP_OVERLAY
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("PlayWitMe Active")
            .setContentText("Game assistant overlay is running")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /** Builds the capture service notification. */
    fun buildCaptureNotification(): Notification {
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Screen Capture Active")
            .setContentText("Reading game screen for analysis")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /** Builds the OCR service notification. */
    fun buildOcrNotification(): Notification {
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Text Analysis Active")
            .setContentText("Analyzing game text in background")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
