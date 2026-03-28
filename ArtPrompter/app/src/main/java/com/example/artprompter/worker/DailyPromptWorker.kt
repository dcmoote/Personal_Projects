package com.dcmoote.inkwell.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dcmoote.inkwell.InkwellApplication
import com.dcmoote.inkwell.util.NotificationHelper

class DailyPromptWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as InkwellApplication).container
        val repository = container.promptRepository

        return try {
            NotificationHelper.showPromptNotification(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
