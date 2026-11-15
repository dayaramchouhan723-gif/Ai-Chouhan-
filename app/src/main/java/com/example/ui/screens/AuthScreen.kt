package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.components.WireframeStar
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GlassCardColor
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextGrey
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Welcome Screen (PATCH 1):
 * Strictly offline-first friendly: shows Logo + Red Star + SANAM Orb +
 * "Built by Dayaram Chouhan - Free Lifetime" + ONLY two clean actions:
 * [Continue] and [Continue Without Login / Skip]. Both go straight to PIN Setup -> Home.
 * Google Sign-In happens inside the app on the Profile Screen.
 */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onContinueWithoutLogin: () -> Unit = onAuthSuccess
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(24.dp)
            .testTag("auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                    contentDescription = "CHOUHAN STAR Logo",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, CrimsonPrimary, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                WireframeStar(
                    size = 42.dp,
                    primaryColor = CrimsonPrimary,
                    glowColor = CrimsonPrimary,
                    animated = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(134.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101324))
                    .border(2.dp, NeonBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_zara_open),
                    contentDescription = "SANAM AI Orb & ZARA",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Welcome to CHOUHAN STAR",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "SANAM AI • ZARA • Built by Dayaram Chouhan - Free Lifetime",
                color = StarGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassCardColor, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureBadge(icon = Icons.Default.Shield, title = "Touch & Motion Guard", subtitle = "Anti-Theft Realtime Siren & Local Snapshot")
                FeatureBadge(icon = Icons.Default.Lock, title = "Military App Lock + GF Mode", subtitle = "PIN, Biometrics & Custom Scold Engine")
                FeatureBadge(icon = Icons.Default.Star, title = "Voice Focus + Live ZARA + 24 Tools", subtitle = "100% Free Forever • Works Completely Offline")
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Primary Continue -> PIN Setup -> Home
            Button(
                onClick = onContinueWithoutLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_continue_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Continue",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Skip -> PIN Setup -> Home (Offline badge)
            OutlinedButton(
                onClick = onContinueWithoutLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("skip_login_offline_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, TextGrey),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(
                    text = "Continue Without Login / Skip",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextGrey
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "You can sign in with Google anytime inside Profile • No Ads • No Tracking",
                color = TextGrey,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureBadge(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFF221426), CircleShape)
                .border(1.dp, CrimsonPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextGrey, fontSize = 11.sp)
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: (displayName: String, email: String) -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onAuthError("Offline Mode — Google client unavailable locally")
        return
    }

    val activity = context as? Activity
    if (activity == null) {
        onAuthError("In-app activity context unavailable")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idCred = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(idCred.idToken, null)
                val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                onAuthSuccess(
                    user?.displayName ?: idCred.displayName ?: "Dayaram Chouhan",
                    user?.email ?: idCred.id
                )
            } else {
                onAuthError("Unexpected credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In offline/fallback: ${e.message}")
            onAuthError(e.localizedMessage ?: "Offline Mode")
        }
    }
}

fun userSignOut(
    context: Context,
    credentialManager: CredentialManager,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    try {
        Firebase.auth.signOut()
    } catch (_: Exception) { }
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("Auth", "Failed to clear credential state", e)
        } finally {
            onSignOutComplete()
        }
    }
}
