package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SanamAgentProfile
import com.example.ui.theme.GlassCardColor
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.ui.theme.YellowSubtitle
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun GlassHudCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NeonBlue.copy(alpha = 0.35f),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val tokens = LocalStarThemeTokens.current
    val baseModifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    Card(
        modifier = baseModifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tokens.cardSurface),
        border = BorderStroke(1.dp, borderColor)
    ) {
        content()
    }
}

@Composable
fun InstallApkTopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(listOf(NeonBlue.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.25f)))
            )
            .border(1.dp, NeonBlue, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("install_download_apk_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Install / Download APK",
            tint = NeonBlue,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "APK",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SubScreenHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onInstallApk: (() -> Unit)? = null
) {
    val tokens = LocalStarThemeTokens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("subscreen_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = tokens.onBackgroundText
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = title,
                    color = tokens.onBackgroundText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = tokens.mutedText,
                    fontSize = 12.sp
                )
            }
        }
        if (onInstallApk != null) {
            InstallApkTopButton(onClick = onInstallApk)
        }
    }
}

@Composable
fun SanamProtocolBar(
    states: List<String>,
    onClick: () -> Unit
) {
    val labels = listOf("01 FACE", "02 VOICE", "03 BRAIN", "04 BUILD")
    GlassHudCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sanam_protocol_bar"),
        borderColor = NeonBlue.copy(alpha = 0.4f),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = "S.A.N.A.M. // BUILD PROTOCOL",
                color = NeonBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                labels.forEachIndexed { idx, label ->
                    val status = states.getOrElse(idx) { "PENDING" }
                    val dotColor = when (status) {
                        "DONE" -> SuccessGreen
                        "RUNNING" -> NeonBlue
                        else -> TextGrey
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0E101E), RoundedCornerShape(10.dp))
                            .border(1.dp, dotColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (status == "DONE") {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = SuccessGreen,
                                modifier = Modifier.size(11.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(dotColor, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            color = if (status == "PENDING") TextGrey else Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZaraAnimatedAvatarOrb(
    agent: SanamAgentProfile,
    isSpeaking: Boolean,
    isOffline: Boolean,
    audioRms: Float,
    avatarSize: Dp = 220.dp,
    selectedAvatarId: String = "zara_open",
    emotion: com.example.data.model.AvatarEmotionState = com.example.data.model.AvatarEmotionState.NEUTRAL,
    onOrbClick: () -> Unit,
    onTripleTapOrb: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "ZaraOrb")
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "RingRotation"
    )
    val breatheScale by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Reverse),
        label = "Breathe"
    )

    val lipPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Restart),
        label = "LipSync"
    )

    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapMs by remember { mutableLongStateOf(0L) }

    val accent = Color(agent.accentHex)

    Box(
        modifier = Modifier
            .size(avatarSize)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val now = System.currentTimeMillis()
                        tapCount = if (now - lastTapMs < 550L) tapCount + 1 else 1
                        lastTapMs = now
                        if (tapCount >= 3) {
                            tapCount = 0
                            onTripleTapOrb()
                        } else {
                            onOrbClick()
                        }
                    }
                )
            }
            .testTag("zara_center_avatar_orb"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.15f
            val voicePulse = if (isSpeaking) (6f + (sin(lipPhase) * 4f) + audioRms * 1.5f) else 2f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.35f), NeonPink.copy(alpha = 0.14f), Color.Transparent),
                    center = center,
                    radius = radius * 1.15f
                ),
                radius = radius * 1.15f,
                center = center
            )

            rotate(ringRotation, pivot = center) {
                drawArc(
                    color = accent,
                    startAngle = 0f,
                    sweepAngle = 115f,
                    useCenter = false,
                    style = Stroke(width = 5f + voicePulse * 0.4f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = NeonPink,
                    startAngle = 145f,
                    sweepAngle = 80f,
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = NeonPurple,
                    startAngle = 250f,
                    sweepAngle = 85f,
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            rotate(-ringRotation * 1.4f, pivot = center) {
                drawCircle(
                    color = accent.copy(alpha = 0.4f),
                    radius = radius * 0.87f,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(avatarSize * 0.76f)
                .scale(breatheScale)
                .clip(CircleShape)
                .background(Color(0xFF0E1220))
                .border(2.dp, accent.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (agent.id == "chouhan_guard") {
                Image(
                    painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                    contentDescription = "${agent.name} Star Crest",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                com.example.ui.screens.LiveHumanEngine(
                    avatarId = selectedAvatarId,
                    emotion = emotion,
                    isSpeaking = isSpeaking,
                    audioRms = audioRms,
                    showHudBadge = false,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Offline / Online Badge on Orb rim
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0xEE0A0A12), RoundedCornerShape(12.dp))
                .border(1.dp, if (isOffline) YellowSubtitle else SuccessGreen, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = "${agent.name} • ${if (isOffline) "OFFLINE" else "ONLINE"}",
                color = if (isOffline) YellowSubtitle else SuccessGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LiveAudioWaveform(
    rmsDb: Float,
    isActive: Boolean,
    accentColor: Color = NeonBlue,
    barCount: Int = 18
) {
    val infinite = rememberInfiniteTransition(label = "WaveBars")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "WavePhase"
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(30.dp)
    ) {
        for (i in 0 until barCount) {
            val wave = ((sin(phase + i * 0.6f) + 1f) * 0.5f)
            val rmsBoost = (rmsDb / 10f).coerceIn(0.2f, 1.4f)
            val barH = if (isActive) (6f + wave * 20f * rmsBoost).coerceIn(5f, 28f).dp else (5f + (i % 3) * 3f).dp
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barH)
                    .background(
                        if (i % 3 == 0) NeonPink else accentColor,
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun SanamFloatingDock(
    currentScreen: String,
    isListening: Boolean,
    onNavigate: (String) -> Unit,
    onMicOrbClick: () -> Unit,
    onTripleTapOrb: () -> Unit
) {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTap by remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("sanam_floating_dock")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xEB121426), RoundedCornerShape(28.dp))
                .border(1.dp, NeonBlue.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(
                label = "Talk",
                icon = Icons.Default.Mic,
                selected = currentScreen == "voice_live",
                tag = "dock_talk",
                onClick = { onNavigate("voice_live") }
            )
            DockItem(
                label = "Chat",
                icon = Icons.Default.ChatBubbleOutline,
                selected = currentScreen == "chat",
                tag = "dock_chat",
                onClick = { onNavigate("chat") }
            )

            // Center Floating Orb Mic Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isListening) listOf(NeonPink, NeonPurple) else listOf(NeonBlue, NeonPurple)
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val now = System.currentTimeMillis()
                                tapCount = if (now - lastTap < 550L) tapCount + 1 else 1
                                lastTap = now
                                if (tapCount >= 3) {
                                    tapCount = 0
                                    onTripleTapOrb()
                                } else {
                                    onMicOrbClick()
                                }
                            }
                        )
                    }
                    .testTag("dock_center_orb_mic"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Center Voice Orb",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            DockItem(
                label = "Tools",
                icon = Icons.Default.Build,
                selected = currentScreen in listOf("marketplace", "prompt_library", "triggers", "memory", "claude_code"),
                tag = "dock_tools",
                onClick = { onNavigate("marketplace") }
            )
            DockItem(
                label = "Control",
                icon = Icons.Default.Tune,
                selected = currentScreen == "control_dashboard",
                tag = "dock_control",
                onClick = { onNavigate("control_dashboard") }
            )
        }
    }
}

@Composable
private fun DockItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val tint = if (selected) NeonBlue else TextGrey
    Column(
        modifier = Modifier
            .size(width = 56.dp, height = 48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
