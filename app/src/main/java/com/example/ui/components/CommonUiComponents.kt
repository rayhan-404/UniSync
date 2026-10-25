package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BaseBackground
import com.example.ui.theme.MainText
import com.example.ui.theme.NeumorphicHighlightLight
import com.example.ui.theme.NeumorphicShadowDark
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SecondaryText

sealed class BottomNavTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Routine : BottomNavTab("routine", "Routine", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    object Tasks : BottomNavTab("tasks", "Tasks & CT", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment)
    object Community : BottomNavTab("community", "Community", Icons.Filled.People, Icons.Outlined.People)
    object Settings : BottomNavTab("settings", "Settings", Icons.Filled.Notifications, Icons.Outlined.Notifications)
}

val BottomNavTabs = listOf(
    BottomNavTab.Routine,
    BottomNavTab.Tasks,
    BottomNavTab.Community,
    BottomNavTab.Settings
)

@Composable
fun AppHeader(
    studentName: String = "Student",
    appName: String = "UniSync",
    subTitle: String = "Timer",
    profilePicUri: String? = null,
    avatarPreset: String = "grad_cap",
    onNotificationClick: () -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = primaryColor.copy(alpha = 0.08f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo + App Name + "● Live" Badge + Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Academate / UniSync App Icon Card
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .neumorphicRaised(cornerRadius = 14.dp, elevation = 3.dp)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFEDE7F6),
                                        Color(0xFFE1BEE7)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "App Logo",
                                tint = Color(0xFF673AB7),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ACADEMATE",
                                fontSize = 5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF512DA8),
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = appName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = onSurfaceColor,
                        letterSpacing = (-0.3).sp
                    )

                    Text(
                        text = subTitle,
                        fontSize = 12.sp,
                        color = SecondaryText,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.sp
                    )
                }
            }

            // Right: Circular Neumorphic Notification Bell + Circular Avatar with Gradient Ring
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Circular Neumorphic Notification Bell Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .neumorphicRaised(
                            cornerRadius = 21.dp,
                            elevation = 4.dp,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onNotificationClick
                        )
                        .testTag("topbar_notification_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF2C3E50),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 2. Circular Profile Picture with Vibrant Blue-Purple Gradient Ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onHeaderClick
                        )
                        .testTag("user_profile_header"),
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient Outer Ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF2196F3),
                                        Color(0xFF9C27B0),
                                        Color(0xFFE91E63),
                                        Color(0xFF2196F3)
                                    )
                                )
                            )
                            .padding(2.5.dp) // Ring thickness
                    ) {
                        // Inner Avatar circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF1E88E5),
                                            Color(0xFF7B1FA2)
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
                                    "grad_cap" -> Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    "code" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    "star" -> Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    "palette" -> Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    "science" -> Icon(Icons.Default.Biotech, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    "vocal" -> Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    else -> {
                                        Text(
                                            text = studentName.take(1).uppercase(),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AppBottomNavigation(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // 1px solid rgba(156,39,176,0.12) top border
                drawLine(
                    color = primaryColor.copy(alpha = 0.12f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .navigationBarsPadding()
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTabs.forEach { tab ->
                val isSelected = currentTab.route == tab.route
                val tabColor by animateColorAsState(
                    targetValue = if (isSelected) primaryColor else SecondaryText,
                    label = "tabColor"
                )

                val tabModifier = if (isSelected) {
                    Modifier
                        .neumorphicInset(cornerRadius = 14.dp, depth = 2.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                } else {
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .then(tabModifier)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        )
                        .testTag("nav_tab_${tab.route}")
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        tint = tabColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = tabColor,
                        letterSpacing = 0.02.sp
                    )
                }
            }
        }
    }
}


@Composable
fun StarRatingBar(
    rating: Float,
    maxStars: Int = 5,
    onRatingChanged: ((Float) -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.StarHalf,
                contentDescription = "Star $i",
                tint = if (isFilled) Color(0xFFFFB74D) else NeumorphicShadowDark.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (onRatingChanged != null) {
                            Modifier.clickable { onRatingChanged(i.toFloat()) }
                        } else Modifier
                    )
            )
            if (i < maxStars) {
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}