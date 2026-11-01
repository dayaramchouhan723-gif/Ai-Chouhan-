package com.example.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ApiScreen
import com.example.ui.screens.ApiWorldModelScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.AuraControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FaceSetupScreen
import com.example.ui.screens.GfModeScreen
import com.example.ui.screens.IntruderAlarmOverlayScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.NotificationsSystemScreen
import com.example.ui.screens.PersonalityVoiceScreen
import com.example.ui.screens.ProfilePremiumScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SocialMapManagerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TouchGuardScreen
import com.example.ui.screens.TriggersHubScreen
import com.example.ui.screens.TriggersScreen
import com.example.ui.screens.WorldModelScreen
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

enum class BottomNavTab(val route: String, val label: String, val icon: ImageVector, val tag: String) {
    HOME("home", "Home", Icons.Default.Home, "bottom_nav_tab_home"),
    CHAT("chat", "Chat", Icons.Default.ChatBubbleOutline, "bottom_nav_tab_chat"),
    ORB("orb", "Orb", Icons.Default.Star, "bottom_nav_tab_orb"),
    TRIGGERS("triggers_tab", "Triggers", Icons.Default.Bolt, "bottom_nav_tab_triggers"),
    SETTINGS("settings_tab", "Settings", Icons.Default.Settings, "bottom_nav_tab_settings")
}

@Composable
fun StarNavigation(viewModel: StarSecurityViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var currentScreen by remember { mutableStateOf("splash") }
    var currentTab by remember { mutableStateOf(BottomNavTab.HOME) }
    val backStack = remember { mutableStateListOf<String>() }

    fun navigateTo(destination: String) {
        if (currentScreen != destination) {
            backStack.add(currentScreen)
            currentScreen = destination
        }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.size - 1)
        } else {
            // Default back to active tab base
            currentScreen = when (currentTab) {
                BottomNavTab.HOME -> "home"
                BottomNavTab.CHAT -> "chat"
                BottomNavTab.ORB -> "orb"
                BottomNavTab.TRIGGERS -> "triggers_tab"
                BottomNavTab.SETTINGS -> "settings_tab"
            }
        }
    }

    // Auth state listener
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                viewModel.onUserSignedIn(
                    auth.currentUser?.displayName ?: "Sovereign Commander",
                    auth.currentUser?.email ?: ""
                )
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    val isTopLevelDestination = currentScreen in listOf("home", "chat", "orb", "triggers_tab", "settings_tab")
    val showBottomBar = currentUser != null && currentScreen != "splash" && currentScreen != "auth" && !uiState.isAlarmActive

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
        containerColor = ObsidianDark,
        bottomBar = {
            if (showBottomBar) {
                StarBottomNavigation(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        currentTab = tab
                        backStack.clear()
                        currentScreen = when (tab) {
                            BottomNavTab.HOME -> "home"
                            BottomNavTab.CHAT -> "chat"
                            BottomNavTab.ORB -> "orb"
                            BottomNavTab.TRIGGERS -> "triggers_tab"
                            BottomNavTab.SETTINGS -> "settings_tab"
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                currentScreen == "splash" -> {
                    SplashScreen(
                        onSplashFinished = {
                            if (currentUser != null) {
                                currentScreen = "home"
                                currentTab = BottomNavTab.HOME
                            } else {
                                currentScreen = "auth"
                            }
                        }
                    )
                }

                currentUser == null || currentScreen == "auth" -> {
                    AuthScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                            currentScreen = "home"
                            currentTab = BottomNavTab.HOME
                        }
                    )
                }

                // 1. Home Tab: Dashboard with Profile & Premium access
                currentScreen == "home" -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateTo = { dest -> navigateTo(dest) }
                    )
                }

                // 2. Chat Tab: ChatList placeholder and security alerts
                currentScreen == "chat" -> {
                    ChatListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                // 3. Orb Tab: AuraControlScreen
                currentScreen == "orb" || currentScreen == "aura_control" -> {
                    if (!isTopLevelDestination) {
                        BackHandler { navigateBack() }
                    }
                    AuraControlScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                // 4. Triggers Tab: TriggersScreen + WorldModelScreen Hub
                currentScreen == "triggers_tab" || currentScreen == "triggers_hub" -> {
                    TriggersHubScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                // 5. Settings Tab: Hub for AppLock, GF Mode, Touch Guard, Face Setup, API, Notifications
                currentScreen == "settings_tab" || currentScreen == "settings" -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateTo = { dest -> navigateTo(dest) }
                    )
                }

                // Sub-screens with BackHandler
                currentScreen == "profile_premium" -> {
                    BackHandler { navigateBack() }
                    ProfilePremiumScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onSignedOut = {
                            currentUser = null
                            currentScreen = "auth"
                        }
                    )
                }

                currentScreen == "app_lock" -> {
                    BackHandler { navigateBack() }
                    AppLockScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "gf_mode" -> {
                    BackHandler { navigateBack() }
                    GfModeScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "touch_guard" -> {
                    BackHandler { navigateBack() }
                    TouchGuardScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onNavigateToFaceSetup = { navigateTo("face_setup") }
                    )
                }

                currentScreen == "face_setup" -> {
                    BackHandler { navigateBack() }
                    FaceSetupScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "api_screen" -> {
                    BackHandler { navigateBack() }
                    ApiScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "social_map_manager" -> {
                    BackHandler { navigateBack() }
                    SocialMapManagerScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "notifications" || currentScreen == "notifications_system" -> {
                    BackHandler { navigateBack() }
                    NotificationsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "personality_voice" -> {
                    BackHandler { navigateBack() }
                    PersonalityVoiceScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "triggers" -> {
                    BackHandler { navigateBack() }
                    TriggersScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "world_model" -> {
                    BackHandler { navigateBack() }
                    WorldModelScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                currentScreen == "api_world" -> {
                    BackHandler { navigateBack() }
                    ApiWorldModelScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                else -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateTo = { dest -> navigateTo(dest) }
                    )
                }
            }

            // High-priority Intruder Alarm Overlay (Stealth breach or armed touch)
            if (uiState.isAlarmActive) {
                IntruderAlarmOverlayScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StarBottomNavigation(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101014))
            .border(1.dp, BorderDark, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .navigationBarsPadding()
            .testTag("star_bottom_navigation")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                val itemColor = if (isSelected) CrimsonPrimary else TextGrey

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .testTag(tab.tag),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isSelected) Color(0xFF2A1015) else Color.Transparent,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = itemColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.label,
                        color = itemColor,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
