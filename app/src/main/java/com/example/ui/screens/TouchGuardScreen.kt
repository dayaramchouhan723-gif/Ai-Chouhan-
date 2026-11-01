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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
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
fun TouchGuardScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFaceSetup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.securityConfig
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("touch_guard_screen")
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("touch_guard_back_button")
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
                    text = "Touch Guard ⭐",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Anti-Theft Motion & Pick Up Vigilance",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Master Armed Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("touch_guard_armed_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (config.touchGuardArmed) CrimsonPrimary else BorderDark)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (config.touchGuardArmed) "TOUCH GUARD IS ARMED" else "TOUCH GUARD DISARMED",
                            color = if (config.touchGuardArmed) CrimsonPrimary else Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Galti se bhi phone mat hilana, siren baj jayega!",
                            color = TextGrey,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Switch(
                        checked = config.touchGuardArmed,
                        onCheckedChange = { viewModel.toggleTouchGuardArmed(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CrimsonPrimary
                        ),
                        modifier = Modifier.testTag("touch_guard_armed_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Motion Sensitivity Selector
        Text(
            text = "MOTION SENSITIVITY",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sensitivities = listOf("Low", "Medium", "High", "Hyper-Sensitive")
            sensitivities.forEach { s ->
                val selected = config.motionSensitivity.equals(s, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected) CrimsonPrimary else SurfaceCard,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (selected) Color.White else BorderDark,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setMotionSensitivity(s) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Siren Volume Slider
        Text(
            text = "SIREN VOLUME (${config.sirenVolume}%)",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Slider(
            value = config.sirenVolume.toFloat(),
            onValueChange = { viewModel.setSirenVolume(it.toLong()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = CrimsonPrimary,
                activeTrackColor = CrimsonPrimary,
                inactiveTrackColor = Color(0xFF262630)
            ),
            modifier = Modifier.testTag("siren_volume_slider")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Warning Modes
        Text(
            text = "4 WARNING MODES",
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
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WarningModeRow(
                    title = "Voice Alert",
                    subtitle = "Hinglish voice warning: Phone chhua toh report jayegi!",
                    checked = config.warningVoice,
                    onCheckedChange = { viewModel.toggleWarningMode("VOICE", it) },
                    testTag = "warning_mode_voice"
                )
                WarningModeRow(
                    title = "Vibration Alert",
                    subtitle = "High frequency haptic feedback pattern",
                    checked = config.warningVibration,
                    onCheckedChange = { viewModel.toggleWarningMode("VIBRATION", it) },
                    testTag = "warning_mode_vibration"
                )
                WarningModeRow(
                    title = "Audible Siren",
                    subtitle = "Loud frequency piercing theft alarm",
                    checked = config.warningSiren,
                    onCheckedChange = { viewModel.toggleWarningMode("SIREN", it) },
                    testTag = "warning_mode_siren"
                )
                WarningModeRow(
                    title = "Full-Screen Flash",
                    subtitle = "Lockdown red alert screen requiring master PIN",
                    checked = config.warningFullScreen,
                    onCheckedChange = { viewModel.toggleWarningMode("FULLSCREEN", it) },
                    testTag = "warning_mode_fullscreen"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stealth Face Lock & Intruder Snapshot
        Text(
            text = "STEALTH BIOMETRIC & INTRUDER LOCAL PHOTO",
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
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Face Lock Stealth Background Check",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agla banda screen dekhega toh stealth face scanning chalu ho jayegi",
                            color = TextGrey,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = config.stealthFaceLock,
                        onCheckedChange = { viewModel.toggleVoiceParameter("STEALTH_FACE", it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Intruder Photo Capture (Local Only)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saves quietly to Pictures/CHOUHAN_STAR (zero cloud upload)",
                            color = StarGold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Switch(
                        checked = config.intruderPhotoEnabled,
                        onCheckedChange = { /* Always enabled */ },
                        colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToFaceSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("re_enroll_face_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF261016),
                        contentColor = CrimsonPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (uiState.isFaceEnrolled) "Re-Enroll Face Samples (3 Angles)" else "Enroll Face Samples (3 Angles)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Simulation Test Button
        Button(
            onClick = {
                viewModel.triggerTouchBreachAlarm("Manual Test: Phone pickup simulated!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("test_touch_guard_alarm_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
        ) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SIMULATE MOTION BREACH (TEST SIREN)", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WarningModeRow(
    title: String,
    subtitle: String,
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
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextGrey, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = CrimsonPrimary),
            modifier = Modifier.testTag(testTag)
        )
    }
}
