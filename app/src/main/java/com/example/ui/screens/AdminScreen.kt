package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.data.remote.SharedRoutineFirestore
import com.example.data.remote.SharedTaskFirestore
import com.example.data.remote.UserProfileFirestore
import com.example.ui.components.NeumorphicBadge
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicInsetSurface
import com.example.ui.components.NeumorphicTextField
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.DeepOrangeAccent
import com.example.ui.theme.DeepOrangeContainer
import com.example.ui.theme.DeepOrangePrimary
import com.example.ui.theme.DeepOrangeSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainText
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodels.MainViewModel

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val allUsers by viewModel.allRegisteredUsers.collectAsState()
    val allCloudRoutines by viewModel.allCloudRoutinesList.collectAsState()
    val allCloudTasks by viewModel.allCloudTasksList.collectAsState()
    val userRoll by viewModel.userRoll.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Roles & Roster, 1: Routines DB, 2: Tasks DB, 3: DB Tools
    var searchQuery by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("ALL") } // ALL, CR, ACR, STUDENT, ADMIN

    // Dialog States
    var showAddUserDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserProfileFirestore?>(null) }
    var showPurgeRoutinesConfirm by remember { mutableStateOf(false) }
    var showPurgeTasksConfirm by remember { mutableStateOf(false) }
    var showBroadcastRoutineDialog by remember { mutableStateOf(false) }
    var showBroadcastTaskDialog by remember { mutableStateOf(false) }

    val adminTabs = listOf(
        "👥 Roster & Roles",
        "📅 Routines DB",
        "📝 Tasks & CTs",
        "⚙️ DB Controls"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            // ADMIN HERO HEADER
            AdminHeroHeader(
                adminName = currentUser,
                adminRoll = userRoll,
                totalUsers = allUsers.size,
                totalRoutines = allCloudRoutines.size,
                totalTasks = allCloudTasks.size
            )

            // ADMIN CONTROL TABS
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DeepOrangePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                adminTabs.forEachIndexed { index, title ->
                    val isSelected = selectedAdminTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedAdminTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) DeepOrangePrimary else SecondaryText
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedAdminTab) {
                0 -> {
                    // TAB 1: USER ROSTER & CR / ACR / ROLL MANAGEMENT
                    RosterAndRolesTab(
                        users = allUsers,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        roleFilter = roleFilter,
                        onRoleFilterChange = { roleFilter = it },
                        onPromoteToCR = { user ->
                            viewModel.adminSetUserRoleAndRoll(
                                docId = user.id.ifBlank { user.roll },
                                name = user.name,
                                roll = user.roll,
                                role = "CR",
                                batch = user.batch
                            )
                            Toast.makeText(context, "${user.name} promoted to Class CR!", Toast.LENGTH_SHORT).show()
                        },
                        onPromoteToACR = { user ->
                            viewModel.adminSetUserRoleAndRoll(
                                docId = user.id.ifBlank { user.roll },
                                name = user.name,
                                roll = user.roll,
                                role = "ACR",
                                batch = user.batch
                            )
                            Toast.makeText(context, "${user.name} set as Assistant CR (ACR)!", Toast.LENGTH_SHORT).show()
                        },
                        onSetStudent = { user ->
                            viewModel.adminSetUserRoleAndRoll(
                                docId = user.id.ifBlank { user.roll },
                                name = user.name,
                                roll = user.roll,
                                role = "STUDENT",
                                batch = user.batch
                            )
                            Toast.makeText(context, "${user.name} role set to Student", Toast.LENGTH_SHORT).show()
                        },
                        onEditUser = { user -> editingUser = user },
                        onDeleteUser = { user ->
                            viewModel.adminDeleteUser(user.id.ifBlank { user.roll })
                            Toast.makeText(context, "User ${user.name} removed from roster", Toast.LENGTH_SHORT).show()
                        },
                        onAddUserClick = { showAddUserDialog = true }
                    )
                }
                1 -> {
                    // TAB 2: ROUTINES DATABASE
                    RoutinesDbTab(
                        routines = allCloudRoutines,
                        onDeleteRoutine = { routineId ->
                            viewModel.adminDeleteCloudRoutine(routineId)
                            Toast.makeText(context, "Routine deleted from cloud", Toast.LENGTH_SHORT).show()
                        },
                        onBroadcastClick = { showBroadcastRoutineDialog = true },
                        onPurgeClick = { showPurgeRoutinesConfirm = true }
                    )
                }
                2 -> {
                    // TAB 3: TASKS DATABASE
                    TasksDbTab(
                        tasks = allCloudTasks,
                        onDeleteTask = { taskId ->
                            viewModel.adminDeleteCloudTask(taskId)
                            Toast.makeText(context, "Task deleted from cloud", Toast.LENGTH_SHORT).show()
                        },
                        onBroadcastClick = { showBroadcastTaskDialog = true },
                        onPurgeClick = { showPurgeTasksConfirm = true }
                    )
                }
                3 -> {
                    // TAB 4: DATABASE SETTINGS & TOOLS
                    DatabaseToolsTab(
                        viewModel = viewModel,
                        userRoll = userRoll,
                        userRole = userRole
                    )
                }
            }
        }
    }

    // DIALOG: Add Student / CR to Database
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onSave = { name, roll, role, batch ->
                viewModel.adminAddStudentOrCR(name, roll, role, batch)
                showAddUserDialog = false
                Toast.makeText(context, "Added $name ($role #$roll) to Firestore", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // DIALOG: Edit Student Roll & Role
    editingUser?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { editingUser = null },
            onSave = { name, roll, role, batch ->
                viewModel.adminSetUserRoleAndRoll(
                    docId = user.id.ifBlank { user.roll },
                    name = name,
                    roll = roll,
                    role = role,
                    batch = batch
                )
                editingUser = null
                Toast.makeText(context, "Updated Roll #$roll ($role)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // DIALOG: Confirm Purge Routines
    if (showPurgeRoutinesConfirm) {
        AlertDialog(
            onDismissRequest = { showPurgeRoutinesConfirm = false },
            title = { Text("Purge All Cloud Routines?", fontWeight = FontWeight.Bold, color = ErrorRed) },
            text = { Text("This will permanently delete all shared routines from the Firestore database across all batches. Proceed?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.adminPurgeAllRoutines()
                        showPurgeRoutinesConfirm = false
                        Toast.makeText(context, "All cloud routines cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete Everything", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeRoutinesConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Confirm Purge Tasks
    if (showPurgeTasksConfirm) {
        AlertDialog(
            onDismissRequest = { showPurgeTasksConfirm = false },
            title = { Text("Purge All Cloud Tasks?", fontWeight = FontWeight.Bold, color = ErrorRed) },
            text = { Text("This will permanently delete all shared tasks, CTs, and assignments from the Firestore database. Proceed?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.adminPurgeAllTasks()
                        showPurgeTasksConfirm = false
                        Toast.makeText(context, "All cloud tasks cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete Everything", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeTasksConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Broadcast Admin Routine
    if (showBroadcastRoutineDialog) {
        AddAdminRoutineDialog(
            onDismiss = { showBroadcastRoutineDialog = false },
            onBroadcast = { routine ->
                viewModel.addRoutine(routine)
                showBroadcastRoutineDialog = false
                Toast.makeText(context, "Admin routine broadcasted to all students!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // DIALOG: Broadcast Admin Task
    if (showBroadcastTaskDialog) {
        AddAdminTaskDialog(
            onDismiss = { showBroadcastTaskDialog = false },
            onBroadcast = { task ->
                viewModel.addTask(task)
                showBroadcastTaskDialog = false
                Toast.makeText(context, "Admin task/CT broadcasted to all students!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun AdminHeroHeader(
    adminName: String,
    adminRoll: String,
    totalUsers: Int,
    totalRoutines: Int,
    totalTasks: Int
) {
    NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 6.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(DeepOrangePrimary, DeepOrangeSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Control Hub",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MainText
                        )
                        Text(
                            text = "Superuser: $adminName (Roll: #$adminRoll)",
                            fontSize = 11.5.sp,
                            color = DeepOrangePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                NeumorphicBadge(
                    text = "MASTER ADMIN",
                    color = DeepOrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Students/CRs",
                    value = "$totalUsers",
                    icon = Icons.Default.Groups,
                    color = DeepOrangePrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Routines",
                    value = "$totalRoutines",
                    icon = Icons.Default.CalendarMonth,
                    color = SecondaryGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tasks & CTs",
                    value = "$totalTasks",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    color = DeepOrangeAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .neumorphicInset(cornerRadius = 12.dp, depth = 1.5.dp)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 9.5.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
fun RosterAndRolesTab(
    users: List<UserProfileFirestore>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    roleFilter: String,
    onRoleFilterChange: (String) -> Unit,
    onPromoteToCR: (UserProfileFirestore) -> Unit,
    onPromoteToACR: (UserProfileFirestore) -> Unit,
    onSetStudent: (UserProfileFirestore) -> Unit,
    onEditUser: (UserProfileFirestore) -> Unit,
    onDeleteUser: (UserProfileFirestore) -> Unit,
    onAddUserClick: () -> Unit
) {
    val filteredUsers = users.filter { user ->
        val matchesQuery = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.roll.contains(searchQuery, ignoreCase = true) ||
                user.batch.contains(searchQuery, ignoreCase = true)

        val matchesRole = when (roleFilter) {
            "CR" -> user.role == "CR"
            "ACR" -> user.role == "ACR"
            "STUDENT" -> user.role == "STUDENT"
            "ADMIN" -> user.role == "ADMIN" || user.role == "AUTHORITY"
            else -> true
        }

        matchesQuery && matchesRole
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Search & Add Student Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = "Search Student Roll, Name or Batch...",
                placeholder = "e.g. 20242025010",
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            NeumorphicButton(
                onClick = onAddUserClick,
                isPrimary = true,
                cornerRadius = 12.dp,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add User", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Role Filter Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("ALL" to "All Students", "CR" to "CRs Only", "ACR" to "ACRs Only", "STUDENT" to "General Students", "ADMIN" to "Admins")
            items(filters) { (key, label) ->
                val isSelected = roleFilter == key
                val mod = if (isSelected) {
                    Modifier.neumorphicRaised(cornerRadius = 10.dp, elevation = 2.dp)
                } else {
                    Modifier.neumorphicInset(cornerRadius = 10.dp, depth = 1.dp)
                }
                Box(
                    modifier = Modifier
                        .then(mod)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onRoleFilterChange(key) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) DeepOrangePrimary else SecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total Roster Count: ${filteredUsers.size} records",
            fontSize = 11.sp,
            color = SecondaryText,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DeepOrangePrimary.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No students found matching query", fontSize = 13.sp, color = SecondaryText)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredUsers, key = { it.id.ifBlank { it.roll } }) { user ->
                    StudentRosterItemCard(
                        user = user,
                        onPromoteToCR = { onPromoteToCR(user) },
                        onPromoteToACR = { onPromoteToACR(user) },
                        onSetStudent = { onSetStudent(user) },
                        onEditUser = { onEditUser(user) },
                        onDeleteUser = { onDeleteUser(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentRosterItemCard(
    user: UserProfileFirestore,
    onPromoteToCR: () -> Unit,
    onPromoteToACR: () -> Unit,
    onSetStudent: () -> Unit,
    onEditUser: () -> Unit,
    onDeleteUser: () -> Unit
) {
    NeumorphicCard(
        cornerRadius = 16.dp,
        elevation = 4.dp,
        contentPadding = PaddingValues(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    "ADMIN" -> DeepOrangePrimary
                                    "CR" -> SecondaryGreen
                                    "ACR" -> Color(0xFF00ACC1)
                                    "AUTHORITY" -> Color(0xFF8E24AA)
                                    else -> DeepOrangeContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            color = if (user.role == "STUDENT") DeepOrangePrimary else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MainText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Roll: #${user.roll}",
                                fontSize = 11.5.sp,
                                color = DeepOrangePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${user.batch}",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }

                // Role Badge
                val badgeColor = when (user.role) {
                    "ADMIN" -> DeepOrangePrimary
                    "CR" -> SecondaryGreen
                    "ACR" -> Color(0xFF00ACC1)
                    "AUTHORITY" -> Color(0xFF8E24AA)
                    else -> SecondaryText
                }
                NeumorphicBadge(text = user.role, color = badgeColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Assign CR, ACR, Student, Edit Roll, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Set CR Button
                if (user.role != "CR") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryGreen.copy(alpha = 0.12f))
                            .clickable { onPromoteToCR() }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("★ Set CR", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                    }
                }

                // Set ACR Button
                if (user.role != "ACR") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00ACC1).copy(alpha = 0.12f))
                            .clickable { onPromoteToACR() }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✦ Set ACR", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00838F))
                    }
                }

                // If currently CR or ACR, allow demoting back to Student
                if (user.role == "CR" || user.role == "ACR") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.12f))
                            .clickable { onSetStudent() }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Student", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = MainText)
                    }
                }

                // Edit Button
                IconButton(onClick = onEditUser, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = DeepOrangePrimary, modifier = Modifier.size(16.dp))
                }

                // Delete Button
                IconButton(onClick = onDeleteUser, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun RoutinesDbTab(
    routines: List<SharedRoutineFirestore>,
    onDeleteRoutine: (String) -> Unit,
    onBroadcastClick: () -> Unit,
    onPurgeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Cloud Class Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MainText)
                Text(text = "Total classes synced in cloud: ${routines.size}", fontSize = 11.sp, color = SecondaryText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton(
                    onClick = onBroadcastClick,
                    isPrimary = true,
                    cornerRadius = 10.dp,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Class", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (routines.isNotEmpty()) {
                    NeumorphicButton(
                        onClick = onPurgeClick,
                        isPrimary = false,
                        cornerRadius = 10.dp,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Purge DB", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (routines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No routines in Firestore yet. Tap Add Class to broadcast.", fontSize = 12.sp, color = SecondaryText)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(routines, key = { it.id }) { r ->
                    NeumorphicCard(
                        cornerRadius = 14.dp,
                        elevation = 3.dp,
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = r.subjectName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MainText)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    NeumorphicBadge(text = r.subjectCode, color = DeepOrangePrimary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${r.dayOfWeek} • ${r.startTime} - ${r.endTime} • Room ${r.roomNo}",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Batch: ${r.batch} | Broadcast by ${r.authorRole} (#${r.authorRoll})",
                                    fontSize = 10.sp,
                                    color = DeepOrangeSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onDeleteRoutine(r.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksDbTab(
    tasks: List<SharedTaskFirestore>,
    onDeleteTask: (String) -> Unit,
    onBroadcastClick: () -> Unit,
    onPurgeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Cloud CTs & Tasks", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MainText)
                Text(text = "Total tasks synced in cloud: ${tasks.size}", fontSize = 11.sp, color = SecondaryText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicButton(
                    onClick = onBroadcastClick,
                    isPrimary = true,
                    cornerRadius = 10.dp,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add CT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (tasks.isNotEmpty()) {
                    NeumorphicButton(
                        onClick = onPurgeClick,
                        isPrimary = false,
                        cornerRadius = 10.dp,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Purge DB", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No tasks or CTs in Firestore yet. Tap Add CT to broadcast.", fontSize = 12.sp, color = SecondaryText)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(tasks, key = { it.id }) { t ->
                    NeumorphicCard(
                        cornerRadius = 14.dp,
                        elevation = 3.dp,
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = t.title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MainText)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    NeumorphicBadge(text = t.taskType, color = DeepOrangePrimary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Subject: ${t.subjectCode} • Batch: ${t.batch}",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Broadcast by: ${t.authorRole} (#${t.authorRoll})",
                                    fontSize = 10.sp,
                                    color = DeepOrangeSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onDeleteTask(t.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatabaseToolsTab(
    viewModel: MainViewModel,
    userRoll: String,
    userRole: String
) {
    val context = LocalContext.current
    var inputPasscode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        NeumorphicCard(
            cornerRadius = 18.dp,
            elevation = 5.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = DeepOrangePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Security & Admin Authority", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MainText)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Active Role: $userRole (Roll: #$userRoll)\nFull administrative permissions are enabled for setting CR, ACR, modifying student rolls, and managing cloud collections.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                NeumorphicTextField(
                    value = inputPasscode,
                    onValueChange = { inputPasscode = it },
                    label = "Admin Passcode (Default: NWU2026)",
                    placeholder = "NWU2026",
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeumorphicButton(
                    onClick = {
                        val ok = viewModel.unlockAdminWithPasscode(inputPasscode)
                        if (ok) {
                            Toast.makeText(context, "Admin Access Refreshed!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Invalid Passcode", Toast.LENGTH_SHORT).show()
                        }
                    },
                    isPrimary = true,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("Re-verify Admin Credentials", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicCard(
            cornerRadius = 18.dp,
            elevation = 5.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = SecondaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Firebase Database Health", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MainText)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Database: ai-studio-android-universi-5f70cfa3-21ae-4be1-8943-94060f8f9f1f\n• Cloud Project: gen-lang-client-0581505834\n• Real-Time Snapshot Sync: Active\n• Auto-Sync Target: All Students in Batch-54",
                    fontSize = 11.5.sp,
                    color = SecondaryText,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                NeumorphicButton(
                    onClick = {
                        viewModel.syncRoutineWithServer()
                        Toast.makeText(context, "Forced full cloud synchronization", Toast.LENGTH_SHORT).show()
                    },
                    isPrimary = true,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Force Full Database Sync", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- DIALOGS ---

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, roll: String, role: String, batch: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STUDENT") }
    var batch by remember { mutableStateOf("Batch-54") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student / CR / ACR", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NeumorphicTextField(value = name, onValueChange = { name = it }, label = "Full Name", placeholder = "Student Name", singleLine = true)
                NeumorphicTextField(value = roll, onValueChange = { roll = it }, label = "Student Roll Number", placeholder = "e.g. 20242025011", singleLine = true)
                NeumorphicTextField(value = batch, onValueChange = { batch = it }, label = "Batch", placeholder = "Batch-54", singleLine = true)

                Text("Assign Role:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepOrangePrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("STUDENT" to "Student", "CR" to "CR", "ACR" to "ACR", "ADMIN" to "Admin").forEach { (rKey, rLabel) ->
                        val isSel = role == rKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) DeepOrangePrimary else DeepOrangeContainer)
                                .clickable { role = rKey }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(rLabel, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else MainText)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && roll.isNotBlank()) {
                        onSave(name, roll, role, batch)
                    }
       ... (9 KB left)