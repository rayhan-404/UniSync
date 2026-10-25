package com.example.data.repository

import com.example.data.local.dao.AudioNoteDao
import com.example.data.local.dao.CommunityDao
import com.example.data.local.dao.RoutineDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.AudioNoteEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.data.local.entities.CommunityPostEntity
import com.example.data.local.entities.CommunityReplyEntity
import com.example.data.local.entities.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {
    val allRoutines: Flow<List<ClassRoutineEntity>> = routineDao.getAllRoutines()

    fun getRoutinesByDay(day: String): Flow<List<ClassRoutineEntity>> =
        routineDao.getRoutinesByDay(day)

    suspend fun insertRoutine(item: ClassRoutineEntity): Long =
        routineDao.insertRoutine(item)

    suspend fun updateRoutine(item: ClassRoutineEntity) =
        routineDao.updateRoutine(item)

    suspend fun deleteRoutine(item: ClassRoutineEntity) =
        routineDao.deleteRoutine(item)
}

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<AcademicTaskEntity>> = taskDao.getAllTasks()

    fun getTasksByType(type: String): Flow<List<AcademicTaskEntity>> =
        taskDao.getTasksByType(type)

    suspend fun insertTask(task: AcademicTaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: AcademicTaskEntity) =
        taskDao.updateTask(task)

    suspend fun updateTaskCompletion(id: Int, completed: Boolean) =
        taskDao.updateTaskCompletion(id, completed)

    suspend fun deleteTask(task: AcademicTaskEntity) =
        taskDao.deleteTask(task)
}

class AudioNoteRepository(private val audioNoteDao: AudioNoteDao) {
    val allAudioNotes: Flow<List<AudioNoteEntity>> = audioNoteDao.getAllAudioNotes()

    suspend fun insertAudioNote(note: AudioNoteEntity): Long =
        audioNoteDao.insertAudioNote(note)

    suspend fun deleteAudioNote(note: AudioNoteEntity) =
        audioNoteDao.deleteAudioNote(note)
}

class CommunityRepository(private val communityDao: CommunityDao) {
    val allPosts: Flow<List<CommunityPostEntity>> = communityDao.getAllPosts()

    fun getRepliesForPost(postId: Int): Flow<List<CommunityReplyEntity>> =
        communityDao.getRepliesForPost(postId)

    suspend fun insertPost(post: CommunityPostEntity): Long =
        communityDao.insertPost(post)

    suspend fun insertReply(reply: CommunityReplyEntity, postId: Int): Long {
        communityDao.incrementReplyCount(postId)
        return communityDao.insertReply(reply)
    }

    suspend fun upvotePost(id: Int) = communityDao.upvotePost(id)
}

class SettingsRepository(private val settingsDao: SettingsDao) {
    val settings: Flow<NotificationSettingsEntity?> = settingsDao.getSettings()

    suspend fun updateSettings(settingsEntity: NotificationSettingsEntity) =
        settingsDao.insertOrUpdateSettings(settingsEntity)
}