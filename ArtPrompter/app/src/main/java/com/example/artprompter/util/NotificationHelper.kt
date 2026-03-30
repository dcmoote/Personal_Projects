package com.dcmoote.inkwell.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dcmoote.inkwell.MainActivity
import com.dcmoote.inkwell.R

// Handles notification channel setup and posting the daily prompt notification.
// The channel must be created before any notification is posted — this is done in Application.onCreate().
object NotificationHelper {

    const val CHANNEL_ID = "daily_prompt"
    private const val NOTIFICATION_ID = 1001

    // Creates the notification channel. Safe to call multiple times — Android ignores duplicates.
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Prompt",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Your daily creative prompt reminder"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    // Posts the reminder notification. Tapping it opens MainActivity.
    // The prompt content is intentionally not shown here — users discover it inside the app.
    fun showPromptNotification(context: Context) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time to create!")
            .setContentText("Your daily prompt is ready. Tap to get inspired.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}
