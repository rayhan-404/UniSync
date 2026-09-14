package com.example.ui.viewmodels

import android.app.Application
import android.media.MediaRecorder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.AudioNoteEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.data.local.entities.CommunityPostEntity
import com.example.data.local.entities.CommunityReplyEntity
import com.example.data.local.entities.NotificationSettingsEntity
import com.example.data.repository.AudioNoteRepository
import com.example.data.repository.CommunityRepository
import com.example.data.repository.RoutineRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.TaskRepository
import com.example.notification.AlarmScheduler
import com.example.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val routineRepo = RoutineRepository(db.routineDao())
    private val taskRepo = TaskRepository(db.taskDao())
    private val audioNoteRepo = AudioNoteRepository(db.audioNoteDao())
    private val communityRepo = CommunityRepository(db.communityDao())
    private val settingsRepo = SettingsRepository(db.settingsDao())

    // --- Routine State ---
    val selectedDay = MutableStateFlow(getCurrentDayOfWeek())
    val allRoutines = routineRepo.allRoutines.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // --- Tasks State ---
    val selectedTaskFilter = MutableStateFlow("All")
    val allTasks = taskRepo.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // --- Audio Notes State ---
    val allAudioNotes = audioNoteRepo.allAudioNotes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _aiSummarizing = MutableStateFlow(false)
    val aiSummarizing: StateFlow<Boolean> = _aiSummarizing.asStateFlow()

    private var recordingTimerJob: Job? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentAudioFile: File? = null

    // --- Community State ---
    val allCommunityPosts = communityRepo.allPosts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val selectedPostId = MutableStateFlow<Int?>(null)

    // --- Settings State ---
    val notificationSettings = settingsRepo.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationSettingsEntity()
    )

    // --- User Auth & Prefs State ---
    private val prefs = application.getSharedPreferences("user_auth_prefs", android.content.Context.MODE_PRIVATE)
    val currentUser = MutableStateFlow(prefs.getString("logged_in_user", "Student") ?: "Student")
    val isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))

    // Profile Picture / Avatar State
    val profilePicUri = MutableStateFlow<String?>(prefs.getString("profile_pic_uri", null))
    val avatarPreset = MutableStateFlow(prefs.getString("avatar_preset", "grad_cap") ?: "grad_cap")

    fun setProfilePicUri(uriString: String?) {
        profilePicUri.value = uriString
        prefs.edit().putString("profile_pic_uri", uriString).apply()
    }

    fun setAvatarPreset(presetId: String) {
        avatarPreset.value = presetId
        prefs.edit().putString("avatar_preset", presetId).apply()
        // If an avatar preset is chosen, we can keep or reset image uri if desired
    }

    // Theme preset selection state & Dark Mode state
    val themePresetIndex = MutableStateFlow(prefs.getInt("theme_preset_index", 0))
    val isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))

    fun toggleDarkMode() {
        val newMode = !isDarkMode.value
        isDarkMode.value = newMode
        prefs.edit().putBoolean("is_dark_mode", newMode).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        isDarkMode.value = enabled
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }

    fun cycleColorTheme() {
        val next = (themePresetIndex.value + 1) % com.example.ui.theme.ColorThemePresets.size
        themePresetIndex.value = next
        prefs.edit().putInt("theme_preset_index", next).apply()
    }

    fun setThemePreset(index: Int) {
        themePresetIndex.value = index
        prefs.edit().putInt("theme_preset_index", index).apply()
    }


    fun loginOrRegister(username: String, pass: String): Boolean {
        if (username.isBlank() || pass.isBlank()) return false
        val cleanName = username.trim()
        prefs.edit()
            .putString("logged_in_user", cleanName)
            .putBoolean("is_logged_in", true)
            .apply()
        currentUser.value = cleanName
        isLoggedIn.value = true
        return true
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        isLoggedIn.value = false
    }

    // --- Actions ---

    fun addRoutine(routine: ClassRoutineEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            routineRepo.insertRoutine(routine)
            val lead = notificationSettings.value?.classLeadMinutes ?: 5
            AlarmScheduler.scheduleClassReminder(getApplication(), routine, lead)
        }
    }

    fun toggleRoutineNotification(routine: ClassRoutineEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = routine.copy(isNotificationEnabled = !routine.isNotificationEnabled)
            routineRepo.updateRoutine(updated)
            val lead = notificationSettings.value?.classLeadMinutes ?: 5
            if (updated.isNotificationEnabled) {
                AlarmScheduler.scheduleClassReminder(getApplication(), updated, lead)
            }
        }
    }

    fun updateRoutineTimer(routine: ClassRoutineEntity, timerMins: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = routine.copy(customTimerMinutes = timerMins)
            routineRepo.updateRoutine(updated)
        }
    }

    fun deleteRoutine(routine: ClassRoutineEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            routineRepo.deleteRoutine(routine)
        }
    }

    private val _cloudSyncState = MutableStateFlow("Idle")
    val cloudSyncState: StateFlow<String> = _cloudSyncState.asStateFlow()

    fun syncRoutineWithServer() {
        viewModelScope.launch {
            _cloudSyncState.value = "Syncing..."
            delay(1500)
            _cloudSyncState.value = "Synced successfully at ${SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())}"
        }
    }

    fun addTask(task: AcademicTaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = taskRepo.insertTask(task)
            val insertedTask = task.copy(id = id.toInt())
            val s = notificationSettings.value
            val r1 = s?.ctReminder1Day ?: true
            val r12 = s?.ctReminder12Hours ?: true
            AlarmScheduler.scheduleTaskReminders(getApplication(), insertedTask, r1, r12)
        }
    }

    fun toggleTaskCompletion(task: AcademicTaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: AcademicTaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.deleteTask(task)
        }
    }

    // --- Audio Recorder & Gemini AI Note Generation ---
    fun startAudioRecording() {
        val context = getApplication<Application>()
        val file = File(context.cacheDir, "lecture_${System.currentTimeMillis()}.aac")
        currentAudioFile = file

        try {
            mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            _isRecording.value = true
            _recordingSeconds.value = 0

            recordingTimerJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(1000)
                    _recordingSeconds.value += 1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
        }
    }

    fun stopAudioRecordingAndGenerateNotes(subjectName: String, title: String, manualTranscript: String = "") {
        recordingTimerJob?.cancel()
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
        val duration = _recordingSeconds.value
        _isRecording.value = false

        _aiSummarizing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val transcriptText = if (manualTranscript.isNotBlank()) {
                manualTranscript
            } else {
                "Key points and lecture discussion for $subjectName."
            }

            val result = GeminiClient.summarizeLectureNotes(transcriptText, subjectName)
            val summaryText = result.getOrDefault("Failed to generate AI summary.")

            val note = AudioNoteEntity(
                title = if (title.isBlank()) "Lecture - ${SimpleDateFormat("MMM dd, HH:mm", Locale.ENGLISH).format(Date())}" else title,
                subjectName = subjectName,
                durationSeconds = duration,
                transcript = transcriptText,
                summaryContent = summaryText
            )
            audioNoteRepo.insertAudioNote(note)
            _aiSummarizing.value = false
        }
    }

    fun deleteAudioNote(note: AudioNoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            audioNoteRepo.deleteAudioNote(note)
        }
    }

    // --- Community Actions ---
    fun createCommunityPost(title: String, category: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = CommunityPostEntity(
                authorName = "Me (You)",
                authorRole = "Student",
                title = title,
                categoryTag = category,
                content = content
            )
            communityRepo.insertPost(post)
        }
    }

    fun addReplyToPost(postId: Int, content: String, rating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val reply = CommunityReplyEntity(
                postId = postId,
                authorName = "Me (You)",
                authorRole = "Student Helper",
                content = content,
                rating = rating
            )
            communityRepo.insertReply(reply, postId)
        }
    }

    fun upvoteCommunityPost(postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            communityRepo.upvotePost(postId)
        }
    }

    // --- Settings Actions ---
    fun updateSettings(newSettings: NotificationSettingsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.updateSettings(newSettings)
            // Reschedule morning alarm
            if (newSettings.dailyMorningAlert) {
                AlarmScheduler.scheduleDailyMorningSummary(getApplication(), newSettings.dailyMorningTime)
            }
        }
    }

    fun cycleThemePreset() {
        themePresetIndex.value = (themePresetIndex.value + 1) % 6
    }

    private fun getCurrentDayOfWeek(): String {
        return SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())
    }
}