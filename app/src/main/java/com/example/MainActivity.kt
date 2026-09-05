package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.notification.NotificationHelper
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppHeader
import com.example.ui.components.BottomNavTab
import com.example.ui.screens.AudioNotesScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.RoutineScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.VarsityRoutineTheme
import com.example.ui.viewmodels.MainViewModel

import com.example.ui.screens.AuthScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val postNotifGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: true
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: true

        if (!postNotifGranted || !audioGranted) {
            Toast.makeText(
                this,
                "Permissions active for notifications and audio recording.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Notification Channels
        NotificationHelper.createNotificationChannels(this)

        // Request Permissions
        checkAndRequestPermissions()

        setContent {
            val themeIndex by viewModel.themePresetIndex.collectAsState()
            val isDark by viewModel.isDarkMode.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val profilePicUri by viewModel.profilePicUri.collectAsState()
            val avatarPreset by viewModel.avatarPreset.collectAsState()

            VarsityRoutineTheme(darkTheme = isDark, themePresetIndex = themeIndex) {
                if (!isLoggedIn) {
                    AuthScreen(viewModel = viewModel)
                } else {
                    var currentTab by remember { mutableStateOf<BottomNavTab>(BottomNavTab.Routine) }

                    Scaffold(
                        topBar = {
                            AppHeader(
                                studentName = currentUser,
                                appName = "UniSync",
                                subTitle = "Timer",
                                profilePicUri = profilePicUri,
                                avatarPreset = avatarPreset,
                                onNotificationClick = { currentTab = BottomNavTab.Settings },
                                onHeaderClick = { currentTab = BottomNavTab.Settings }
                            )
                        },
                        bottomBar = {
                            AppBottomNavigation(
                                currentTab = currentTab,
                                onTabSelected = { tab -> currentTab = tab }
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(
                                targetState = currentTab,
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ),
                                label = "tabTransition"
                            ) { tab ->
                                when (tab) {
                                    BottomNavTab.Routine -> RoutineScreen(viewModel = viewModel)
                                    BottomNavTab.Tasks -> TasksScreen(viewModel = viewModel)
                                    BottomNavTab.Community -> CommunityScreen(viewModel = viewModel)
                                    BottomNavTab.Settings -> SettingsScreen(viewModel = viewModel)
                                }
                            }

                        }
                    }
                }
            }
        }

    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

