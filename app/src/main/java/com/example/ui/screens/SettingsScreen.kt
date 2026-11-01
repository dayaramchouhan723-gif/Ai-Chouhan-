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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

@Composable
fun SettingsScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("settings_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "System Settings",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Security Engines & Integration Controls",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF261016), RoundedCornerShape(14.dp))
                    .border(1.dp, CrimsonPrimary, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Personal Build",
                    color = StarGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SECURITY GUARDS & BIOMETRICS",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "App Lock Engine",
            subtitle = if (uiState.securityConfig.appLockEnabled) "${uiState.lockedApps.count { it.isLocked }} Apps Guarded (PIN & Bio)" else "Disabled",
            icon = Icons.Default.Lock,
            tag = "settings_item_app_lock",
            onClick = { onNavigateTo("app_lock") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "GF Mode Scold Engine",
            subtitle = if (uiState.securityConfig.gfModeEnabled) "Active • Custom Hinglish Scold" else "Disabled",
            icon = Icons.Default.Favorite,
            iconTint = Color(0xFFFF4081),
            tag = "settings_item_gf_mode",
            onClick = { onNavigateTo("gf_mode") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Touch Guard Siren",
            subtitle = if (uiState.securityConfig.touchGuardArmed) "Armed • Motion Alarm 100%" else "Standby",
            icon = Icons.Default.Vibration,
            tag = "settings_item_touch_guard",
            onClick = { onNavigateTo("touch_guard") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Face Setup & Stealth Bio",
            subtitle = if (uiState.isFaceEnrolled) "Enrolled • Local 3 Angles" else "Not enrolled",
            icon = Icons.Default.CameraFront,
            tag = "settings_item_face_setup",
            onClick = { onNavigateTo("face_setup") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "INTEGRATION, CLOUD & ALERTS",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Social Agent, Maps & Tools",
            subtitle = "IG/FB/WA Post, Maps, Pictures & Groq JSON",
            icon = Icons.Default.Send,
            tag = "settings_item_social_map",
            onClick = { onNavigateTo("social_map_manager") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "API & Cloud Vault",
            subtitle = "Encrypted OpenAI, Gemini & Webhooks",
            icon = Icons.Default.Cloud,
            tag = "settings_item_api",
            onClick = { onNavigateTo("api_screen") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Notifications & Cache",
            subtitle = "Call announcements & clear storage",
            icon = Icons.Default.Notifications,
            tag = "settings_item_notifications",
            onClick = { onNavigateTo("notifications") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Voice Personality",
            subtitle = uiState.securityConfig.voicePersonality,
            icon = Icons.Default.GraphicEq,
            tag = "settings_item_personality",
            onClick = { onNavigateTo("personality_voice") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItemCard(
            title = "Profile & Lifetime License",
            subtitle = "Personal Lifetime Sovereign Access",
            icon = Icons.Default.Person,
            iconTint = StarGold,
            tag = "settings_item_profile",
            onClick = { onNavigateTo("profile_premium") }
        )

        Spacer(modifier = Modifier.height(30.dp))
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF261016), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
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
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
