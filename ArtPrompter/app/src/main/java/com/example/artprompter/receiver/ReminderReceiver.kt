package com.dcmoote.inkwell.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.dcmoote.inkwell.util.NotificationHelper
import com.dcmoote.inkwell.util.ReminderScheduler

// Fired by AlarmManager at the user's scheduled reminder time.
// Shows the notification, then re-schedules the alarm for the same time tomorrow.
// AlarmManager alarms are one-shot, so rescheduling here is what makes the reminder repeat daily.
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showPromptNotification(context)
        val prefs = UserPreferencesManager(context)
        if (prefs.reminderEnabled) {
            ReminderScheduler.schedule(context, prefs.reminderTimeHour, prefs.reminderTimeMinute)
        }
    }
}
