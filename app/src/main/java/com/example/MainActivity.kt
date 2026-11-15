package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.StarNavigation
import com.example.ui.theme.ChouhanStarTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.StarSecurityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val starViewModel: StarSecurityViewModel = viewModel()
            val uiState by starViewModel.uiState.collectAsState()

            ChouhanStarTheme(
                auraName = uiState.currentThemeAura,
                coreThemeMode = uiState.coreThemeMode,
                moodTheme = uiState.moodTheme,
                largeFont = uiState.largeFontEnabled
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                ) {
                    StarNavigation(viewModel = starViewModel)
                }
            }
        }
    }
}
