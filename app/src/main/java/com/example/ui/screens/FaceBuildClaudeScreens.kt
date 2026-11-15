package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.SubScreenHeader
import com.example.ui.components.WireframeStar
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

// Screen 14: Face Studio (GENERATE / FIND ONE / RUN SCAN tabs, Wireframe Telemetry HUD, 12 HUD Grid, 3 styles)
@Composable
fun FaceStudioScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    var activeTab by remember { mutableStateOf("GENERATE") }
    var hudPrompt by remember { mutableStateOf("Arc Reactor Cyan HUD + Rajputana Star Halo") }
    var selectedStyle by remember { mutableStateOf("HUD") }
    var smileMode by remember { mutableStateOf(true) }
    var selectedHud by remember { mutableStateOf(SanamCatalog.face12HudGrid.first()) }
    val styles = listOf("Orb", "HUD", "Anime")

    val scanPulse = rememberInfiniteTransition(label = "FaceScan")
    val scanAlpha by scanPulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanAlpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("face_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Face & Wireframe HUD Studio",
                subtitle = "12 HUD Presets • Orb / HUD / Anime • Telemetry Scan",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("GENERATE", "FIND ONE", "RUN SCAN").forEach { tab ->
                    val sel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (sel) NeonBlue else Color(0xFF1A1A2E))
                            .clickable {
                                activeTab = tab
                                if (tab == "RUN SCAN") {
                                    viewModel.vibrate(80)
                                    viewModel.sendChatMessage("Wireframe face scan: Confidence 97.8%, Liveness Verified")
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tab, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue.copy(alpha = scanAlpha)) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(156.dp), contentAlignment = Alignment.Center) {
                        WireframeStar(size = 156.dp, primaryColor = NeonBlue, glowColor = NeonPink, animated = true)
                        Image(
                            painter = painterResource(
                                id = when (selectedStyle) {
                                    "Anime" -> if (smileMode) R.drawable.avatar_anime_wink else R.drawable.avatar_anime_pout
                                    "Orb" -> R.drawable.img_chouhan_star_icon
                                    else -> if (smileMode) R.drawable.avatar_zara_open else R.drawable.avatar_zara_front
                                }
                            ),
                            contentDescription = "Face Wireframe Preview",
                            modifier = Modifier.size(96.dp).clip(CircleShape).border(2.dp, NeonBlue, CircleShape)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Telemetry HUD Pills
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "FACE LOCK ACTIVE" to SuccessGreen,
                            "CONFIDENCE 97.8%" to NeonBlue,
                            "AGE 25-30" to StarGold,
                            "EMOTION ${uiState.avatarEmotion.name}" to NeonPink,
                            "LIVENESS VERIFIED" to SuccessGreen
                        ).forEach { (pill, pillColor) ->
                            Box(
                                modifier = Modifier
                                    .background(pillColor.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
                                    .border(1.dp, pillColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(pill, color = pillColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Active: $selectedHud ($selectedStyle)", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    if (activeTab == "GENERATE") {
                        OutlinedTextField(
                            value = hudPrompt,
                            onValueChange = { hudPrompt = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("HUD Generation Prompt") }
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    // 3 Styles: Orb / HUD / Anime + Smile / Serious Toggle
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        styles.forEach { st ->
                            val sel = selectedStyle == st
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (sel) NeonPink else Color(0xFF141628))
                                    .clickable { selectedStyle = st }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(st, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (smileMode) SuccessGreen.copy(alpha = 0.25f) else NeonPurple.copy(alpha = 0.25f))
                                .border(1.dp, if (smileMode) SuccessGreen else NeonPurple, RoundedCornerShape(10.dp))
                                .clickable { smileMode = !smileMode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (smileMode) "Smile Face" else "Serious HUD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("12 HUD PRESET GRID", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.face12HudGrid, accent = NeonBlue) {
                selectedHud = it
                viewModel.sendChatMessage("HUD switched to $it")
            }
        }

        item {
            Button(
                onClick = { onNavigateTo("face_setup") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text("Open Biometric 3-Angle Face Lock Setup", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 15: Build Protocol (BUILD YOUR OWN S.A.N.A.M. STEP 1/2/3/4 + ZARA voice)
@Composable
fun BuildProtocolScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val steps = listOf(
        "STEP 1: FACE ENROLL & WIREFRAME ORB",
        "STEP 2: VOICE WAKE WORD ('HEY SANAM')",
        "STEP 3: BRAIN MEMORY & MULTI-LANGUAGE",
        "STEP 4: BUILD & LOCAL VAULT PACKAGE"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("build_protocol_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "BUILD YOUR OWN S.A.N.A.M.",
                subtitle = uiState.buildStatusText,
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        items(steps.indices.toList()) { index ->
            val done = uiState.buildProtocolStep >= index + 1
            GlassHudCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (done) SuccessGreen else NeonBlue.copy(alpha = 0.3f)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.Build,
                        contentDescription = null,
                        tint = if (done) SuccessGreen else TextGrey,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(steps[index], color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.runFullBuildProtocol() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("build_now_free_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("BUILD NOW FREE NO CODING", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 16: Claude Code (Left sidebar New/Projects/Artifacts/Customize + Right Chat with Bug Fix POST /charges, Test, Migration, Review)
@Composable
fun ClaudeCodeScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val sidebarTabs = listOf("New", "Projects", "Artifacts", "Customize")
    var promptText by remember { mutableStateOf("Fix double charge on POST /charges endpoint") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(14.dp)
            .testTag("claude_code_screen")
    ) {
        SubScreenHeader(
            title = "Claude Code Studio",
            subtitle = "API Audit • POST /charges Double Charge Fix",
            onBack = onNavigateBack,
            onInstallApk = { viewModel.exportApkAction() }
        )

        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Left Sidebar
            Column(
                modifier = Modifier
                    .width(92.dp)
                    .fillMaxSize()
                    .background(Color(0xFF121426), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF2B2B44), RoundedCornerShape(14.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sidebarTabs.forEach { tab ->
                    val sel = uiState.claudeSidebarTab == tab
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) NeonBlue else Color.Transparent)
                            .clickable { viewModel.setClaudeSidebarTab(tab) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        Text(tab, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Right Coding Chat & Actions Pane
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color(0xFF0E101E), RoundedCornerShape(14.dp))
                    .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Tab: ${uiState.claudeSidebarTab} — POST /charges Guard", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Bug Fix", "Test", "Migration", "Review").forEach { action ->
                        Button(
                            onClick = { viewModel.runClaudeCodeAction(action) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(action, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(uiState.claudeCodeLogs) { log ->
                        Text(log, color = SuccessGreen, fontSize = 11.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.runClaudeCodeAction("Bug Fix") },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        Text("Run", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(74.dp))
    }
}
