package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.StarSecurityViewModel

private val DarkBg = Color(0xFF0A0A0A)
private val CardBg = Color(0xFF1A1A1A)
private val CrimsonAura = Color(0xFFE53935)
private val TextGrey = Color(0xFFA0A0A0)

@Composable
fun NotificationsScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("notifications_screen")
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("notifications_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Notifications",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Alerts & announcements",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Main Announcement Alerts
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("notifications_card_1"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotificationToggleRow(
                    title = "Call Assistant",
                    subtitle = "Announce calls",
                    checked = uiState.callAssistant,
                    onCheckedChange = { viewModel.setCallAssistant(it) },
                    testTag = "call_assistant_switch"
                )

                NotificationToggleRow(
                    title = "Chat Notifications",
                    subtitle = "Message alerts",
                    checked = uiState.chatNotifications,
                    onCheckedChange = { viewModel.setChatNotifications(it) },
                    testTag = "chat_notifications_switch"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 2: Advanced Triggers
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("notifications_card_2_advanced"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Advanced",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                NotificationToggleRow(
                    title = "Screen & Unlock trigger",
                    subtitle = null,
                    checked = uiState.screenUnlockTrigger,
                    onCheckedChange = { viewModel.setScreenUnlockTrigger(it) },
                    testTag = "screen_unlock_trigger_switch"
                )

                NotificationToggleRow(
                    title = "Headphones trigger",
                    subtitle = null,
                    checked = uiState.headphonesTrigger,
                    onCheckedChange = { viewModel.setHeadphonesTrigger(it) },
                    testTag = "headphones_trigger_switch"
                )

                NotificationToggleRow(
                    title = "WiFi trigger",
                    subtitle = null,
                    checked = uiState.wifiTrigger,
                    onCheckedChange = { viewModel.setWifiTrigger(it) },
                    testTag = "wifi_trigger_switch"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(28.dp))

        // Bottom Button: "Clear Cache" Grey Outlined
        OutlinedButton(
            onClick = { viewModel.clearCache() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("clear_cache_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextGrey
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444))
        ) {
            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = TextGrey, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Clear Cache",
                color = TextGrey,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextGrey,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CrimsonAura,
                uncheckedThumbColor = TextGrey,
                uncheckedTrackColor = Color(0xFF262626)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
