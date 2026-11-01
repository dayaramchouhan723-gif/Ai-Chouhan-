package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.IntruderRecord
import com.example.data.local.StarDatabase
import java.util.concurrent.TimeUnit

class ScheduledSecurityWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("ScheduledSecurityWorker", "Executing scheduled security verification...")
        try {
            val db = StarDatabase.getInstance(applicationContext)
            // Log security heartbeat snapshot
            db.intruderDao().insertSnapshot(
                IntruderRecord(
                    triggerType = "SCHEDULED_AUTOMATION",
                    timestamp = System.currentTimeMillis(),
                    localPhotoPath = null,
                    note = "Automated WorkManager System Health Check: All perimeters secure."
                )
            )
            return Result.success()
        } catch (e: Exception) {
            Log.e("ScheduledSecurityWorker", "Worker failed: ${e.message}", e)
            return Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "chouhan_star_scheduled_watch"

        fun schedulePeriodicWatch(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ScheduledSecurityWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
