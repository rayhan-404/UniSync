package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.AudioNoteEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.data.local.entities.CommunityPostEntity
import com.example.data.local.entities.CommunityReplyEntity
import com.example.data.local.entities.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_items ORDER BY startTime ASC")
    fun getAllRoutines(): Flow<List<ClassRoutineEntity>>

    @Query("SELECT * FROM routine_items WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getRoutinesByDay(day: String): Flow<List<ClassRoutineEntity>>

    @Query("SELECT * FROM routine_items WHERE dayOfWeek = :day ORDER BY startTime ASC")
    suspend fun getRoutinesByDaySync(day: String): List<ClassRoutineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: ClassRoutineEntity): Long

    @Update
    suspend fun updateRoutine(item: ClassRoutineEntity)

    @Delete
    suspend fun deleteRoutine(item: ClassRoutineEntity)

    @Query("DELETE FROM routine_items WHERE id = :id")
    suspend fun deleteRoutineById(id: Int)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM academic_tasks ORDER BY isCompleted ASC, dueDateEpochMs ASC")
    fun getAllTasks(): Flow<List<AcademicTaskEntity>>

    @Query("SELECT * FROM academic_tasks WHERE taskType = :type ORDER BY dueDateEpochMs ASC")
    fun getTasksByType(type: String): Flow<List<AcademicTaskEntity>>

    @Query("SELECT * FROM academic_tasks WHERE isCompleted = 0 ORDER BY dueDateEpochMs ASC")
    suspend fun getPendingTasksSync(): List<AcademicTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AcademicTaskEntity): Long

    @Update
    suspend fun updateTask(task: AcademicTaskEntity)

    @Query("UPDATE academic_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateTaskCompletion(id: Int, completed: Boolean)

    @Delete
    suspend fun deleteTask(task: AcademicTaskEntity)

    @Query("DELETE FROM academic_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}

@Dao
interface AudioNoteDao {
    @Query("SELECT * FROM audio_notes ORDER BY createdAt DESC")
    fun getAllAudioNotes(): Flow<List<AudioNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioNote(note: AudioNoteEntity): Long

    @Delete
    suspend fun deleteAudioNote(note: AudioNoteEntity)

    @Query("DELETE FROM audio_notes WHERE id = :id")
    suspend fun deleteAudioNoteById(id: Int)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_replies WHERE postId = :postId ORDER BY createdAt ASC")
    fun getRepliesForPost(postId: Int): Flow<List<CommunityReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: CommunityReplyEntity): Long

    @Query("UPDATE community_posts SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvotePost(id: Int)

    @Query("UPDATE community_posts SET solutionCount = solutionCount + 1 WHERE id = :id")
    suspend fun incrementReplyCount(id: Int)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM notification_settings WHERE id = 1")
    fun getSettings(): Flow<NotificationSettingsEntity?>

    @Query("SELECT * FROM notification_settings WHERE id = 1")
    suspend fun getSettingsSync(): NotificationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: NotificationSettingsEntity)
}