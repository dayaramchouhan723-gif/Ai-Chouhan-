package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.InstallApkTopButton
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

// Screen 9: Settings Full Control
@Composable
fun SettingsScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    var customNameDraft by remember(uiState.customAppName) { mutableStateOf(uiState.customAppName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("System Settings", color = tokens.onBackgroundText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Themes, 14 Agents, Privacy & Security Engines", color = tokens.mutedText, fontSize = 12.sp)
            }
            InstallApkTopButton(onClick = { viewModel.exportApkAction() })
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Custom App Name (DataStore custom_app_name)
        GlassHudCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CUSTOM HEADER NAME (DataStore)", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customNameDraft,
                        onValueChange = { customNameDraft = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("custom_app_name_input")
                    )
                    Button(
                        onClick = { viewModel.setCustomAppName(customNameDraft) },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Core Themes: AMOLED / Dark / Light + 10 Mood Themes
        GlassHudCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("3 CORE THEMES", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SanamCatalog.coreThemes.forEach { mode ->
                        val sel = uiState.coreThemeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) NeonBlue else Color(0xFF141628))
                                .clickable { viewModel.setCoreThemeMode(mode) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(mode, color = if (sel) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("10 MOOD THEMES", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SanamCatalog.moodThemes.forEach { (mood, color) ->
                        val sel = uiState.moodTheme == mood
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) color.copy(alpha = 0.3f) else Color(0xFF141628))
                                .border(1.dp, if (sel) color else Color(0xFF2B2B44), RoundedCornerShape(10.dp))
                                .clickable { viewModel.setMoodTheme(mood) }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(mood, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Text("LANGUAGE (AUTO + 8 MANUAL)", color = StarGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SanamCatalog.supportedLanguages.forEach { lang ->
                        val sel = uiState.currentLanguage == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) StarGold else Color(0xFF141628))
                                .clickable { viewModel.setLanguage(lang) }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(lang, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // VOICE FOCUS + 32 VOICES + PITCH 1.2 / RATE 0.9 + ALWAYS LISTENING (OFF DEFAULT)
        GlassHudCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("VOICE FOCUS & 32 STUDIO VOICES", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                SettingsToggleRow(
                    title = "Voice Focus Mode (Pure Black #000000)",
                    subtitle = "Hide cards & show full-screen live ZARA when Mic turns ON",
                    checked = uiState.voiceFocusModeEnabled,
                    tag = "toggle_voice_focus_mode"
                ) { viewModel.setVoiceFocusModeEnabled(it) }

                SettingsToggleRow(
                    title = "Always Listening Wake Word ('Hey Sanam suno')",
                    subtitle = "OFF by default for battery & mic privacy",
                    checked = uiState.isWakeWordEnabled,
                    tag = "toggle_always_listening"
                ) { viewModel.setAlwaysListeningEnabled(it) }

                SettingsToggleRow(
                    title = "Multi-Speaker Expressive Engine",
                    subtitle = "Alternate soft & witty cadence during stories",
                    checked = uiState.multiSpeakerEnabled,
                    tag = "toggle_multi_speaker"
                ) { viewModel.toggleMultiSpeaker(it) }

                Text("POCKET TTS ENGINE", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    SanamCatalog.pocketTtsModes.forEach { engine ->
                        val sel = uiState.pocketTtsEngine == engine
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) NeonBlue else Color(0xFF141628))
                                .clickable { viewModel.setPocketTtsEngine(engine) }
                                .padding(vertical = 7.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(engine.replace("Pocket ", ""), color = if (sel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("PITCH (${String.format("%.2f", uiState.voicePitch)} • default 1.20)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                androidx.compose.material3.Slider(
                    value = uiState.voicePitch,
                    onValueChange = { viewModel.setVoicePitchSlider(it) },
                    valueRange = 0.6f..1.8f,
                    colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = NeonBlue, activeTrackColor = NeonBlue)
                )

                Text("SPEED RATE (${String.format("%.2f", uiState.voiceRate)} • default 0.90)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                androidx.compose.material3.Slider(
                    value = uiState.voiceRate,
                    onValueChange = { viewModel.setVoiceRateSlider(it) },
                    valueRange = 0.6f..1.8f,
                    colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                )

                Text("32 VOICES (${SanamCatalog.thirtyTwoVoices.size} READY — HINDI / URDU / ENGLISH / SINDHI / DHATKI)", color = StarGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SanamCatalog.thirtyTwoVoices.forEach { v ->
                        val sel = uiState.selectedVoiceId == v.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) NeonPink.copy(alpha = 0.28f) else Color(0xFF141628))
                                .border(1.dp, if (sel) NeonPink else Color(0xFF2B2B44), RoundedCornerShape(10.dp))
                                .clickable { viewModel.selectVoiceProfile(v.id) }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("voice_chip_${v.id}")
                        ) {
                            Text(v.label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Accessibility + Privacy > Hidden Mode (Calculator Icon)
        GlassHudCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ACCESSIBILITY & PRIVACY", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                SettingsToggleRow(
                    title = "Large Accessibility Font",
                    subtitle = "Increase headline/body text legibility",
                    checked = uiState.largeFontEnabled,
                    tag = "toggle_large_font"
                ) { viewModel.setLargeFont(it) }

                SettingsToggleRow(
                    title = "Voice-Only Minimal Mode",
                    subtitle = "Spoken ZARA feedback prioritized",
                    checked = uiState.voiceOnlyMode,
                    tag = "toggle_voice_only"
                ) { viewModel.setVoiceOnlyMode(it) }

                SettingsToggleRow(
                    title = "Privacy > Hidden Mode (Calculator Icon)",
                    subtitle = "Disguise launcher icon & label as Calculator",
                    checked = uiState.hiddenCalculatorMode,
                    tag = "toggle_hidden_calculator_mode"
                ) { viewModel.toggleHiddenCalculatorMode(it) }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("SECURITY GUARDS & BIOMETRICS", color = tokens.mutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard("App Lock Engine", "${uiState.lockedApps.count { it.isLocked }} Apps Guarded (PIN & Bio)", Icons.Default.Lock, "settings_item_app_lock") { onNavigateTo("app_lock") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("GF Mode Scold Engine", if (uiState.securityConfig.gfModeEnabled) "Active • Custom Hinglish Scold" else "Disabled", Icons.Default.Favorite, "settings_item_gf_mode", Color(0xFFFF4081)) { onNavigateTo("gf_mode") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Touch Guard Siren", if (uiState.securityConfig.touchGuardArmed) "Armed • Motion Alarm 100%" else "Standby", Icons.Default.Vibration, "settings_item_touch_guard") { onNavigateTo("touch_guard") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Face Setup & Stealth Bio", if (uiState.isFaceEnrolled) "Enrolled • Local 3 Angles" else "Not enrolled", Icons.Default.CameraFront, "settings_item_face_setup") { onNavigateTo("face_setup") }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ACCOUNT, PRIVACY, AVATARS & TOOLS", color = tokens.mutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard("Account & Google Sign-In", "Connect Google or manage Lifetime License", Icons.Default.Person, "settings_item_profile", StarGold) { onNavigateTo("profile_premium") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Privacy & JSON Backup Dashboard", "Collection rules, Export/Import JSON & Data Wipe", Icons.Default.Lock, "settings_item_privacy_dashboard", Color(0xFF00E676)) { onNavigateTo("privacy_dashboard") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("8-Step Permissions Guide", "Clear Hinglish reasons + Photo Picker (Zero Broad Perm)", Icons.Default.Notifications, "settings_item_permissions_guide", NeonBlue) { onNavigateTo("permissions_guide") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("4x4 Live Avatar Gallery", "Switch Neon Cyber, Anime, MAYA & ZARA live faces", Icons.Default.Person, "settings_item_avatar_gallery", NeonPink) { onNavigateTo("avatar_gallery") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("24 Interactive Tools / Teacher / Meeting", "Teacher 5/5 Quiz, Meeting 6-Task, Music & DeepSearch", Icons.Default.Send, "settings_item_tools_hub", StarGold) { onNavigateTo("tools_hub") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Social Agent, Maps & Tools", "IG/FB/WA Post, Maps 29C, Pictures/ & Groq JSON", Icons.Default.Send, "settings_item_social_map") { onNavigateTo("social_map_manager") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("AI Router & Cloud Vault", "Encrypted Gemini, Groq, DeepSeek, OpenRouter & Ollama", Icons.Default.Cloud, "settings_item_api") { onNavigateTo("api_screen") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Notifications & Cache", "Call announcements & clear storage", Icons.Default.Notifications, "settings_item_notifications") { onNavigateTo("notifications") }
        Spacer(Modifier.height(8.dp))
        SettingsItemCard("Voice Personality & Wake Word", "Hey Sanam • ${uiState.securityConfig.voicePersonality}", Icons.Default.GraphicEq, "settings_item_personality") { onNavigateTo("personality_voice") }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun SettingsToggleRow(title: String, subtitle: String, checked: Boolean, tag: String, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextGrey, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = NeonBlue),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun SettingsItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    iconTint: Color = CrimsonPrimary,
    onClick: () -> Unit
) {
    GlassHudCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(38.dp).background(Color(0xFF221426), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextGrey, fontSize = 11.sp)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextGrey, modifier = Modifier.size(14.dp))
        }
    }
}
