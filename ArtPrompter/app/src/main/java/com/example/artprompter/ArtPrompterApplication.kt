package com.dcmoote.inkwell

import android.app.Application
import androidx.work.WorkManager
import com.dcmoote.inkwell.util.AppContainer
import com.dcmoote.inkwell.util.NotificationHelper

// Application entry point. Runs once when the process starts — before any Activity or receiver.
class InkwellApplication : Application() {

    // Holds all app-wide dependencies (database, prefs, repository, billing).
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Create the notification channel early so it exists before any alarm fires.
        NotificationHelper.createChannel(this)
        // Cancel legacy WorkManager job — reminders are now handled exclusively by AlarmManager.
        WorkManager.getInstance(this).cancelUniqueWork("daily_prompt_reminder")
    }
}
