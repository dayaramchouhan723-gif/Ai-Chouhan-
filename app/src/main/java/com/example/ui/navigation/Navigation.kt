package com.example.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.SanamFloatingDock
import com.example.ui.screens.ApiScreen
import com.example.ui.screens.ApiWorldModelScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.AuraControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AvatarGalleryScreen
import com.example.ui.screens.BuildProtocolScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ClaudeCodeScreen
import com.example.ui.screens.ControlDashboardScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeveloperPanelScreen
import com.example.ui.screens.FaceSetupScreen
import com.example.ui.screens.FaceStudioScreen
import com.example.ui.screens.FlowShiftDhaiScreen
import com.example.ui.screens.GfModeScreen
import com.example.ui.screens.GhostWorldScreen
import com.example.ui.screens.IntruderAlarmOverlayScreen
import com.example.ui.screens.LauncherScreen
import com.example.ui.screens.LiaChatScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OsWorldScreen
import com.example.ui.screens.PermissionsGuideScreen
import com.example.ui.screens.PersonalityVoiceScreen
import com.example.ui.screens.PinLockSetupScreen
import com.example.ui.screens.PrivacyDashboardScreen
import com.example.ui.screens.ProfilePremiumScreen
import com.example.ui.screens.PromptLibraryScreen
import com.example.ui.screens.SafetyConfirmationOverlay
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SocialMapManagerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ToolsHubScreen
import com.example.ui.screens.TouchGuardScreen
import com.example.ui.screens.TriggersHubScreen
import com.example.ui.screens.TriggersScreen
import com.example.ui.screens.UmiWorldScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.VoiceFocusOverlay
import com.example.ui.screens.VoiceLiveScreen
import com.example.ui.screens.WorldModelScreen
import com.example.ui.theme.LocalStarThemeTokens
import com.example.viewmodel.StarSecurityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

@Composable
fun StarNavigation(viewModel: StarSecurityViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current

    var currentUser by remember {
        mutableStateOf<FirebaseUser?>(
            try { Firebase.auth.currentUser } catch (_: Exception) { null }
        )
    }
    var currentScreen by remember { mutableStateOf("splash") }
    val backStack = remember { mutableStateListOf<String>() }

    fun navigateTo(destination: String) {
        if (currentScreen != destination) {
            backStack.add(currentScreen)
            currentScreen = destination
        }
    }

    fun navigateBack() {
        currentScreen = if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.size - 1)
        } else {
            "home"
        }
    }

    DisposableEffect(Unit) {
        var authInstance: FirebaseAuth? = null
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                viewModel.onUserSignedIn(
                    auth.currentUser?.displayName ?: "Dayaram Chouhan",
                    auth.currentUser?.email ?: ""
                )
            }
        }
        try {
            authInstance = Firebase.auth
            authInstance.addAuthStateListener(listener)
        } catch (_: Exception) {
            // Firebase optional — offline mode works seamlessly
        }
        onDispose {
            try {
                authInstance?.removeAuthStateListener(listener)
            } catch (_: Exception) { }
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    LaunchedEffect(uiState.pendingVoiceNavTarget) {
        val target = uiState.pendingVoiceNavTarget ?: return@LaunchedEffect
        viewModel.clearPendingVoiceNav()
        if (target == "__BACK__") {
            navigateBack()
        } else {
            if (target == "home") backStack.clear()
            navigateTo(target)
        }
    }

    val isAuthenticatedOrSkipped = currentUser != null || uiState.hasSkippedLogin || uiState.isPinConfigured
    val showDock = currentScreen !in listOf("splash", "auth", "pin_setup", "pin_unlock") &&
        isAuthenticatedOrSkipped && uiState.isAppUnlocked && !uiState.isAlarmActive && !uiState.isVoiceFocusActive

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background),
        containerColor = tokens.background,
        bottomBar = {
            if (showDock) {
                SanamFloatingDock(
                    currentScreen = currentScreen,
                    isListening = uiState.isWakeWordListening,
                    onNavigate = { dest ->
                        if (dest == "home") backStack.clear()
                        navigateTo(dest)
                    },
                    onMicOrbClick = {
                        viewModel.activateVoiceFocusMic()
                        if (!uiState.voiceFocusModeEnabled) {
                            navigateTo("voice_live")
                        }
                    },
                    onTripleTapOrb = {
                        navigateTo("developer_panel")
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
                            currentScreen = when {
                                uiState.isPinConfigured && !uiState.isAppUnlocked -> "pin_unlock"
                                currentUser != null || uiState.hasSkippedLogin -> "home"
                                else -> "auth"
                            }
                        }
                    )
                }

                currentScreen == "auth" || (!isAuthenticatedOrSkipped && currentScreen != "pin_setup") -> {
                    AuthScreen(
                        onAuthSuccess = {
                            viewModel.completeWelcomeContinue()
                            if (uiState.isPinConfigured) {
                                viewModel.unlockWithBiometric()
                                currentScreen = "home"
                            } else {
                                currentScreen = "pin_setup"
                            }
                        },
                        onContinueWithoutLogin = {
                            viewModel.completeSkipToPinSetup()
                            currentScreen = "pin_setup"
                        }
                    )
                }

                currentScreen == "pin_setup" -> {
                    PinLockSetupScreen(
                        isSetupMode = true,
                        fingerprintEnabled = uiState.fingerprintLockEnabled,
                        failedAttempts = uiState.failedAttempts,
                        onSetupComplete = { pin, fp ->
                            val ok = viewModel.setupPinAndUnlock(pin, fp)
                            if (ok) {
                                backStack.clear()
                                currentScreen = "home"
                            }
                            ok
                        },
                        onUnlockSuccess = { false },
                        onBiometricUnlocked = {
                            viewModel.unlockWithBiometric()
                            currentScreen = "home"
                        },
                        onSignOutBackToWelcome = {
                            viewModel.signOutKeepData()
                            currentScreen = "auth"
                        }
                    )
                }

                currentScreen == "pin_unlock" || (uiState.isPinConfigured && !uiState.isAppUnlocked) -> {
                    PinLockSetupScreen(
                        isSetupMode = false,
                        fingerprintEnabled = uiState.fingerprintLockEnabled,
                        failedAttempts = uiState.failedAttempts,
                        onSetupComplete = { _, _ -> false },
                        onUnlockSuccess = { pin ->
                            val ok = viewModel.unlockWithPin(pin)
                            if (ok) {
                                backStack.clear()
                                currentScreen = "home"
                            }
                            ok
                        },
                        onBiometricUnlocked = {
                            viewModel.unlockWithBiometric()
                            backStack.clear()
                            currentScreen = "home"
                        },
                        onSignOutBackToWelcome = {
                            viewModel.signOutKeepData()
                            currentScreen = "auth"
                        }
                    )
                }

                // 3. Home
                currentScreen == "home" -> {
                    DashboardScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) })
                }

                // 4. Chat
                currentScreen == "chat" -> {
                    BackHandler { navigateBack() }
                    ChatListScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 5. Voice Live
                currentScreen == "voice_live" -> {
                    BackHandler { navigateBack() }
                    VoiceLiveScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onTripleTapDev = { navigateTo("developer_panel") }
                    )
                }

                // 6. Control Dashboard (Phone 12 + PC 12 + Hardware Controls)
                currentScreen == "control_dashboard" -> {
                    BackHandler { navigateBack() }
                    ControlDashboardScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 7. Vault (PIN Pad + Fingerprint + Hidden Files)
                currentScreen == "vault" -> {
                    BackHandler { navigateBack() }
                    VaultScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 8. Prompt Library
                currentScreen == "prompt_library" -> {
                    BackHandler { navigateBack() }
                    PromptLibraryScreen(
                        viewModel = viewModel,
                        onUsePromptInChat = { navigateTo("chat") },
                        onNavigateBack = { navigateBack() }
                    )
                }

                // 9. Settings Full Control
                currentScreen == "settings_tab" || currentScreen == "settings" -> {
                    BackHandler { navigateBack() }
                    SettingsScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) })
                }

                // 10. Automation (Triggers +77 & World Model Hub)
                currentScreen == "triggers_tab" || currentScreen == "triggers_hub" -> {
                    BackHandler { navigateBack() }
                    TriggersHubScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }
                currentScreen == "triggers" -> {
                    BackHandler { navigateBack() }
                    TriggersScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 11. Developer Panel (Triple Tap Orb)
                currentScreen == "developer_panel" -> {
                    BackHandler { navigateBack() }
                    DeveloperPanelScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 12. Memory
                currentScreen == "memory" -> {
                    BackHandler { navigateBack() }
                    MemoryScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 13. Marketplace (3x3 9 Grid)
                currentScreen == "marketplace" -> {
                    BackHandler { navigateBack() }
                    MarketplaceScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 14. Face HUD Studio
                currentScreen == "face_studio" -> {
                    BackHandler { navigateBack() }
                    FaceStudioScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 15. Build Protocol
                currentScreen == "build_protocol" -> {
                    BackHandler { navigateBack() }
                    BuildProtocolScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 16. Claude Code
                currentScreen == "claude_code" -> {
                    BackHandler { navigateBack() }
                    ClaudeCodeScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 17. Ghost 10
                currentScreen == "ghost_world" -> {
                    BackHandler { navigateBack() }
                    GhostWorldScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 18. UMI 12
                currentScreen == "umi_world" -> {
                    BackHandler { navigateBack() }
                    UmiWorldScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 19. SANAM OS 9 + SANAM GO 9
                currentScreen == "os_world" -> {
                    BackHandler { navigateBack() }
                    OsWorldScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                // 20. Launcher
                currentScreen == "launcher" -> {
                    BackHandler { navigateBack() }
                    LauncherScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 21. LIA Chat
                currentScreen == "lia_chat" -> {
                    BackHandler { navigateBack() }
                    LiaChatScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 22. FlowShift + dhaibuilds
                currentScreen == "flow_shift" -> {
                    BackHandler { navigateBack() }
                    FlowShiftDhaiScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                // 23. Profile + Preserved Security Modules (Sign-out stays in Profile & preserves data)
                currentScreen == "profile_premium" -> {
                    BackHandler { navigateBack() }
                    ProfilePremiumScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onSignedOut = {
                            viewModel.signOutToGuestKeepData()
                            currentUser = null
                        }
                    )
                }

                currentScreen == "avatar_gallery" -> {
                    BackHandler { navigateBack() }
                    AvatarGalleryScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "tools_hub" -> {
                    BackHandler { navigateBack() }
                    ToolsHubScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) }, onNavigateBack = { navigateBack() })
                }

                currentScreen == "permissions_guide" -> {
                    BackHandler { navigateBack() }
                    PermissionsGuideScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "privacy_dashboard" -> {
                    BackHandler { navigateBack() }
                    PrivacyDashboardScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "orb" || currentScreen == "aura_control" -> {
                    BackHandler { navigateBack() }
                    AuraControlScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "app_lock" -> {
                    BackHandler { navigateBack() }
                    AppLockScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "gf_mode" -> {
                    BackHandler { navigateBack() }
                    GfModeScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
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
                    FaceSetupScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "api_screen" -> {
                    BackHandler { navigateBack() }
                    ApiScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "social_map_manager" -> {
                    BackHandler { navigateBack() }
                    SocialMapManagerScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "notifications" || currentScreen == "notifications_system" -> {
                    BackHandler { navigateBack() }
                    NotificationsScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "personality_voice" -> {
                    BackHandler { navigateBack() }
                    PersonalityVoiceScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "world_model" -> {
                    BackHandler { navigateBack() }
                    WorldModelScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                currentScreen == "api_world" -> {
                    BackHandler { navigateBack() }
                    ApiWorldModelScreen(viewModel = viewModel, onNavigateBack = { navigateBack() })
                }

                else -> {
                    DashboardScreen(viewModel = viewModel, onNavigateTo = { navigateTo(it) })
                }
            }

            if (uiState.isVoiceFocusActive) {
                VoiceFocusOverlay(
                    viewModel = viewModel,
                    onVoiceNavRequest = { target ->
                        if (target == "home") backStack.clear()
                        navigateTo(target)
                    }
                )
            }

            uiState.pendingConfirmation?.let { req ->
                SafetyConfirmationOverlay(
                    request = req,
                    onAllow = { viewModel.resolvePendingConfirmation(true) },
                    onDeny = { viewModel.resolvePendingConfirmation(false) }
                )
            }

            if (uiState.isAlarmActive) {
                IntruderAlarmOverlayScreen(viewModel = viewModel)
            }
        }
    }
}
