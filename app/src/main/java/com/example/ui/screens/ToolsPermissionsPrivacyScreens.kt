package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

/**
 * Interactive 24 Tools & Productivity Hub:
 * Teacher Mode (Samjhao + 5/5 Quiz), Meeting 1-Command 6-Task, Music Spotify<->YouTube Switcher,
 * DeepSearch, Reverse Engineering/Diagrams, Broker Watch (Draft Only), Gmail Draft, and 24-Tool Registry.
 */
@Composable
fun ToolsHubScreen(
    viewModel: StarSecurityViewModel,
    onNavigateTo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current

    val tabs = listOf(
        "Teacher 5/5",
        "Meeting 6-Task",
        "Music",
        "DeepSearch",
        "Reverse",
        "Broker",
        "Gmail",
        "24 Tools"
    )
    var selectedTab by remember { mutableStateOf(tabs.first()) }

    var teacherTopic by remember { mutableStateOf("CHOUHAN STAR AES-256 & Barge-In") }
    var meetingTopic by remember { mutableStateOf("CHOUHAN STAR v2.2.8 Release Sync") }
    var meetingEmail by remember { mutableStateOf("team@chouhanstar.local") }
    var songQuery by remember(uiState.currentMusicQuery) { mutableStateOf(uiState.currentMusicQuery) }
    var searchQuery by remember { mutableStateOf("Kotlin Compose MVVM Offline AI Security") }
    var reverseInput by remember { mutableStateOf("Auth -> PinLock -> Home -> VoiceFocusOverlay") }
    var brokerSymbol by remember { mutableStateOf("INFY / NIFTY50") }
    var gmailTo by remember { mutableStateOf("dayaram@example.com") }
    var gmailSubject by remember { mutableStateOf("CHOUHAN STAR v2.2.8 Briefing") }
    var gmailBody by remember { mutableStateOf("Namaste, aaj ka status update aur summary bhej raha hoon.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("tools_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Interactive 24 Tools Hub",
                subtitle = "Teacher 5/5 • Meeting 6-Task • Music • DeepSearch • Reverse",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    val active = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) NeonBlue else Color(0xFF1A1A2E))
                            .border(1.dp, if (active) NeonBlue else Color(0xFF2B2B44), RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("tools_tab_${tab.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = tab,
                            color = if (active) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            "Teacher 5/5" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("ZARA TEACHER MODE ('Samjhao' + 5/5 Quiz)", color = NeonBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(
                                value = teacherTopic,
                                onValueChange = { teacherTopic = it },
                                label = { Text("Topic to explain in Hinglish") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Button(
                                onClick = { viewModel.runTeacherSamjhao(teacherTopic) },
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("teacher_samjhao_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                            ) {
                                Text("Samjhao (ZARA Hinglish Explanation)", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            if (uiState.teacherExplanation.isNotBlank()) {
                                Text(
                                    text = uiState.teacherExplanation,
                                    color = YellowSubtitle,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF121426), RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                }

                items(SanamCatalog.teacherLessons) { q ->
                    val chosen = uiState.teacherQuizAnswers[q.id]
                    GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${q.id.uppercase()} • ${q.topic}", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(q.prompt, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            q.options.forEachIndexed { idx, opt ->
                                val isSel = chosen == idx
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) NeonBlue.copy(alpha = 0.22f) else Color(0xFF141628))
                                        .border(1.dp, if (isSel) NeonBlue else Color(0xFF2B2B44), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.selectTeacherAnswer(q.id, idx) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${idx + 1}. $opt", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.submitTeacherQuiz() },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("teacher_submit_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Submit 5/5 Practice Quiz", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    }
                    if (uiState.teacherResultMessage.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = uiState.teacherResultMessage,
                            color = StarGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            "Meeting 6-Task" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPurple) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("1-COMMAND 6-TASK MEETING ORCHESTRATOR", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(
                                value = meetingTopic,
                                onValueChange = { meetingTopic = it },
                                label = { Text("Meeting Topic") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = meetingEmail,
                                onValueChange = { meetingEmail = it },
                                label = { Text("Participant Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Button(
                                onClick = { viewModel.runMeetingOneCommandSixTasks(meetingTopic, meetingEmail) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("meeting_automate_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Text("Run 1 Command -> Generate 6 Tasks", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            uiState.meetingTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF141628), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text("${task.step}. ${task.label}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(task.detail, color = TextGrey, fontSize = 11.sp)
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { viewModel.launchMeetingAction("calendar", meetingTopic, meetingEmail) },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Calendar", fontSize = 11.sp) }
                                OutlinedButton(
                                    onClick = { viewModel.launchMeetingAction("email", meetingTopic, meetingEmail) },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Email", fontSize = 11.sp) }
                                Button(
                                    onClick = { viewModel.launchMeetingAction("meet", meetingTopic, meetingEmail) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) { Text("Join Now", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            "Music" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPink) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonPink, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("SPOTIFY <-> YOUTUBE INSTANT SONG SWITCHER", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Active: ${uiState.activeMusicPlatform} • Song preserved without retyping", color = StarGold, fontSize = 12.sp)
                            OutlinedTextField(
                                value = songQuery,
                                onValueChange = { songQuery = it },
                                label = { Text("Track / Artist Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { viewModel.switchMusicPlatform("Spotify", songQuery) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) { Text("Play in Spotify", color = Color.Black, fontWeight = FontWeight.Bold) }
                                Button(
                                    onClick = { viewModel.switchMusicPlatform("YouTube", songQuery) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                                ) { Text("Switch YouTube", color = Color.White, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }

            "DeepSearch" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("DEEP RESEARCH ENGINE", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Research Query") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Button(
                                onClick = { viewModel.runDeepSearchTool(searchQuery) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                            ) { Text("Synthesize Deep Research", color = Color.Black, fontWeight = FontWeight.Bold) }
                            if (uiState.deepSearchReport.isNotBlank()) {
                                Text(
                                    text = uiState.deepSearchReport,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.background(Color(0xFF121426), RoundedCornerShape(10.dp)).padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            "Reverse" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonPurple) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("REVERSE ENGINEERING & ARCHITECTURE DIAGRAMS", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = reverseInput,
                                onValueChange = { reverseInput = it },
                                label = { Text("Code / Screen Flow / Component") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Diagram -> Code", "Code -> Diagram", "Screenshot -> UI", "Reverse Prompt").chunked(2).forEach { pair ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        pair.forEach { mode ->
                                            Button(
                                                onClick = { viewModel.runReverseDiagramTool(reverseInput, mode) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23253D))
                                            ) { Text(mode, color = Color.White, fontSize = 11.sp) }
                                        }
                                    }
                                }
                            }
                            if (uiState.reverseDiagramOutput.isNotBlank()) {
                                Text(
                                    text = uiState.reverseDiagramOutput,
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    modifier = Modifier.fillMaxWidth().background(Color(0xFF0E101E), RoundedCornerShape(10.dp)).padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            "Broker" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = StarGold) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("BROKER MARKET WATCHLIST (DRAFT ONLY — ZERO AUTO TRADE)", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = brokerSymbol,
                                onValueChange = { brokerSymbol = it },
                                label = { Text("Ticker / Index") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Button(
                                onClick = { viewModel.trackBrokerSymbol(brokerSymbol) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StarGold)
                            ) { Text("Update Draft Watchlist", color = Color.Black, fontWeight = FontWeight.Bold) }
                            if (uiState.brokerSummary.isNotBlank()) {
                                Text(uiState.brokerSummary, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            "Gmail" -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("GMAIL VOICE DRAFT (CONFIRM BEFORE SEND)", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedTextField(value = gmailTo, onValueChange = { gmailTo = it }, label = { Text("To") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                            OutlinedTextField(value = gmailSubject, onValueChange = { gmailSubject = it }, label = { Text("Subject") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                            OutlinedTextField(value = gmailBody, onValueChange = { gmailBody = it }, label = { Text("Message Draft") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { viewModel.createGmailVoiceDraft(gmailTo, gmailSubject, gmailBody) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                                ) { Text("Preview Draft", color = Color.White, fontWeight = FontWeight.Bold) }
                                Button(
                                    onClick = { viewModel.launchGmailDraftIntent(gmailTo, gmailSubject, gmailBody) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                                ) { Text("Open Email App", color = Color.Black, fontWeight = FontWeight.Bold) }
                            }
                            if (uiState.gmailDraftPreview.isNotBlank()) {
                                Text(uiState.gmailDraftPreview, color = YellowSubtitle, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            else -> {
                item {
                    GlassHudCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("ALL 24 TOOLS • SOVEREIGN REGISTRY", color = StarGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            SanamCatalog.twentyFourTools.forEach { tool ->
                                Text("• $tool", color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(onClick = { onNavigateTo("permissions_guide") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)) {
                                    Text("8 Permissions", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(onClick = { onNavigateTo("privacy_dashboard") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                                    Text("Privacy & JSON", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(84.dp)) }
    }
}

/**
 * 8-Step Permissions Guide:
 * Clear Hinglish explanation per permission + Allow/Skip + zero broad storage permission (uses Android Photo Picker).
 */
@Composable
fun PermissionsGuideScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val context = LocalContext.current
    var activeStepNum by remember { mutableIntStateOf(1) }

    val runtimePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.markPermissionStepHandled(activeStepNum)
        Toast.makeText(
            context,
            if (granted) "Permission granted cleanly!" else "Skipped — works offline without it",
            Toast.LENGTH_SHORT
        ).show()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.markPermissionStepHandled(5)
        if (uri != null) {
            viewModel.addVaultEncryptedItem("Photos", "Vault Picked Photo", "Local encrypted URI", uri.toString())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("permissions_guide_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SubScreenHeader(
                title = "8-Step Permissions Guide",
                subtitle = "${uiState.completedPermissionSteps.size} / 8 Configured • Zero Fake Data",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        items(SanamCatalog.eightPermissionSteps, key = { it.stepNumber }) { step ->
            val handled = step.stepNumber in uiState.completedPermissionSteps
            GlassHudCard(
                modifier = Modifier.fillMaxWidth().testTag("perm_step_${step.stepNumber}"),
                borderColor = if (handled) SuccessGreen else NeonBlue.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).background(if (handled) SuccessGreen else NeonBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${step.stepNumber}", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(step.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(step.badge, color = if (handled) SuccessGreen else NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(step.reasonHinglish, color = TextGrey, fontSize = 12.sp, lineHeight = 17.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { viewModel.markPermissionStepHandled(step.stepNumber) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            border = BorderStroke(1.dp, Color(0xFF2B2B44))
                        ) {
                            Text("Skip for Now", color = TextGrey, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                activeStepNum = step.stepNumber
                                when (step.permissionKey) {
                                    "PHOTO_PICKER" -> photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    "ACCESSIBILITY" -> {
                                        viewModel.markPermissionStepHandled(step.stepNumber)
                                        viewModel.openAccessibilitySetup()
                                    }
                                    else -> runtimePermissionLauncher.launch(step.permissionKey)
                                }
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (handled) SuccessGreen else NeonBlue)
                        ) {
                            Text(if (handled) "Re-Check" else "Allow", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(84.dp)) }
    }
}

/**
 * Privacy & Backup JSON Dashboard:
 * Shows exact collection boundaries + Export/Import JSON Backup + Clear Memory + Delete All Data.
 */
@Composable
fun PrivacyDashboardScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens = LocalStarThemeTokens.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var importJsonInput by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background)
            .padding(16.dp)
            .testTag("privacy_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SubScreenHeader(
                title = "Privacy & JSON Backup",
                subtitle = "100% Transparent Local Rules • Export & Wipe Control",
                onBack = onNavigateBack,
                onInstallApk = { viewModel.exportApkAction() }
            )
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = SuccessGreen) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("WHAT IS COLLECTED (SOVEREIGN RULES)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("• Contacts: Only read locally when you say a call command.", color = Color.White, fontSize = 12.sp)
                    Text("• Microphone & Camera: Always Listening is OFF by default; Camera OFF by default.", color = Color.White, fontSize = 12.sp)
                    Text("• Zero Auto-Fill: Never stores or auto-fills bank cards/passwords.", color = Color.White, fontSize = 12.sp)
                    Text("• Safety Card: Pay / Send / Delete strictly require explicit Allow tap.", color = Color.White, fontSize = 12.sp)
                    Text("• Local Encryption: API keys, PIN & Vault items encrypted via AES-256-GCM.", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonBlue) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("EXPORT / IMPORT MEMORY BACKUP JSON", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.exportJsonBackup() },
                            modifier = Modifier.weight(1f).height(46.dp).testTag("export_backup_json_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Export JSON", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        if (uiState.lastBackupJsonPreview.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(uiState.lastBackupJsonPreview))
                                    Toast.makeText(context, "Backup JSON copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Text("Copy JSON", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (uiState.lastBackupJsonPreview.isNotBlank()) {
                        Text(
                            text = uiState.lastBackupJsonPreview.take(400),
                            color = YellowSubtitle,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF101220), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        label = { Text("Paste Backup JSON to Restore") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Button(
                        onClick = {
                            viewModel.importJsonBackup(importJsonInput)
                            importJsonInput = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("Import Backup JSON", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            GlassHudCard(modifier = Modifier.fillMaxWidth(), borderColor = CrimsonPrimary) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("MEMORY & DATA WIPE CONTROLS", color = CrimsonPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.clearAllMemoryOnly() },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        border = BorderStroke(1.dp, StarGold)
                    ) {
                        Text("Clear Chat & Room Memory Only", color = StarGold, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("delete_all_data_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Delete All Personal Data & API Secrets", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(84.dp)) }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF17192D),
            title = { Text("Wipe All Local Data?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Isse Room memory, Vault secrets aur saved API keys delete ho jayenge. Kya aap sure hain?",
                    color = TextGrey,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteAllPersonalData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("Delete Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
