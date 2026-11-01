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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val time: String,
    val isSystem: Boolean = false,
    val icon: ImageVector = Icons.Default.Security
)

@Composable
fun ChatListScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                sender = "CHOUHAN STAR AI",
                text = "॥ JAI SHRI RAM ॥ Commander! System is armed and local encrypted vault is active.",
                time = "Just now",
                isSystem = true,
                icon = Icons.Default.SmartToy
            ),
            ChatMessage(
                id = "2",
                sender = "Touch Guard Alert",
                text = "Gyroscope & Accelerometer armed at 100% volume.",
                time = "2m ago",
                isSystem = true,
                icon = Icons.Default.Security
            ),
            ChatMessage(
                id = "3",
                sender = "GF Mode Engine",
                text = "Scold line loaded: \"${uiState.securityConfig.gfScoldText}\"",
                time = "5m ago",
                isSystem = false,
                icon = Icons.Default.RecordVoiceOver
            ),
            ChatMessage(
                id = "4",
                sender = "App Lock Manager",
                text = "${uiState.lockedApps.count { it.isLocked }} apps guarded with PIN & Biometric.",
                time = "10m ago",
                isSystem = false,
                icon = Icons.Default.Lock
            )
        )
    }

    androidx.compose.runtime.LaunchedEffect(uiState.wakeWordTriggerCount) {
        if (uiState.wakeWordTriggerCount > 0 && uiState.myraResponseText != null) {
            messages.add(
                ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "Myra Voice AI",
                    text = uiState.myraResponseText ?: "Wake word 'Hey Myra' detected.",
                    time = "Just now",
                    isSystem = true,
                    icon = Icons.Default.RecordVoiceOver
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .testTag("chat_list_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Security Chat & Alerts",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "End-to-End local communication hub",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF142B1B), RoundedCornerShape(12.dp))
                    .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(SuccessGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Online", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.isSystem) Color(0xFF221115) else SurfaceCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (msg.isSystem) CrimsonPrimary.copy(alpha = 0.3f) else BorderDark
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (msg.isSystem) Color(0xFF381014) else Color(0xFF22222E),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = msg.icon,
                                contentDescription = null,
                                tint = if (msg.isSystem) CrimsonPrimary else StarGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg.sender,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = msg.time,
                                    color = TextGrey,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                color = Color(0xFFE0E0E0),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Ask security guard or note status...", color = TextGrey, fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    viewModel.simulateWakeWord("Hey Myra")
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(if (uiState.isWakeWordListening) CrimsonPrimary else Color(0xFF252530), CircleShape)
                    .testTag("chat_mic_wake_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Hey Myra Wake Word",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(
                            ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                sender = "Commander",
                                text = messageText,
                                time = "Now",
                                isSystem = false,
                                icon = Icons.Default.NotificationsActive
                            )
                        )
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(CrimsonPrimary, CircleShape)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
