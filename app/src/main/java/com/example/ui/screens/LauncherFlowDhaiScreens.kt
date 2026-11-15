package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassHudCard
import com.example.ui.components.SubScreenHeader
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel
import kotlinx.coroutines.delay

private data class LauncherAppShortcut(val name: String, val pkg: String, val icon: ImageVector, val color: Color)

// Screen 20: Launcher (WhatsApp/YouTube/Camera/Gallery/Spotify/Chrome + Search + Clock/Weather/Music Widgets)
@Composable
fun LauncherScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }

    val shortcuts = remember {
        listOf(
            LauncherAppShortcut("WhatsApp", "com.whatsapp", Icons.Default.Chat, SuccessGreen),
            LauncherAppShortcut("YouTube", "com.google.android.youtube", Icons.Default.VideoLibrary, NeonPink),
            LauncherAppShortcut("Camera", "android.media.action.IMAGE_CAPTURE", Icons.Default.CameraAlt, NeonBlue),
            LauncherAppShortcut("Gallery", "com.google.android.apps.photos", Icons.Default.PhotoLibrary, StarGold),
            LauncherAppShortcut("Spotify", "com.spotify.music", Icons.Default.MusicNote, SuccessGreen),
            LauncherAppShortcut("Chrome", "com.android.chrome", Icons.Default.Public, NeonPurple)
        )
    }

    val visibleApps = remember(search) {
        if (search.isBlank()) shortcuts else shortcuts.filter { it.name.contains(search, true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("launcher_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "CHOUHAN STAR Launcher",
                subtitle = "App Dock • Clock • Weather • Music Widgets",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // Widgets: Clock, Weather, Music
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassHudCard(modifier = Modifier.weight(1f), borderColor = NeonBlue) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CLOCK", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("10:45 PM", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                GlassHudCard(modifier = Modifier.weight(1f), borderColor = StarGold) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("WEATHER", color = StarGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(uiState.mapWeatherTemp.substringBefore("•").trim(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                GlassHudCard(modifier = Modifier.weight(1f), borderColor = NeonPink) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("MUSIC", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Lo-Fi Vibe", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search apps (WhatsApp, YouTube, Camera...)", color = TextGrey, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                visibleApps.chunked(3).forEach { rowApps ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowApps.forEach { app ->
                            GlassHudCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(92.dp),
                                borderColor = app.color.copy(alpha = 0.5f),
                                onClick = {
                                    val launch = context.packageManager.getLaunchIntentForPackage(app.pkg)
                                    if (launch != null) {
                                        context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                    } else {
                                        Toast.makeText(context, "${app.name} queued in CHOUHAN STAR Launcher", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(app.icon, contentDescription = app.name, tint = app.color, modifier = Modifier.size(26.dp))
                                    Spacer(Modifier.height(6.dp))
                                    Text(app.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 22: FlowShift + dhaibuilds (Padhai -> Gaane -> Coding -> Journal + New/Open/Share Build)
@Composable
fun FlowShiftDhaiScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val context = LocalContext.current
    var remainingSecs by remember(uiState.flowShiftSeconds, uiState.flowShiftStep) {
        mutableStateOf(uiState.flowShiftSeconds)
    }
    var newBuildName by remember { mutableStateOf("") }

    LaunchedEffect(uiState.flowShiftRunning, uiState.flowShiftStep) {
        while (uiState.flowShiftRunning && remainingSecs > 0) {
            delay(1000L)
            remainingSecs -= 1
        }
    }

    val mins = remainingSecs / 60
    val secs = remainingSecs % 60
    val timerText = String.format(java.util.Locale.US, "%02d:%02d", mins, secs)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("flow_shift_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "FlowShift & dhaibuilds",
                subtitle = "Padhai -> Gaane -> Coding -> Journal + App Builds",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // FlowShift Flow Chain
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ACTIVE FLOW: ${uiState.flowShiftStep.uppercase()}", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(timerText, color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Padhai", "Gaane", "Coding", "Journal").forEach { step ->
                            val sel = uiState.flowShiftStep == step
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (sel) NeonBlue else Color(0xFF141628))
                                    .clickable { viewModel.startFlowShift(step) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(step, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.toggleFlowShiftPause() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (uiState.flowShiftRunning) NeonPink else SuccessGreen),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(if (uiState.flowShiftRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(Modifier.width(6.dp))
                        Text(if (uiState.flowShiftRunning) "Pause Session" else "Resume Session", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // dhaibuilds Projects + New / Open / Share Build
        item {
            Text("DHAIBUILDS STUDIO", color = StarGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newBuildName,
                    onValueChange = { newBuildName = it },
                    placeholder = { Text("New project name...", color = TextGrey, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.addDhaiBuildProject(newBuildName, "Kotlin + Compose")
                        newBuildName = ""
                    },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StarGold)
                ) {
                    Text("New Build", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(uiState.dhaiBuilds, key = { it.id }) { proj ->
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(proj.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${proj.stack} • ${proj.status} • ${proj.updated}", color = TextGrey, fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { viewModel.sendChatMessage("Opened dhaibuilds project: ${proj.name}") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Open", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "CHOUHAN STAR Build: ${proj.name} (${proj.stack})")
                                }
                                context.startActivity(Intent.createChooser(send, "Share Build").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Share", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
