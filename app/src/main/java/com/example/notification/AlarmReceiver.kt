package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(AlarmScheduler.EXTRA_NOTIFICATION_ID, 101)
        val channelId = intent.getStringExtra(AlarmScheduler.EXTRA_CHANNEL_ID) ?: NotificationHelper.CHANNEL_CLASSES
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_TITLE) ?: "Varsity Routine Alert"
        val message = intent.getStringExtra(AlarmScheduler.EXTRA_MESSAGE) ?: "You have an upcoming academic schedule."

        NotificationHelper.createNotificationChannels(context)
        NotificationHelper.sendNotification(context, notificationId, channelId, title, message)
    }
}