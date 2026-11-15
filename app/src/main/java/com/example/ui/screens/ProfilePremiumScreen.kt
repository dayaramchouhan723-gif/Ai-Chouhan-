package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.WireframeStar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextGrey
import com.example.ui.theme.YellowSubtitle
import com.example.viewmodel.StarSecurityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

/**
 * PATCH 1: In-App Profile & Google Sign-In Hub.
 * Launch CredentialManager bottom sheet directly inside the app (never external browser).
 * Handles Logged-Out (Guest Mode), Logged-In, Switch Account, Offline Retry, and
 * Sign-Out Confirmation staying on Profile & preserving all user data.
 */
@Composable
fun ProfilePremiumScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit,
    onSignedOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var firebaseUser by remember {
        mutableStateOf(try { Firebase.auth.currentUser } catch (_: Exception) { null })
    }
    var isSigningIn by remember { mutableStateOf(false) }
    var lastAuthError by remember { mutableStateOf<String?>(null) }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val isSignedIn = firebaseUser != null

    fun triggerInAppGoogleSignIn() {
        isSigningIn = true
        lastAuthError = null
        onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { displayName, email ->
                isSigningIn = false
                firebaseUser = try { Firebase.auth.currentUser } catch (_: Exception) { null }
                viewModel.onUserSignedIn(displayName, email)
                Toast.makeText(context, "Welcome back, ${displayName.substringBefore(" ")}", Toast.LENGTH_SHORT).show()
            },
            onAuthError = { err ->
                isSigningIn = false
                lastAuthError = err
                Toast.makeText(context, "Offline Mode", Toast.LENGTH_SHORT).show()
            },
            scope = scope,
            onAuthCancelled = {
                isSigningIn = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("profile_premium_screen")
    ) {
        // Header with Logo + Red Star + Back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("profile_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = painterResource(id = R.drawable.img_chouhan_star_icon),
                contentDescription = "CHOUHAN STAR",
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.dp, CrimsonPrimary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            WireframeStar(size = 24.dp, primaryColor = CrimsonPrimary, glowColor = CrimsonPrimary, animated = true)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Profile & Account",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CHOUHAN STAR • Personal Lifetime Sovereign",
                    color = TextGrey,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Profile Photo + Identity Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_account_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.2.dp, if (isSignedIn) SuccessGreen else CrimsonPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSignedIn && firebaseUser?.photoUrl != null) {
                    AsyncImage(
                        model = firebaseUser?.photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(2.dp, SuccessGreen, CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF232538))
                            .border(2.dp, if (isSignedIn) SuccessGreen else TextGrey, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Guest Avatar",
                            tint = if (isSignedIn) NeonBlue else TextGrey,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSignedIn) (firebaseUser?.displayName ?: "Dayaram Chouhan") else "Guest User / Dayaram Chouhan",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (isSignedIn) (firebaseUser?.email ?: "") else "Aap login nahi hain",
                    color = if (isSignedIn) NeonBlue else YellowSubtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isSignedIn) {
                    // Red In-App Google Sign-In Button
                    Button(
                        onClick = { triggerInAppGoogleSignIn() },
                        enabled = !isSigningIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_google_sign_in_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(text = "G", fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Sign in with Google", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (lastAuthError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Offline Mode: $lastAuthError",
                            color = YellowSubtitle,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = { triggerInAppGoogleSignIn() },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("profile_retry_signin_button"),
                            border = BorderStroke(1.dp, NeonBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Sign-In", color = NeonBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("profile_continue_without_login"),
                        border = BorderStroke(1.dp, TextGrey)
                    ) {
                        Text("Continue Without Login", color = TextGrey, fontSize = 13.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { triggerInAppGoogleSignIn() },
                            modifier = Modifier.weight(1f).height(46.dp).testTag("switch_account_button"),
                            border = BorderStroke(1.dp, NeonBlue)
                        ) {
                            Icon(imageVector = Icons.Default.SwitchAccount, contentDescription = "Switch Account", tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Account", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showSignOutConfirmDialog = true },
                            modifier = Modifier.weight(1f).height(46.dp).testTag("sign_out_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A1016), contentColor = Color(0xFFFF5252))
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Lifetime Active Personal Build Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("personal_build_banner"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF200A10)),
            border = BorderStroke(1.2.dp, CrimsonPrimary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF380E16), Color(0xFF1E080C))))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PERSONAL BUILD v2.2.8", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Box(
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("100% FREE LIFETIME", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("CHOUHAN STAR Sovereign Edition", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Built by Dayaram Chouhan. Zero subscription, AES-256 encrypted local vault, 32 voices & Voice Focus Mode.",
                        color = TextGrey,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Diagnostics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Local Vault Cipher", color = TextGrey, fontSize = 13.sp)
                    Text("AES-256-GCM Hardware", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Intruder Photos Isolation", color = TextGrey, fontSize = 13.sp)
                    Text("Pictures/CHOUHAN_STAR/", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Synced Chats / Notes", color = TextGrey, fontSize = 13.sp)
                    Text("${uiState.chatMessages.size} Chats • ${uiState.vaultItems.size} Vault", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }

    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            title = { Text("Confirm Sign Out", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Kya aap Sign Out karna chahte ho? Aapka sara local vault aur Room data safe rahega.", color = TextGrey) },
            containerColor = SurfaceCard,
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirmDialog = false
                        userSignOut(
                            context = context,
                            credentialManager = credentialManager,
                            onSignOutComplete = {
                                firebaseUser = null
                                viewModel.signOutToGuestKeepData()
                                onSignedOut()
                            },
                            scope = scope
                        )
                    }
                ) {
                    Text("Yes, Sign Out", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmDialog = false }) {
                    Text("No, Stay", color = NeonBlue)
                }
            }
        )
    }
}
