package com.example.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.GlassCardColor
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextGrey

@Composable
fun PinLockSetupScreen(
    isSetupMode: Boolean,
    fingerprintEnabled: Boolean,
    failedAttempts: Int,
    onSetupComplete: (pin: String, fingerprint: Boolean) -> Boolean,
    onUnlockSuccess: (pin: String) -> Boolean,
    onBiometricUnlocked: () -> Unit,
    onSignOutBackToWelcome: () -> Unit
) {
    val context = LocalContext.current
    var pinInput by remember { mutableStateOf("") }
    var useFingerprint by remember(fingerprintEnabled) { mutableStateOf(fingerprintEnabled) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun launchBiometricPrompt() {
        val activity = context as? FragmentActivity
        if (activity == null) {
            onBiometricUnlocked()
            return
        }
        val canAuth = BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            onBiometricUnlocked()
            return
        }
        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onBiometricUnlocked()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    errorMsg = "Biometric skipped — enter 4-digit PIN (default: 1234)"
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("CHOUHAN STAR Sovereign Unlock")
            .setSubtitle("Verify fingerprint to unlock SANAM AI")
            .setNegativeButtonText("Use PIN")
            .build()
        prompt.authenticate(info)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(24.dp)
            .testTag("pin_lock_setup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(GlassCardColor, CircleShape)
                    .border(1.5.dp, NeonBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = NeonBlue, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (isSetupMode) "Setup 4-Digit Sovereign PIN" else "Unlock CHOUHAN STAR",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isSetupMode)
                    "Personal offline vault • Stored AES-256 locally"
                else
                    "Enter 4-digit PIN (default 1234) or use Fingerprint",
                color = TextGrey,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(4) { index ->
                    val filled = index < pinInput.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(if (filled) NeonBlue else Color(0xFF1A1A2E), CircleShape)
                            .border(1.5.dp, if (filled) NeonBlue else TextGrey, CircleShape)
                    )
                }
            }

            if (errorMsg != null || failedAttempts > 0) {
                Text(
                    text = errorMsg ?: "Wrong PIN ($failedAttempts attempts)",
                    color = NeonPink,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        if (isSetupMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassCardColor, RoundedCornerShape(14.dp))
                    .border(1.dp, NeonBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Fingerprint", tint = NeonBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Fingerprint Quick Unlock", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Switch(
                    checked = useFingerprint,
                    onCheckedChange = { useFingerprint = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = NeonBlue)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(GlassCardColor)
                                .border(1.dp, NeonBlue.copy(alpha = 0.28f), CircleShape)
                                .clickable {
                                    errorMsg = null
                                    when (key) {
                                        "DEL" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                        "BIO" -> launchBiometricPrompt()
                                        else -> if (pinInput.length < 4) {
                                            pinInput += key
                                            if (pinInput.length == 4) {
                                                if (isSetupMode) {
                                                    onSetupComplete(pinInput, useFingerprint)
                                                } else {
                                                    val ok = onUnlockSuccess(pinInput)
                                                    if (!ok) {
                                                        errorMsg = "Incorrect PIN. Try again or default 1234."
                                                        pinInput = ""
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .testTag("pin_key_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            when (key) {
                                "DEL" -> Icon(imageVector = Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete", tint = Color.White)
                                "BIO" -> Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Fingerprint", tint = NeonBlue, modifier = Modifier.size(28.dp))
                                else -> Text(text = key, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onSignOutBackToWelcome,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Welcome Screen", color = TextGrey, fontSize = 12.sp)
            }
            if (isSetupMode) {
                Button(
                    onClick = { onSetupComplete("1234", useFingerprint) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("pin_quick_enter_home_btn")
                ) {
                    Text("Use Default 1234 -> Home", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
