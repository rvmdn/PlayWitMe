package com.playwithme.assistant.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for PlayWitMe.
 *
 * [@HiltAndroidApp] triggers Hilt's code generation and sets up the
 * application-level DI component.
 *
 * Also initializes:
 * - Timber logging (debug builds only)
 * - WorkManager with Hilt worker factory
 */
@HiltAndroidApp
class PlayWitMeApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (com.playwithme.assistant.BuildConfig.ENABLE_LOGGING) {
            Timber.plant(Timber.DebugTree())
            Timber.d("PlayWitMeApplication: Timber initialized (DEBUG)")
        }
        // In release: plant a crash-reporting tree (e.g., Crashlytics)
        // Timber.plant(CrashlyticsTree())
    }
}
