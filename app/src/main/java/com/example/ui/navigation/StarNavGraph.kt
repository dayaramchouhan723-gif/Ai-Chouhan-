package com.example.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.ApiScreen
import com.example.ui.screens.ApiWorldModelScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.AuraControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FaceSetupScreen
import com.example.ui.screens.IntruderAlarmOverlayScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.NotificationsSystemScreen
import com.example.ui.screens.PersonalityVoiceScreen
import com.example.ui.screens.ProfilePremiumScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TouchGuardScreen
import com.example.ui.screens.TriggersScreen
import com.example.ui.screens.WorldModelScreen
import com.example.viewmodel.StarSecurityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun StarNavGraph(viewModel: StarSecurityViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var currentScreen by remember { mutableStateOf("splash") }

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

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            currentScreen == "splash" -> {
                SplashScreen(
                    onSplashFinished = {
                        if (currentUser != null) {
                            currentScreen = "dashboard"
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
                        currentScreen = "dashboard"
                    }
                )
            }

            currentScreen == "dashboard" -> {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateTo = { destination -> currentScreen = destination }
                )
            }

            currentScreen == "api_screen" -> {
                BackHandler { currentScreen = "dashboard" }
                ApiScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "world_model" -> {
                BackHandler { currentScreen = "dashboard" }
                WorldModelScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "triggers" -> {
                BackHandler { currentScreen = "dashboard" }
                TriggersScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "notifications" -> {
                BackHandler { currentScreen = "dashboard" }
                NotificationsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "touch_guard" -> {
                BackHandler { currentScreen = "dashboard" }
                TouchGuardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" },
                    onNavigateToFaceSetup = { currentScreen = "face_setup" }
                )
            }

            currentScreen == "app_lock" -> {
                BackHandler { currentScreen = "dashboard" }
                AppLockScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "face_setup" -> {
                BackHandler { currentScreen = "dashboard" }
                FaceSetupScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "aura_control" -> {
                BackHandler { currentScreen = "dashboard" }
                AuraControlScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "personality_voice" -> {
                BackHandler { currentScreen = "dashboard" }
                PersonalityVoiceScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "api_world" -> {
                BackHandler { currentScreen = "dashboard" }
                ApiWorldModelScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "notifications_system" -> {
                BackHandler { currentScreen = "dashboard" }
                NotificationsSystemScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" }
                )
            }

            currentScreen == "profile_premium" -> {
                BackHandler { currentScreen = "dashboard" }
                ProfilePremiumScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "dashboard" },
                    onSignedOut = {
                        currentUser = null
                        currentScreen = "auth"
                    }
                )
            }
        }

        // Intruder Alarm Overlay (Higher z-index when armed breach occurs)
        if (uiState.isAlarmActive) {
            IntruderAlarmOverlayScreen(viewModel = viewModel)
        }
    }
}
