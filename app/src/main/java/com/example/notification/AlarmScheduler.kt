package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.ClassRoutineEntity
import java.util.Calendar

object AlarmScheduler {

    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_CHANNEL_ID = "extra_channel_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"

    fun scheduleClassReminder(
        context: Context,
        routine: ClassRoutineEntity,
        leadMinutes: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val targetCalendar = getNextOccurrenceCalendar(routine.dayOfWeek, routine.startTime, leadMinutes)
            ?: return

        val title = "⏰ Upcoming Class in $leadMinutes mins!"
        val message = "${leadMinutes} minute por ${routine.teacherName} er ${routine.subjectCode} (${routine.subjectName}) class ache in ${routine.roomNo}."

        val requestCode = 1000 + routine.id
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            putExtra(EXTRA_CHANNEL_ID, NotificationHelper.CHANNEL_CLASSES)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(alarmManager, targetCalendar.timeInMillis, pendingIntent)
    }

    fun scheduleTaskReminders(
        context: Context,
        task: AcademicTaskEntity,
        remind1Day: Boolean = true,
        remind12Hours: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val now = System.currentTimeMillis()

        // 1 Day (24 hours) Before CT/Task
        if (remind1Day) {
            val oneDayBefore = task.dueDateEpochMs - (24 * 60 * 60 * 1000L)
            if (oneDayBefore > now) {
                val reqCode = 20000 + task.id * 10 + 1
                val title = "📅 ${task.taskType} Reminder (1 Day Left)"
                val message = "${task.title} (${task.subjectCode}) is due tomorrow!"
                val intent = createAlarmIntent(context, reqCode, NotificationHelper.CHANNEL_TASKS, title, message)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                scheduleAlarm(alarmManager, oneDayBefore, pendingIntent)
            }
        }

        // 12 Hours Before CT/Task
        if (remind12Hours) {
            val twelveHoursBefore = task.dueDateEpochMs - (12 * 60 * 60 * 1000L)
            if (twelveHoursBefore > now) {
                val reqCode = 20000 + task.id * 10 + 2
                val title = "⚠️ Urgent ${task.taskType} Reminder (12 Hours Left)"
                val message = "${task.title} (${task.subjectCode}) is due in 12 hours!"
                val intent = createAlarmIntent(context, reqCode, NotificationHelper.CHANNEL_TASKS, title, message)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                scheduleAlarm(alarmManager, twelveHoursBefore, pendingIntent)
            }
        }

        // Custom Specified Reminder Time
        task.customReminderEpochMs?.let { customTime ->
            if (customTime > now) {
                val reqCode = 20000 + task.id * 10 + 3
                val title = "🔔 Custom Reminder: ${task.taskType}"
                val message = "${task.title} (${task.subjectCode}) scheduled reminder."
                val intent = createAlarmIntent(context, reqCode, NotificationHelper.CHANNEL_TASKS, title, message)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                scheduleAlarm(alarmManager, customTime, pendingIntent)
            }
        }
    }

    fun scheduleDailyMorningSummary(context: Context, morningTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeParts = morningTime.split(":")
        if (timeParts.size != 2) return
        val hour = timeParts[0].toIntOrNull() ?: 7
        val minute = timeParts[1].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1) // Schedule for tomorrow morning if time already passed today
            }
        }

        val reqCode = 9999
        val title = "🌅 Today's Varsity Schedule Summary"
        val message = "Check your class timetable, CTs and pending tasks for today!"
        val intent = createAlarmIntent(context, reqCode, NotificationHelper.CHANNEL_DAILY, title, message)
        val pendingIntent = PendingIntent.getBroadcast(
            context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    private fun createAlarmIntent(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String
    ): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }
    }

    private fun scheduleAlarm(alarmManager: AlarmManager, triggerTimeMs: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing, falling back to inexact alarm: ${e.message}")
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error setting alarm: ${e.message}")
        }
    }

    private fun getNextOccurrenceCalendar(dayName: String, timeStr: String, leadMinutes: Int): Calendar? {
        val targetDay = when (dayName.lowercase()) {
            "sunday" -> Calendar.SUNDAY
            "monday" -> Calendar.MONDAY
            "tuesday" -> Calendar.TUESDAY
            "wednesday" -> Calendar.WEDNESDAY
            "thursday" -> Calendar.THURSDAY
            "friday" -> Calendar.FRIDAY
            "saturday" -> Calendar.SATURDAY
            else -> Calendar.MONDAY
        }

        val timeParts = timeStr.split(":")
        if (timeParts.size != 2) return null
        val hour = timeParts[0].toIntOrNull() ?: 9
        val minute = timeParts[1].toIntOrNull() ?: 0

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -leadMinutes) // Subtract lead time
        }

        // Advance to next matching day of week if past
        var attempts = 0
        while ((cal.get(Calendar.DAY_OF_WEEK) != targetDay || cal.timeInMillis <= System.currentTimeMillis()) && attempts < 7) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            attempts++
        }
        return cal
    }
}