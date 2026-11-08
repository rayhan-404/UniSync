package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationHelper.createNotificationChannels(context)

            val db = AppDatabase.getDatabase(context)
            CoroutineScope(Dispatchers.IO).launch {
                val settings = db.settingsDao().getSettingsSync() ?: return@launch
                val leadMinutes = settings.classLeadMinutes

                // Reschedule pre-class reminders for current day
                val todayStr = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())
                val todayClasses = db.routineDao().getRoutinesByDaySync(todayStr)
                todayClasses.forEach { routine ->
                    AlarmScheduler.scheduleClassReminder(context, routine, leadMinutes)
                }

                // Reschedule pending tasks
                val pendingTasks = db.taskDao().getPendingTasksSync()
                pendingTasks.forEach { task ->
                    AlarmScheduler.scheduleTaskReminders(
                        context,
                        task,
                        settings.ctReminder1Day,
                        settings.ctReminder12Hours
                    )
                }

                // Reschedule daily morning summary
                if (settings.dailyMorningAlert) {
                    AlarmScheduler.scheduleDailyMorningSummary(context, settings.dailyMorningTime)
                }

                // Schedule 24h background database auto-sync worker & FCM push topic subscription
                com.example.worker.DailyDatabaseSyncWorker.scheduleDailySync(context)
                UniSyncFirebaseMessagingService.subscribeToDefaultTopics()
            }
        }
    }
}