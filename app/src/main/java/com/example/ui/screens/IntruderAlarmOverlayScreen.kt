package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WireframeStar
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.StarGold
import com.example.viewmodel.StarSecurityViewModel

@Composable
fun IntruderAlarmOverlayScreen(
    viewModel: StarSecurityViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "FlashAlarm")
    val alphaFlash by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaFlash"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E050A).copy(alpha = alphaFlash))
            .padding(24.dp)
            .testTag("intruder_alarm_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            WireframeStar(
                size = 100.dp,
                variant = "Crimson Wireframe Star",
                primaryColor = Color(0xFFFF1E27),
                glowColor = Color(0xFFFF0000),
                animated = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "॥ SECURITY BREACH ॥",
                color = StarGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "PHONE PICKED UP WITHOUT PERMISSION!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (uiState.securityConfig.gfModeEnabled) {
                    uiState.securityConfig.gfScoldText
                } else {
                    "Khabardar! Chehra match nahi hua. Chupchap wapas rakho phone!"
                },
                color = Color(0xFFFF8A80),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Intruder photo saved to Pictures/CHOUHAN_STAR",
                color = TextGreyForAlarm,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val filled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (filled) (if (pinError) Color(0xFFFF1744) else Color.White)
                                else Color.Transparent,
                                CircleShape
                            )
                            .border(2.dp, if (pinError) Color(0xFFFF1744) else Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disarm Keypad (3x4 Grid)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "DEL")
                )

                digits.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFF1E080C), CircleShape)
                                    .border(1.dp, Color(0xFF5E141D), CircleShape)
                                    .clickable {
                                        when (digit) {
                                            "C" -> {
                                                enteredPin = ""
                                                pinError = false
                                            }
                                            "DEL" -> {
                                                if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                pinError = false
                                            }
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += digit
                                                    pinError = false
                                                    if (enteredPin.length == 4) {
                                                        if (viewModel.verifyPin(enteredPin)) {
                                                            viewModel.dismissAlarm()
                                                        } else {
                                                            pinError = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .testTag("keypad_$digit"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (digit == "DEL") {
                                    Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(text = digit, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val TextGreyForAlarm = Color(0xFFB0B0C0)
