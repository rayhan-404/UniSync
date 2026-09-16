package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClassRoutineEntity::class,
        AcademicTaskEntity::class,
        AudioNoteEntity::class,
        CommunityPostEntity::class,
        CommunityReplyEntity::class,
        NotificationSettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun taskDao(): TaskDao
    abstract fun audioNoteDao(): AudioNoteDao
    abstract fun communityDao(): CommunityDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "varsity_routine_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            // Initial Notification Settings
            db.settingsDao().insertOrUpdateSettings(
                NotificationSettingsEntity(
                    id = 1,
                    dailyMorningAlert = true,
                    dailyMorningTime = "07:00",
                    classLeadMinutes = 5,
                    ctReminder1Day = true,
                    ctReminder12Hours = true,
                    soundEnabled = true
                )
            )

            // Initial Class Routines (Sample University Schedule)
            val initialRoutines = listOf(
                ClassRoutineEntity(
                    dayOfWeek = "Monday",
                    subjectName = "Data Structures & Algorithms",
                    subjectCode = "CSE-2101",
                    teacherName = "Dr. Raihan Sir",
                    roomNo = "Room 304",
                    startTime = "09:00",
                    endTime = "10:15",
                    building = "CS Building",
                    notes = "Bring laptop for live coding lab"
                ),
                ClassRoutineEntity(
                    dayOfWeek = "Monday",
                    subjectName = "Database Management Systems",
                    subjectCode = "CSE-2103",
                    teacherName = "Prof. Anisur Rahman",
                    roomNo = "Lab 201",
                    startTime = "10:30",
                    endTime = "12:00",
                    building = "CS Building",
                    notes = "SQL Queries practice"
                ),
                ClassRoutineEntity(
                    dayOfWeek = "Tuesday",
                    subjectName = "Object Oriented Programming",
                    subjectCode = "CSE-2105",
                    teacherName = "Dr. Raihan Sir",
                    roomNo = "Room 402",
                    startTime = "09:30",
                    endTime = "11:00",
                    building = "Engineering Complex",
                    notes = "Java & Kotlin inheritance concepts"
                ),
                ClassRoutineEntity(
                    dayOfWeek = "Wednesday",
                    subjectName = "Digital Logic Design",
                    subjectCode = "EEE-2101",
                    teacherName = "Engr. Fatima Tuz Johra",
                    roomNo = "Room 105",
                    startTime = "11:00",
                    endTime = "12:30",
                    building = "Science Building",
                    notes = "K-Map simplification discussion"
                ),
                ClassRoutineEntity(
                    dayOfWeek = "Thursday",
                    subjectName = "Computer Architecture",
                    subjectCode = "CSE-2107",
                    teacherName = "Dr. Raihan Sir",
                    roomNo = "Room 304",
                    startTime = "09:00",
                    endTime = "10:30",
                    building = "CS Building",
                    notes = "Assembly instruction sets"
                )
            )
            initialRoutines.forEach { db.routineDao().insertRoutine(it) }

            // Initial Academic Tasks (CTs, Assignments, Labs)
            val now = System.currentTimeMillis()
            val dayInMs = 24 * 60 * 60 * 1000L
            val initialTasks = listOf(
                AcademicTaskEntity(
                    title = "Class Test 01 - Tree & Graph Traversal",
                    subjectCode = "CSE-2101",
                    taskType = "CT",
                    dueDateEpochMs = now + dayInMs * 2,
                    details = "Topics: BFS, DFS, Binary Search Tree, AVL Tree. 20 Marks.",
                    isCompleted = false
                ),
                AcademicTaskEntity(
                    title = "DBMS Lab Report 03 - Complex SQL Joins",
                    subjectCode = "CSE-2103",
                    taskType = "Lab Report",
                    dueDateEpochMs = now + dayInMs * 3,
                    details = "Submit printed copy with ER Diagram & DDL queries",
                    isCompleted = false
                ),
                AcademicTaskEntity(
                    title = "OOP Assignment - Banking System Architecture",
                    subjectCode = "CSE-2105",
                    taskType = "Assignment",
                    dueDateEpochMs = now + dayInMs * 5,
                    details = "Implement Polymorphism & Abstract Classes in Kotlin",
                    isCompleted = false
                )
            )
            initialTasks.forEach { db.taskDao().insertTask(it) }

            // Initial Audio Notes Sample
            db.audioNoteDao().insertAudioNote(
                AudioNoteEntity(
                    title = "Lecture 14: Graph Algorithms Summary",
                    subjectName = "CSE-2101 - Algorithms",
                    durationSeconds = 184,
                    transcript = "Today Raihan sir discussed Dijkstra's shortest path algorithm. Key time complexity is O((V + E) log V) using priority queue. Sir hinted that Dijkstra pseudocode will be in CT 1.",
                    summaryContent = "### Key Takeaways:\n- **Algorithm**: Dijkstra's Shortest Path\n- **Time Complexity**: O((V + E) log V)\n- **Data Structure**: Priority Queue (Min-Heap)\n- ⚠️ **Exam Tip**: Pseudocode & trace table guaranteed for CT 1."
                )
            )

            // Initial Community Posts & Replies
            val postId1 = db.communityDao().insertPost(
                CommunityPostEntity(
                    authorName = "Tahmid Hasan",
                    authorRole = "3rd Year CSE",
                    title = "How to optimize Dijkstra's algorithm for dense graphs?",
                    categoryTag = "CSE-2101",
                    content = "In today's lecture Raihan sir mentioned dense graphs use adjacency matrix instead of min-heap. Can someone explain why array lookup is faster than priority queue when E = V^2?",
                    solutionCount = 2,
                    upvotes = 8
                )
            )
            db.communityDao().insertReply(
                CommunityReplyEntity(
                    postId = postId1.toInt(),
                    authorName = "Raihan (Teaching Assistant)",
                    authorRole = "Graduate TA",
                    content = "For dense graphs where E ~ V^2, binary heap gives O(V^2 log V), but using an un-indexed array to search min vertex gives O(V^2) total time because decreasing key is O(1) in array!",
                    rating = 5.0f
                )
            )
            db.communityDao().insertReply(
                CommunityReplyEntity(
                    postId = postId1.toInt(),
                    authorName = "Sabrina Akter",
                    authorRole = "2nd Year CSE",
                    content = "Clear explanation! Fibonacci heaps can also achieve O(E + V log V) in theoretical bounds.",
                    rating = 4.8f
                )
            )
        }
    }
}