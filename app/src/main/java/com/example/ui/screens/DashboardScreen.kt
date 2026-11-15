package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.InstallApkTopButton
import com.example.ui.components.LiveAudioWaveform
import com.example.ui.components.SanamProtocolBar
import com.example.ui.components.ZaraAnimatedAvatarOrb
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.ui.theme.YellowSubtitle
import com.example.viewmodel.StarSecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val activeAgent = remember(uiState.activeAgentId) {
        SanamCatalog.fourteenAgents.find { it.id == uiState.activeAgentId } ?: SanamCatalog.fourteenAgents.first()
    }
    var quickInput by remember { mutableStateOf("") }
    val currentClock = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(horizontal = 14.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. TOP BAR: CHOUHAN STAR Logo + Good Evening, Dayaram + Time + Battery + Net + Mood + APK + Settings + Bell
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTo("profile_premium") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                        contentDescription = "CHOUHAN STAR Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, tokens.primaryAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = uiState.customAppName,
                            color = tokens.onBackgroundText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Good Evening, Dayaram",
                            color = StarGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    InstallApkTopButton(onClick = { viewModel.exportApkAction() })
                    IconButton(
                        onClick = { onNavigateTo("notifications") },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_bell_button")
                    ) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = tokens.onBackgroundText)
                    }
                    IconButton(
                        onClick = { onNavigateTo("settings_tab") },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_settings_button")
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = tokens.primaryAccent)
                    }
                }
            }

            // Telemetry Strip: Time + Battery + Net + Mood
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TelemetryChip(text = currentClock, color = NeonBlue)
                TelemetryChip(
                    text = "${uiState.batteryPercent}%",
                    color = SuccessGreen,
                    icon = Icons.Default.BatteryChargingFull
                )
                TelemetryChip(
                    text = if (uiState.isOfflineMode) "Offline Safe" else "Net Online",
                    color = if (uiState.isOfflineMode) YellowSubtitle else SuccessGreen
                )
                TelemetryChip(text = uiState.moodTheme, color = NeonPink)
                TelemetryChip(text = uiState.detectedLanguage, color = NeonPurple)
            }
        }

        // Top 14 Agents Switcher Strip
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SanamCatalog.fourteenAgents) { agent ->
                    val selected = agent.id == uiState.activeAgentId
                    val accent = Color(agent.accentHex)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) accent.copy(alpha = 0.25f) else tokens.cardSurface)
                            .border(1.dp, if (selected) accent else Color(0xFF2B2B44), RoundedCornerShape(14.dp))
                            .clickable {
                                viewModel.selectAgent(agent.id)
                                if (agent.id == "lia") onNavigateTo("lia_chat")
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("agent_switch_${agent.id}")
                    ) {
                        Text(
                            text = agent.name,
                            color = if (selected) Color.White else tokens.mutedText,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. Protocol Bar: S.A.N.A.M. // BUILD PROTOCOL 01 FACE / 02 VOICE / 03 BRAIN / 04 BUILD
        item {
            SanamProtocolBar(
                states = uiState.protocolStates,
                onClick = { onNavigateTo("build_protocol") }
            )
        }

        // 3. Center Big Avatar (220x220) + Mood + Waveform + Yellow Subtitles + Status + REAL RECORDING + Hey Sanam suno
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ZaraAnimatedAvatarOrb(
                    agent = activeAgent,
                    isSpeaking = uiState.assistantStatus == "SPEAKING" || uiState.isSanamResponding,
                    isOffline = uiState.isOfflineMode,
                    audioRms = uiState.wakeWordAudioRms,
                    avatarSize = 220.dp,
                    selectedAvatarId = uiState.selectedAvatarId,
                    emotion = uiState.avatarEmotion,
                    onOrbClick = { onNavigateTo("avatar_gallery") },
                    onTripleTapOrb = { onNavigateTo("developer_panel") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${activeAgent.name} • ${activeAgent.moodTag}",
                        color = tokens.primaryAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonBlue.copy(alpha = 0.2f))
                            .border(1.dp, NeonBlue, RoundedCornerShape(10.dp))
                            .clickable { onNavigateTo("avatar_gallery") }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("home_4x4_avatars_pill")
                    ) {
                        Text("4x4 Avatars", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LiveAudioWaveform(
                    rmsDb = uiState.wakeWordAudioRms,
                    isActive = uiState.isWakeWordListening || uiState.isSanamResponding,
                    accentColor = tokens.primaryAccent
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Yellow Subtitles (#FFD700)
                Text(
                    text = uiState.yellowSubtitleText,
                    color = YellowSubtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x551A1A2E), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status + REAL RECORDING green dot + Wake-word Hey Sanam suno
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF102818), RoundedCornerShape(10.dp))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(7.dp).background(SuccessGreen, CircleShape))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("REAL RECORDING", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = uiState.assistantStatus,
                        color = NeonBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Hey Sanam suno",
                        color = NeonPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.simulateWakeWord("Hey Sanam suno", "status report") }
                    )
                }
            }
        }

        // 4. Three Focus Buttons: Gaane / Padhai / Journal
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FocusPillButton("Gaane", Icons.Default.MusicNote, NeonPink, Modifier.weight(1f)) {
                    viewModel.startFlowShift("Gaane")
                    onNavigateTo("flow_shift")
                }
                FocusPillButton("Padhai", Icons.Default.MenuBook, NeonBlue, Modifier.weight(1f)) {
                    viewModel.startFlowShift("Padhai")
                    onNavigateTo("flow_shift")
                }
                FocusPillButton("Journal", Icons.Default.Book, NeonPurple, Modifier.weight(1f)) {
                    viewModel.startFlowShift("Journal")
                    onNavigateTo("flow_shift")
                }
            }
        }

        // 5. Three Info Cards: Mausam / Aaj / Mood
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoMiniCard("Mausam", uiState.mapWeatherTemp.substringBefore("•").trim(), "Maps Radar", NeonBlue, Modifier.weight(1f)) {
                    onNavigateTo("social_map_manager")
                }
                InfoMiniCard("Aaj", "${uiState.roomTriggers.count { it.isEnabled }} Active", "Tasks & Rules", SuccessGreen, Modifier.weight(1f)) {
                    onNavigateTo("triggers")
                }
                InfoMiniCard("Mood", uiState.moodTheme.substringBefore(" "), "10 Themes", NeonPink, Modifier.weight(1f)) {
                    onNavigateTo("aura_control")
                }
            }
        }

        // 6. Quick Actions: WiFi, Torch, Call, SMS, Music, Reminder
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        color = tokens.mutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickChip("WiFi", Icons.Default.Wifi, uiState.wifiEnabled) { viewModel.toggleWifiControl() }
                        QuickChip("Torch", Icons.Default.FlashlightOn, uiState.torchEnabled) { viewModel.toggleTorch(!uiState.torchEnabled) }
                        QuickChip("Call", Icons.Default.Call, false) { viewModel.sendChatMessage("Call log check karo") }
                        QuickChip("SMS", Icons.Default.Sms, false) { onNavigateTo("chat") }
                        QuickChip("Music", Icons.Default.MusicNote, false) { viewModel.startFlowShift("Gaane") }
                        QuickChip("Reminder", Icons.Default.Alarm, false) { onNavigateTo("triggers") }
                    }
                }
            }
        }

        // 7. Input Pill: ZARA se kuch bhi poochho + Mic + Send
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickInput,
                    onValueChange = { quickInput = it },
                    placeholder = { Text("ZARA se kuch bhi poochho...", color = TextGrey, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_zara_input_pill"),
                    shape = RoundedCornerShape(26.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = Color(0xFF2B2B44),
                        focusedTextColor = tokens.onBackgroundText,
                        unfocusedTextColor = tokens.onBackgroundText,
                        focusedContainerColor = tokens.cardSurface,
                        unfocusedContainerColor = tokens.cardSurface
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.activateVoiceFocusMic()
                        if (!uiState.voiceFocusModeEnabled) {
                            onNavigateTo("voice_live")
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonPurple, CircleShape)
                        .testTag("home_voice_mic_btn")
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Focus Mic", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (quickInput.isNotBlank()) {
                            viewModel.sendChatMessage(quickInput)
                            quickInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NeonBlue, CircleShape)
                        .testTag("home_zara_send_btn")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Ask ZARA", tint = Color.Black)
                }
            }
        }

        // 8. PRESERVED CORE SECURITY CARDS: Touch & Motion Guard, Military App Lock + GF Mode, Aura Control
        item {
            GlassHudCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_status_card"),
                borderColor = CrimsonPrimary.copy(alpha = 0.55f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.securityConfig.touchGuardArmed) "TOUCH & MOTION GUARD ARMED" else "TOUCH & MOTION GUARD",
                                color = if (uiState.securityConfig.touchGuardArmed) CrimsonPrimary else SuccessGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Military App Lock (${uiState.lockedApps.count { it.isLocked }} apps) • GF Mode ${if (uiState.securityConfig.gfModeEnabled) "Active" else "Off"}",
                                color = tokens.mutedText,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = uiState.securityConfig.touchGuardArmed,
                            onCheckedChange = { viewModel.toggleTouchGuardArmed(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary),
                            modifier = Modifier.testTag("touch_guard_master_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecurityActionButton("Touch Guard", Icons.Default.Vibration, Modifier.weight(1f)) { onNavigateTo("touch_guard") }
                        SecurityActionButton("App Lock + GF", Icons.Default.Lock, Modifier.weight(1f)) { onNavigateTo("app_lock") }
                        SecurityActionButton("Aura Control", Icons.Default.Palette, Modifier.weight(1f)) { onNavigateTo("aura_control") }
                    }
                }
            }
        }

        // 9. ALL 23+ SCREENS & MODULES LAUNCHER HUB
        item {
            Text(
                text = "ALL 23 SCREENS • ${SanamCatalog.twentyFourTools.size} TOOLS • ${SanamCatalog.aiConnectors.size} CONNECTORS • ${SanamCatalog.systemModes.size} MODES",
                color = NeonBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val screenDestinations = listOf(
                Triple("4x4 Avatars", "avatar_gallery", Icons.Default.Face),
                Triple("24 Tools Hub", "tools_hub", Icons.Default.Bolt),
                Triple("Voice Live", "voice_live", Icons.Default.Mic),
                Triple("Control Deck", "control_dashboard", Icons.Default.Settings),
                Triple("PIN Vault", "vault", Icons.Default.Lock),
                Triple("Prompts", "prompt_library", Icons.Default.MenuBook),
                Triple("Memory", "memory", Icons.Default.Person),
                Triple("Marketplace", "marketplace", Icons.Default.Bolt),
                Triple("Face HUD", "face_studio", Icons.Default.Face),
                Triple("Build Protocol", "build_protocol", Icons.Default.Security),
                Triple("Claude Code", "claude_code", Icons.Default.Code),
                Triple("Ghost 10", "ghost_world", Icons.Default.Security),
                Triple("UMI 12", "umi_world", Icons.Default.Palette),
                Triple("SANAM OS", "os_world", Icons.Default.Bolt),
                Triple("Launcher", "launcher", Icons.Default.Palette),
                Triple("LIA Chat", "lia_chat", Icons.Default.Person),
                Triple("FlowShift", "flow_shift", Icons.Default.MusicNote),
                Triple("Maps & Tools", "social_map_manager", Icons.Default.Map),
                Triple("8 Permissions", "permissions_guide", Icons.Default.Security),
                Triple("Privacy & JSON", "privacy_dashboard", Icons.Default.Lock),
                Triple("Dev Panel", "developer_panel", Icons.Default.Code),
                Triple("API Vault", "api_screen", Icons.Default.Security),
                Triple("GF Mode", "gf_mode", Icons.Default.Vibration),
                Triple("Triggers +77", "triggers", Icons.Default.Bolt)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                screenDestinations.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { (title, route, icon) ->
                            GlassHudCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_nav_$route"),
                                onClick = { onNavigateTo(route) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = icon, contentDescription = title, tint = tokens.primaryAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = title, color = tokens.onBackgroundText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(92.dp)) }
    }
}

@Composable
private fun TelemetryChip(text: String, color: Color, icon: ImageVector? = null) {
    Row(
        modifier = Modifier
            .background(Color(0xFF141628), RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(text = text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FocusPillButton(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp).testTag("focus_btn_${label.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f), contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun InfoMiniCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    GlassHudCard(modifier = modifier, borderColor = color.copy(alpha = 0.4f), onClick = onClick) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(text = subtitle, color = TextGrey, fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
private fun QuickChip(label: String, icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    val color = if (active) NeonBlue else TextGrey
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) NeonBlue.copy(alpha = 0.2f) else Color(0xFF101222))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SecurityActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF28121B))
            .border(1.dp, CrimsonPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = CrimsonPrimary, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}
