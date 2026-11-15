package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ActionConfirmationRequest
import com.example.data.model.AvatarEmotionState
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.LiveAudioWaveform
import com.example.ui.components.SubScreenHeader
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.ui.theme.YellowSubtitle
import com.example.viewmodel.StarSecurityViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * LiveHumanEngine:
 * Layered Image + Canvas biological animation engine for the selected AI agent.
 * - Natural idling: rhythmic chest breathing (scaleX/scaleY + lift), organic eye-contact micro-gaze
 *   tracking, and randomized blinking (2.1s–4.6s intervals + occasional double-blink + Canvas eyelid sweep).
 * - State-based facial reactions: Happy, Shy, Angry, Sad, plus Neutral/Listening & Speaking lip-sync.
 */
@Composable
fun LiveHumanEngine(
    avatarId: String,
    emotion: AvatarEmotionState,
    isSpeaking: Boolean,
    audioRms: Float,
    modifier: Modifier = Modifier,
    showHudBadge: Boolean = true
) {
    val avatarItem = remember(avatarId) {
        SanamCatalog.avatarGalleryItems.find { it.id == avatarId } ?: SanamCatalog.avatarGalleryItems.first()
    }
    val baseAccent = Color(avatarItem.accentHex)

    val reactionAccent by animateColorAsState(
        targetValue = when (emotion) {
            AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING, AvatarEmotionState.WAVING, AvatarEmotionState.WINK -> StarGold
            AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC, AvatarEmotionState.HAIR_TUCK -> NeonPink
            AvatarEmotionState.ROAST_ANGRY -> CrimsonPrimary
            AvatarEmotionState.SAD_CARING -> Color(0xFF38BDF8)
            else -> baseAccent
        },
        animationSpec = tween(360),
        label = "ReactionAccentColor"
    )

    val breathingDurationMs = when (emotion) {
        AvatarEmotionState.SAD_CARING -> 3400
        AvatarEmotionState.ROAST_ANGRY -> 1700
        AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING -> 2100
        else -> 2600
    }

    val infinite = rememberInfiniteTransition(label = "LiveHumanEngineLoop")
    val breathCycle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathCycle"
    )
    val microPulsePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "AmbientSparkPhase"
    )
    val lipPulse by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(210, easing = LinearEasing), RepeatMode.Reverse),
        label = "LipSyncPulse"
    )

    // Randomized natural eye-contact gaze drift
    var targetGazeX by remember { mutableFloatStateOf(0f) }
    var targetGazeY by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(avatarId, emotion, isSpeaking) {
        while (true) {
            if (isSpeaking) {
                // Direct attentive eye contact while talking
                targetGazeX = (Random.nextFloat() - 0.5f) * 3f
                targetGazeY = (Random.nextFloat() - 0.5f) * 2f
            } else {
                // Gentle human micro-saccades when idling
                targetGazeX = (Random.nextFloat() - 0.5f) * 10f
                targetGazeY = (Random.nextFloat() - 0.5f) * 6f
            }
            delay(Random.nextLong(1600L, 3200L))
        }
    }
    val smoothGazeX by animateFloatAsState(
        targetValue = targetGazeX,
        animationSpec = tween(950, easing = FastOutSlowInEasing),
        label = "EyeContactX"
    )
    val smoothGazeY by animateFloatAsState(
        targetValue = targetGazeY,
        animationSpec = tween(950, easing = FastOutSlowInEasing),
        label = "EyeContactY"
    )

    // Randomized natural blinking (with occasional natural double-blink)
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(avatarId) {
        while (true) {
            delay(Random.nextLong(2100L, 4600L))
            isBlinking = true
            delay(125L)
            isBlinking = false
            if (Random.nextFloat() < 0.22f) {
                delay(150L)
                isBlinking = true
                delay(110L)
                isBlinking = false
            }
        }
    }

    val blinkSweep by animateFloatAsState(
        targetValue = if (isBlinking) 1f else 0f,
        animationSpec = tween(90),
        label = "EyelidBlinkSweep"
    )

    val targetHeadTilt = when (emotion) {
        AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC, AvatarEmotionState.HAIR_TUCK -> -3.4f
        AvatarEmotionState.SAD_CARING -> -2.4f
        AvatarEmotionState.ROAST_ANGRY -> 2.1f
        AvatarEmotionState.SURPRISED, AvatarEmotionState.WINK -> 2.6f
        AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING -> 1.2f
        else -> (smoothGazeX * 0.14f)
    }
    val headTilt by animateFloatAsState(
        targetValue = targetHeadTilt,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 260f),
        label = "HeadTilt"
    )

    val emotionScale by animateFloatAsState(
        targetValue = when (emotion) {
            AvatarEmotionState.ROAST_ANGRY -> 1.045f
            AvatarEmotionState.SAD_CARING -> 0.98f
            AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING -> 1.02f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = 0.72f),
        label = "EmotionScale"
    )

    val chestScaleX = (1.0f + breathCycle * 0.016f) * emotionScale
    val chestScaleY = (1.0f + breathCycle * 0.032f) * emotionScale
    val breathLiftPx = -breathCycle * 6f
    val drawableRes = SanamCatalog.resolveAvatarDrawable(avatarId, emotion, isBlinking)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .testTag("live_human_engine"),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Canvas Biological Breathing & Emotion Aura Backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.44f)
            val baseRadius = size.minDimension * (0.46f + breathCycle * 0.05f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        reactionAccent.copy(alpha = 0.34f),
                        baseAccent.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.28f
                ),
                radius = baseRadius * 1.28f,
                center = center
            )

            // Subtle breathing pulse ring synced to diaphragm cycle
            drawCircle(
                color = reactionAccent.copy(alpha = 0.25f + breathCycle * 0.22f),
                radius = baseRadius * 0.94f,
                center = center,
                style = Stroke(width = 2.5f)
            )
        }

        // Layer 2: Crossfaded Expressive Portrait with Breathing, Gaze Offset & Head Tilt
        Crossfade(
            targetState = drawableRes,
            animationSpec = tween(210),
            label = "AvatarExpressionCrossfade"
        ) { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = "${avatarItem.title} Live Human Engine",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = chestScaleX
                        scaleY = chestScaleY
                        translationX = smoothGazeX
                        translationY = breathLiftPx + smoothGazeY
                        rotationZ = headTilt
                    }
            )
        }

        // Layer 3: Canvas Facial Reactions, Eye Contact Reticle, Blink Sweep & Speaking Waveform
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gazeAnchor = Offset(w * 0.5f + smoothGazeX * 1.4f, h * 0.33f + smoothGazeY)

            // Eye-contact ocular shimmer arcs around gaze zone
            val eyeLeft = Offset(gazeAnchor.x - w * 0.085f, gazeAnchor.y)
            val eyeRight = Offset(gazeAnchor.x + w * 0.085f, gazeAnchor.y)
            if (blinkSweep > 0.08f) {
                // Eyelid blink sweep shadow line across upper eye line
                drawLine(
                    color = Color(0xCC0A0A12),
                    start = Offset(eyeLeft.x - 16f, eyeLeft.y + blinkSweep * 4f),
                    end = Offset(eyeLeft.x + 16f, eyeLeft.y + blinkSweep * 4f),
                    strokeWidth = 4.5f * blinkSweep,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xCC0A0A12),
                    start = Offset(eyeRight.x - 16f, eyeRight.y + blinkSweep * 4f),
                    end = Offset(eyeRight.x + 16f, eyeRight.y + blinkSweep * 4f),
                    strokeWidth = 4.5f * blinkSweep,
                    cap = StrokeCap.Round
                )
            } else {
                // Living eye-contact spark reflection
                drawCircle(color = Color.White.copy(alpha = 0.24f), radius = 3.2f, center = eyeLeft)
                drawCircle(color = Color.White.copy(alpha = 0.24f), radius = 3.2f, center = eyeRight)
            }

            // State-based facial reaction overlays
            when (emotion) {
                AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC, AvatarEmotionState.HAIR_TUCK -> {
                    // Warm bilateral cheek blush radial glows
                    val leftCheek = Offset(w * 0.36f + smoothGazeX, h * 0.43f + smoothGazeY)
                    val rightCheek = Offset(w * 0.64f + smoothGazeX, h * 0.43f + smoothGazeY)
                    drawCircle(
                        brush = Brush.radialGradient(listOf(NeonPink.copy(alpha = 0.44f), Color.Transparent), center = leftCheek, radius = w * 0.16f),
                        radius = w * 0.16f,
                        center = leftCheek
                    )
                    drawCircle(
                        brush = Brush.radialGradient(listOf(NeonPink.copy(alpha = 0.44f), Color.Transparent), center = rightCheek, radius = w * 0.16f),
                        radius = w * 0.16f,
                        center = rightCheek
                    )
                }

                AvatarEmotionState.ROAST_ANGRY -> {
                    // Fierce crimson vignette & sharp perimeter flare
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, CrimsonPrimary.copy(alpha = 0.42f)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = w * 0.7f
                        )
                    )
                    drawArc(
                        color = CrimsonPrimary.copy(alpha = 0.78f),
                        startAngle = 210f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(w * 0.22f, h * 0.08f),
                        size = Size(w * 0.56f, h * 0.22f),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }

                AvatarEmotionState.SAD_CARING -> {
                    // Cool caring sapphire wash + gentle tear-gleam shimmer
                    drawRect(color = Color(0x2838BDF8))
                    val gleamOffset = (sin(microPulsePhase.toDouble()).toFloat() + 1f) * 6f
                    drawLine(
                        color = Color(0xAA7DD3FC),
                        start = Offset(eyeLeft.x - 4f, eyeLeft.y + 10f + gleamOffset),
                        end = Offset(eyeLeft.x - 4f, eyeLeft.y + 24f + gleamOffset),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }

                AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING, AvatarEmotionState.WAVING, AvatarEmotionState.WINK -> {
                    // Buoyant golden-cyan joy sparks orbiting cheeks
                    for (idx in 0 until 5) {
                        val angle = microPulsePhase + idx * 1.25f
                        val sx = w * 0.5f + kotlin.math.cos(angle.toDouble()).toFloat() * (w * 0.34f)
                        val sy = h * 0.32f + sin(angle.toDouble()).toFloat() * (h * 0.18f)
                        drawCircle(
                            color = if (idx % 2 == 0) StarGold.copy(alpha = 0.65f) else NeonBlue.copy(alpha = 0.6f),
                            radius = 4.5f,
                            center = Offset(sx, sy)
                        )
                    }
                }

                else -> Unit
            }

            // Speaking Lip-Sync Waveform Arc at lower chin level
            if (isSpeaking) {
                val barWidth = (w * 0.18f * lipPulse + audioRms * 3.5f).coerceIn(36f, w * 0.42f)
                val barY = h - 22f
                drawLine(
                    color = reactionAccent.copy(alpha = 0.92f),
                    start = Offset(w * 0.5f - barWidth / 2f, barY),
                    end = Offset(w * 0.5f + barWidth / 2f, barY),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
        }

        if (showHudBadge) {
            val stateLabel = when (emotion) {
                AvatarEmotionState.HAPPY, AvatarEmotionState.LAUGHING -> "HAPPY"
                AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC, AvatarEmotionState.HAIR_TUCK -> "SHY"
                AvatarEmotionState.ROAST_ANGRY -> "ANGRY"
                AvatarEmotionState.SAD_CARING -> "SAD"
                AvatarEmotionState.WAVING -> "GREET"
                else -> "IDLE • EYE CONTACT"
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(Color(0xBB0A0A12), RoundedCornerShape(10.dp))
                    .border(1.dp, reactionAccent.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stateLabel,
                    color = reactionAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun LiveHumanAvatarStage(
    avatarId: String,
    emotion: AvatarEmotionState,
    isSpeaking: Boolean,
    audioRms: Float,
    modifier: Modifier = Modifier
) {
    LiveHumanEngine(
        avatarId = avatarId,
        emotion = emotion,
        isSpeaking = isSpeaking,
        audioRms = audioRms,
        modifier = modifier
    )
}

/**
 * Full-Screen Pure Black Voice Focus Mode (#000000).
 * Integrates LiveHumanEngine (breathing, eye contact, random blinking, and facial reactions)
 * cleanly with yellow Hinglish subtitles, waveform, reaction chips, and bottom Mic OFF button.
 */
@Composable
fun VoiceFocusOverlay(
    viewModel: StarSecurityViewModel,
    onVoiceNavRequest: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSpeaking = uiState.assistantStatus == "SPEAKING" || uiState.isSanamResponding
    val activeAgent = remember(uiState.activeAgentId) {
        SanamCatalog.fourteenAgents.find { it.id == uiState.activeAgentId } ?: SanamCatalog.fourteenAgents.first()
    }

    BackHandler { viewModel.deactivateVoiceFocusMic() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .testTag("voice_focus_overlay")
    ) {
        // Large centered LiveHumanEngine Avatar Portrait
        LiveHumanEngine(
            avatarId = uiState.selectedAvatarId,
            emotion = uiState.avatarEmotion,
            isSpeaking = isSpeaking,
            audioRms = uiState.wakeWordAudioRms,
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .align(Alignment.Center)
                .padding(horizontal = 12.dp)
                .offset { IntOffset(0, -34.dp.roundToPx()) }
        )

        // Subtle Bottom Vignette Fade for readable yellow subtitles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xDD000000), Color.Black)
                    )
                )
        )

        // Minimal Top Bar: CHOUHAN STAR corner mark + active Agent & Personality + Close
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                    contentDescription = "CHOUHAN STAR",
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${uiState.customAppName} • ${activeAgent.name}",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    when (uiState.assistantStatus) {
                                        "SPEAKING" -> NeonBlue
                                        "THINKING" -> NeonPurple
                                        else -> SuccessGreen
                                    },
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${uiState.assistantStatus} • ${uiState.moodTheme}",
                            color = NeonBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = { viewModel.deactivateVoiceFocusMic() },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .background(Color(0x44FFFFFF), CircleShape)
                    .testTag("voice_focus_close_btn")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Voice Focus", tint = Color.White)
            }
        }

        // Bottom Controls: Waveform + Yellow Subtitles + Facial Reaction Triggers + Voice Chips + Mic OFF
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LiveAudioWaveform(
                rmsDb = uiState.wakeWordAudioRms.coerceAtLeast(if (isSpeaking) 5f else 2f),
                isActive = true,
                accentColor = if (isSpeaking) NeonBlue else CrimsonPrimary,
                barCount = 20
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.yellowSubtitleText,
                color = YellowSubtitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // State-Based Facial Reaction Bar inside Voice Focus (Idle / Happy / Shy / Angry / Sad)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("Idle", AvatarEmotionState.NEUTRAL, NeonBlue),
                    Triple("Happy", AvatarEmotionState.HAPPY, StarGold),
                    Triple("Shy", AvatarEmotionState.SHY_BLUSH, NeonPink),
                    Triple("Angry", AvatarEmotionState.ROAST_ANGRY, CrimsonPrimary),
                    Triple("Sad", AvatarEmotionState.SAD_CARING, Color(0xFF38BDF8))
                ).forEach { (label, state, pillColor) ->
                    val active = uiState.avatarEmotion == state
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (active) pillColor.copy(alpha = 0.28f) else Color(0x441A1A2E))
                            .border(1.dp, if (active) pillColor else Color(0x55FFFFFF), RoundedCornerShape(14.dp))
                            .clickable { viewModel.triggerAvatarEmotionDemo(state) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("voice_focus_emotion_${label.lowercase()}")
                    ) {
                        Text(label, color = if (active) pillColor else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick spoken navigation & prompt chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "I love you" to "I love you",
                    "Roast me" to "Roast karo",
                    "Udaas hoon" to "Thoda udaas hoon",
                    "Home Screen Kholo" to "Home Screen Kholo",
                    "Vault Kholo" to "Vault Kholo",
                    "Screen Band Karo" to "Screen Band Karo"
                ).forEach { (chipLabel, spoken) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x331A1A2E))
                            .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(14.dp))
                            .clickable {
                                if (spoken == "Home Screen Kholo") {
                                    viewModel.deactivateVoiceFocusMic()
                                    onVoiceNavRequest("home")
                                } else {
                                    viewModel.sendChatMessage(spoken)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(chipLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Single Bottom Mic OFF Button
            Button(
                onClick = { viewModel.deactivateVoiceFocusMic() },
                modifier = Modifier
                    .height(52.dp)
                    .testTag("voice_focus_mic_off_btn"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary, contentColor = Color.White)
            ) {
                Icon(imageVector = Icons.Default.MicOff, contentDescription = "Mic OFF", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mic OFF • Exit Focus", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * AvatarGallery:
 * 4x4 grid of all 16 AI agents (Neon Blue, Neon Purple, Neon Ghost, Neon Solar, ZARA, MAYA, LIA, KAYRA).
 * Clicking any avatar persists activeAgentId, selectedAvatarId, moodTheme UI personality, and voicePitch in DataStore.
 */
@Composable
fun AvatarGallery(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Neon Cyber", "ZARA Black Blazer", "Green Dress MAYA", "Anime Silver")

    val selectedItem = remember(uiState.selectedAvatarId) {
        SanamCatalog.avatarGalleryItems.find { it.id == uiState.selectedAvatarId } ?: SanamCatalog.avatarGalleryItems.first()
    }
    val activeAgent = remember(uiState.activeAgentId) {
        SanamCatalog.fourteenAgents.find { it.id == uiState.activeAgentId } ?: SanamCatalog.fourteenAgents.first()
    }

    val filtered = remember(selectedCategory) {
        if (selectedCategory == "All") SanamCatalog.avatarGalleryItems
        else SanamCatalog.avatarGalleryItems.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .navigationBarsPadding()
            .padding(16.dp)
            .testTag("avatar_gallery_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Avatar Gallery • 4x4",
                subtitle = "Select an agent to sync DataStore & UI personality",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // Live Preview Hero Card using LiveHumanEngine
        item {
            GlassHudCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(selectedItem.accentHex).copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiveHumanEngine(
                        avatarId = uiState.selectedAvatarId,
                        emotion = uiState.avatarEmotion,
                        isSpeaking = uiState.isSanamResponding,
                        audioRms = uiState.wakeWordAudioRms,
                        showHudBadge = false,
                        modifier = Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, Color(selectedItem.accentHex), RoundedCornerShape(18.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = selectedItem.title,
                            color = Color(selectedItem.accentHex),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Agent: ${activeAgent.name} • ${activeAgent.role}",
                            color = tokens.onBackgroundText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "UI Personality: ${uiState.moodTheme}",
                            color = StarGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Saved in DataStore • Pitch ${"%.2f".format(uiState.voicePitch)}",
                            color = SuccessGreen,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Category Filter Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val sel = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (sel) tokens.primaryAccent else Color(0xFF1A1A2E))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (cat == "All") "All 4x4 (16)" else cat,
                            color = if (sel) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4x4 Grid of Agents (Neon Blue, Neon Purple, Neon Ghost, Neon Solar, ZARA, MAYA, LIA, KAYRA)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isSelected = uiState.selectedAvatarId == item.id
                            val itemAccent = Color(item.accentHex)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) itemAccent.copy(alpha = 0.24f) else Color(0xFF141628))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) itemAccent else itemAccent.copy(alpha = 0.32f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { viewModel.selectAvatarFromGallery(item.id) }
                                    .padding(4.dp)
                                    .testTag("avatar_tile_${item.id}"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Image(
                                        painter = painterResource(id = item.primaryDrawable),
                                        contentDescription = item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(86.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Active",
                                            tint = itemAccent,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(16.dp)
                                                .background(Color(0xCC0A0A12), CircleShape)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    color = if (isSelected) itemAccent else Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.moodTheme.substringBefore(" "),
                                    color = itemAccent.copy(alpha = 0.85f),
                                    fontSize = 8.sp,
                                    maxLines = 1
                                )
                            }
                        }
                        repeat((4 - rowItems.size).coerceAtLeast(0)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Live Human Engine Facial Reaction Controls
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPink.copy(alpha = 0.6f)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "LIVE HUMAN ENGINE • FACIAL REACTIONS & BLINK",
                        color = NeonPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Idle ✨" to AvatarEmotionState.NEUTRAL,
                            "Happy 😄" to AvatarEmotionState.HAPPY,
                            "Shy ❤️" to AvatarEmotionState.SHY_BLUSH,
                            "Angry 🔥" to AvatarEmotionState.ROAST_ANGRY,
                            "Sad 🤍" to AvatarEmotionState.SAD_CARING,
                            "Wink 😉" to AvatarEmotionState.WINK
                        ).forEach { (label, state) ->
                            val isCurrent = uiState.avatarEmotion == state
                            Button(
                                onClick = { viewModel.triggerAvatarEmotionDemo(state) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrent) tokens.primaryAccent else Color(0xFF23253D)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isCurrent) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(84.dp)) }
    }
}

@Composable
fun AvatarGalleryScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    AvatarGallery(viewModel = viewModel, onNavigateBack = onNavigateBack)
}

/**
 * Safety Confirmation Card: Strictly blocks Pay / Send / Delete actions until explicit Allow/Deny tap.
 */
@Composable
fun SafetyConfirmationOverlay(
    request: ActionConfirmationRequest,
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .padding(24.dp)
            .testTag("safety_confirmation_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF17192D)),
            border = BorderStroke(2.dp, StarGold)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = StarGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("SAFETY GUARD • ${request.actionType}", color = StarGold, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Text(request.targetLabel, color = TextGrey, fontSize = 11.sp)
                    }
                }

                Text(text = request.summary, color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDeny,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("safety_deny_btn"),
                        border = BorderStroke(1.2.dp, CrimsonPrimary)
                    ) {
                        Text("Deny", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAllow,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("safety_allow_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Allow", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
