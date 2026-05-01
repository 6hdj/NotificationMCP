package com.notificationmcp.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class KeepAliveWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Ensure the keep-alive service is running
        val intent = android.content.Intent(applicationContext, KeepAliveService::class.java)
        applicationContext.startForegroundService(intent)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "keep_alive_worker"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<KeepAliveWorker>(
                15, TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
