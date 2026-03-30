package com.dcmoote.inkwell.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.dcmoote.inkwell.util.ReminderScheduler

// AlarmManager alarms are wiped when the device reboots. This receiver listens for
// BOOT_COMPLETED and re-schedules the reminder so it survives device restarts.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = UserPreferencesManager(context)
            if (prefs.reminderEnabled) {
                ReminderScheduler.schedule(context, prefs.reminderTimeHour, prefs.reminderTimeMinute)
            }
        }
    }
}
