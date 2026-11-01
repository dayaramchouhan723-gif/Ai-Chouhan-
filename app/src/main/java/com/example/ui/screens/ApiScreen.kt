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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.StarSecurityViewModel

private val DarkBg = Color(0xFF0A0A0A)
private val CardBg = Color(0xFF1A1A1A)
private val CrimsonAura = Color(0xFFE53935)
private val TextGrey = Color(0xFFA0A0A0)

@Composable
fun ApiScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var geminiInput by remember(uiState.geminiKey) { mutableStateOf(uiState.geminiKey) }
    var groqInput by remember(uiState.groqKey) { mutableStateOf(uiState.groqKey) }
    var openAiInput by remember(uiState.openAiKey) { mutableStateOf(uiState.openAiKey) }
    var claudeInput by remember(uiState.claudeKey) { mutableStateOf(uiState.claudeKey) }
    var customUrlInput by remember(uiState.customApiUrl) { mutableStateOf(uiState.customApiUrl) }

    var isGeminiVisible by remember { mutableStateOf(false) }
    var isGroqVisible by remember { mutableStateOf(false) }
    var isOpenAiVisible by remember { mutableStateOf(false) }
    var isClaudeVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
            .testTag("api_screen")
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("api_back_button")
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
                    text = "API & Cloud Vault",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Encrypted LLM keys & endpoints",
                    color = TextGrey,
                    fontSize = 12.sp
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
            // Card 1: API Keys
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_keys_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LLM API Keys (Encrypted)",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Gemini Key Field
                        ApiKeyField(
                            label = "Gemini API Key",
                            value = geminiInput,
                            onValueChange = { geminiInput = it },
                            placeholder = "AIzaSy...",
                            isVisible = isGeminiVisible,
                            onToggleVisibility = { isGeminiVisible = !isGeminiVisible },
                            onTestAndSave = { viewModel.testAndSaveApiKey("gemini", geminiInput) },
                            testTag = "gemini_key_field"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Groq Key Field
                        ApiKeyField(
                            label = "Groq API Key",
                            value = groqInput,
                            onValueChange = { groqInput = it },
                            placeholder = "gsk_...",
                            isVisible = isGroqVisible,
                            onToggleVisibility = { isGroqVisible = !isGroqVisible },
                            onTestAndSave = { viewModel.testAndSaveApiKey("groq", groqInput) },
                            testTag = "groq_key_field"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // OpenAI Key Field
                        ApiKeyField(
                            label = "OpenAI API Key",
                            value = openAiInput,
                            onValueChange = { openAiInput = it },
                            placeholder = "sk-proj-...",
                            isVisible = isOpenAiVisible,
                            onToggleVisibility = { isOpenAiVisible = !isOpenAiVisible },
                            onTestAndSave = { viewModel.testAndSaveApiKey("openai", openAiInput) },
                            testTag = "openai_key_field"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Claude Key Field
                        ApiKeyField(
                            label = "Claude API Key",
                            value = claudeInput,
                            onValueChange = { claudeInput = it },
                            placeholder = "sk-ant-...",
                            isVisible = isClaudeVisible,
                            onToggleVisibility = { isClaudeVisible = !isClaudeVisible },
                            onTestAndSave = { viewModel.testAndSaveApiKey("claude", claudeInput) },
                            testTag = "claude_key_field"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom API URL Field
                        Text(text = "Custom API Endpoint URL", color = TextGrey, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            placeholder = { Text("https://api.yourdomain.com/v1", color = Color(0xFF555555), fontSize = 12.sp) },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.saveCustomApiUrl(customUrlInput) }) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Save Custom URL",
                                        tint = CrimsonAura,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("custom_api_url_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonAura,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextGrey, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "All keys stored securely via EncryptedSharedPreferences",
                                color = TextGrey,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Card 2: Connections Mini API
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("connections_mini_api_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A2A))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = CrimsonAura, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "External Integrations",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ConnectorRow(
                            title = "WhatsApp Connector",
                            subtitle = "Direct emergency messaging bridge",
                            checked = uiState.whatsappConnector,
                            onCheckedChange = { viewModel.toggleWhatsappConnector(it) },
                            hasConfig = true,
                            testTag = "whatsapp_connector_toggle"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        ConnectorRow(
                            title = "Gmail Connector",
                            subtitle = "Automated security report emails",
                            checked = uiState.gmailConnector,
                            onCheckedChange = { viewModel.toggleGmailConnector(it) },
                            hasConfig = false,
                            testTag = "gmail_connector_toggle"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        ConnectorRow(
                            title = "Calendar Connector",
                            subtitle = "Scheduled guard sync & events",
                            checked = uiState.calendarConnector,
                            onCheckedChange = { viewModel.toggleCalendarConnector(it) },
                            hasConfig = false,
                            testTag = "calendar_connector_toggle"
                        )
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
private fun ApiKeyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onTestAndSave: () -> Unit,
    testTag: String
) {
    Column {
        Text(text = label, color = TextGrey, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF555555), fontSize = 12.sp) },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = TextGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Button(
                        onClick = onTestAndSave,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAura),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp).height(32.dp)
                    ) {
                        Text(text = "Test & Save", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag(testTag),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonAura,
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
private fun ConnectorRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    hasConfig: Boolean,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (hasConfig) {
                IconButton(onClick = { /* Open config */ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Configure", tint = TextGrey, modifier = Modifier.size(18.dp))
                }
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
}
