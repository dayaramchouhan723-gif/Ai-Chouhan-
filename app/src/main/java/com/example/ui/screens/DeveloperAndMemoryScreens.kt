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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.ZaraAnimatedAvatarOrb
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

// Screen 11: Developer Panel (Triggered via Triple Tap on Center Orb)
@Composable
fun DeveloperPanelScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    var groqTestResult by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("developer_panel_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Developer Command Panel",
                subtitle = "Triple-Tap Orb Console • 24 Tools • 5 AI Connectors • 4 Modes",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // 4 System Modes
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("4 SYSTEM MODES", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SanamCatalog.systemModes.forEach { mode ->
                            val sel = uiState.systemMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (sel) NeonBlue else Color(0xFF141628))
                                    .clickable { viewModel.setSystemMode(mode) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(mode.substringBefore(" "), color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 5 AI Connectors
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPurple.copy(alpha = 0.5f)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("5 AI CONNECTORS", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SanamCatalog.aiConnectors.forEach { conn ->
                        Text("• $conn", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.simulateWakeWord("Hey Sanam", "status report") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Test 'Hey Sanam'", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.callGroqWithJsonRetry("System telemetry check") { groqTestResult = it } },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Groq JSON Retry", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (groqTestResult.isNotBlank()) {
                        Text(groqTestResult, color = SuccessGreen, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // 24 Tools Catalog
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("24 SYSTEM TOOLS REGISTRY", color = StarGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    SanamCatalog.twentyFourTools.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pair.forEach { tool ->
                                Text(tool, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 12: Memory (Center Orb + 4 Cards + Persistent Room Notes/Todos/Birthdays + JSON Backup Link)
@Composable
fun MemoryScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val agent = remember(uiState.activeAgentId) {
        SanamCatalog.fourteenAgents.find { it.id == uiState.activeAgentId } ?: SanamCatalog.fourteenAgents.first()
    }
    var selectedSection by remember { mutableStateOf("ABOUT ME") }
    var newNoteType by remember { mutableStateOf("TODO") }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("memory_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "SANAM Sovereign Memory",
                subtitle = "Notes • Todos • Birthdays • Last 50 Chats • Backup JSON",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ZaraAnimatedAvatarOrb(
                    agent = agent,
                    isSpeaking = uiState.isSanamResponding,
                    isOffline = uiState.isOfflineMode,
                    audioRms = uiState.wakeWordAudioRms,
                    avatarSize = 165.dp,
                    selectedAvatarId = uiState.selectedAvatarId,
                    emotion = uiState.avatarEmotion,
                    onOrbClick = { onNavigateTo("avatar_gallery") },
                    onTripleTapOrb = { onNavigateTo("developer_panel") }
                )
            }
        }

        item {
            val cards = listOf("ABOUT ME", "PROJECTS", "PAST CONVERSATIONS", "PREFERENCES")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cards.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { card ->
                            val sel = selectedSection == card
                            GlassHudCard(
                                modifier = Modifier.weight(1f).testTag("memory_card_$card"),
                                borderColor = if (sel) NeonBlue else Color(0xFF2B2B44),
                                onClick = { selectedSection = card }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(card, color = if (sel) NeonBlue else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = when (card) {
                                            "ABOUT ME" -> "Dayaram Chouhan • Owner"
                                            "PROJECTS" -> "${uiState.dhaiBuilds.size} Active Builds"
                                            "PAST CONVERSATIONS" -> "${uiState.chatMessages.size} Synced Messages"
                                            else -> "${uiState.currentLanguage} • ${uiState.moodTheme}"
                                        },
                                        color = TextGrey,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPurple.copy(alpha = 0.5f)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("ACTIVE MEMORY VIEW: $selectedSection", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    when (selectedSection) {
                        "ABOUT ME" -> {
                            Text("Owner: Dayaram Chouhan (Personal Lifetime Edition v2.2.8)", color = Color.White, fontSize = 13.sp)
                            Text("Likes: ${uiState.ownerLikes}", color = NeonBlue, fontSize = 12.sp)
                            Text("Location Radar: ${uiState.mapWeatherTemp}", color = TextGrey, fontSize = 12.sp)
                        }
                        "PROJECTS" -> uiState.dhaiBuilds.forEach {
                            Text("• ${it.name} (${it.stack}) [${it.status}]", color = Color.White, fontSize = 12.sp)
                        }
                        "PAST CONVERSATIONS" -> uiState.chatMessages.takeLast(5).forEach {
                            Text("• [${it.senderName}] ${it.text}", color = Color.White, fontSize = 12.sp)
                        }
                        else -> uiState.roomWorldModel.forEach {
                            Text("• ${it.category}: ${it.title} -> ${it.learnedAction}", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Persistent Notes, To-dos, Reminders & Birthdays + Backup JSON
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue.copy(alpha = 0.55f)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ADD LOCAL NOTE / TO-DO / REMINDER / BIRTHDAY", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("NOTE", "TODO", "REMINDER", "BIRTHDAY").forEach { t ->
                            val sel = newNoteType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) NeonBlue else Color(0xFF141628))
                                    .clickable { newNoteType = t }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, color = if (sel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    androidx.compose.material3.OutlinedTextField(
                        value = newNoteTitle,
                        onValueChange = { newNoteTitle = it },
                        placeholder = { Text("Title...", color = TextGrey) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        placeholder = { Text("Details...", color = TextGrey) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (newNoteTitle.isNotBlank() || newNoteContent.isNotBlank()) {
                                    viewModel.addMemoryNote(newNoteType, newNoteTitle, newNoteContent)
                                    newNoteTitle = ""
                                    newNoteContent = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Save Local", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onNavigateTo("privacy_dashboard") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Export JSON", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(uiState.memoryItems.filter { it.itemType != "CHAT" }.size) { idx ->
            val mem = uiState.memoryItems.filter { it.itemType != "CHAT" }[idx]
            GlassHudCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (mem.isCompleted) SuccessGreen else Color(0xFF2B2B44),
                onClick = { viewModel.toggleMemoryTodo(mem.id, !mem.isCompleted) }
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${mem.itemType} • ${mem.title}", color = if (mem.isCompleted) SuccessGreen else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        if (mem.content.isNotBlank()) {
                            Text(mem.content, color = TextGrey, fontSize = 11.sp)
                        }
                    }
                    Text(if (mem.isCompleted) "DONE ✓" else "TAP TO CHECK", color = if (mem.isCompleted) SuccessGreen else StarGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 13: Marketplace (3x3 = 9 Grid)
@Composable
fun MarketplaceScreen(
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
            .testTag("marketplace_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "CHOUHAN STAR Marketplace",
                subtitle = "100% Free Forever Modules (3x3 Grid)",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SanamCatalog.marketplaceItems.chunked(3).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { item ->
                            GlassHudCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(118.dp)
                                    .testTag("market_item_${item.id}"),
                                borderColor = NeonBlue.copy(alpha = 0.45f),
                                onClick = { onNavigateTo(item.targetScreen) }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(NeonPink.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(item.badge, color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(item.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(item.subtitle, color = TextGrey, fontSize = 10.sp, maxLines = 2)
                                    }
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
