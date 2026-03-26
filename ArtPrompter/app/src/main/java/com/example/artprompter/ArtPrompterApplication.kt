package com.dcmoote.inkwell

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dcmoote.inkwell.util.AppContainer
import com.dcmoote.inkwell.util.NotificationHelper
import com.dcmoote.inkwell.worker.DailyPromptWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class InkwellApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
        scheduleReminderIfEnabled()
    }

    private fun scheduleReminderIfEnabled() {
        val prefs = container.userPreferencesManager
        if (!prefs.reminderEnabled) return

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, prefs.reminderTimeHour)
            set(Calendar.MINUTE, prefs.reminderTimeMinute)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        val initialDelay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<DailyPromptWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_prompt_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
