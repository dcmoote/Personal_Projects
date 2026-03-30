package com.dcmoote.inkwell.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dcmoote.inkwell.MainActivity
import com.dcmoote.inkwell.receiver.ReminderReceiver
import java.util.Calendar

object ReminderScheduler {

    private const val REQUEST_CODE = 1002

    fun schedule(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val operation = buildBroadcastIntent(context)
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }
        // setAlarmClock requires SCHEDULE_EXACT_ALARM on API 31+. Fall back to
        // setAndAllowWhileIdle (still Doze-exempt, just not pinned to the clock UI)
        // if the permission hasn't been granted yet.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                operation
            )
            return
        }
        // showIntent must be an activity PendingIntent — used by the system clock UI
        val showIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(target.timeInMillis, showIntent),
            operation
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildBroadcastIntent(context))
    }

    private fun buildBroadcastIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
