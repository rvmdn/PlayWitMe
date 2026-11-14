package com.playwithme.assistant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.repository.HeroRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for syncing hero data from the remote API.
 *
 * Runs as a periodic background task (daily, requires network).
 * Uses Hilt injection via [@HiltWorker] and [@AssistedInject].
 *
 * Schedule via:
 * ```kotlin
 * HeroSyncWorker.schedule(workManager)
 * ```
 */
@HiltWorker
class HeroSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val heroRepository: HeroRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("HeroSyncWorker: starting sync")

        return when (val result = heroRepository.syncHeroesFromRemote()) {
            is com.playwithme.assistant.core.common.Result.Success -> {
                Timber.d("HeroSyncWorker: synced ${result.data} heroes")
                Result.success(
                    workDataOf("synced_count" to result.data)
                )
            }
            is com.playwithme.assistant.core.common.Result.Error -> {
                Timber.e(result.exception, "HeroSyncWorker: sync failed")
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
            else -> Result.failure()
        }
    }

    companion object {
        fun schedule(workManager: WorkManager) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<HeroSyncWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .addTag(Constants.WORK_TAG_HERO_SYNC)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            workManager.enqueueUniquePeriodicWork(
                Constants.WORK_TAG_HERO_SYNC,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )

            Timber.d("HeroSyncWorker: scheduled (daily)")
        }
    }
}

/**
 * WorkManager worker for database cleanup.
 * Removes stale or orphaned data.
 */
@HiltWorker
class DatabaseCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("DatabaseCleanupWorker: running cleanup")
        // TODO: Add cleanup logic (e.g., delete old notes, stale cache)
        return Result.success()
    }

    companion object {
        fun scheduleWeekly(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<DatabaseCleanupWorker>(7, TimeUnit.DAYS)
                .addTag(Constants.WORK_TAG_DB_CLEANUP)
                .build()

            workManager.enqueueUniquePeriodicWork(
                Constants.WORK_TAG_DB_CLEANUP,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
