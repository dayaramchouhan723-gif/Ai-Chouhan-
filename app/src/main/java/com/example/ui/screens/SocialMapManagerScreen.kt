package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.viewmodel.StarSecurityViewModel

private val DarkBg = Color(0xFF0A0A0A)
private val CardBg = Color(0xFF1A1A1A)
private val CrimsonAura = Color(0xFFE53935)
private val TextGrey = Color(0xFFA0A0A0)

@Composable
fun SocialMapManagerScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var socialMessage by remember { mutableStateOf("॥ Jai Shri Ram ॥ Sovereign Security Active - Chouhan Star Star Build") }
    var groqPromptInput by remember { mutableStateOf("Summarize device security status in JSON format") }
    var groqOutputResult by remember { mutableStateOf("") }
    var latitudeInput by remember { mutableStateOf("28.6139") }
    var longitudeInput by remember { mutableStateOf("77.2090") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .testTag("social_map_manager_screen")
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("social_back_button")
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
                    text = "Social, Maps & Advanced Tools",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "IG/FB/WA Agent, Maps, File Manager & Groq JSON",
                    color = TextGrey,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!uiState.statusMessage.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Text(
                    text = uiState.statusMessage ?: "",
                    color = CrimsonAura,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: New Toggles (Gaming Assistant, ChatGPT Image, Auto Payments)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(text = "Advanced System Toggles", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        ToggleRow(
                            title = "Gaming Assistant",
                            subtitle = "Performance boost & touch lock during games",
                            checked = uiState.gamingAssistant,
                            onCheckedChange = { viewModel.toggleGamingAssistant(it) },
                            testTag = "toggle_gaming_assistant"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ToggleRow(
                            title = "ChatGPT Image Agent",
                            subtitle = "AI visual generation in chat assistant",
                            checked = uiState.chatGptImage,
                            onCheckedChange = { viewModel.toggleChatGptImage(it) },
                            testTag = "toggle_chatgpt_image"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ToggleRow(
                            title = "Auto Payments Shield",
                            subtitle = "Secure biometric authorization for transactions",
                            checked = uiState.autoPayments,
                            onCheckedChange = { viewModel.toggleAutoPayments(it) },
                            testTag = "toggle_auto_payments"
                        )
                    }
                }
            }

            // Card 2: Social Agent Status Poster (IG, FB, WA)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Social Agent (IG, FB, WA Status Post)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = socialMessage,
                            onValueChange = { socialMessage = it },
                            placeholder = { Text("Enter status update message...", color = Color(0xFF555555)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonAura,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.postSocialStatus("WhatsApp", socialMessage) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Post WA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.postSocialStatus("Instagram", socialMessage) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Post IG", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.postSocialStatus("Facebook", socialMessage) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Post FB", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Card 3: Maps & Screenshot Tool
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Maps & Screenshot Tool", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = latitudeInput,
                                onValueChange = { latitudeInput = it },
                                label = { Text("Latitude", color = TextGrey) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CrimsonAura, unfocusedBorderColor = Color(0xFF333333), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = longitudeInput,
                                onValueChange = { longitudeInput = it },
                                label = { Text("Longitude", color = TextGrey) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CrimsonAura, unfocusedBorderColor = Color(0xFF333333), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.postSocialStatus("Maps Radar", "Coordinates Lat: $latitudeInput, Lng: $longitudeInput pinned successfully.") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Pin & Locate on Maps Radar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.captureLocalScreenshot() },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Capture Local Screenshot to Pictures/CHOUHAN_STAR", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Card 4: Triggers Suggestions, Device, Timezone & Samsung / Android 7-15 Support
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Device & Trigger Diagnostics", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = uiState.deviceInfo, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "✓ Samsung OneUI & Stock Android optimization active.\n✓ Compatible across Android 7.0 (Nougat) to Android 15.\n✓ Automated timezone sync & background trigger worker ready.", color = TextGrey, fontSize = 11.sp)
                    }
                }
            }

            // Card 5: PC Connect & File Manager (Pictures folder)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "PC Connect & Pictures File Manager", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Folder: /Pictures/CHOUHAN_STAR/", color = TextGrey, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.picturesFiles.isEmpty()) {
                            Text(text = "No files found in local Pictures folder.", color = TextGrey, fontSize = 12.sp)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                uiState.picturesFiles.forEach { file ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(Color(0xFF141414), RoundedCornerShape(6.dp)).padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = file, color = Color.White, fontSize = 12.sp)
                                        Text(text = "Local Only", color = CrimsonAura, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 6: Groq JSON Retry Tester
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Groq JSON Agent with Auto-Retry", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = groqPromptInput,
                            onValueChange = { groqPromptInput = it },
                            label = { Text("Prompt", color = TextGrey) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CrimsonAura, unfocusedBorderColor = Color(0xFF333333), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.callGroqWithJsonRetry(groqPromptInput) { json ->
                                    groqOutputResult = json
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Execute Groq JSON Request (3x Retry)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (groqOutputResult.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Response JSON:", color = TextGrey, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = groqOutputResult, color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.background(Color(0xFF141414), RoundedCornerShape(8.dp)).padding(10.dp).fillMaxWidth())
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ToggleRow(
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
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextGrey, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CrimsonAura,
                uncheckedThumbColor = TextGrey,
                uncheckedTrackColor = Color(0xFF262626)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
