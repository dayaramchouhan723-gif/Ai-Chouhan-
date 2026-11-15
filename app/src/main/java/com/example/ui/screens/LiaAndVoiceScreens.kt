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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import com.example.data.model.AvatarEmotionState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.LiveAudioWaveform
import com.example.ui.components.SubScreenHeader
import com.example.ui.components.ZaraAnimatedAvatarOrb
import com.example.ui.theme.GlassCardColor
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextGrey
import com.example.ui.theme.YellowSubtitle
import com.example.viewmodel.StarSecurityViewModel

// Screen 5: Voice Live / Voice Room
@Composable
fun VoiceLiveScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit,
    onTripleTapDev: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val activeAgent = remember(uiState.activeAgentId) {
        SanamCatalog.fourteenAgents.find { it.id == uiState.activeAgentId } ?: SanamCatalog.fourteenAgents.first()
    }
    var customVoicePrompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("voice_live_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SubScreenHeader(
            title = "Voice Live Room",
            subtitle = "Real-time Hinglish / Urdu / Multi-Lang Voice",
            onBack = onNavigateBack,
            onInstallApk = { viewModel.exportApkAction() }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ZaraAnimatedAvatarOrb(
                agent = activeAgent,
                isSpeaking = uiState.isSanamResponding || uiState.assistantStatus == "SPEAKING",
                isOffline = uiState.isOfflineMode,
                audioRms = uiState.wakeWordAudioRms,
                avatarSize = 230.dp,
                selectedAvatarId = uiState.selectedAvatarId,
                emotion = uiState.avatarEmotion,
                onOrbClick = { viewModel.activateVoiceFocusMic() },
                onTripleTapOrb = onTripleTapDev
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                listOf(
                    "Idle" to AvatarEmotionState.NEUTRAL,
                    "Happy" to AvatarEmotionState.HAPPY,
                    "Shy" to AvatarEmotionState.SHY_BLUSH,
                    "Angry" to AvatarEmotionState.ROAST_ANGRY,
                    "Sad" to AvatarEmotionState.SAD_CARING
                ).forEach { (label, state) ->
                    val active = uiState.avatarEmotion == state
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) tokens.primaryAccent.copy(alpha = 0.25f) else GlassCardColor)
                            .border(1.dp, if (active) tokens.primaryAccent else Color(0xFF2B2B44), RoundedCornerShape(12.dp))
                            .clickable { viewModel.triggerAvatarEmotionDemo(state) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("voicelive_emotion_${label.lowercase()}")
                    ) {
                        Text(label, color = if (active) tokens.primaryAccent else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LiveAudioWaveform(
                rmsDb = uiState.wakeWordAudioRms.coerceAtLeast(3f),
                isActive = true,
                accentColor = if (uiState.isSanamResponding) NeonBlue else NeonPink,
                barCount = 22
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.yellowSubtitleText,
                color = YellowSubtitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassCardColor, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick spoken commands
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.activateVoiceFocusMic() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Listening Red", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Red Listen", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.sendChatMessage("Aaj ka weather aur security check sunao") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    Icon(imageVector = Icons.Default.GraphicEq, contentDescription = "Speaking Blue", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Blue Speak", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = customVoicePrompt,
                    onValueChange = { customVoicePrompt = it },
                    placeholder = { Text("Speak or type voice prompt...", color = TextGrey, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = Color(0xFF2B2B44),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (customVoicePrompt.isNotBlank()) {
                            viewModel.sendChatMessage(customVoicePrompt)
                            customVoicePrompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonBlue)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Speak", tint = Color.Black)
                }
            }
        }
    }
}

// Screen 21: LIA Chat (LIA Avatar + Short Cute Hinglish + Shared Memory)
@Composable
fun LiaChatScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val liaAgent = remember { SanamCatalog.fourteenAgents.first { it.id == "lia" } }
    var liaInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("lia_chat_screen")
    ) {
        SubScreenHeader(
            title = "LIA Cute Chat",
            subtitle = "Short Cute Hinglish • Shared SANAM Memory",
            onBack = onNavigateBack,
            onInstallApk = { viewModel.exportApkAction() }
        )

        GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPink.copy(alpha = 0.55f)) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = 0.25f))
                        .border(1.5.dp, NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LIA", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(liaAgent.greetingHinglish, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Memory synced (${uiState.chatMessages.size} messages)", color = NeonPink, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.chatMessages) { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (msg.isUser) NeonPink.copy(alpha = 0.18f) else GlassCardColor,
                            RoundedCornerShape(14.dp)
                        )
                        .border(1.dp, if (msg.isUser) NeonPink else Color(0xFF2B2B44), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("${msg.senderName} • ${msg.timestamp}", color = if (msg.isUser) NeonPink else NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(msg.text, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 72.dp)) {
            OutlinedTextField(
                value = liaInput,
                onValueChange = { liaInput = it },
                placeholder = { Text("LIA ko bolo...", color = TextGrey, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = Color(0xFF2B2B44),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NeonPink)
                    .clickable {
                        if (liaInput.isNotBlank()) {
                            viewModel.sendChatMessage(liaInput, agentOverrideId = "lia")
                            liaInput = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send to LIA", tint = Color.White)
            }
        }
    }
}
