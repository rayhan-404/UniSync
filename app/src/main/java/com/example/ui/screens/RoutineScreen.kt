package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AcademicTaskEntity
import com.example.data.local.entities.ClassRoutineEntity
import com.example.ui.components.NeumorphicBadge
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.NeumorphicInsetSurface
import com.example.ui.components.NeumorphicStatChip
import com.example.ui.components.NeumorphicTextField
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.ErrorLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MainText
import com.example.ui.theme.MutedText
import com.example.ui.theme.NeumorphicHighlightLight
import com.example.ui.theme.NeumorphicShadowDark
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.PrimaryMediumBlue
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.TertiaryPurple
import com.example.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val WeekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun RoutineScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedDay by viewModel.selectedDay.collectAsState()
    val allRoutines by viewModel.allRoutines.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredRoutines = remember(allRoutines, selectedDay) {
        allRoutines.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
    }

    // All Upcoming Pending Tasks
    val upcomingTasks = remember(allTasks) {
        allTasks.filter { !it.isCompleted }
            .sortedBy { it.dueDateEpochMs }
    }

    // 2 Tasks per slide (side-by-side blocks)
    val taskPages = remember(upcomingTasks) {
        upcomingTasks.chunked(2)
    }
    val pagerState = rememberPagerState(pageCount = { taskPages.size })

    val userRole by viewModel.userRole.collectAsState()
    val userRoll by viewModel.userRoll.collectAsState()
    val userBatch by viewModel.userBatch.collectAsState()

    val isCrOrAuthority = userRole in listOf("CR", "ACR", "ADMIN", "AUTHORITY")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Role & Live Sync Status Card
            item {
                NeumorphicCard(
                    cornerRadius = 16.dp,
                    elevation = 4.dp,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isCrOrAuthority) SecondaryGreen else PrimaryBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isCrOrAuthority) "Official Broadcast ($userRole)" else "Class Sync Active ($userBatch)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MainText
                                )
                                Text(
                                    text = if (isCrOrAuthority) "Your additions auto-sync to all students" else "Roll #$userRoll • Auto-synced from CR",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        NeumorphicBadge(
                            text = userRole,
                            color = if (isCrOrAuthority) SecondaryGreen else PrimaryBlue
                        )
                    }
                }
            }

            // 1. Hero Next Class Banner
            item {
                NextClassHeroBanner(
                    todayClasses = filteredRoutines,
                    pendingTasksCount = upcomingTasks.size
                )
            }

            // 2. Quick Add Task Tactile Action Button with Deep Card Shadow
            item {
                NeumorphicCard(
                    cornerRadius = 18.dp,
                    elevation = 6.dp,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showAddTaskDialog = true }
                        )
                        .testTag("add_ct_assignment_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(vertical = 15.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCrOrAuthority) "Broadcast CT / Assignment (All Students)" else "Add CT / Assignment or Any Task",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                }
            }

            // 3. Upcoming Tasks Section (Side-by-side 2 blocks per slide, right-to-left swipeable)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Upcoming Tasks",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainText,
                                letterSpacing = (-0.1).sp
                            )
                            if (taskPages.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(Slide →)",
                                    fontSize = 11.sp,
                                    color = SecondaryText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        NeumorphicBadge(
                            text = "${upcomingTasks.size} Pending",
                            color = if (upcomingTasks.isNotEmpty()) TertiaryPurple else SecondaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (upcomingTasks.isEmpty()) {
                        NeumorphicCard(
                            cornerRadius = 18.dp,
                            elevation = 4.dp,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .neumorphicInset(cornerRadius = 18.dp, depth = 2.dp, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SecondaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "All caught up with tasks!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MainText
                                    )
                                    Text(
                                        text = "No pending CTs, assignments, or lab reports scheduled.",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                }
                            }
                        }
                    } else {
                        Column {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth(),
                                pageSpacing = 12.dp,
                                flingBehavior = PagerDefaults.flingBehavior(
                                    state = pagerState,
                                    snapAnimationSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    )
                                )
                            ) { pageIndex ->
                                val pageTasks = taskPages[pageIndex]
                                val pageOffset = (
                                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                                ).absoluteValue.coerceIn(0f, 1f)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer {
                                            // Premium smooth scale, alpha, and subtle depth transition
                                            val scale = 1f - (pageOffset * 0.07f)
                                            val alphaVal = 1f - (pageOffset * 0.35f)
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = alphaVal
                                            // Subtle smooth parallax shift
                                            translationX = pageOffset * 14f
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pageTasks.forEach { task ->
                                        HomeUpcomingTaskBlock(
                                            task = task,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(150.dp)
                                        )
                                    }
                                    // If only 1 task on last page, add invisible spacer with identical height
                                    if (pageTasks.size == 1) {
                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(150.dp)
                                        )
                                    }
                                }
                            }

                            // Slide pagination dots if multiple pages
                            if (taskPages.size > 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(taskPages.size) { iteration ->
                                        val isCurrentPage = pagerState.currentPage == iteration
                                        val dotWidth by animateDpAsState(
                                            targetValue = if (isCurrentPage) 18.dp else 6.dp,
                                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                            label = "dotWidth"
                                        )
                                        val dotColor by animateColorAsState(
                                            targetValue = if (isCurrentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                            label = "dotColor"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 3.dp)
                                                .size(dotWidth, 6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(dotColor)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Day Selector Segmented Bar
            item {
                Column {
                    Text(
                        text = "Select Day of Week",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        letterSpacing = 0.1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(WeekDays) { day ->
                            val isSelected = day.equals(selectedDay, ignoreCase = true)
                            val dayColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else SecondaryText,
                                label = "dayColor"
                            )

                            val modifier = if (isSelected) {
                                Modifier
                                    .neumorphicInset(cornerRadius = 16.dp, depth = 2.5.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { viewModel.selectedDay.value = day }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            } else {
                                Modifier
                                    .neumorphicRaised(cornerRadius = 16.dp, elevation = 4.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { viewModel.selectedDay.value = day }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            }

                            Box(
                                modifier = modifier.testTag("day_chip_$day"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = dayColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Section Header: Selected Day Classes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$selectedDay Classes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            letterSpacing = (-0.1).sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeumorphicBadge(
                            text = "${filteredRoutines.size} Scheduled",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    NeumorphicButton(
                        onClick = { showAddClassDialog = true },
                        isPrimary = true,
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                        modifier = Modifier.testTag("add_class_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isCrOrAuthority) "Broadcast Class" else "Add Class",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 6. Routines List
            if (filteredRoutines.isEmpty()) {
                item {
                    NeumorphicCard(
                        cornerRadius = 20.dp,
                        elevation = 4.dp,
                        contentPadding = PaddingValues(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .neumorphicInset(cornerRadius = 28.dp, depth = 3.dp, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "No classes",
                                    tint = MutedText,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No classes scheduled for $selectedDay",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MainText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'Add Class' above to configure your lecture timetable",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }
            } else {
                items(filteredRoutines) { item ->
                    RoutineCardItem(
                        routine = item,
                        onToggleNotification = {
                            viewModel.toggleRoutineNotification(item)
                            val status = if (!item.isNotificationEnabled) "ON" else "OFF"
                            Toast.makeText(context, "Notification turned $status for ${item.subjectCode}", Toast.LENGTH_SHORT).show()
                        },
                        onUpdateTimer = { newMins ->
                            viewModel.updateRoutineTimer(item, newMins)
                            Toast.makeText(context, "Timer set to $newMins mins before class for ${item.subjectCode}", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteRoutine(item)
                            Toast.makeText(context, "Class removed from schedule", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddClassDialog) {
        AddRoutineDialog(
            defaultDay = selectedDay,
            onDismiss = { showAddClassDialog = false },
            onConfirm = { routine ->
                viewModel.addRoutine(routine)
                showAddClassDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { task ->
                viewModel.addTask(task)
                showAddTaskDialog = false
            }
        )
    }
}

data class ClassScheduleStatus(
    val activeClass: ClassRoutineEntity?,
    val isClassOngoing: Boolean,
    val timeRemainingMin: Int,
    val statusBadge: String,
    val donutMainText: String,
    val donutSubLabel: String,
    val donutProgress: Float,
    val donutColor: Color
)

fun calculateClassScheduleStatus(todayClasses: List<ClassRoutineEntity>): ClassScheduleStatus {
    if (todayClasses.isEmpty()) {
        return ClassScheduleStatus(
            activeClass = null,
            isClassOngoing = false,
            timeRemainingMin = 0,
            statusBadge = "NO CLASSES SCHEDULED",
            donutMainText = "Free",
            donutSubLabel = "off day",
            donutProgress = 1f,
            donutColor = SecondaryGreen
        )
    }

    fun parseTimeToMinutes(timeStr: String): Int {
        val clean = timeStr.trim().uppercase()
        val isPm = clean.contains("PM")
        val isAm = clean.contains("AM")
        val digits = clean.replace("AM", "").replace("PM", "").trim()
        val parts = digits.split(":")
        if (parts.size >= 2) {
            var hour = parts[0].trim().toIntOrNull() ?: 0
            val min = parts[1].trim().toIntOrNull() ?: 0
            if (isPm && hour < 12) hour += 12
            if (isAm && hour == 12) hour = 0
            return hour * 60 + min
        }
        return 0
    }

    val cal = java.util.Calendar.getInstance()
    val currentMinutes = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)

    val sorted = todayClasses.sortedBy { parseTimeToMinutes(it.startTime) }

    // 1. Is any class ongoing right now? (start <= current < end)
    val ongoingClass = sorted.firstOrNull {
        val start = parseTimeToMinutes(it.startTime)
        val end = parseTimeToMinutes(it.endTime).let { e -> if (e <= start) start + 60 else e }
        currentMinutes in start until end
    }

    if (ongoingClass != null) {
        val start = parseTimeToMinutes(ongoingClass.startTime)
        val end = parseTimeToMinutes(ongoingClass.endTime).let { e -> if (e <= start) start + 60 else e }
        val remaining = (end - currentMinutes).coerceAtLeast(1)
        val duration = (end - start).coerceAtLeast(1)
        val progress = (remaining.toFloat() / duration).coerceIn(0.08f, 1f)
        val color = if (remaining <= 10) ErrorRed else SecondaryGreen

        return ClassScheduleStatus(
            activeClass = ongoingClass,
            isClassOngoing = true,
            timeRemainingMin = remaining,
            statusBadge = "CLASS IN PROGRESS",
            donutMainText = "${remaining}m",
            donutSubLabel = "left",
            donutProgress = progress,
            donutColor = color
        )
    }

    // 2. Next class upcoming today
    val nextClass = sorted.firstOrNull { parseTimeToMinutes(it.startTime) > currentMinutes }
    if (nextClass != null) {
        val start = parseTimeToMinutes(nextClass.startTime)
        val remaining = (start - currentMinutes).coerceAtLeast(1)
        val prevClass = sorted.lastOrNull { parseTimeToMinutes(it.endTime) <= currentMinutes }
        val prevEnd = prevClass?.let { parseTimeToMinutes(it.endTime) } ?: (start - remaining)
        val breakWindow = (start - prevEnd).coerceAtLeast(remaining).coerceAtLeast(1)
        val progress = (remaining.toFloat() / breakWindow).coerceIn(0.08f, 1f)

        val displayText = if (remaining >= 60) "${remaining / 60}h ${remaining % 60}m" else "${remaining}m"
        val badge = if (remaining >= 60) "NEXT CLASS IN ${remaining / 60}h ${remaining % 60}m" else "NEXT CLASS IN ${remaining}m"
        val color = if (remaining <= 15) Color(0xFFFFB300) else TertiaryPurple

        return ClassScheduleStatus(
            activeClass = nextClass,
            isClassOngoing = false,
            timeRemainingMin = remaining,
            statusBadge = badge,
            donutMainText = displayText,
            donutSubLabel = "starts in",
            donutProgress = progress,
            donutColor = color
        )
    }

    // 3. All classes done for today
    return ClassScheduleStatus(
        activeClass = null,
        isClassOngoing = false,
        timeRemainingMin = 0,
        statusBadge = "ALL CLASSES COMPLETED",
        donutMainText = "Done",
        donutSubLabel = "for today",
        donutProgress = 1f,
        donutColor = SecondaryGreen
    )
}

@Composable
fun ClassCountdownDonut(
    status: ClassScheduleStatus,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .neumorphicInset(cornerRadius = 30.dp, depth = 2.dp, shape = CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 4.dp.toPx()
            val arcSize = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background track
            drawArc(
                color = status.donutColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                color = status.donutColor,
                startAngle = -90f,
                sweepAngle = 360f * status.donutProgress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = status.donutMainText,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = status.donutColor,
                lineHeight = 13.sp
            )
            Text(
                text = status.donutSubLabel,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                lineHeight = 8.5.sp
            )
        }
    }
}

@Composable
fun TaskCountdownDonut(
    dueDateEpochMs: Long,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { System.currentTimeMillis() }
    val diffMs = dueDateEpochMs - currentTime

    // 7 days window for progress arc scaling
    val totalTimeWindow = 7 * 24 * 3600 * 1000L
    val progress = if (diffMs <= 0) 1f else (diffMs.toFloat() / totalTimeWindow).coerceIn(0.08f, 1f)

    val (timerColor, countdownText, unitLabel) = when {
        diffMs <= 0 -> Triple(ErrorRed, "Due", "overdue")
        diffMs < 24 * 3600 * 1000L -> {
            val hours = (diffMs / (3600 * 1000L)).coerceAtLeast(0)
            val mins = ((diffMs % (3600 * 1000L)) / (60 * 1000L)).coerceAtLeast(0)
            val text = if (hours > 0) "${hours}h" else "${mins}m"
            val label = if (hours > 0) "${mins}m" else "left"
            Triple(ErrorRed, text, label)
        }
        diffMs < 72 * 3600 * 1000L -> {
            val days = (diffMs / (24 * 3600 * 1000L))
            Triple(Color(0xFFFFB300), "${days}d", "left")
        }
        else -> {
            val days = (diffMs / (24 * 3600 * 1000L))
            Triple(SecondaryGreen, "${days}d", "left")
        }
    }

    Box(
        modifier = modifier
            .neumorphicInset(cornerRadius = 24.dp, depth = 2.dp, shape = CircleShape)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val arcSize = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background faint track ring
            drawArc(
                color = timerColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Foreground dynamic colored timer arc (Red, Yellow, Green)
            drawArc(
                color = timerColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center Countdown Display inside donut
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = countdownText,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = timerColor,
                lineHeight = 11.sp
            )
            Text(
                text = unitLabel,
                fontSize = 6.5.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                lineHeight = 7.sp
            )
        }
    }
}

@Composable
fun HomeUpcomingTaskBlock(
    task: AcademicTaskEntity,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(task.dueDateEpochMs) {
        SimpleDateFormat("EEE, MMM dd", Locale.ENGLISH).format(Date(task.dueDateEpochMs))
    }
    val timeStr = remember(task.dueDateEpochMs) {
        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(task.dueDateEpochMs))
    }

    val typeColor = when (task.taskType) {
        "CT" -> ErrorRed
        "Assignment" -> TertiaryPurple
        "Lab Report" -> SecondaryGreen
        else -> MaterialTheme.colorScheme.primary
    }

    NeumorphicCard(
        cornerRadius = 18.dp,
        elevation = 5.dp,
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Type Badge + Donut Countdown Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f, fill = false).padding(end = 4.dp)) {
                    NeumorphicBadge(
                        text = task.taskType,
                        color = typeColor
                    )
                }
                TaskCountdownDonut(
                    dueDateEpochMs = task.dueDateEpochMs,
                    modifier = Modifier.size(42.dp)
                )
            }

            // Task Title (2 lines max with uniform height)
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = MainText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )

            // Date & Time Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$dateStr • $timeStr",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HomeUpcomingTaskCard(task: AcademicTaskEntity) {
    HomeUpcomingTaskBlock(task = task, modifier = Modifier.fillMaxWidth().height(150.dp))
}

@Composable
fun NextClassHeroBanner(
    todayClasses: List<ClassRoutineEntity>,
    pendingTasksCount: Int = 0
) {
    val status = remember(todayClasses) {
        calculateClassScheduleStatus(todayClasses)
    }
    val activeClass = status.activeClass
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.tertiary

    NeumorphicCard(
        cornerRadius = 28.dp,
        elevation = 7.dp,
        hasDecorations = true,
        contentPadding = PaddingValues(22.dp)
    ) {
        Column {
            // Live pulsing badge (.hero-badge)
            Box(
                modifier = Modifier
                    .neumorphicInset(cornerRadius = 20.dp, depth = 2.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(status.donutColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = status.statusBadge,
                        color = SecondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (activeClass != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = activeClass.subjectName,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 25.sp,
                            letterSpacing = (-0.4).sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Course: ${activeClass.subjectCode}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    // Live Class Countdown Doughnut Timer
                    ClassCountdownDonut(
                        status = status,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Schedule details pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicInset(cornerRadius = 14.dp, depth = 2.dp)
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = primaryColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${activeClass.startTime} - ${activeClass.endTime}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Teacher",
                            tint = accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = activeClass.teacherName,
                            color = SecondaryText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = if (todayClasses.isEmpty()) "No Classes Today" else "All Lectures Completed!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (todayClasses.isEmpty()) "Enjoy your free day or prepare ahead for upcoming tasks." else "No more lectures remaining for today. Great job!",
                            fontSize = 12.5.sp,
                            color = SecondaryText,
                            lineHeight = 17.sp
                        )
                    }

                    ClassCountdownDonut(
                        status = status,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stat chips: Lectures, Pending Tasks, Next Room
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeumorphicStatChip(
                    number = "${todayClasses.size}",
                    label = "Lectures",
                    modifier = Modifier.weight(1f),
                    numberColor = primaryColor
                )
                NeumorphicStatChip(
                    number = "$pendingTasksCount",
                    label = "Pending Tasks",
                    modifier = Modifier.weight(1f),
                    numberColor = if (pendingTasksCount > 0) TertiaryPurple else SecondaryGreen
                )
                NeumorphicStatChip(
                    number = if (activeClass != null) activeClass.roomNo else if (todayClasses.isEmpty()) "Off Day" else "Done",
                    label = "Room",
                    modifier = Modifier.weight(1f),
                    numberColor = accentColor
                )
            }
        }
    }
}


@Composable
fun RoutineCardItem(
    routine: ClassRoutineEntity,
    onToggleNotification: () -> Unit,
    onUpdateTimer: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var showTimerDialog by remember { mutableStateOf(false) }

    NeumorphicCard(
        cornerRadius = 20.dp,
        elevation = 6.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.testTag("routine_card_${routine.id}")
    ) {
        Column {
            // 1. Top Row: Course Badge + Name + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    NeumorphicBadge(
                        text = routine.subjectCode,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = routine.subjectName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MainText,
                        letterSpacing = (-0.1).sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tactile Notification Toggle
                    NeumorphicIconButton(
                        onClick = onToggleNotification,
                        icon = if (routine.isNotificationEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = "Notification Toggle",
                        size = 38.dp,
                        iconSize = 18.dp,
                        isActive = routine.isNotificationEnabled,
                        activeColor = MaterialTheme.colorScheme.primary,
                        tint = MutedText,
                        modifier = Modifier.testTag("toggle_notif_class_${routine.id}")
                    )

                    // Tactile Delete Button
                    NeumorphicIconButton(
                        onClick = onDelete,
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete",
                        size = 38.dp,
                        iconSize = 18.dp,
                        tint = ErrorRed.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Instructor Name Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Instructor: ${routine.teacherName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainText
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Room Number & Schedule Time Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = routine.roomNo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.width(18.dp))

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${routine.startTime} - ${routine.endTime}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Stepper Timer & Notification Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tactile Recessed Stepper
                Box(
                    modifier = Modifier
                        .neumorphicInset(cornerRadius = 14.dp, depth = 2.5.dp)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("direct_timer_stepper_${routine.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                val newMins = (routine.customTimerMinutes - 5).coerceAtLeast(1)
                                onUpdateTimer(newMins)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Timer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Text(
                            text = "- ${routine.customTimerMinutes} min +",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { showTimerDialog = true }
                                .padding(horizontal = 6.dp)
                        )

                        IconButton(
                            onClick = {
                                val newMins = routine.customTimerMinutes + 5
                                onUpdateTimer(newMins)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Timer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (routine.isNotificationEnabled) SecondaryGreen else MutedText)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (routine.isNotificationEnabled) "Alert Active" else "Alert Off",
                        fontSize = 11.sp,
                        color = if (routine.isNotificationEnabled) SecondaryGreen else MutedText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showTimerDialog) {
        SetClassTimerDialog(
            currentMins = routine.customTimerMinutes,
            onDismiss = { showTimerDialog = false },
            onConfirm = { mins ->
                onUpdateTimer(mins)
                showTimerDialog = false
            }
        )
    }
}

@Composable
fun SetClassTimerDialog(
    currentMins: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var timerMinsInput by remember { mutableStateOf(currentMins.toString()) }
    val presets = listOf(3, 5, 10, 15, 30)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Set Class Timer Lead Time",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MainText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Select how many minutes before class you want the reminder alarm:",
                    fontSize = 13.sp,
                    color = SecondaryText
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { mins ->
                        val selected = timerMinsInput == mins.toString()
                        val mod = if (selected) {
                            Modifier.neumorphicInset(cornerRadius = 10.dp, depth = 2.dp)
                        } else {
                            Modifier.neumorphicRaised(cornerRadius = 10.dp, elevation = 3.dp)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(mod)
                                .clickable { timerMinsInput = mins.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) PrimaryBlue else SecondaryText
                            )
                        }
                    }
                }

                NeumorphicTextField(
                    value = timerMinsInput,
                    onValueChange = { timerMinsInput = it },
                    label = "Or enter custom minutes",
                    placeholder = "Minutes",
                    singleLine = true
                )
            }
        },
        confirmButton = {
            NeumorphicButton(
                onClick = {
                    val mins = timerMinsInput.toIntOrNull() ?: 5
                    onConfirm(mins)
                },
                isPrimary = true,
                cornerRadius = 12.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Set Timer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun AddRoutineDialog(
    defaultDay: String,
    onDismiss: () -> Unit,
    onConfirm: (ClassRoutineEntity) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var subjectCode by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var roomNo by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("10:15 AM") }
    var dayOfWeek by remember { mutableStateOf(defaultDay) }
    var notes by remember { mutableStateOf("") }

    val quickSubjects = remember {
        listOf(
            "CSE-2101" to "Data Structures",
            "CSE-2102" to "Algorithms",
            "EEE-1201" to "Digital Logic",
            "MATH-1101" to "Linear Algebra",
            "PHY-1102" to "Physics II"
        )
    }

    val quickRooms = remember { listOf("Room 304", "Room 301", "Room 402", "Lab 01") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BaseBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Class Routine",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MainText
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryLight)
                        .clickable {
                            val randomSub = quickSubjects.random()
                            subjectCode = randomSub.first
                            subjectName = randomSub.second
                            teacherName = "Raihan Sir"
                            roomNo = quickRooms.random()
                            startTime = "09:00 AM"
                            endTime = "10:15 AM"
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ Sample Fill",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Day Selection Row
                Text(
                    text = "Target Day:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(WeekDays) { day ->
                        val isSel = day.equals(dayOfWeek, ignoreCase = true)
                        val mod = if (isSel) {
                            Modifier.neumorphicInset(cornerRadius = 10.dp, depth = 2.dp)
                        } else {
                            Modifier.neumorphicRaised(cornerRadius = 10.dp, elevation = 2.dp)
                        }

                        Box(
                            modifier = Modifier
                                .then(mod)
                                .clickable { dayOfWeek = day }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = day.take(3),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) PrimaryBlue else SecondaryText
                            )
                        }
                    }
                }

                // Quick Subject Pickers
                Text(
                    text = "Quick Course Preset:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickSubjects) { (code, name) ->
                        val isSel = subjectCode == code
                        val mod = if (isSel) {
                            Modifier.neumorphicInset(cornerRadius = 10.dp, depth = 2.dp)
                        } else {
                            Modifier.neumorphicRaised(cornerRadius = 10.dp, elevation = 2.dp)
                        }

                        Box(
                            modifier = Modifier
                                .then(mod)
                                .clickable {
                                    subjectCode = code
                                    subjectName = name
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = code,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) PrimaryBlue else SecondaryText
                            )
                        }
                    }
                }

                NeumorphicTextField(
                    value = subjectCode,
                    onValueChange = { subjectCode = it },
                    label = "Course Code (e.g. CSE-2101)",
                    placeholder = "CSE-2101",
                    singleLine = true,
                    testTag = "input_subject_code"
                )

                NeumorphicTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = "Subject Name (e.g. Data Structures)",
                    placeholder = "Data Structures",
                    singleLine = true,
                    testTag = "input_subject_name"
                )

                // Manual Time Inputs
                Text(
                    text = "Class Schedule Time:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeumorphicTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = "Start Time",
                        placeholder = "09:00 AM",
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        testTag = "input_start_time"
                    )
                    NeumorphicTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = "End Time",
                        placeholder = "10:15 AM",
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        testTag = "input_end_time"
                    )
                }

                NeumorphicTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = "Teacher Name (e.g. Raihan Sir)",
                    placeholder = "Instructor Name",
                    singleLine = true,
                    testTag = "input_teacher_name"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Room:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                    quickRooms.forEach { room ->
                        val isSel = roomNo == room
                        val mod = if (isSel) {
                            Modifier.neumorphicInset(cornerRadius = 8.dp, depth = 2.dp)
                        } else {
                            Modifier.neumorphicRaised(cornerRadius = 8.dp, elevation = 2.dp)
                        }

                        Box(
                            modifier = Modifier
                                .then(mod)
                                .clickable { roomNo = room }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = room,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) PrimaryBlue else SecondaryText
                            )
                        }
                    }
                }

                NeumorphicTextField(
                    value = roomNo,
                    onValueChange = { roomNo = it },
                    label = "Room No",
                    placeholder = "Room 304",
                    singleLine = true,
                    testTag = "input_room_no"
                )
            }
        },
        confirmButton = {
            NeumorphicButton(
                onClick = {
                    val code = if (subjectCode.isBlank()) "CSE-101" else subjectCode
                    val name = if (subjectName.isBlank()) "General Class" else subjectName
                    onConfirm(
                        ClassRoutineEntity(
                            dayOfWeek = dayOfWeek,
                            subjectName = name,
                            subjectCode = code,
                            teacherName = if (teacherName.isBlank()) "Faculty" else teacherName,
                            roomNo = if (roomNo.isBlank()) "Room 304" else roomNo,
                            startTime = if (startTime.isBlank()) "09:00 AM" else startTime,
                            endTime = if (endTime.isBlank()) "10:15 AM" else endTime,
                            notes = notes
                        )
                    )
                },
                isPrimary = true,
                cornerRadius = 14.dp,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                modifier = Modifier.testTag("confirm_add_routine_btn")
            ) {
                Text("Save Routine", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}