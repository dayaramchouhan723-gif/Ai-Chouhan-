package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.StarNavigation
import com.example.ui.theme.ChouhanStarTheme
import com.example.ui.theme.ObsidianDark
import com.example.viewmodel.StarSecurityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val starViewModel: StarSecurityViewModel = viewModel()
            val uiState by starViewModel.uiState.collectAsState()

            // Runtime Permissions Request: Camera, Audio, Notifications
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                if (results[Manifest.permission.RECORD_AUDIO] == true) {
                    starViewModel.onAudioPermissionGranted()
                }
            }

            LaunchedEffect(Unit) {
                val permissionsToRequest = mutableListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            }

            ChouhanStarTheme(auraName = uiState.currentThemeAura) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianDark)
                        .statusBarsPadding()
                ) {
                    StarNavigation(viewModel = starViewModel)
                }
            }
        }
    }
}
