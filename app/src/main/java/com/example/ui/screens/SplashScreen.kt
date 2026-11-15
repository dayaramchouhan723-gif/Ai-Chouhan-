package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LiveAudioWaveform
import com.example.ui.components.WireframeStar
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    var progress by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, tween(450))
        for (pct in 0..100 step 10) {
            progress = pct
            delay(65)
        }
        delay(250)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Text(
                text = "॥ JAI SHRI RAM ॥",
                color = StarGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(contentAlignment = Alignment.Center) {
                WireframeStar(
                    size = 165.dp,
                    variant = "Neon Crimson Pulsar",
                    primaryColor = CrimsonPrimary,
                    glowColor = NeonBlue,
                    animated = true
                )
                Image(
                    painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                    contentDescription = "CHOUHAN STAR & SANAM Orb Logo",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonBlue, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LiveAudioWaveform(rmsDb = 6f, isActive = true, accentColor = NeonBlue, barCount = 16)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "C H O U H A N   S T A R",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "SANAM AI • ZARA INSIDE • v2.2.8",
                color = NeonBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            LinearProgressIndicator(
                progress = { progress / 100f },
                color = NeonBlue,
                trackColor = Color(0xFF1A1A2E),
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "LOADING $progress% — 01 FACE / 02 VOICE / 03 BRAIN / 04 BUILD Ready",
                color = if (progress >= 90) SuccessGreen else TextGrey,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
