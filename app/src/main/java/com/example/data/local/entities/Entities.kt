package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_items")
data class ClassRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: String, // e.g., "Monday", "Tuesday", etc.
    val subjectName: String,
    val subjectCode: String,
    val teacherName: String,
    val roomNo: String,
    val startTime: String, // HH:mm format, e.g. "09:00"
    val endTime: String,   // HH:mm format, e.g. "10:15"
    val building: String = "Academic Building",
    val notes: String = "",
    val isNotificationEnabled: Boolean = true,
    val customTimerMinutes: Int = 5
)

@Entity(tableName = "academic_tasks")
data class AcademicTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectCode: String,
    val taskType: String, // "CT", "Assignment", "Lab Report", "Class Task", "Quiz", "Viva"
    val dueDateEpochMs: Long,
    val details: String = "",
    val isCompleted: Boolean = false,
    val customReminderEpochMs: Long? = null,
    val selectedRemindersCsv: String = "1d,12h", // Custom reminder timings like "1d,2d,12h,5h"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audio_notes")
data class AudioNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectName: String,
    val durationSeconds: Int,
    val transcript: String,
    val summaryContent: String, // Structured notes from Gemini AI
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorRole: String = "Student",
    val title: String,
    val categoryTag: String, // e.g., "CSE-3101", "Lab Help", "Math", "General"
    val content: String,
    val solutionCount: Int = 0,
    val upvotes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_replies")
data class CommunityReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorRole: String = "Peer Helper",
    val content: String,
    val rating: Float = 5.0f, // 1 to 5 star rating given by peers
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notification_settings")
data class NotificationSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dailyMorningAlert: Boolean = true,
    val dailyMorningTime: String = "07:00",
    val morningTimesCsv: String = "06:30,07:00,07:30", // Multiple morning reminder times!
    val classLeadMinutes: Int = 5, // Pre-class alert minutes slider
    val ctReminder1Day: Boolean = true, // 24h before CT
    val ctReminder12Hours: Boolean = true, // 12h before CT
    val ctCustomRemindersCsv: String = "1d,2d,12h,5h", // Custom timings for CT/Assignment/Lab
    val ctAlertSlidersCsv: String = "24", // Dynamic slider values (in hours) for CT
    val otherTasksAlertSlidersCsv: String = "48", // Dynamic slider values (in hours) for Assignment, Lab & Class Task
    val soundEnabled: Boolean = true
)
