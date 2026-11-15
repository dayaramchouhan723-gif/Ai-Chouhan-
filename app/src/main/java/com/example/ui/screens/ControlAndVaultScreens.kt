package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SanamCatalog
import com.example.ui.components.GlassHudCard
import com.example.ui.components.SubScreenHeader
import com.example.ui.theme.LocalStarThemeTokens
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

// Screen 6: Control Dashboard (WiFi, BT, Torch, Brightness, Volume, DND, Alarm, Screenshot + Phone 12 + PC 12 Grids)
@Composable
fun ControlDashboardScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("control_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Control Dashboard",
                subtitle = "Phone 12 • PC 12 • Hardware Quick Toggles",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        // System Control Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlTile("WiFi", if (uiState.wifiEnabled) "Connected" else "Off", Icons.Default.Wifi, uiState.wifiEnabled, Modifier.weight(1f)) {
                    viewModel.toggleWifiControl()
                }
                ControlTile("Bluetooth", if (uiState.bluetoothEnabled) "Paired" else "Off", Icons.Default.Bluetooth, uiState.bluetoothEnabled, Modifier.weight(1f)) {
                    viewModel.toggleBluetoothControl()
                }
                ControlTile("Torch", if (uiState.torchEnabled) "ON" else "OFF", Icons.Default.FlashlightOn, uiState.torchEnabled, Modifier.weight(1f)) {
                    viewModel.toggleTorch(!uiState.torchEnabled)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlTile("DND", if (uiState.dndEnabled) "Focus" else "Off", Icons.Default.DoNotDisturbOn, uiState.dndEnabled, Modifier.weight(1f)) {
                    viewModel.toggleDndControl()
                }
                ControlTile("Alarm", uiState.nextAlarmLabel.substringBefore("•").trim(), Icons.Default.Alarm, true, Modifier.weight(1f)) {
                    viewModel.setQuickAlarm("05:30 AM • Early Rise")
                }
                ControlTile("Screenshot", "Pictures/", Icons.Default.CameraAlt, false, Modifier.weight(1f)) {
                    viewModel.captureLocalScreenshot()
                }
            }
        }

        // Brightness & Media Volume Sliders
        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Brightness (${uiState.brightnessPercent.toInt()}%)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = uiState.brightnessPercent,
                        onValueChange = { viewModel.setBrightnessPercent(it) },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonBlue, activeTrackColor = NeonBlue)
                    )
                    Text("Media Volume (${uiState.mediaVolumePercent.toInt()}%)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = uiState.mediaVolumePercent,
                        onValueChange = { viewModel.setMediaVolumePercent(it) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink)
                    )
                }
            }
        }

        // PHONE 12 Grid
        item {
            Text("PHONE 12 GRID", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.phone12Grid, accent = NeonBlue) { item ->
                when (item) {
                    "WiFi" -> viewModel.toggleWifiControl()
                    "Bluetooth" -> viewModel.toggleBluetoothControl()
                    "Torch" -> viewModel.toggleTorch(!uiState.torchEnabled)
                    "DND" -> viewModel.toggleDndControl()
                    "Screenshot" -> viewModel.captureLocalScreenshot()
                    "Touch Guard" -> onNavigateTo("touch_guard")
                    "App Lock" -> onNavigateTo("app_lock")
                    "Hidden Calc" -> viewModel.toggleHiddenCalculatorMode(!uiState.hiddenCalculatorMode)
                    else -> viewModel.sendChatMessage("Phone control triggered: $item")
                }
            }
        }

        // PC 12 Grid
        item {
            Text("PC 12 CONNECT GRID", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            CompactThreeColumnGrid(items = SanamCatalog.pc12Grid, accent = NeonPurple) { item ->
                viewModel.sendChatMessage("PC Connect action: $item")
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun ControlTile(title: String, state: String, icon: ImageVector, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    GlassHudCard(
        modifier = modifier.height(82.dp),
        borderColor = if (active) NeonBlue else Color(0xFF2B2B44),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = if (active) NeonBlue else TextGrey, modifier = Modifier.size(20.dp))
            Column {
                Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(state, color = if (active) SuccessGreen else TextGrey, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun CompactThreeColumnGrid(items: List<String>, accent: Color, onItemClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x661A1A2E))
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onItemClick(label) }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
        }
    }
}

// Screen 7: Vault (Numeric PIN Pad 1-9/0/BIO/DEL + 30s Lockout + 3 Encrypted Folders + Blur/Fake Vault/Auto-Lock)
@Composable
fun VaultScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    var unlockedLocal by remember { mutableStateOf(false) }
    var pinEntry by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf("Photos") }
    var newTitle by remember { mutableStateOf("") }
    var newSecret by remember { mutableStateOf("") }

    val photoPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addVaultEncryptedItem(
                folder = "Photos",
                title = "Vault Photo #${uiState.vaultItems.size + 1}",
                plainSecret = "Encrypted Photo Reference: ${uri.lastPathSegment ?: "local_media"}",
                mediaUri = uri.toString()
            )
        }
    }

    val visibleItems = remember(uiState.vaultItems, selectedFolder, uiState.fakeVaultMode) {
        uiState.vaultItems.filter { it.folder == selectedFolder && it.isDecoy == uiState.fakeVaultMode }
            .ifEmpty { uiState.vaultItems.filter { it.folder == selectedFolder } }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("vault_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Sovereign AES-256 Vault",
                subtitle = if (uiState.fakeVaultMode) "Fake Decoy Mode Active • Protected" else "Photos • Notes • Prompts • Local Pictures/",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        if (!unlockedLocal) {
            item {
                GlassHudCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (uiState.vaultLockoutSeconds > 0) Color(0xFFE53935) else NeonBlue
                ) {
                    Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Vault Lock",
                            tint = if (uiState.vaultLockoutSeconds > 0) Color(0xFFE53935) else NeonBlue,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.vaultLockoutSeconds > 0) {
                            Text(
                                text = "LOCKED: ${uiState.vaultLockoutSeconds}s COOLDOWN",
                                color = Color(0xFFE53935),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.testTag("vault_lockout_banner")
                            )
                            Text(
                                text = "Bhai galat PIN 3 baar daala — 30 second wait karo",
                                color = TextGrey,
                                fontSize = 11.sp
                            )
                        } else {
                            Text("Enter 4-Digit Vault PIN (default 1234)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            if (uiState.vaultFailedCount > 0) {
                                Text("Wrong PIN attempts: ${uiState.vaultFailedCount}/3", color = NeonPink, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // PIN dots indicator
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            repeat(4) { idx ->
                                val filled = idx < pinEntry.length
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(if (filled) NeonBlue else Color(0xFF1E223A))
                                        .border(1.dp, NeonBlue, androidx.compose.foundation.shape.CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Numeric Keypad 1-9, BIO, 0, DEL
                        val keypadRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("BIO", "0", "DEL")
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            keypadRows.forEach { rowKeys ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    rowKeys.forEach { key ->
                                        Button(
                                            onClick = {
                                                when (key) {
                                                    "BIO" -> {
                                                        if (uiState.vaultLockoutSeconds == 0) unlockedLocal = true
                                                    }
                                                    "DEL" -> {
                                                        if (pinEntry.isNotEmpty()) pinEntry = pinEntry.dropLast(1)
                                                    }
                                                    else -> {
                                                        if (uiState.vaultLockoutSeconds == 0 && pinEntry.length < 4) {
                                                            pinEntry += key
                                                            if (pinEntry.length == 4) {
                                                                val ok = viewModel.unlockVaultWithPin(pinEntry)
                                                                if (ok) unlockedLocal = true else pinEntry = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                                .testTag("vault_key_$key"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = when (key) {
                                                    "BIO" -> NeonPurple
                                                    "DEL" -> Color(0xFF281620)
                                                    else -> Color(0xFF191D33)
                                                }
                                            )
                                        ) {
                                            Text(key, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Controls Bar: Blur Preview Toggle, Fake Vault Mode, Auto-Lock Timer
            item {
                GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPurple.copy(alpha = 0.6f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Blur Sensitive Secret Previews", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            androidx.compose.material3.Switch(
                                checked = uiState.vaultBlurEnabled,
                                onCheckedChange = { viewModel.toggleVaultBlur(it) }
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Fake Decoy Vault View", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            androidx.compose.material3.Switch(
                                checked = uiState.fakeVaultMode,
                                onCheckedChange = { viewModel.toggleFakeVaultMode(it) }
                            )
                        }
                        Text("AUTO-LOCK TIMER", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("30s", "1m", "5m", "Never").forEach { opt ->
                                val sel = uiState.vaultAutoLockLabel == opt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) NeonBlue else Color(0xFF141628))
                                        .clickable { viewModel.setVaultAutoLockLabel(opt) }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(opt, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3 Folders Tabs: Photos / Notes / Prompts
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("Photos", "Notes", "Prompts").forEach { folder ->
                        val active = selectedFolder == folder
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (active) NeonBlue else Color(0xFF1A1A2E))
                                .clickable { selectedFolder = folder }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(folder, color = if (active) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Add Item / Zero-Permission Photo Picker
            item {
                GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue.copy(alpha = 0.5f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("ADD ENCRYPTED $selectedFolder ITEM", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (selectedFolder == "Photos") {
                            Button(
                                onClick = {
                                    photoPicker.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("vault_photo_picker_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                            ) {
                                Text("Lock Photo via Android Photo Picker (Zero Perm)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text("Title...", color = TextGrey) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = newSecret,
                            onValueChange = { newSecret = it },
                            placeholder = { Text("AES-256 Encrypted Content...", color = TextGrey) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank() || newSecret.isNotBlank()) {
                                    viewModel.addVaultEncryptedItem(selectedFolder, newTitle, newSecret)
                                    newTitle = ""
                                    newSecret = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Encrypt & Lock in $selectedFolder", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(visibleItems, key = { it.id }) { item ->
                val plain = remember(item.id, item.encryptedPayload) { viewModel.decryptVaultItemText(item) }
                GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = SuccessGreen.copy(alpha = 0.5f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("AES-256", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = if (uiState.vaultBlurEnabled) "••••••••••• (Blurred — toggle above to reveal)" else plain,
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = SuccessGreen) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("HIDDEN VAULT FILES (/Pictures/CHOUHAN_STAR/)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        uiState.picturesFiles.forEach { fileName ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = StarGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(fileName, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text("LOCAL", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { onNavigateTo("api_screen") },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Open Encrypted API Vault (Gemini/Groq/DeepSeek/OpenRouter)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// Screen 8: Prompt Library
@Composable
fun PromptLibraryScreen(
    viewModel: StarSecurityViewModel,
    onUsePromptInChat: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Reverse", "Diagrams", "ZARA Dialogues", "Automation")

    val filtered = remember(uiState.promptTemplates, searchQuery, selectedCategory) {
        uiState.promptTemplates.filter { item ->
            (selectedCategory == "All" || item.category == selectedCategory) &&
                (searchQuery.isBlank() || item.title.contains(searchQuery, true) || item.promptText.contains(searchQuery, true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("prompt_library_screen")
    ) {
        SubScreenHeader(
            title = "Prompt Library",
            subtitle = "Reverse • Diagrams • ZARA Dialogues • Automation",
            onBack = onNavigateBack,
            onInstallApk = { viewModel.exportApkAction() }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search prompts...", color = TextGrey, fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val sel = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (sel) NeonBlue else Color(0xFF1A1A2E))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(cat, color = if (sel) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { item ->
                GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(item.category, color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { viewModel.togglePromptFavorite(item.id) }) {
                                Icon(
                                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite Prompt",
                                    tint = StarGold
                                )
                            }
                        }
                        Text(item.promptText, color = TextGrey, fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    clipboard.setText(AnnotatedString(item.promptText))
                                    Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23253D))
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Copy", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.sendChatMessage(item.promptText)
                                    onUsePromptInChat(item.promptText)
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                            ) {
                                Text("Use with ZARA", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(78.dp)) }
        }
    }
}
