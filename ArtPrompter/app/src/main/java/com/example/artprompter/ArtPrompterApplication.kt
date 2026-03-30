package com.dcmoote.inkwell

import android.app.Application
import androidx.work.WorkManager
import com.dcmoote.inkwell.util.AppContainer
import com.dcmoote.inkwell.util.NotificationHelper

class InkwellApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
        // Cancel legacy WorkManager job — reminders are now handled exclusively by AlarmManager.
        WorkManager.getInstance(this).cancelUniqueWork("daily_prompt_reminder")
    }
}
