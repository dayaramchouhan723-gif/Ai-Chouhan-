package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.SubScreenHeader
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.MoodGhostPurple
import com.example.ui.theme.MoodNeonGreen
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

// Screen 17: Ghost (10-item Stealth Grid)
@Composable
fun GhostWorldScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val tokens = LocalStarThemeTokens.current
    var activeGhostModule by remember { mutableStateOf("Stealth Mic") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("ghost_world_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "GHOST Tactical Protocol",
                subtitle = "10-Module Counter-Surveillance & Decoy Suite",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = MoodGhostPurple) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ACTIVE GHOST NODE: $activeGhostModule", color = MoodGhostPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Zero network trace • Hardware level stealth", color = TextGrey, fontSize = 11.sp)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SanamCatalog.ghost10Grid.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { module ->
                            val sel = activeGhostModule == module
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (sel) MoodGhostPurple.copy(alpha = 0.3f) else Color(0x661A1A2E))
                                    .border(1.dp, if (sel) MoodGhostPurple else Color(0xFF2B2B44), RoundedCornerShape(14.dp))
                                    .clickable {
                                        activeGhostModule = module
                                        viewModel.sendChatMessage("Ghost Protocol toggled: $module")
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(module, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 18: UMI (12 Expressions + Local AI NO CLOUD / NO API + Chess/Draw/Game/Notch)
@Composable
fun UmiWorldScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val currentExp = SanamCatalog.umiExpressions.getOrElse(uiState.umiExpressionIndex) {
        SanamCatalog.umiExpressions.first()
    }
    var activeMiniTool by remember { mutableStateOf("Chess") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("umi_world_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "UMI Offline Companion",
                subtitle = "12 Expressions • Local AI NO CLOUD / NO API",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0E1629))
                            .border(2.dp, NeonBlue, CircleShape)
                            .clickable { viewModel.nextUmiExpression() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentExp.second, color = NeonBlue, fontSize = 26.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("${currentExp.first} (Tap orb to switch)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("LOCAL AI • NO CLOUD • NO API KEY NEEDED", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Text("UMI 12 EXPRESSIONS GRID", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.umiExpressions.map { it.first }, accent = NeonBlue) { chosen ->
                val idx = SanamCatalog.umiExpressions.indexOfFirst { it.first == chosen }.coerceAtLeast(0)
                viewModel.nextUmiExpression(idx)
            }
        }

        item {
            Text("OFFLINE MINI DECK (CHESS / DRAW / GAME / NOTCH)", color = StarGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Chess", "Draw", "Game", "Notch").forEach { tool ->
                    val sel = activeMiniTool == tool
                    Button(
                        onClick = { activeMiniTool = tool },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (sel) NeonBlue else Color(0xFF1A1A2E))
                    ) {
                        Text(tool, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = when (activeMiniTool) {
                        "Chess" -> "UMI Chess Local Engine ready (White e2 -> e4 opening queued offline)."
                        "Draw" -> "UMI Sketch Pad active — draw doodle gestures on canvas."
                        "Game" -> "UMI Reaction Reflex Score: 240 pts (Zero-latency offline)."
                        else -> "Dynamic Island / Notch Pill widget synced with CHOUHAN STAR notifications."
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 19: SANAM OS (9-Item Grid + Neon Pink Clock 10:45 + SANAM GO 9 Grid)
@Composable
fun OsWorldScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val tokens = LocalStarThemeTokens.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("os_world_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "SANAM OS Desktop",
                subtitle = "9-Grid OS Matrix • Neon Pink Clock • SANAM GO 9",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // Neon Pink Clock Widget
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPink) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("10:45", color = NeonPink, fontSize = 36.sp, fontWeight = FontWeight.Black)
                        Text("SANAM OS HUD • ALL SENSORS ARMED", color = MoodNeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onNavigateTo("launcher") },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                    ) {
                        Text("Open Launcher", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("SANAM OS 9 GRID", color = MoodNeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.os9Grid, accent = MoodNeonGreen) { item ->
                if (item == "Launcher Pro") onNavigateTo("launcher") else viewModel.sendChatMessage("SANAM OS triggered: $item")
            }
        }

        item {
            Text("SANAM GO 9 QUICK GRID", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.go9Grid, accent = NeonPink) { item ->
                when (item) {
                    "Share APK" -> viewModel.exportApkAction()
                    "Vault Lock" -> onNavigateTo("vault")
                    "Torch Toggle" -> viewModel.toggleTorch(true)
                    else -> viewModel.sendChatMessage("SANAM GO action: $item")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
