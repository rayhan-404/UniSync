package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.DeepOrangePrimary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ClassRoutineEntity
import com.example.ui.components.NeumorphicBadge
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.NeumorphicInsetSurface
import com.example.ui.components.NeumorphicSwitch
import com.example.ui.components.NeumorphicTextField
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.ColorThemePresets
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainText
import com.example.ui.theme.MutedText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TertiaryPurple
import com.example.ui.viewmodels.MainViewModel
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val settings by viewModel.notificationSettings.collectAsState()
    val currentThemePreset by viewModel.themePresetIndex.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncState.collectAsState()
    val allRoutines by viewModel.allRoutines.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var dailyMorningAlert by remember(settings) { mutableStateOf(settings?.dailyMorningAlert ?: true) }
    var morningTimesList by remember(settings) {
        mutableStateOf(
            (settings?.morningTimesCsv ?: "06:30,07:00,07:30")
                .split(",")
                .filter { it.isNotBlank() }
                .toMutableList()
        )
    }
    var newMorningTimeInput by remember { mutableStateOf("") }

    var preClassMinutesSlider by remember(settings) {
        mutableStateOf((settings?.classLeadMinutes ?: 10).toFloat())
    }

    var ctSliders by remember(settings) {
        mutableStateOf(
            (settings?.ctAlertSlidersCsv ?: "24")
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .ifEmpty { listOf(24) }
        )
    }

    var otherTasksSliders by remember(settings) {
        mutableStateOf(
            (settings?.otherTasksAlertSlidersCsv ?: "48")
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .ifEmpty { listOf(48) }
        )
    }

    var showAddRoutineInSettings by remember { mutableStateOf(false) }

    val profilePicUri by viewModel.profilePicUri.collectAsState()
    val avatarPreset by viewModel.avatarPreset.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.setProfilePicUri(uri.toString())
            }
        }
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(bottom = 96.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Settings Screen Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .neumorphicInset(cornerRadius = 18.dp, depth = 2.dp, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Settings & Routine Control",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MainText,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "Notification timings, morning alarms, pre-class slider & cloud sync",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PROFILE & AVATAR CARD
            NeumorphicCard(
                cornerRadius = 24.dp,
                elevation = 6.dp,
                contentPadding = PaddingValues(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.sweepGradient(
                                            listOf(
                                                com.example.ui.theme.DeepOrangePrimary,
                                                com.example.ui.theme.DeepOrangeSecondary,
                                                com.example.ui.theme.DeepOrangeAccent,
                                                com.example.ui.theme.DeepOrangePrimary
                                            )
                                        )
                                    )
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                listOf(
                                                    com.example.ui.theme.DeepOrangePrimary,
                                                    com.example.ui.theme.DeepOrangeSecondary
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                if (profilePicUri != null) {
                                    coil.compose.AsyncImage(
                                        model = profilePicUri,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    when (avatarPreset) {
                                        "grad_cap" -> Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        "code" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        "star" -> Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        "palette" -> Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        "science" -> Icon(Icons.Default.Biotech, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        "vocal" -> Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                        else -> {
                                            Text(
                                                text = currentUser.take(1).uppercase(),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                fontSize = 24.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Customize Student Profile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MainText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pick a dynamic preset or upload a custom student portal picture.",
                                fontSize = 11.sp,
                                color = SecondaryText,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons to change Profile Picture
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeumorphicButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            isPrimary = true,
                            cornerRadius = 12.dp,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f).testTag("upload_profile_btn")
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Photo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (profilePicUri != null) {
                            NeumorphicButton(
                                onClick = {
                                    viewModel.setProfilePicUri(null)
                                    Toast.makeText(context, "Reset to standard avatar preset", Toast.LENGTH_SHORT).show()
                                },
                                cornerRadius = 12.dp,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f).testTag("reset_avatar_btn")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset Avatar", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset selection
                    Text(
                        text = "Or Choose Avatar Preset Symbol:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val presets = listOf(
                        "grad_cap" to Icons.Default.School,
                        "code" to Icons.Default.Code,
                        "star" to Icons.Default.Star,
                        "palette" to Icons.Default.Palette,
                        "science" to Icons.Default.Biotech,
                        "vocal" to Icons.Default.RecordVoiceOver
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presets) { (id, icon) ->
                            val isSelected = (avatarPreset == id && profilePicUri == null)
                            val ringMod = if (isSelected) {
                                Modifier.neumorphicInset(cornerRadius = 14.dp, depth = 2.5.dp)
                            } else {
                                Modifier.neumorphicRaised(cornerRadius = 14.dp, elevation = 3.dp)
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .then(ringMod)
                                    .clickable(
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            viewModel.setProfilePicUri(null)
                                            viewModel.setAvatarPreset(id)
                                        }
                                    )
                                    .testTag("avatar_preset_$id"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = id,
                                    tint = if (isSelected) PrimaryBlue else MutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CARD: Student Roll & Class Representative Role
            val userRoll by viewModel.userRoll.collectAsState()
            val userRole by viewModel.userRole.collectAsState()
            val userBatch by viewModel.userBatch.collectAsState()

            var editRoll by remember(userRoll) { mutableStateOf(userRoll) }
            var editName by remember(currentUser) { mutableStateOf(currentUser) }
            var editRole by remember(userRole) { mutableStateOf(userRole) }
            var editBatch by remember(userBatch) { mutableStateOf(userBatch) }

            NeumorphicCard(
                cornerRadius = 20.dp,
                elevation = 5.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Roll & CR Authority Role",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MainText
                            )
                        }

                        NeumorphicBadge(
                            text = editRole,
                            color = if (editRole == "CR" || editRole == "AUTHORITY") SecondaryGreen else PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Set your Student Roll and Role. When CR or Authority adds or updates classes and CTs, they auto-sync to all students!",
                        fontSize = 11.5.sp,
                        color = SecondaryText,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Selector Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "STUDENT" to "Student",
                            "CR" to "CR",
                            "ACR" to "ACR",
                            "ADMIN" to "Admin"
                        ).forEach { (rKey, rLabel) ->
                            val isSel = editRole == rKey
                            val mod = if (isSel) {
                                Modifier.neumorphicRaised(cornerRadius = 10.dp, elevation = 2.5.dp)
                            } else {
                                Modifier.neumorphicInset(cornerRadius = 10.dp, depth = 1.dp)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(mod)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { editRole = rKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = rLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) DeepOrangePrimary else SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NeumorphicTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = "Student Name",
                        placeholder = "Your name",
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeumorphicTextField(
                        value = editRoll,
                        onValueChange = { editRoll = it },
                        label = "Student Roll Number",
                        placeholder = "e.g. 20242025010",
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeumorphicTextField(
                        value = editBatch,
                        onValueChange = { editBatch = it },
                        label = "Class Batch / Section",
                        placeholder = "e.g. Batch-54",
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeumorphicButton(
                        onClick = {
                            viewModel.updateProfile(
                                name = editName,
                                roll = editRoll,
                                role = editRole,
                                batch = editBatch,
                                dept = viewModel.userDept.value,
                                semester = viewModel.userSemester.value
                            )
                            Toast.makeText(context, "Role updated to $editRole with Roll #$editRoll", Toast.LENGTH_SHORT).show()
                        },
                        isPrimary = true,
                        cornerRadius = 12.dp,
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Update Profile & Sync Role", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CARD: ADMIN CONTROL HUB WITH GMAIL VERIFICATION (rayhan6355@gmail.com)
            val isAdmin by viewModel.isAdminAccess.collectAsState()
            var emailVerifyInput by remember { mutableStateOf("") }
            var showAdminDashboardInSettings by remember { mutableStateOf(false) }

            NeumorphicCard(
                cornerRadius = 20.dp,
                elevation = 6.dp,
                contentPadding = PaddingValues(16.dp),
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
                                    .background(DeepOrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Admin Hub & Gmail Verification",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MainText
                                )
                                Text(
                                    text = "Main Admin: rayhan6355@gmail.com",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        NeumorphicBadge(
                            text = if (isAdmin) "VERIFIED ADMIN" else "LOCKED",
                            color = if (isAdmin) SecondaryGreen else SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAdmin) {
                        Text(
                            text = "Admin Access Verified via rayhan6355@gmail.com. You have full superuser permissions to assign CR, ACR, edit student rolls, and manage Firestore database collections.",
                            fontSize = 11.5.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NeumorphicButton(
                            onClick = { showAdminDashboardInSettings = !showAdminDashboardInSettings },
                            isPrimary = true,
                            cornerRadius = 12.dp,
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = if (showAdminDashboardInSettings) "Hide Admin Control Panel" else "Open Admin Control Panel 👑",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Enter the authorized administrator Gmail account (rayhan6355@gmail.com) to verify and unlock the Admin Control Hub:",
                            fontSize = 11.5.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeumorphicTextField(
                            value = emailVerifyInput,
                            onValueChange = { emailVerifyInput = it },
                            label = "Admin Gmail (rayhan6355@gmail.com)",
                            placeholder = "rayhan6355@gmail.com",
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeumorphicButton(
                                onClick = {
                                    val verified = viewModel.adminVerifyEmail(emailVerifyInput)
                                    if (verified) {
                                        Toast.makeText(context, "Admin Verified via rayhan6355@gmail.com!", Toast.LENGTH_LONG).show()
                                        showAdminDashboardInSettings = true
                                        emailVerifyInput = ""
                                    } else {
                                        Toast.makeText(context, "Invalid Admin Gmail. Access Denied.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isPrimary = true,
                                cornerRadius = 12.dp,
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Verify Admin Gmail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            NeumorphicButton(
                                onClick = {
                                    // Quick shortcut for testing/lab presentation
                                    viewModel.adminVerifyEmail("rayhan6355@gmail.com")
                                    Toast.makeText(context, "Quick Admin Access Granted!", Toast.LENGTH_SHORT).show()
                                    showAdminDashboardInSettings = true
                                },
                                isPrimary = false,
                                cornerRadius = 12.dp,
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Quick Bypass", color = DeepOrangePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // EMBEDDED ADMIN DASHBOARD CONTROLS IN SETTINGS
            if (isAdmin && showAdminDashboardInSettings) {
                Spacer(modifier = Modifier.height(16.dp))

                val allUsers by viewModel.allRegisteredUsers.collectAsState()
                val allCloudRoutines by viewModel.allCloudRoutinesList.collectAsState()
                val allCloudTasks by viewModel.allCloudTasksList.collectAsState()

                NeumorphicCard(
                    cornerRadius = 20.dp,
                    elevation = 6.dp,
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = DeepOrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Admin Roster & Student Roll Manager", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MainText)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Total Registered Students/CRs in Cloud: ${allUsers.size}", fontSize = 11.sp, color = SecondaryText)

                        Spacer(modifier = Modifier.height(10.dp))

                        allUsers.take(5).forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${user.name} (Roll: #${user.roll})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MainText)
                                    Text(text = "Batch: ${user.batch} | Role: ${user.role}", fontSize = 10.5.sp, color = SecondaryText)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (user.role != "CR") {
                                        NeumorphicButton(
                                            onClick = {
                                                viewModel.adminSetUserRoleAndRoll(user.id.ifBlank { user.roll }, user.name, user.roll, "CR", user.batch)
                                                Toast.makeText(context, "${user.name} set as CR", Toast.LENGTH_SHORT).show()
                                            },
                                            isPrimary = false,
                                            cornerRadius = 8.dp,
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text("CR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                                        }
                                    }
                                    if (user.role != "ACR") {
                                        NeumorphicButton(
                                            onClick = {
                                                viewModel.adminSetUserRoleAndRoll(user.id.ifBlank { user.roll }, user.name, user.roll, "ACR", user.batch)
                                                Toast.makeText(context, "${user.name} set as ACR", Toast.LENGTH_SHORT).show()
                                            },
                                            isPrimary = false,
                                            cornerRadius = 8.dp,
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text("ACR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00ACC1))
                                        }
                                    }
                                    if (user.role != "STUDENT") {
                                        NeumorphicButton(
                                            onClick = {
                                                viewModel.adminSetUserRoleAndRoll(user.id.ifBlank { user.roll }, user.name, user.roll, "STUDENT", user.batch)
                                                Toast.makeText(context, "${user.name} set as Student", Toast.LENGTH_SHORT).show()
                                            },
                                            isPrimary = false,
                                            cornerRadius = 8.dp,
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text("Std", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = MainText)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeumorphicButton(
                                onClick = {
                                    viewModel.adminPurgeAllRoutines()
                                    Toast.makeText(context, "All cloud routines purged", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = false,
                                cornerRadius = 10.dp,
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Purge Routines (${allCloudRoutines.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                            }

                            NeumorphicButton(
                                onClick = {
                                    viewModel.adminPurgeAllTasks()
                                    Toast.makeText(context, "All cloud tasks purged", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = false,
                                cornerRadius = 10.dp,
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Purge Tasks (${allCloudTasks.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CARD 1: Routine Management & Cloud Server Sync
            NeumorphicCard(
                cornerRadius = 20.dp,
                elevation = 5.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Routine & Cloud Sync",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MainText
                            )
                        }

                        NeumorphicButton(
                            onClick = { showAddRoutineInSettings = true },
                            isPrimary = true,
                            cornerRadius = 12.dp,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Class", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Total Scheduled Classes: ${allRoutines.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cloud Sync Inset Box
                    NeumorphicInsetSurface(
                        cornerRadius = 14.dp,
                        depth = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("University Cloud Server", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MainText)
                                }
                                Text(
                                    text = "Status: $cloudSyncStatus",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }

                            NeumorphicButton(
                                onClick = { viewModel.syncRoutineWithServer() },
                                isPrimary = true,
                                cornerRadius = 10.dp,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("cloud_sync_btn")
                            ) {
                                Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CARD 2: Multiple Morning Notifications & Custom Times
            NeumorphicCard(
                cornerRadius = 20.dp,
                elevation = 5.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Multiple Morning Notifications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MainText
                            )
                            Text(
                                text = "Set multiple custom morning alarm reminders for your classes",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                        NeumorphicSwitch(
                            checked = dailyMorningAlert,
                            onCheckedChange = { dailyMorningAlert = it },
                            modifier = Modifier.testTag("toggle_daily_morning")
                        )
                    }

                    if (dailyMorningAlert) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Scheduled Morning Times:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(morningTimesList) { time ->
                                Box(
                                    modifier = Modifier
                                        .neumorphicInset(cornerRadius = 10.dp, depth = 2.dp)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = time,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = ErrorRed.copy(alpha = 0.8f),
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    morningTimesList = morningTimesList
                                                        .filter { it != time }
                                                        .toMutableList()
                                                }
                                      ... (26 KB left)