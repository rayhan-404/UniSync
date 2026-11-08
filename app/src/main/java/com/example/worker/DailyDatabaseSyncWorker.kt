package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.notification.AlarmScheduler
import com.example.notification.NotificationHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class DailyDatabaseSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting daily database sync worker...")
        return try {
            val db = FirebaseFirestore.getInstance(
                applicationContext.getString(R.string.firestore_database_id)
            )
            val roomDb = AppDatabase.getDatabase(applicationContext)
            val routineDao = roomDb.routineDao()
            val taskDao = roomDb.taskDao()

            // 1. Sync Shared Routines from Firestore
            val routineSnapshot = db.collection("routines").get().await()
            var routineCount = 0
            for (doc in routineSnapshot.documents) {
                val day = doc.getString("dayOfWeek") ?: continue
                val subjectName = doc.getString("subjectName") ?: continue
                val subjectCode = doc.getString("subjectCode") ?: continue
                val startTime = doc.getString("startTime") ?: "09:00"
                val endTime = doc.getString("endTime") ?: "10:15"
                val teacherName = doc.getString("teacherName") ?: ""
                val roomNo = doc.getString("roomNo") ?: ""
                val building = doc.getString("building") ?: "Academic Building"
                val notes = doc.getString("notes") ?: ""
                val authorRole = doc.getString("authorRole") ?: "CR"
                val authorRoll = doc.getString("authorRoll") ?: ""

                val entity = ClassRoutineEntity(
                    id = (doc.id.hashCode() and 0x7FFFFFFF) % 100000 + 1000,
                    dayOfWeek = day,
                    subjectName = subjectName,
                    subjectCode = subjectCode,
                    teacherName = if (authorRoll.isNotBlank()) "$teacherName ($authorRole #$authorRoll)" else teacherName,
                    roomNo = roomNo,
                    startTime = startTime,
                    endTime = endTime,
                    building = building,
                    notes = notes,
                    isNotificationEnabled = true,
                    customTimerMinutes = 10
                )
                routineDao.insertRoutine(entity)
                AlarmScheduler.scheduleClassReminder(applicationContext, entity, 10)
                routineCount++
            }

            // 2. Sync Shared Tasks & CTs from Firestore
            val taskSnapshot = db.collection("tasks").get().await()
            var taskCount = 0
            for (doc in taskSnapshot.documents) {
                val title = doc.getString("title") ?: continue
                val subjectCode = doc.getString("subjectCode") ?: continue
                val taskType = doc.getString("taskType") ?: "CT"
                val dueDateMs = doc.getLong("dueDateEpochMs") ?: (System.currentTimeMillis() + 86400000L)
                val details = doc.getString("details") ?: ""

                val taskEntity = AcademicTaskEntity(
                    id = (doc.id.hashCode() and 0x7FFFFFFF) % 100000 + 1000,
                    title = title,
                    subjectCode = subjectCode,
                    taskType = taskType,
                    dueDateEpochMs = dueDateMs,
                    details = details,
                    isCompleted = false
                )
                taskDao.insertTask(taskEntity)
                AlarmScheduler.scheduleTaskReminders(
                    context = applicationContext,
                    task = taskEntity,
                    remind1Day = true,
                    remind12Hours = true
                )
                taskCount++
            }

            Log.d(TAG, "Daily sync complete: $routineCount routines & $taskCount tasks updated.")

            // Send daily sync completed push notification
            NotificationHelper.sendNotification(
                context = applicationContext,
                notificationId = 8888,
                channelId = NotificationHelper.CHANNEL_DAILY,
                title = "🔄 Daily Database Auto-Sync",
                message = "Synced $routineCount class schedules and $taskCount CTs/tasks for today."
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Daily database sync worker failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DailyDbSyncWorker"
        const val WORK_NAME = "UniSyncDailyDatabaseSyncWork"

        fun scheduleDailySync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<DailyDatabaseSyncWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
            Log.d(TAG, "Enqueued 24h periodic daily database sync work.")
        }
    }
}