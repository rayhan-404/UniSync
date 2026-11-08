package com.example.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class UniSyncFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        subscribeToDefaultTopics()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "🔔 UniSync Notice"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: remoteMessage.data["body"]
            ?: "New schedule or academic task update received!"

        val notificationType = remoteMessage.data["type"] ?: "GENERAL"
        val channelId = when (notificationType.uppercase()) {
            "ROUTINE", "CLASS" -> NotificationHelper.CHANNEL_CLASSES
            "TASK", "CT", "ASSIGNMENT" -> NotificationHelper.CHANNEL_TASKS
            else -> NotificationHelper.CHANNEL_DAILY
        }

        val notificationId = (System.currentTimeMillis() % 100000).toInt() + 5000

        // Send high priority push notification to student device
        NotificationHelper.sendNotification(
            context = applicationContext,
            notificationId = notificationId,
            channelId = channelId,
            title = title,
            message = body
        )
    }

    companion object {
        private const val TAG = "UniSyncFCM"

        fun subscribeToDefaultTopics(batchName: String = "Batch-54") {
            try {
                val cleanBatch = batchName.replace(" ", "_").replace("-", "_").lowercase()
                FirebaseMessaging.getInstance().subscribeToTopic("all_students")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic: all_students")
                        }
                    }

                FirebaseMessaging.getInstance().subscribeToTopic("batch_$cleanBatch")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic: batch_$cleanBatch")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error subscribing to FCM topics: ${e.message}")
            }
        }
    }
}