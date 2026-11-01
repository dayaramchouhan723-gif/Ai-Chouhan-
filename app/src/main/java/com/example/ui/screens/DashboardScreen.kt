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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WireframeStar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

@Composable
fun DashboardScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header: Inscription & Profile Access
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WireframeStar(
                        size = 38.dp,
                        variant = uiState.currentIconVariant,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        glowColor = Color(0xFFFF4D4D),
                        animated = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CHOUHAN STAR ⭐",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "॥ JAI SHRI RAM ॥",
                            color = StarGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Lifetime Sovereign Badge button
                Box(
                    modifier = Modifier
                        .background(Color(0xFF261016), RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .clickable { onNavigateTo("profile_premium") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("premium_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(SuccessGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Personal Lifetime",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Visualizer Waveform Status Bar Lock Orb
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14141C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Hey Myra 🎙️ Status Orb", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
                                repeat(8) { idx ->
                                    val barHeight = (8 + (uiState.wakeWordAudioRms * (idx + 1) % 18)).coerceIn(4f, 22.dp.value).dp
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(barHeight)
                                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.simulateWakeWord("Hey Myra", "status report") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "Test Orb", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions: Deep Research & Image Search
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = StarGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Quick Actions (Deep Research & Image Search)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.runDeepResearch("Sovereign security protocols & threat mitigation") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "🔍 Deep Research", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.runImageSearch("Secure perimeter wireframe star security node") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "🖼️ Image Search", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (uiState.securityConfig.touchGuardArmed) "STAR SHIELD ARMED" else "SYSTEM READY",
                                color = if (uiState.securityConfig.touchGuardArmed) CrimsonPrimary else SuccessGreen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (uiState.securityConfig.touchGuardArmed)
                                    "Touch Guard Active: Galti se bhi phone mat hilana!"
                                else
                                    "Touch Guard Standby: Tap to arm security guard",
                                color = TextGrey,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Switch(
                            checked = uiState.securityConfig.touchGuardArmed,
                            onCheckedChange = { viewModel.toggleTouchGuardArmed(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CrimsonPrimary,
                                uncheckedThumbColor = TextGrey,
                                uncheckedTrackColor = Color(0xFF262630)
                            ),
                            modifier = Modifier.testTag("touch_guard_master_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary Quick Status Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickMetricBadge(
                            label = "App Lock",
                            value = if (uiState.securityConfig.appLockEnabled) "${uiState.lockedApps.count { it.isLocked }} Apps Locked" else "Disabled",
                            color = if (uiState.securityConfig.appLockEnabled) MaterialTheme.colorScheme.primary else TextGrey,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            label = "GF Mode",
                            value = if (uiState.securityConfig.gfModeEnabled) "Active" else "Off",
                            color = if (uiState.securityConfig.gfModeEnabled) StarGold else TextGrey,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            label = "Voice Aura",
                            value = uiState.securityConfig.voicePersonality.split(" ").firstOrNull() ?: "Dabang",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wake Word "Hey Myra" Quick Status Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTo("personality_voice") }
                    .testTag("dashboard_wake_word_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isWakeWordListening) Color(0xFF261217) else Color(0xFF1B1B22)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else BorderDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else Color(0xFF2A2A35),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = if (uiState.isWakeWordListening) Color.White else TextGrey,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Wake Word: Hey Myra",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (uiState.isWakeWordListening) Color(0xFF00E676) else Color(0xFFFFB300),
                                            CircleShape
                                        )
                                )
                            }
                            Text(
                                text = if (uiState.isWakeWordListening)
                                    "Listening • Say 'Hey Myra' or tap test"
                                else
                                    "Standby • Tap to open voice engine",
                                color = TextGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.simulateWakeWord("Hey Myra") }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("dashboard_test_myra_button")
                    ) {
                        Text(
                            text = "TEST",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "COMMAND MODULES",
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Navigation Grid Items (2 Columns)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Touch Guard",
                    subtitle = "Motion Siren & Theft Alarm",
                    icon = Icons.Default.Vibration,
                    tag = "module_touch_guard",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("touch_guard") }
                )
                ModuleCard(
                    title = "App Lock + GF",
                    subtitle = "PIN, Bio & Scold Engine",
                    icon = Icons.Default.Lock,
                    tag = "module_app_lock",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("app_lock") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Face Setup",
                    subtitle = "3 Samples Camera Enrollment",
                    icon = Icons.Default.Face,
                    tag = "module_face_setup",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("face_setup") }
                )
                ModuleCard(
                    title = "Aura Control",
                    subtitle = "4 Star Icons & Wallpapers",
                    icon = Icons.Default.Palette,
                    tag = "module_aura_control",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("aura_control") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Personality & Voice",
                    subtitle = "Hinglish Persona & Speed",
                    icon = Icons.Default.GraphicEq,
                    tag = "module_personality_voice",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("personality_voice") }
                )
                ModuleCard(
                    title = "API & Cloud",
                    subtitle = "Encrypted Vault & Connectors",
                    icon = Icons.Default.Cloud,
                    tag = "module_api_cloud",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("api_screen") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "World Model",
                    subtitle = "Learned Apps & Contacts",
                    icon = Icons.Default.Hub,
                    tag = "module_world_model",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("world_model") }
                )
                ModuleCard(
                    title = "Triggers +77",
                    subtitle = "Custom Automation Rules",
                    icon = Icons.Default.Bolt,
                    tag = "module_triggers",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("triggers") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Notifications",
                    subtitle = "Call Announce & Cache",
                    icon = Icons.Default.Notifications,
                    tag = "module_notifications",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("notifications") }
                )
                ModuleCard(
                    title = "Profile & Lifetime",
                    subtitle = "Personal Build License",
                    icon = Icons.Default.Person,
                    tag = "module_profile",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("profile_premium") }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent Incident & Local Snapshot Feed Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SECURITY AUDIT LOGS (${uiState.incidentLogs.size + uiState.localSnapshots.size})",
                    color = TextGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (uiState.localSnapshots.isNotEmpty()) {
                    Text(
                        text = "Clear Cache",
                        color = CrimsonPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { viewModel.clearCache() }
                            .testTag("clear_cache_text_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.localSnapshots.isEmpty() && uiState.incidentLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Device bilkul safe hai boss! Koi breach record nahi hua.",
                        color = TextGrey,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(uiState.localSnapshots.take(4)) { snapshot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF381014), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = snapshot.note,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Local Snapshot stored: Pictures/CHOUHAN_STAR",
                                color = TextGrey,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun QuickMetricBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceCardElevated, RoundedCornerShape(12.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextGrey, fontSize = 10.sp)
            Text(
                text = value,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF261014), RoundedCornerShape(10.dp))
                    .border(1.dp, CrimsonPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextGrey,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
