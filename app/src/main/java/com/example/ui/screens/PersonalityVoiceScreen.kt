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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
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
fun PersonalityVoiceScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.securityConfig
    val scrollState = rememberScrollState()

    var interruptionAllowed by remember { mutableStateOf(true) }
    var muteOnPickup by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("personality_voice_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("personality_voice_back_button")
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
                    text = "Personality & Voice ⭐",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI Security Voice Persona & Speech Nuances",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Wake Word Engine Section
        Text(
            text = "WAKE WORD ENGINE: HEY MYRA ⭐",
            color = StarGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wake_word_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else BorderDark
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Master Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    if (uiState.isWakeWordListening) Color(0xFF3B1219) else Color(0xFF1E1E24),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else BorderDark,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isWakeWordListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Microphone",
                                tint = if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else TextGrey,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Wake Word: 'Hey Myra'",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "SpeechRecognizer continuous detection",
                                color = TextGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = uiState.isWakeWordEnabled,
                        onCheckedChange = { viewModel.toggleWakeWord(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary),
                        modifier = Modifier.testTag("wake_word_toggle")
                    )
                }

                if (uiState.isWakeWordEnabled) {
                    // Audio Level / Listening Status Indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141419), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF262632), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (uiState.isWakeWordListening) Color(0xFF00E676) else Color(0xFFFFB300),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isWakeWordListening)
                                        "Listening Active: Speak 'Hey Myra'"
                                    else
                                        "Initializing Speech Recognizer...",
                                    color = if (uiState.isWakeWordListening) Color(0xFF00E676) else Color(0xFFFFB300),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Dynamic RMS Waveform Indicator
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val rms = uiState.wakeWordAudioRms.coerceIn(0f, 10f)
                                for (i in 1..4) {
                                    val barHeight = if (uiState.isWakeWordListening) {
                                        (6 + (rms * i * 1.5f).toInt()).coerceIn(6, 24).dp
                                    } else {
                                        4.dp
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(barHeight)
                                            .background(
                                                if (uiState.isWakeWordListening) MaterialTheme.colorScheme.primary else TextGrey,
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // Last Event / Spoken Command Preview
                    if (uiState.lastWakeWordEvent != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF24151B), RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Hearing,
                                        contentDescription = null,
                                        tint = StarGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "WAKE WORD TRIGGERED (${uiState.wakeWordTriggerCount}x)",
                                        color = StarGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (uiState.myraResponseText != null) {
                                    Text(
                                        text = uiState.myraResponseText ?: "",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Test Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.simulateWakeWord("Hey Myra") },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("test_wake_word_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "TEST 'HEY MYRA'", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.simulateWakeWord("Hey Myra", "status report") },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("test_command_status_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2228))
                        ) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp), tint = StarGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "TEST STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Supported Commands Guide
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF16161C), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "COMMANDS RECOGNIZED AFTER WAKE WORD:",
                            color = TextGrey,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf(
                            "\"Hey Myra, lock phone\"" to "Emergency breach lockdown",
                            "\"Hey Myra, arm guard\"" to "Arms Touch Guard sensor",
                            "\"Hey Myra, status report\"" to "Spoken security telemetry",
                            "\"Hey Myra, activate crimson aura\"" to "Instant theme morph"
                        ).forEach { (cmd, effect) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = cmd, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = effect, color = TextGrey, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personality Selector
        Text(
            text = "AI VOICE PERSONALITY",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val personalities = listOf(
            "Dabang Guardian" to "Bold, street-smart vigilant Hinglish tone. Fast and assertive warning.",
            "Royal Rajput" to "Dignified Rajputi honor & warrior pride: 'Khamma Ghani! Veerta aur shaan.'",
            "Cyber Sher" to "High-tech algorithmic tactical scanner: 'Perimeter threat analysis active.'",
            "Silent Phantom" to "Subtle stealth whisper: 'Silent observer armed. Unseen vigilance.'"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            personalities.forEach { (name, desc) ->
                val isSelected = config.voicePersonality == name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateVoicePersonality(name) }
                        .testTag("personality_$name"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else BorderDark
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF261014), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else CrimsonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = desc, color = TextGrey, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CrimsonPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Voice Speed Slider
        Text(
            text = "VOICE SPEED (${String.format(java.util.Locale.US, "%.1fx", config.voiceSpeed)})",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Slider(
            value = config.voiceSpeed.toFloat(),
            onValueChange = { viewModel.updateVoiceSpeed(it.toDouble()) },
            valueRange = 0.5f..2.0f,
            steps = 5,
            colors = SliderDefaults.colors(
                thumbColor = CrimsonPrimary,
                activeTrackColor = CrimsonPrimary,
                inactiveTrackColor = Color(0xFF262630)
            ),
            modifier = Modifier.testTag("voice_speed_slider")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vocal Nuance Toggles
        Text(
            text = "SPEECH DYNAMICS & NUANCES",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ToggleRow(
                    title = "Fast Response",
                    subtitle = "Instant low-latency audio cue without delay",
                    checked = config.fastResponse,
                    onCheckedChange = { viewModel.toggleVoiceParameter("FAST_RESPONSE", it) }
                )
                ToggleRow(
                    title = "Natural Pause",
                    subtitle = "Dynamic cadence resembling human breath and suspense",
                    checked = config.naturalPause,
                    onCheckedChange = { viewModel.toggleVoiceParameter("NATURAL_PAUSE", it) }
                )
                ToggleRow(
                    title = "Emotional Tone",
                    subtitle = "Adaptive inflection shifting from calm to urgent warning",
                    checked = config.emotionalTone,
                    onCheckedChange = { viewModel.toggleVoiceParameter("EMOTIONAL_TONE", it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Interruption Toggles
        Text(
            text = "INTERRUPTION & MUTE BEHAVIOR",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ToggleRow(
                    title = "Speech Interruption",
                    subtitle = "Cancel alert announcement when commander speaks",
                    checked = interruptionAllowed,
                    onCheckedChange = { interruptionAllowed = it }
                )
                ToggleRow(
                    title = "Auto-Mute on Pickup",
                    subtitle = "Lower siren automatically once owner's face is recognized",
                    checked = muteOnPickup,
                    onCheckedChange = { muteOnPickup = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.updateVoicePersonality(config.voicePersonality)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("test_personality_greeting_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SPEAK PERSONALITY GREETING", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextGrey, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary)
        )
    }
}
