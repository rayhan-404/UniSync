package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {

    const val CHANNEL_CLASSES = "channel_classes"
    const val CHANNEL_TASKS = "channel_tasks"
    const val CHANNEL_DAILY = "channel_daily"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // Long vibration pattern: 1s vibrate, 0.5s pause, repeated
            val longVibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000)

            // Pre-Class Alert Channel
            val classChannel = NotificationChannel(
                CHANNEL_CLASSES,
                "Upcoming Class Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies a few minutes before class starts"
                enableVibration(true)
                vibrationPattern = longVibrationPattern
                setSound(soundUri, audioAttributes)
            }

            // Task / CT Alert Channel
            val taskChannel = NotificationChannel(
                CHANNEL_TASKS,
                "CT & Assignment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies 1 day & 12h before CTs and Assignments"
                enableVibration(true)
                vibrationPattern = longVibrationPattern
                setSound(soundUri, audioAttributes)
            }

            // Daily Morning Digest Channel
            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY,
                "Daily Morning Schedule Digest",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Sends daily morning summary of classes and tasks"
            }

            notificationManager.createNotificationChannel(classChannel)
            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(dailyChannel)
        }
    }

    fun sendNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val longVibe = longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmUri)
            .setVibrate(longVibe)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())

        // Explicit physical vibration trigger
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            }
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        android.os.VibrationEffect.createWaveform(
                            longArrayOf(0, 600, 200, 600, 200, 600),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 600, 200, 600, 200, 600), -1)
                }
            }
        } catch (_: Exception) {}
    }
}