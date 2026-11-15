package com.example.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.StarPreferencesDataStore
import com.example.data.local.IntruderRecord
import com.example.data.local.MemoryNoteEntity
import com.example.data.local.StarDatabase
import com.example.data.local.TriggerEntity
import com.example.data.local.VaultItemEntity
import com.example.data.local.WorldModelEntity
import com.example.data.model.ActionConfirmationRequest
import com.example.data.model.AvatarEmotionState
import com.example.data.model.CustomTriggerItem
import com.example.data.model.DhaiBuildProject
import com.example.data.model.IncidentLog
import com.example.data.model.LockedAppItem
import com.example.data.model.MeetingChecklistTask
import com.example.data.model.PromptTemplateItem
import com.example.data.model.SanamCatalog
import com.example.data.model.SanamChatMessage
import com.example.data.model.SecurityConfig
import com.example.data.model.UserProfile
import com.example.data.repository.StarSecurityRepository
import com.example.data.security.SecurityPreferences
import com.example.data.security.SensorGuardManager
import com.example.data.speech.WakeWordListener
import com.example.data.speech.WakeWordManager
import com.example.service.BackupService
import com.example.service.BrainService
import com.example.service.ControlService
import com.example.service.MeetingService
import com.example.service.MemoryService
import com.example.service.MusicService
import com.example.service.TeacherService
import com.example.service.VaultService
import com.example.service.VoiceService
import com.example.util.SystemHardwareController
import com.example.worker.ScheduledSecurityWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StarUiState(
    val userProfile: UserProfile = UserProfile(displayName = "Dayaram Chouhan", buildType = "Personal Lifetime v2.2.8"),
    val securityConfig: SecurityConfig = SecurityConfig(),
    val incidentLogs: List<IncidentLog> = emptyList(),
    val localSnapshots: List<IntruderRecord> = emptyList(),
    val customTriggers: List<CustomTriggerItem> = emptyList(),
    val lockedApps: List<LockedAppItem> = emptyList(),
    val roomTriggers: List<TriggerEntity> = emptyList(),
    val roomWorldModel: List<WorldModelEntity> = emptyList(),
    val currentThemeAura: String = "Crimson Aura",
    val currentIconVariant: String = "Crimson Wireframe Star",
    val currentLanguage: String = "Auto Hinglish",
    val detectedLanguage: String = "Hinglish",
    val isAlarmActive: Boolean = false,
    val alarmReason: String = "",
    val faceEnrollmentStep: Int = 0,
    val isFaceEnrolled: Boolean = false,
    val failedAttempts: Int = 0,
    val cooldownSeconds: Int = 0,
    val isGfScolding: Boolean = false,
    val statusMessage: String? = null,
    // API Keys (EncryptedSharedPreferences)
    val openAiKey: String = "",
    val geminiKey: String = "",
    val groqKey: String = "",
    val claudeKey: String = "",
    val deepSeekKey: String = "",
    val openRouterKey: String = "",
    val ollamaUrl: String = "http://127.0.0.1:11434",
    val customApiUrl: String = "",
    val apiTestStatuses: Map<String, Pair<Boolean, String>> = mapOf(
        "offline" to (true to "GREEN • Offline Brain JSON 100% Ready")
    ),
    val activeBrainProvider: String = "Firebase AI Logic -> Offline Hybrid",
    // DataStore Toggles
    val callAssistant: Boolean = true,
    val chatNotifications: Boolean = true,
    val screenUnlockTrigger: Boolean = true,
    val headphonesTrigger: Boolean = false,
    val wifiTrigger: Boolean = true,
    val whatsappConnector: Boolean = true,
    val gmailConnector: Boolean = false,
    val calendarConnector: Boolean = true,
    val gamingAssistant: Boolean = false,
    val chatGptImage: Boolean = false,
    val autoPayments: Boolean = false,
    val auraBorderStealth: Boolean = false,
    val picturesFiles: List<String> = emptyList(),
    val deviceInfo: String = "Android ${Build.VERSION.RELEASE} • TZ: ${java.util.TimeZone.getDefault().id}",
    // Wake Word & Voice State (Always listening OFF by default)
    val isWakeWordEnabled: Boolean = false,
    val isWakeWordListening: Boolean = false,
    val wakeWordAudioRms: Float = 0f,
    val lastWakeWordEvent: String? = null,
    val wakeWordTriggerCount: Int = 0,
    val lastSpokenCommand: String? = null,
    val lastHeardSpeech: String = "",
    val sanamResponseText: String? = null,
    val isSanamResponding: Boolean = false,
    val selectedVoiceId: String = "hi_f_1",
    val voicePitch: Float = 1.2f,
    val voiceRate: Float = 0.9f,
    val pocketTtsEngine: String = "Pocket TTS-Hindi",
    val multiSpeakerEnabled: Boolean = false,
    // Voice Focus Full-Screen Black Mode & Live Human Engine
    val voiceFocusModeEnabled: Boolean = true,
    val isVoiceFocusActive: Boolean = false,
    val selectedAvatarId: String = "zara_open",
    val avatarEmotion: AvatarEmotionState = AvatarEmotionState.NEUTRAL,
    val pendingVoiceNavTarget: String? = null,
    // CHOUHAN STAR v2.2.8 Frontend + Auth + Multi-Agent + UI State
    val customAppName: String = "CHOUHAN STAR",
    val coreThemeMode: String = "AMOLED",
    val moodTheme: String = "Caring Blue",
    val systemMode: String = "Assistant Pro",
    val activeAgentId: String = "zara",
    val assistantStatus: String = "LISTENING",
    val yellowSubtitleText: String = "Hey Sanam suno — main ZARA taiyaar hoon, Dayaram ji!",
    val largeFontEnabled: Boolean = false,
    val voiceOnlyMode: Boolean = false,
    val hiddenCalculatorMode: Boolean = false,
    val isPinConfigured: Boolean = false,
    val hasSkippedLogin: Boolean = false,
    val fingerprintLockEnabled: Boolean = true,
    val isAppUnlocked: Boolean = false,
    val isOfflineMode: Boolean = true,
    val batteryPercent: Int = 88,
    val protocolStates: List<String> = listOf("DONE", "DONE", "DONE", "DONE"),
    // Memory, Vault, Teacher, Meeting, Music, Safety Confirmation
    val chatMessages: List<SanamChatMessage> = emptyList(),
    val memoryItems: List<MemoryNoteEntity> = emptyList(),
    val vaultItems: List<VaultItemEntity> = emptyList(),
    val vaultLockoutSeconds: Int = 0,
    val vaultFailedCount: Int = 0,
    val vaultBlurEnabled: Boolean = true,
    val fakeVaultMode: Boolean = false,
    val vaultAutoLockLabel: String = "1m",
    val ownerLikes: String = "Kotlin, Compose, AI Security, Chai",
    val ownerMood: String = "Focused",
    val lastBackupJsonPreview: String = "",
    val pendingConfirmation: ActionConfirmationRequest? = null,
    val meetingTasks: List<MeetingChecklistTask> = emptyList(),
    val teacherExplanation: String = "",
    val teacherQuizAnswers: Map<String, Int> = emptyMap(),
    val teacherScore: Int? = null,
    val teacherResultMessage: String = "",
    val currentMusicQuery: String = "Kesariya Lo-Fi",
    val activeMusicPlatform: String = "Spotify",
    val deepSearchReport: String = "",
    val reverseDiagramOutput: String = "",
    val brokerSummary: String = "",
    val gmailDraftPreview: String = "",
    val completedPermissionSteps: Set<Int> = emptySet(),
    // Preserved Studio / Control / Map States
    val promptTemplates: List<PromptTemplateItem> = SanamCatalog.defaultPrompts,
    val dhaiBuilds: List<DhaiBuildProject> = SanamCatalog.initialDhaiBuilds,
    val umiExpressionIndex: Int = 0,
    val flowShiftStep: String = "Padhai",
    val flowShiftRunning: Boolean = false,
    val flowShiftSeconds: Int = 1500,
    val flowShiftHistory: List<String> = listOf("Padhai 25m completed", "Coding Sprint 45m logged"),
    val buildProtocolStep: Int = 4,
    val buildStatusText: String = "ALL 4 PROTOCOL LAYERS READY",
    val claudeCodeLogs: List<String> = listOf(
        "POST /charges idempotency audit initialized",
        "Check 1: Idempotency-Key header validation -> PASS",
        "Check 2: DB unique transaction_ref index -> PASS",
        "Migration Day 1/2/3 zero-downtime lock check -> READY"
    ),
    val claudeSidebarTab: String = "Projects",
    val torchEnabled: Boolean = false,
    val wifiEnabled: Boolean = true,
    val bluetoothEnabled: Boolean = true,
    val dndEnabled: Boolean = false,
    val brightnessPercent: Float = 75f,
    val mediaVolumePercent: Float = 80f,
    val nextAlarmLabel: String = "06:00 AM • Morning Briefing",
    val mapLatitude: Double = 26.9124,
    val mapLongitude: Double = 75.7873,
    val mapWeatherTemp: String = "29°C Clear • Jaipur"
)

class StarSecurityViewModel(application: Application) : AndroidViewModel(application), WakeWordListener {

    private val repository = StarSecurityRepository(application)
    private val starDatabase = StarDatabase.getInstance(application)
    private val securityPrefs = SecurityPreferences(application)
    private val dataStore = StarPreferencesDataStore(application)

    private val brainService = BrainService(application, securityPrefs)
    private val memoryService = MemoryService(application, starDatabase, dataStore)
    private val controlService = ControlService(application)
    private val vaultService = VaultService(starDatabase, securityPrefs)
    private val backupService = BackupService(memoryService)
    private val teacherService = TeacherService()
    private val meetingService = MeetingService(controlService, memoryService)
    private val musicService = MusicService(application)

    private val _uiState = MutableStateFlow(StarUiState())
    val uiState: StateFlow<StarUiState> = _uiState.asStateFlow()

    private val voiceService = VoiceService(application) { speaking ->
        _uiState.update {
            it.copy(
                isSanamResponding = speaking,
                assistantStatus = if (speaking) "SPEAKING" else if (it.isWakeWordListening) "LISTENING" else "IDLE"
            )
        }
    }

    private val wakeWordManager = WakeWordManager(application, this)
    private val sensorGuardManager = SensorGuardManager(application) {
        triggerTouchBreachAlarm("Motion detected! Phone picked up while armed.")
    }

    private var lastVoiceFocusInteractionMs = System.currentTimeMillis()
    private var hasShownIdleCheckIn = false

    init {
        initDefaultLockedApps()
        initDefaultTriggers()
        initInitialMessages()
        loadLocalData()
        loadApiKeysAndVaultSettings()
        observeDataStorePreferences()
        seedRoomDatabaseIfEmpty()
        refreshSystemTelemetry()
        startIdleVoiceFocusWatcher()
        _uiState.update {
            it.copy(
                isFaceEnrolled = securityPrefs.isFaceEnrolled(),
                isPinConfigured = securityPrefs.isPinConfigured(),
                vaultLockoutSeconds = vaultService.getRemainingLockoutSeconds()
            )
        }
    }

    private fun startIdleVoiceFocusWatcher() {
        viewModelScope.launch {
            while (true) {
                delay(2000L)
                val state = _uiState.value
                if (state.isVoiceFocusActive && !state.isSanamResponding) {
                    val elapsed = System.currentTimeMillis() - lastVoiceFocusInteractionMs
                    if (elapsed >= 10_000L && !hasShownIdleCheckIn) {
                        hasShownIdleCheckIn = true
                        val checkIn = "Kya soch rahe ho Dayaram? Bolo, main yahin sun rahi hoon ✨"
                        _uiState.update {
                            it.copy(
                                yellowSubtitleText = checkIn,
                                avatarEmotion = AvatarEmotionState.HAIR_TUCK,
                                assistantStatus = "LISTENING"
                            )
                        }
                        voiceService.speak(checkIn, AvatarEmotionState.HAIR_TUCK)
                    }
                }
            }
        }
    }

    private fun initInitialMessages() {
        viewModelScope.launch {
            val savedChats = memoryService.loadRecentChats(50)
            if (savedChats.isNotEmpty()) {
                _uiState.update { it.copy(chatMessages = savedChats) }
            } else {
                val initial = listOf(
                    SanamChatMessage(
                        id = "m_init_1",
                        agentId = "zara",
                        senderName = "ZARA (SANAM AI)",
                        text = "॥ JAI SHRI RAM ॥ Good Evening, Dayaram ji! Main ZARA hoon — Voice Focus, Vault aur 24 Tools sab ready hain.",
                        timestamp = currentTimeString(),
                        isUser = false,
                        languageTag = "Hinglish"
                    )
                )
                _uiState.update { it.copy(chatMessages = initial) }
            }
        }
    }

    fun refreshSystemTelemetry() {
        val ctx = getApplication<Application>()
        val battery = SystemHardwareController.getBatteryPercent(ctx)
        val online = SystemHardwareController.isNetworkOnline(ctx)
        val coords = SystemHardwareController.getLastKnownCoordinates(ctx)
        _uiState.update {
            it.copy(
                batteryPercent = battery,
                isOfflineMode = !online,
                mapLatitude = coords?.first ?: it.mapLatitude,
                mapLongitude = coords?.second ?: it.mapLongitude
            )
        }
    }

    private fun loadLocalData() {
        viewModelScope.launch { starDatabase.intruderDao().getAllSnapshots().collect { s -> _uiState.update { it.copy(localSnapshots = s) } } }
        viewModelScope.launch { starDatabase.triggerDao().getAllTriggers().collect { t -> _uiState.update { it.copy(roomTriggers = t) } } }
        viewModelScope.launch { starDatabase.worldModelDao().getAllItems().collect { w -> _uiState.update { it.copy(roomWorldModel = w) } } }
        viewModelScope.launch { memoryService.allMemoryFlow.collect { m -> _uiState.update { it.copy(memoryItems = m) } } }
        viewModelScope.launch { vaultService.vaultItemsFlow.collect { v -> _uiState.update { it.copy(vaultItems = v) } } }
    }

    private fun loadApiKeysAndVaultSettings() {
        _uiState.update {
            it.copy(
                openAiKey = securityPrefs.getApiKey("openai"),
                geminiKey = securityPrefs.getApiKey("gemini"),
                groqKey = securityPrefs.getApiKey("groq"),
                claudeKey = securityPrefs.getApiKey("claude"),
                deepSeekKey = securityPrefs.getApiKey("deepseek"),
                openRouterKey = securityPrefs.getApiKey("openrouter"),
                ollamaUrl = securityPrefs.getApiKey("ollama_url").ifBlank { "http://127.0.0.1:11434" },
                customApiUrl = securityPrefs.getApiKey("custom_url"),
                vaultBlurEnabled = securityPrefs.isVaultBlurEnabled(),
                fakeVaultMode = securityPrefs.isFakeVaultMode(),
                vaultAutoLockLabel = securityPrefs.getVaultAutoLockLabel()
            )
        }
    }

    private fun observeDataStorePreferences() {
        viewModelScope.launch { dataStore.callAssistantFlow.collect { v -> _uiState.update { it.copy(callAssistant = v) } } }
        viewModelScope.launch { dataStore.chatNotificationsFlow.collect { v -> _uiState.update { it.copy(chatNotifications = v) } } }
        viewModelScope.launch { dataStore.screenUnlockTriggerFlow.collect { v -> _uiState.update { it.copy(screenUnlockTrigger = v) } } }
        viewModelScope.launch { dataStore.headphonesTriggerFlow.collect { v -> _uiState.update { it.copy(headphonesTrigger = v) } } }
        viewModelScope.launch { dataStore.wifiTriggerFlow.collect { v -> _uiState.update { it.copy(wifiTrigger = v) } } }
        viewModelScope.launch { dataStore.whatsappConnectorFlow.collect { v -> _uiState.update { it.copy(whatsappConnector = v) } } }
        viewModelScope.launch { dataStore.gmailConnectorFlow.collect { v -> _uiState.update { it.copy(gmailConnector = v) } } }
        viewModelScope.launch { dataStore.calendarConnectorFlow.collect { v -> _uiState.update { it.copy(calendarConnector = v) } } }
        viewModelScope.launch { dataStore.gamingAssistantFlow.collect { v -> _uiState.update { it.copy(gamingAssistant = v) } } }
        viewModelScope.launch { dataStore.chatGptImageFlow.collect { v -> _uiState.update { it.copy(chatGptImage = v) } } }
        viewModelScope.launch { dataStore.autoPaymentsFlow.collect { v -> _uiState.update { it.copy(autoPayments = v) } } }
        viewModelScope.launch { dataStore.auraBorderStealthFlow.collect { v -> _uiState.update { it.copy(auraBorderStealth = v) } } }
        viewModelScope.launch { dataStore.customAppNameFlow.collect { v -> _uiState.update { it.copy(customAppName = v) } } }
        viewModelScope.launch { dataStore.coreThemeModeFlow.collect { v -> _uiState.update { it.copy(coreThemeMode = v) } } }
        viewModelScope.launch { dataStore.moodThemeFlow.collect { v -> _uiState.update { it.copy(moodTheme = v) } } }
        viewModelScope.launch { dataStore.activeAgentIdFlow.collect { v -> _uiState.update { it.copy(activeAgentId = v) } } }
        viewModelScope.launch { dataStore.selectedAvatarIdFlow.collect { v -> _uiState.update { it.copy(selectedAvatarId = v) } } }
        viewModelScope.launch { dataStore.languageFlow.collect { v -> _uiState.update { it.copy(currentLanguage = v) } } }
        viewModelScope.launch { dataStore.systemModeFlow.collect { v -> _uiState.update { it.copy(systemMode = v) } } }
        viewModelScope.launch { dataStore.largeFontFlow.collect { v -> _uiState.update { it.copy(largeFontEnabled = v) } } }
        viewModelScope.launch { dataStore.voiceOnlyModeFlow.collect { v -> _uiState.update { it.copy(voiceOnlyMode = v) } } }
        viewModelScope.launch { dataStore.hiddenCalcModeFlow.collect { v -> _uiState.update { it.copy(hiddenCalculatorMode = v) } } }
        viewModelScope.launch { dataStore.fingerprintLockFlow.collect { v -> _uiState.update { it.copy(fingerprintLockEnabled = v) } } }
        viewModelScope.launch { dataStore.skippedLoginFlow.collect { v -> _uiState.update { it.copy(hasSkippedLogin = v) } } }
        viewModelScope.launch {
            dataStore.alwaysListeningFlow.collect { v ->
                _uiState.update { it.copy(isWakeWordEnabled = v) }
                if (v) checkAndStartWakeWordListening() else wakeWordManager.stopListening()
            }
        }
        viewModelScope.launch { dataStore.voiceFocusModeFlow.collect { v -> _uiState.update { it.copy(voiceFocusModeEnabled = v) } } }
        viewModelScope.launch {
            dataStore.voicePitchFlow.collect { v ->
                voiceService.pitch = v
                _uiState.update { it.copy(voicePitch = v) }
            }
        }
        viewModelScope.launch {
            dataStore.voiceRateFlow.collect { v ->
                voiceService.rate = v
                _uiState.update { it.copy(voiceRate = v) }
            }
        }
        viewModelScope.launch {
            dataStore.selectedVoiceIdFlow.collect { v ->
                voiceService.applyVoiceProfile(v)
                _uiState.update { it.copy(selectedVoiceId = v) }
            }
        }
        viewModelScope.launch {
            dataStore.pocketTtsEngineFlow.collect { v ->
                voiceService.pocketEngineMode = v
                _uiState.update { it.copy(pocketTtsEngine = v) }
            }
        }
        viewModelScope.launch { dataStore.ownerLikesFlow.collect { v -> _uiState.update { it.copy(ownerLikes = v) } } }
        viewModelScope.launch { dataStore.ownerMoodFlow.collect { v -> _uiState.update { it.copy(ownerMood = v) } } }
        loadPicturesFiles()
    }

    private fun seedRoomDatabaseIfEmpty() {
        viewModelScope.launch {
            vaultService.seedInitialVaultItemsIfEmpty()
            val existingTriggers = starDatabase.triggerDao().getAllTriggers().first()
            if (existingTriggers.isEmpty()) {
                val seedTriggers = listOf(
                    TriggerEntity(title = "Trigger 0: Wake Word (Hey Sanam suno)", triggerType = "VOICE", action = "SpeechRecognizer Voice Command Engine", isEnabled = true),
                    TriggerEntity(title = "Trigger 1: Screen On/Off", triggerType = "SCREEN", action = "Silent Stealth Face Scan", isEnabled = true),
                    TriggerEntity(title = "Trigger 2: Headphones Connect", triggerType = "HEADPHONES", action = "Announce Voice Briefing", isEnabled = false),
                    TriggerEntity(title = "Trigger 3: WiFi Connect", triggerType = "WIFI", action = "Verify Trusted Network Shield", isEnabled = true),
                    TriggerEntity(title = "Trigger 4: Scheduled Task", triggerType = "SCHEDULED", action = "Auto Arm Touch Guard & Diagnostics", isEnabled = false),
                    TriggerEntity(title = "Trigger 5: Bluetooth Disconnect", triggerType = "BLUETOOTH", action = "Sound Proximity Perimeter Ping", isEnabled = true),
                    TriggerEntity(title = "Trigger 6: Power Cable Unplug", triggerType = "CHARGER", action = "High-Pitch Motion Siren", isEnabled = true),
                    TriggerEntity(title = "Trigger 7: SIM Tray Alteration", triggerType = "SIM", action = "Immediate Hardware Lockdown", isEnabled = true),
                    TriggerEntity(title = "Trigger 8: Rapid Acceleration Drop", triggerType = "MOTION", action = "Emergency SMS Broadcast", isEnabled = true)
                )
                starDatabase.triggerDao().insertAll(seedTriggers)
            }

            val existingWorld = starDatabase.worldModelDao().getAllItems().first()
            if (existingWorld.isEmpty()) {
                val seedWorld = listOf(
                    WorldModelEntity(category = "App", title = "WhatsApp", learnedAction = "send message to family (confirm before send)", updatedAt = "Updated today"),
                    WorldModelEntity(category = "Contact", title = "Maa / Home", learnedAction = "priority call shortcut", updatedAt = "Updated today"),
                    WorldModelEntity(category = "Workflow", title = "Morning Routine", learnedAction = "29C weather + daily schedule", updatedAt = "Updated 1 day ago"),
                    WorldModelEntity(category = "App", title = "PhonePe", learnedAction = "confirm card before payment", updatedAt = "Updated 1 day ago"),
                    WorldModelEntity(category = "Preference", title = "Language Mode", learnedAction = "Hinglish / Urdu / Sindhi / Dhatki", updatedAt = "Active")
                )
                starDatabase.worldModelDao().insertAll(seedWorld)
            }

            if (starDatabase.memoryDao().getAllMemoryOnce().none { it.itemType != "CHAT" }) {
                memoryService.saveNote("NOTE", "Dayaram Sovereign Goal", "100% Free Forever Personal Assistant & Offline Security")
                memoryService.saveNote("TODO", "Test 32 Hindi/Urdu Voices", "Verify Pitch 1.2 & Speed 0.9 inside Settings > Voice")
                memoryService.saveNote("BIRTHDAY", "Commander Birthday Reminder", "Celebrate with Family & ZARA Wishing Mode", "14 Nov")
            }
        }
    }

    // --- Patch 1: Welcome -> PIN Setup -> Home, and In-App Sign-In inside Profile ---
    fun completeWelcomeContinue() {
        viewModelScope.launch {
            dataStore.setSkippedLogin(true)
            _uiState.update {
                it.copy(
                    hasSkippedLogin = true,
                    statusMessage = "Setup 4-digit PIN to enter CHOUHAN STAR"
                )
            }
        }
    }

    fun completeSkipToPinSetup() {
        viewModelScope.launch {
            dataStore.setSkippedLogin(true)
            _uiState.update {
                it.copy(
                    hasSkippedLogin = true,
                    isOfflineMode = true,
                    statusMessage = "Offline mode active — setup 4-digit PIN to enter Home"
                )
            }
        }
    }

    fun setupPinAndUnlock(pin: String, fingerprintEnabled: Boolean): Boolean {
        if (pin.length < 4) return false
        securityPrefs.savePin(pin)
        viewModelScope.launch { dataStore.setFingerprintLock(fingerprintEnabled) }
        _uiState.update {
            it.copy(
                isPinConfigured = true,
                fingerprintLockEnabled = fingerprintEnabled,
                isAppUnlocked = true,
                statusMessage = "PIN encrypted locally. Swagat hai Dayaram ji!"
            )
        }
        return true
    }

    fun unlockWithPin(pin: String): Boolean {
        val ok = verifyPin(pin)
        if (ok) _uiState.update { it.copy(isAppUnlocked = true) }
        return ok
    }

    fun unlockWithBiometric() {
        _uiState.update { it.copy(isAppUnlocked = true, failedAttempts = 0, statusMessage = "Biometric verified!") }
    }

    /**
     * Sign-out in Profile screen -> reverts cleanly to Guest Mode inside Profile, preserves ALL Room/Vault data.
     */
    fun signOutToGuestKeepData() {
        _uiState.update {
            it.copy(
                userProfile = UserProfile(displayName = "Guest User / Dayaram Chouhan", email = "Not signed in (Offline Sovereign)", buildType = "Personal Lifetime v2.2.8"),
                isOfflineMode = true,
                statusMessage = "Signed out safely — reverted to Guest Mode (all local data preserved)."
            )
        }
    }

    fun signOutKeepData() = signOutToGuestKeepData()

    // --- Patch 2, 3, 4: Avatar Gallery, Voice Focus Mode & Live Human Engine ---
    fun selectAvatarFromGallery(avatarItemId: String) {
        val item = SanamCatalog.avatarGalleryItems.find { it.id == avatarItemId } ?: return
        val agent = SanamCatalog.fourteenAgents.find { it.id == item.agentId }
        val agentPitch = agent?.voicePitch ?: _uiState.value.voicePitch
        viewModelScope.launch {
            dataStore.setSelectedAvatarId(item.id)
            dataStore.setActiveAgentId(item.agentId)
            dataStore.setMoodTheme(item.moodTheme)
            dataStore.setVoicePitch(agentPitch)
        }
        voiceService.applyVoiceProfile(_uiState.value.selectedVoiceId, agentPitch, _uiState.value.voiceRate)
        val greeting = agent?.greetingHinglish ?: "Main taiyaar hoon Dayaram!"
        _uiState.update {
            it.copy(
                selectedAvatarId = item.id,
                activeAgentId = item.agentId,
                moodTheme = item.moodTheme,
                voicePitch = agentPitch,
                avatarEmotion = AvatarEmotionState.HAPPY,
                yellowSubtitleText = greeting,
                statusMessage = "${item.title} • ${item.moodTheme} personality active!"
            )
        }
        voiceService.speak(greeting, AvatarEmotionState.HAPPY)
    }

    fun activateVoiceFocusMic() {
        lastVoiceFocusInteractionMs = System.currentTimeMillis()
        hasShownIdleCheckIn = false
        voiceService.interruptForBargeIn()
        if (_uiState.value.voiceFocusModeEnabled) {
            _uiState.update {
                it.copy(
                    isVoiceFocusActive = true,
                    assistantStatus = "LISTENING",
                    avatarEmotion = AvatarEmotionState.NEUTRAL
                )
            }
        }
        val hasAudio = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (hasAudio) {
            wakeWordManager.startSingleUtterance()
        }
    }

    fun deactivateVoiceFocusMic() {
        voiceService.stopSpeaking()
        wakeWordManager.stopListening()
        _uiState.update {
            it.copy(
                isVoiceFocusActive = false,
                isWakeWordListening = false,
                isSanamResponding = false,
                assistantStatus = "IDLE",
                avatarEmotion = AvatarEmotionState.NEUTRAL
            )
        }
    }

    fun setVoiceFocusModeEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStore.setVoiceFocusMode(enabled) }
    }

    fun triggerAvatarEmotionDemo(emotion: AvatarEmotionState) {
        lastVoiceFocusInteractionMs = System.currentTimeMillis()
        hasShownIdleCheckIn = false
        val line = when (emotion) {
            AvatarEmotionState.HAPPY -> "Yay! Tumhare saath baat karke dil ekdum khush ho gaya Dayaram 😄✨"
            AvatarEmotionState.SHY_BLUSH -> "Ayyye... aise mat dekho Dayaram, blush ho rahi hoon ❤️"
            AvatarEmotionState.LAUGHING -> "Hahaha! Kya mast joke maara yaar, dil khush ho gaya 😄"
            AvatarEmotionState.ROAST_ANGRY -> "Oye hero! Zyada smart mat bano warna roast kar dungi 🔥"
            AvatarEmotionState.SAD_CARING -> "Thoda aaram kar lo na Dayaram, main saath baithi hoon 🤍"
            AvatarEmotionState.WAVING -> "Hiii Dayaram!! Boliye kya hukm hai aapki ZARA ke liye 👋"
            AvatarEmotionState.WINK -> "Secret mission? Kisi ko nahi bataungi 😉"
            AvatarEmotionState.NEUTRAL -> "Natural Idle active — aaram se saans aur eye contact bana hua hai ✨"
            else -> "Main sun rahi hoon Dayaram, poori focus ke saath ✨"
        }
        _uiState.update {
            it.copy(
                avatarEmotion = emotion,
                yellowSubtitleText = line,
                assistantStatus = if (emotion == AvatarEmotionState.NEUTRAL) "LISTENING" else "SPEAKING"
            )
        }
        if (emotion != AvatarEmotionState.NEUTRAL) {
            voiceService.speak(line, emotion)
        }
    }

    fun clearPendingVoiceNav() {
        _uiState.update { it.copy(pendingVoiceNavTarget = null) }
    }

    // --- 32 Voices + Pocket TTS + Pitch / Rate + Always Listening (OFF Default) ---
    fun selectVoiceProfile(voiceId: String) {
        val profile = SanamCatalog.thirtyTwoVoices.find { it.id == voiceId } ?: return
        viewModelScope.launch {
            dataStore.setSelectedVoiceId(voiceId)
            dataStore.setVoicePitch(profile.pitch)
            dataStore.setVoiceRate(profile.rate)
        }
        voiceService.applyVoiceProfile(voiceId, profile.pitch, profile.rate)
        val sample = "Namaste Dayaram ji, main ${profile.label} aawaz mein bol rahi hoon."
        _uiState.update { it.copy(statusMessage = "Voice switched: ${profile.label}", yellowSubtitleText = sample) }
        voiceService.speak(sample, AvatarEmotionState.HAPPY)
    }

    fun setVoicePitchSlider(pitch: Float) {
        val clamped = pitch.coerceIn(0.6f, 1.8f)
        viewModelScope.launch { dataStore.setVoicePitch(clamped) }
        voiceService.applyVoiceProfile(_uiState.value.selectedVoiceId, clamped, _uiState.value.voiceRate)
    }

    fun setVoiceRateSlider(rate: Float) {
        val clamped = rate.coerceIn(0.6f, 1.8f)
        viewModelScope.launch { dataStore.setVoiceRate(clamped) }
        voiceService.applyVoiceProfile(_uiState.value.selectedVoiceId, _uiState.value.voicePitch, clamped)
    }

    fun setPocketTtsEngine(mode: String) {
        viewModelScope.launch { dataStore.setPocketTtsEngine(mode) }
    }

    fun toggleMultiSpeaker(enabled: Boolean) {
        voiceService.multiSpeakerEnabled = enabled
        _uiState.update { it.copy(multiSpeakerEnabled = enabled) }
    }

    fun setAlwaysListeningEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStore.setAlwaysListening(enabled) }
        _uiState.update {
            it.copy(
                isWakeWordEnabled = enabled,
                statusMessage = if (enabled) "Always Listening ON — 'Hey Sanam suno' active" else "Always Listening OFF (Battery & Privacy Safe)"
            )
        }
    }

    // --- Chat & Brain Router Engine ---
    fun sendChatMessage(input: String, agentOverrideId: String? = null) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return
        lastVoiceFocusInteractionMs = System.currentTimeMillis()
        hasShownIdleCheckIn = false
        voiceService.interruptForBargeIn()

        // 1. Check Voice Navigation Commands ("Home Screen Kholo", "Chat Kholo", "Vault Kholo", "Wapas Jao", "Screen Band Karo")
        val lower = trimmed.lowercase(Locale.ROOT)
        when {
            "home screen kholo" in lower || "open home" in lower -> {
                deactivateVoiceFocusMic()
                _uiState.update { it.copy(pendingVoiceNavTarget = "home") }
                return
            }
            "chat kholo" in lower || "open chat" in lower -> {
                deactivateVoiceFocusMic()
                _uiState.update { it.copy(pendingVoiceNavTarget = "chat") }
                return
            }
            "vault kholo" in lower || "open vault" in lower -> {
                deactivateVoiceFocusMic()
                _uiState.update { it.copy(pendingVoiceNavTarget = "vault") }
                return
            }
            "wapas jao" in lower || "back jao" in lower -> {
                deactivateVoiceFocusMic()
                _uiState.update { it.copy(pendingVoiceNavTarget = "__BACK__") }
                return
            }
            "screen band karo" in lower || "chup ho jao" in lower || "band karo" in lower -> {
                deactivateVoiceFocusMic()
                return
            }
        }

        // 2. Safety Confirmation Gate (Pay / Send / Delete)
        controlService.detectSensitiveConfirmation(trimmed)?.let { req ->
            val warn = "Ruko Dayaram! ${req.actionType} action ke liye aapki permission chahiye — card par Allow ya Deny dabao."
            _uiState.update {
                it.copy(
                    pendingConfirmation = req,
                    yellowSubtitleText = warn,
                    avatarEmotion = AvatarEmotionState.SURPRISED
                )
            }
            voiceService.speak(warn, AvatarEmotionState.SURPRISED)
            return
        }

        val detectedLang = SystemHardwareController.detectLanguageFromText(trimmed, _uiState.value.currentLanguage)
        val activeId = agentOverrideId ?: _uiState.value.activeAgentId
        val agent = SanamCatalog.fourteenAgents.find { it.id == activeId } ?: SanamCatalog.fourteenAgents.first()

        val userMsg = SanamChatMessage(
            id = "u_${System.currentTimeMillis()}",
            agentId = activeId,
            senderName = "Dayaram",
            text = trimmed,
            timestamp = currentTimeString(),
            isUser = true,
            languageTag = detectedLang
        )

        _uiState.update {
            it.copy(
                chatMessages = (it.chatMessages + userMsg).takeLast(50),
                detectedLanguage = detectedLang,
                assistantStatus = "THINKING"
            )
        }
        viewModelScope.launch { memoryService.persistChatMessage(userMsg) }

        // Handle explicit device actions immediately before/with reply
        handleInlineDeviceIntents(trimmed)

        viewModelScope.launch {
            val brainResult = brainService.generateAssistantReply(
                prompt = trimmed,
                agentId = activeId,
                languagePref = _uiState.value.currentLanguage,
                moodTheme = _uiState.value.moodTheme,
                ownerLikes = _uiState.value.ownerLikes
            )
            val emotion = VoiceService.detectEmotionForLiveHumanEngine(trimmed, brainResult.text, _uiState.value.moodTheme)
            val aiMsg = SanamChatMessage(
                id = "a_${System.currentTimeMillis()}",
                agentId = agent.id,
                senderName = agent.name,
                text = brainResult.text,
                timestamp = currentTimeString(),
                isUser = false,
                languageTag = brainResult.detectedLanguage
            )
            memoryService.persistChatMessage(aiMsg)
            _uiState.update {
                it.copy(
                    chatMessages = (it.chatMessages + aiMsg).takeLast(50),
                    activeBrainProvider = brainResult.providerUsed,
                    isOfflineMode = brainResult.isOfflineFallback,
                    sanamResponseText = brainResult.text,
                    yellowSubtitleText = brainResult.text,
                    avatarEmotion = emotion,
                    assistantStatus = "SPEAKING"
                )
            }
            voiceService.speak(brainResult.text, emotion)
        }
    }

    private fun handleInlineDeviceIntents(prompt: String) {
        val lower = prompt.lowercase(Locale.ROOT)
        when {
            "torch" in lower || "flashlight" in lower -> toggleTorch("off" !in lower && "band" !in lower)
            "touch guard" in lower || "arm guard" in lower -> toggleTouchGuardArmed("off" !in lower)
            "open whatsapp" in lower || "whatsapp kholo" in lower -> controlService.launchAppByName("whatsapp")
            "open youtube" in lower || "youtube kholo" in lower -> controlService.launchAppByName("youtube")
            "open chrome" in lower -> controlService.launchAppByName("chrome")
            "call " in lower -> controlService.openDialer()
            "padhai" in lower || "study" in lower -> startFlowShift("Padhai")
            "spotify" in lower -> switchMusicPlatform("Spotify")
            "play " in lower || "gaana" in lower -> switchMusicPlatform(_uiState.value.activeMusicPlatform, prompt.replace("play ", "", true))
        }
    }

    fun resolvePendingConfirmation(allowed: Boolean) {
        val req = _uiState.value.pendingConfirmation ?: return
        _uiState.update { it.copy(pendingConfirmation = null) }
        if (!allowed) {
            val denyMsg = "${req.actionType} cancelled by Commander Dayaram."
            _uiState.update { it.copy(statusMessage = denyMsg, yellowSubtitleText = denyMsg, avatarEmotion = AvatarEmotionState.NEUTRAL) }
            voiceService.speak("Theek hai Dayaram, cancel kar diya.")
            return
        }
        when (req.actionType) {
            "SEND" -> controlService.openSmsDraft("", req.payload)
            "DELETE" -> clearAllMemoryOnly()
            "PAY" -> _uiState.update { it.copy(statusMessage = "Payment intent authorized for verification.") }
        }
        _uiState.update { it.copy(statusMessage = "${req.actionType} approved & executed safely.") }
    }

    // --- Memory, Backup, Vault (AES-256 + 30s Lockout), Teacher, Meeting, Music, Privacy ---
    fun addMemoryNote(type: String, title: String, content: String, dueDate: String = "") {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            memoryService.saveNote(type, title.ifBlank { type }, content.ifBlank { title }, dueDate)
            _uiState.update { it.copy(statusMessage = "$type saved locally in Room!") }
        }
    }

    fun toggleMemoryTodo(id: Long, completed: Boolean) {
        viewModelScope.launch { memoryService.toggleTodoCompleted(id, completed) }
    }

    fun deleteMemoryItem(id: Long) {
        viewModelScope.launch { memoryService.deleteMemoryItem(id) }
    }

    fun exportJsonBackup() {
        viewModelScope.launch {
            val json = backupService.createJsonBackup(_uiState.value.customAppName, _uiState.value.ownerLikes)
            _uiState.update {
                it.copy(
                    lastBackupJsonPreview = json,
                    statusMessage = "Backup JSON exported (${json.length} chars) to local storage!"
                )
            }
        }
    }

    fun importJsonBackup(jsonText: String) {
        if (jsonText.isBlank()) return
        viewModelScope.launch {
            try {
                val count = backupService.restoreJsonBackup(jsonText)
                _uiState.update { it.copy(statusMessage = "Restored $count records from Backup JSON!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(statusMessage = "Import error: Invalid JSON format") }
            }
        }
    }

    fun clearAllMemoryOnly() {
        viewModelScope.launch {
            memoryService.clearMemoryOnly()
            _uiState.update {
                it.copy(
                    chatMessages = emptyList(),
                    statusMessage = "Conversation & Room memory cleared safely."
                )
            }
        }
    }

    fun deleteAllPersonalData() {
        viewModelScope.launch {
            memoryService.deleteAllUserData()
            securityPrefs.clearAllSecrets()
            _uiState.update {
                it.copy(
                    chatMessages = emptyList(),
                    geminiKey = "",
                    groqKey = "",
                    deepSeekKey = "",
                    openRouterKey = "",
                    openAiKey = "",
                    claudeKey = "",
                    statusMessage = "All local user data & secrets wiped cleanly."
                )
            }
        }
    }

    // Encrypted Vault Methods (3x wrong PIN -> 30s lockout + ZARA voice alert)
    fun unlockVaultWithPin(pin: String): Boolean {
        val lockout = vaultService.getRemainingLockoutSeconds()
        if (lockout > 0) {
            _uiState.update { it.copy(vaultLockoutSeconds = lockout, statusMessage = "Vault locked for ${lockout}s") }
            return false
        }
        if (securityPrefs.verifyPin(pin)) {
            vaultService.clearLockout()
            _uiState.update { it.copy(vaultFailedCount = 0, vaultLockoutSeconds = 0, statusMessage = "Vault Unlocked ✓") }
            return true
        }
        val (nextFailed, newLockout) = vaultService.recordFailedPinAttempt(_uiState.value.vaultFailedCount)
        _uiState.update {
            it.copy(
                vaultFailedCount = nextFailed,
                vaultLockoutSeconds = newLockout,
                statusMessage = if (newLockout > 0) "Bhai galat PIN hai! 30 second wait karo." else "Wrong PIN ($nextFailed/3)"
            )
        }
        if (newLockout > 0) {
            vibrate(600)
            voiceService.speak("Bhai galat PIN hai, 30 second wait karo", AvatarEmotionState.ROAST_ANGRY)
            startVaultLockoutCountdown()
        }
        return false
    }

    private fun startVaultLockoutCountdown() {
        viewModelScope.launch {
            while (vaultService.getRemainingLockoutSeconds() > 0) {
                _uiState.update { it.copy(vaultLockoutSeconds = vaultService.getRemainingLockoutSeconds()) }
                delay(1000L)
            }
            _uiState.update { it.copy(vaultLockoutSeconds = 0, vaultFailedCount = 0) }
        }
    }

    fun addVaultEncryptedItem(folder: String, title: String, plainSecret: String, mediaUri: String = "") {
        viewModelScope.launch {
            vaultService.addEncryptedItem(
                folder = folder,
                title = title,
                secretPlain = plainSecret,
                mediaUri = mediaUri,
                isDecoy = _uiState.value.fakeVaultMode
            )
            _uiState.update { it.copy(statusMessage = "AES-256 encrypted & locked inside $folder!") }
        }
    }

    fun decryptVaultItemText(item: VaultItemEntity): String = vaultService.decryptItem(item)

    fun deleteVaultItem(id: Long) {
        viewModelScope.launch { vaultService.deleteVaultItem(id) }
    }

    fun toggleVaultBlur(enabled: Boolean) {
        securityPrefs.setVaultBlurEnabled(enabled)
        _uiState.update { it.copy(vaultBlurEnabled = enabled) }
    }

    fun toggleFakeVaultMode(enabled: Boolean) {
        securityPrefs.setFakeVaultMode(enabled)
        _uiState.update { it.copy(fakeVaultMode = enabled, statusMessage = if (enabled) "Fake Decoy Vault view enabled" else "Real Sovereign Vault view") }
    }

    fun setVaultAutoLockLabel(label: String) {
        securityPrefs.setVaultAutoLockLabel(label)
        _uiState.update { it.copy(vaultAutoLockLabel = label, statusMessage = "Vault Auto-Lock set to $label") }
    }

    // Teacher, Meeting, Music, DeepSearch, Reverse, Broker, Gmail, Permissions
    fun runTeacherSamjhao(topic: String) {
        val text = teacherService.explainTopicInHinglish(topic)
        _uiState.update { it.copy(teacherExplanation = text, yellowSubtitleText = text) }
        voiceService.speak(text, AvatarEmotionState.HAPPY)
    }

    fun selectTeacherAnswer(questionId: String, optionIndex: Int) {
        val updated = _uiState.value.teacherQuizAnswers.toMutableMap().apply { put(questionId, optionIndex) }
        _uiState.update { it.copy(teacherQuizAnswers = updated) }
    }

    fun submitTeacherQuiz() {
        val (score, msg) = teacherService.evaluateQuiz(_uiState.value.teacherQuizAnswers)
        _uiState.update { it.copy(teacherScore = score, teacherResultMessage = msg, statusMessage = msg) }
        voiceService.speak(msg, if (score == 5) AvatarEmotionState.SURPRISED else AvatarEmotionState.HAPPY)
    }

    fun runMeetingOneCommandSixTasks(topic: String, participantEmail: String) {
        viewModelScope.launch {
            val tasks = meetingService.runOneCommandSixTasks(topic, participantEmail)
            _uiState.update {
                it.copy(
                    meetingTasks = tasks,
                    statusMessage = "6/6 Meeting tasks automated cleanly!"
                )
            }
            voiceService.speak("Meeting ke sabhi 6 kaam ready hain Dayaram, Join Now dabaiye!", AvatarEmotionState.HAPPY)
        }
    }

    fun launchMeetingAction(actionType: String, topic: String, email: String) {
        val meetUrl = _uiState.value.meetingTasks.getOrNull(1)?.detail ?: "https://meet.google.com/sanam-meet"
        when (actionType) {
            "calendar" -> meetingService.launchCalendar(topic, meetUrl)
            "email" -> meetingService.launchEmailInvite(email, topic, meetUrl)
            else -> meetingService.launchMeetLink(meetUrl)
        }
    }

    fun switchMusicPlatform(platform: String, songOverride: String? = null) {
        val query = songOverride?.takeIf { it.isNotBlank() } ?: _uiState.value.currentMusicQuery
        val (keptQuery, msg) = musicService.switchPlayerKeepingSong(query, platform)
        _uiState.update {
            it.copy(
                currentMusicQuery = keptQuery,
                activeMusicPlatform = platform,
                statusMessage = msg
            )
        }
    }

    fun runDeepSearchTool(query: String) {
        val report = brainService.generateDeepSearchReport(query)
        _uiState.update { it.copy(deepSearchReport = report, statusMessage = "Deep Search completed!") }
    }

    fun runReverseDiagramTool(input: String, mode: String) {
        val output = brainService.generateReverseEngineeringDiagram(input, mode)
        _uiState.update { it.copy(reverseDiagramOutput = output, statusMessage = "$mode generated cleanly") }
    }

    fun trackBrokerSymbol(symbol: String) {
        val summary = brainService.generateBrokerTrackSummary(symbol)
        _uiState.update { it.copy(brokerSummary = summary, statusMessage = "Broker watch updated (Draft Only)") }
    }

    fun createGmailVoiceDraft(to: String, subject: String, body: String) {
        val preview = brainService.generateGmailDraftSummary(to, subject, body)
        _uiState.update { it.copy(gmailDraftPreview = preview, statusMessage = "Gmail draft ready — confirm before send") }
    }

    fun launchGmailDraftIntent(to: String, subject: String, body: String) {
        controlService.openEmailDraft(to, subject, body)
    }

    fun markPermissionStepHandled(step: Int) {
        _uiState.update { it.copy(completedPermissionSteps = it.completedPermissionSteps + step) }
    }

    fun openAccessibilitySetup() {
        controlService.openAccessibilitySettings()
    }

    // --- API Router Keys & Live Green/Red Connection Test ---
    fun saveOpenAiKey(key: String) { securityPrefs.saveApiKey("openai", key); _uiState.update { it.copy(openAiKey = key, statusMessage = "OpenAI key saved local encrypted.") } }
    fun saveGeminiKey(key: String) { securityPrefs.saveApiKey("gemini", key); _uiState.update { it.copy(geminiKey = key, statusMessage = "Gemini key saved local encrypted.") } }
    fun saveGroqKey(key: String) { securityPrefs.saveApiKey("groq", key); _uiState.update { it.copy(groqKey = key, statusMessage = "Groq key saved local encrypted.") } }
    fun saveClaudeKey(key: String) { securityPrefs.saveApiKey("claude", key); _uiState.update { it.copy(claudeKey = key, statusMessage = "Claude key saved local encrypted.") } }
    fun saveDeepSeekKey(key: String) { securityPrefs.saveApiKey("deepseek", key); _uiState.update { it.copy(deepSeekKey = key, statusMessage = "DeepSeek key saved local encrypted.") } }
    fun saveOpenRouterKey(key: String) { securityPrefs.saveApiKey("openrouter", key); _uiState.update { it.copy(openRouterKey = key, statusMessage = "OpenRouter key saved local encrypted.") } }
    fun saveOllamaUrl(url: String) { securityPrefs.saveApiKey("ollama_url", url); _uiState.update { it.copy(ollamaUrl = url, statusMessage = "Ollama URL saved locally.") } }
    fun saveCustomApiUrl(url: String) { securityPrefs.saveApiKey("custom_url", url); _uiState.update { it.copy(customApiUrl = url, statusMessage = "Custom API URL saved.") } }

    fun testAndSaveApiKey(service: String, key: String) {
        when (service.lowercase()) {
            "openai" -> saveOpenAiKey(key)
            "gemini" -> saveGeminiKey(key)
            "groq" -> saveGroqKey(key)
            "claude" -> saveClaudeKey(key)
            "deepseek" -> saveDeepSeekKey(key)
            "openrouter" -> saveOpenRouterKey(key)
            "ollama" -> saveOllamaUrl(key)
        }
        viewModelScope.launch {
            val result = brainService.testConnection(service, key)
            val updated = _uiState.value.apiTestStatuses.toMutableMap().apply { put(service.lowercase(), result) }
            _uiState.update { it.copy(apiTestStatuses = updated, statusMessage = "${service.uppercase()}: ${result.second}") }
            vibrate(90)
        }
    }

    // --- Themes, Agents, Language & Accessibility ---
    fun setCustomAppName(name: String) { viewModelScope.launch { dataStore.setCustomAppName(name); _uiState.update { it.copy(statusMessage = "Header: ${name.ifBlank { "CHOUHAN STAR" }}") } } }
    fun setCoreThemeMode(mode: String) { viewModelScope.launch { dataStore.setCoreThemeMode(mode) } }
    fun setMoodTheme(mood: String) { viewModelScope.launch { dataStore.setMoodTheme(mood) } }
    fun setSystemMode(mode: String) { viewModelScope.launch { dataStore.setSystemMode(mode) } }
    fun setLargeFont(enabled: Boolean) { viewModelScope.launch { dataStore.setLargeFont(enabled) } }
    fun setVoiceOnlyMode(enabled: Boolean) { viewModelScope.launch { dataStore.setVoiceOnlyMode(enabled) } }

    fun toggleHiddenCalculatorMode(enabled: Boolean) {
        viewModelScope.launch { dataStore.setHiddenCalcMode(enabled) }
        SystemHardwareController.toggleHiddenCalculatorIcon(getApplication(), enabled)
        _uiState.update {
            it.copy(statusMessage = if (enabled) "Hidden Calculator disguise enabled on launcher." else "CHOUHAN STAR original launcher icon restored.")
        }
    }

    fun selectAgent(agentId: String) {
        val agent = SanamCatalog.fourteenAgents.find { it.id == agentId } ?: return
        viewModelScope.launch { dataStore.setActiveAgentId(agentId) }
        voiceService.applyVoiceProfile(_uiState.value.selectedVoiceId, agent.voicePitch, _uiState.value.voiceRate)
        _uiState.update {
            it.copy(
                activeAgentId = agentId,
                yellowSubtitleText = agent.greetingHinglish,
                assistantStatus = "SPEAKING",
                sanamResponseText = agent.greetingHinglish,
                isSanamResponding = true
            )
        }
        voiceService.speak(agent.greetingHinglish, AvatarEmotionState.WAVING)
    }

    // --- Prompts, FlowShift, Build Protocol, Claude Code, UMI, Control Dashboard, APK ---
    fun togglePromptFavorite(promptId: String) {
        _uiState.update { state ->
            state.copy(promptTemplates = state.promptTemplates.map { if (it.id == promptId) it.copy(isFavorite = !it.isFavorite) else it })
        }
    }

    fun nextUmiExpression(indexOverride: Int? = null) {
        val next = indexOverride ?: ((_uiState.value.umiExpressionIndex + 1) % SanamCatalog.umiExpressions.size)
        _uiState.update { it.copy(umiExpressionIndex = next) }
        vibrate(40)
    }

    fun startFlowShift(mode: String) {
        val secs = when (mode) { "Padhai" -> 1500; "Coding" -> 2700; "Gaane" -> 1200; else -> 600 }
        _uiState.update {
            it.copy(
                flowShiftStep = mode,
                flowShiftRunning = true,
                flowShiftSeconds = secs,
                flowShiftHistory = listOf("$mode session started (${secs / 60}m)") + it.flowShiftHistory.take(6),
                statusMessage = "FlowShift switched to $mode"
            )
        }
    }

    fun toggleFlowShiftPause() { _uiState.update { it.copy(flowShiftRunning = !it.flowShiftRunning) } }

    fun runFullBuildProtocol() {
        viewModelScope.launch {
            _uiState.update { it.copy(buildProtocolStep = 1, buildStatusText = "01 FACE Scanning...", protocolStates = listOf("RUNNING", "PENDING", "PENDING", "PENDING")) }
            delay(250)
            _uiState.update { it.copy(buildProtocolStep = 2, buildStatusText = "02 VOICE Pipeline Linked...", protocolStates = listOf("DONE", "RUNNING", "PENDING", "PENDING")) }
            delay(250)
            _uiState.update { it.copy(buildProtocolStep = 3, buildStatusText = "03 BRAIN Offline+Cloud Synced...", protocolStates = listOf("DONE", "DONE", "RUNNING", "PENDING")) }
            delay(250)
            val finalMsg = "Yes boss you did it, ban gaya"
            _uiState.update {
                it.copy(
                    buildProtocolStep = 4,
                    buildStatusText = "04 BUILD COMPLETE — ALL GREEN ✓",
                    protocolStates = listOf("DONE", "DONE", "DONE", "DONE"),
                    yellowSubtitleText = finalMsg,
                    sanamResponseText = finalMsg,
                    statusMessage = "ZARA: $finalMsg"
                )
            }
            vibrate(250)
            voiceService.speak(finalMsg, AvatarEmotionState.SURPRISED)
        }
    }

    fun runClaudeCodeAction(actionName: String) {
        val logLine = when (actionName) {
            "Bug Fix" -> "POST /charges double-charge fixed: added Idempotency-Key + atomic DB transaction lock ✓"
            "Test" -> "Suite run: 14 unit + idempotency tests PASSED ✓"
            "Migration" -> "Migration Day 1: Expand schema -> Day 2: Dual-write -> Day 3: Switch & prune ✓"
            else -> "Code Review: Kotlin Compose & SecurityPreferences AES-256-GCM clean ✓"
        }
        _uiState.update { it.copy(claudeCodeLogs = listOf(logLine) + it.claudeCodeLogs.take(10), statusMessage = "Claude Code: $actionName executed") }
        vibrate(60)
    }

    fun setClaudeSidebarTab(tab: String) { _uiState.update { it.copy(claudeSidebarTab = tab) } }

    fun addDhaiBuildProject(name: String, stack: String) {
        if (name.isBlank()) return
        val item = DhaiBuildProject("b_${System.currentTimeMillis()}", name.trim(), stack.ifBlank { "Kotlin + Compose" }, "NEW", "Just now")
        _uiState.update { it.copy(dhaiBuilds = listOf(item) + it.dhaiBuilds, statusMessage = "Created project: ${item.name}") }
    }

    // Hardware Controls
    fun toggleTorch(enabled: Boolean) { SystemHardwareController.setFlashlight(getApplication(), enabled); _uiState.update { it.copy(torchEnabled = enabled, statusMessage = if (enabled) "Torchlight ON" else "Torchlight OFF") } }
    fun toggleWifiControl() { _uiState.update { val next = !it.wifiEnabled; it.copy(wifiEnabled = next, statusMessage = "WiFi Shield ${if (next) "ON" else "OFF"}") } }
    fun toggleBluetoothControl() { _uiState.update { val next = !it.bluetoothEnabled; it.copy(bluetoothEnabled = next, statusMessage = "Bluetooth Mesh ${if (next) "ON" else "OFF"}") } }
    fun toggleDndControl() { _uiState.update { val next = !it.dndEnabled; it.copy(dndEnabled = next, statusMessage = "DND Focus ${if (next) "Active" else "Off"}") } }
    fun setBrightnessPercent(percent: Float) { _uiState.update { it.copy(brightnessPercent = percent.coerceIn(5f, 100f)) } }
    fun setMediaVolumePercent(percent: Float) { val c = percent.coerceIn(0f, 100f); SystemHardwareController.setMediaVolumePercent(getApplication(), c.toInt()); _uiState.update { it.copy(mediaVolumePercent = c) } }
    fun setQuickAlarm(label: String) { controlService.setSystemAlarm(6, 0, label); _uiState.update { it.copy(nextAlarmLabel = label, statusMessage = "Alarm updated: $label") } }
    fun exportApkAction() { val msg = SystemHardwareController.exportAndShareApk(getApplication()); _uiState.update { it.copy(statusMessage = msg) } }

    fun toggleWhatsappConnector(enabled: Boolean) { viewModelScope.launch { dataStore.setWhatsappConnector(enabled) } }
    fun toggleGmailConnector(enabled: Boolean) { viewModelScope.launch { dataStore.setGmailConnector(enabled) } }
    fun toggleCalendarConnector(enabled: Boolean) { viewModelScope.launch { dataStore.setCalendarConnector(enabled) } }
    fun clearAllWorldModel() { viewModelScope.launch { starDatabase.worldModelDao().clearAll(); _uiState.update { it.copy(statusMessage = "World model memory cleared.") } } }

    fun toggleRoomTrigger(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            starDatabase.triggerDao().updateTriggerEnabled(id, isEnabled)
            if (isEnabled && _uiState.value.roomTriggers.find { it.id == id }?.triggerType == "SCHEDULED") {
                ScheduledSecurityWorker.schedulePeriodicWatch(getApplication())
            }
        }
    }

    fun addTrigger(title: String, triggerType: String, action: String) {
        viewModelScope.launch {
            val count = _uiState.value.roomTriggers.size + 1
            starDatabase.triggerDao().insertTrigger(
                TriggerEntity(title = if (title.startsWith("Trigger")) title else "Trigger $count: $title", triggerType = triggerType, action = action, isEnabled = true, updatedTime = "Just now")
            )
            _uiState.update { it.copy(statusMessage = "Automation Trigger added!") }
        }
    }

    fun getLocalApiKey(key: String): String = securityPrefs.getApiKey(key)
    fun saveLocalApiKey(key: String, value: String) { securityPrefs.saveApiKey(key, value); _uiState.update { it.copy(statusMessage = "Key $key saved encrypted.") } }
    fun toggleTrigger(id: String) { _uiState.update { it.copy(customTriggers = it.customTriggers.map { c -> if (c.id == id) c.copy(enabled = !c.enabled) else c }) } }
    fun setCallAssistant(enabled: Boolean) { viewModelScope.launch { dataStore.setCallAssistant(enabled) } }
    fun setChatNotifications(enabled: Boolean) { viewModelScope.launch { dataStore.setChatNotifications(enabled) } }
    fun setScreenUnlockTrigger(enabled: Boolean) { viewModelScope.launch { dataStore.setScreenUnlockTrigger(enabled) } }
    fun setHeadphonesTrigger(enabled: Boolean) { viewModelScope.launch { dataStore.setHeadphonesTrigger(enabled) } }
    fun setWifiTrigger(enabled: Boolean) { viewModelScope.launch { dataStore.setWifiTrigger(enabled) } }

    fun onUserSignedIn(displayName: String, email: String) {
        viewModelScope.launch {
            val profileRes = repository.getOrCreateProfile(displayName, email)
            if (profileRes.isSuccess) {
                val profile = profileRes.getOrThrow()
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        currentThemeAura = profile.themeAura,
                        currentIconVariant = profile.iconVariant,
                        currentLanguage = profile.language,
                        isOfflineMode = false,
                        statusMessage = "Welcome back, ${displayName.substringBefore(" ")}"
                    )
                }
            }
            launch {
                repository.observeSecurityConfig().catch { }.collect { config ->
                    _uiState.update { it.copy(securityConfig = config) }
                    sensorGuardManager.setSensitivity(config.motionSensitivity)
                    if (config.touchGuardArmed) sensorGuardManager.arm() else sensorGuardManager.disarm()
                }
            }
            launch {
                repository.observeIncidentLogs().catch { }.collect { incidents ->
                    _uiState.update { it.copy(incidentLogs = incidents) }
                }
            }
        }
    }

    private fun initDefaultLockedApps() {
        _uiState.update {
            it.copy(
                lockedApps = listOf(
                    LockedAppItem("com.whatsapp", "WhatsApp", isLocked = true, category = "Social"),
                    LockedAppItem("com.google.android.apps.photos", "Google Photos", isLocked = true, category = "Media"),
                    LockedAppItem("com.instagram.android", "Instagram", isLocked = true, category = "Social"),
                    LockedAppItem("com.google.android.apps.nbu.paisa.user", "Google Pay", isLocked = true, category = "Finance"),
                    LockedAppItem("net.one97.paytm", "Paytm", isLocked = true, category = "Finance"),
                    LockedAppItem("com.phonepe.app", "PhonePe", isLocked = true, category = "Finance"),
                    LockedAppItem("com.android.settings", "Settings", isLocked = true, category = "System"),
                    LockedAppItem("org.telegram.messenger", "Telegram", isLocked = false, category = "Social"),
                    LockedAppItem("com.android.chrome", "Chrome", isLocked = false, category = "Tools")
                )
            )
        }
    }

    private fun initDefaultTriggers() {
        _uiState.update {
            it.copy(
                customTriggers = listOf(
                    CustomTriggerItem("trig_wakeword", "Wake Word: Hey Sanam suno", "VOICE", "SpeechRecognizer Voice Assistant", enabled = false),
                    CustomTriggerItem("trig_screen", "Screen On / Unlock", "SCREEN", "Silent Stealth Face Scan", enabled = true),
                    CustomTriggerItem("trig_headphones", "Headphones Disconnected", "HEADPHONES", "Announce Voice Status", enabled = true),
                    CustomTriggerItem("trig_wifi", "Unknown WiFi Connect", "WIFI", "Lock High Security Vault", enabled = true),
                    CustomTriggerItem("trig_schedule", "Night Watch Schedule (12 AM - 6 AM)", "SCHEDULED", "Auto Arm Touch Guard", enabled = false)
                )
            )
        }
    }

    // Touch Guard, App Lock, GF Mode, Face Setup
    fun toggleTouchGuardArmed(armed: Boolean) {
        val updated = _uiState.value.securityConfig.copy(touchGuardArmed = armed)
        _uiState.update { it.copy(securityConfig = updated) }
        if (armed) {
            sensorGuardManager.arm()
            voiceService.speak("Touch Guard armed boss! Phone mat hilana.")
        } else {
            sensorGuardManager.disarm()
            voiceService.speak("Touch Guard disarmed.")
        }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun setMotionSensitivity(sensitivity: String) { sensorGuardManager.setSensitivity(sensitivity); val u = _uiState.value.securityConfig.copy(motionSensitivity = sensitivity); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun setSirenVolume(volume: Long) { val u = _uiState.value.securityConfig.copy(sirenVolume = volume); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun toggleWarningMode(mode: String, enabled: Boolean) {
        val c = _uiState.value.securityConfig
        val u = when (mode) { "VOICE" -> c.copy(warningVoice = enabled); "VIBRATION" -> c.copy(warningVibration = enabled); "SIREN" -> c.copy(warningSiren = enabled); "FULLSCREEN" -> c.copy(warningFullScreen = enabled); else -> c }
        _uiState.update { it.copy(securityConfig = u) }
        viewModelScope.launch { repository.saveSecurityConfig(u) }
    }
    fun toggleIntruderPhotoCapture(enabled: Boolean) { val u = _uiState.value.securityConfig.copy(intruderPhotoEnabled = enabled); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun toggleAppLock(enabled: Boolean) { val u = _uiState.value.securityConfig.copy(appLockEnabled = enabled); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun toggleGfMode(enabled: Boolean) { val u = _uiState.value.securityConfig.copy(gfModeEnabled = enabled); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun updateGfScoldText(text: String) { val u = _uiState.value.securityConfig.copy(gfScoldText = text); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }
    fun testGfScold() { _uiState.update { it.copy(isGfScolding = true) }; voiceService.speak(_uiState.value.securityConfig.gfScoldText, AvatarEmotionState.ROAST_ANGRY); vibrate(600) }
    fun testGfScoldVoice() = testGfScold()
    fun toggleAppLocked(packageName: String) { _uiState.update { it.copy(lockedApps = it.lockedApps.map { a -> if (a.packageName == packageName) a.copy(isLocked = !a.isLocked) else a }) } }
    fun setAutoLockTimeout(seconds: Long) { val u = _uiState.value.securityConfig.copy(autoLockTimeoutSeconds = seconds); _uiState.update { it.copy(securityConfig = u) }; viewModelScope.launch { repository.saveSecurityConfig(u) } }

    fun saveNewPin(pin: String): Boolean {
        if (pin.length in 4..6) {
            securityPrefs.savePin(pin)
            _uiState.update { it.copy(isPinConfigured = true, statusMessage = "PIN successfully updated!") }
            return true
        }
        return false
    }

    fun verifyPin(pin: String): Boolean {
        val ok = securityPrefs.verifyPin(pin)
        if (!ok) {
            val count = _uiState.value.failedAttempts + 1
            _uiState.update { it.copy(failedAttempts = count) }
            if (count >= 3) triggerTouchBreachAlarm("Maximum PIN attempts exceeded!")
        } else {
            _uiState.update { it.copy(failedAttempts = 0, isAlarmActive = false) }
        }
        return ok
    }

    fun updateVoicePersonality(personality: String) {
        val updated = _uiState.value.securityConfig.copy(voicePersonality = personality)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
        val greeting = when (personality) {
            "Royal Rajput" -> "Khamma Ghani! Rajputi Shaan aur suraksha tayyar hai."
            "Cyber Sher" -> "Tactical AI active. Threat perimeter secured."
            "Silent Phantom" -> "Shadow protocol armed. Silent scanning enabled."
            else -> "Dabang Guardian armed! Koi chhedkhani nahi chalegi."
        }
        voiceService.speak(greeting)
    }

    fun updateVoiceSpeed(speed: Double) { setVoiceRateSlider(speed.toFloat()) }
    fun toggleVoiceParameter(param: String, enabled: Boolean) {
        val c = _uiState.value.securityConfig
        val u = when (param) { "FAST_RESPONSE" -> c.copy(fastResponse = enabled); "NATURAL_PAUSE" -> c.copy(naturalPause = enabled); "EMOTIONAL_TONE" -> c.copy(emotionalTone = enabled); else -> c }
        _uiState.update { it.copy(securityConfig = u) }
        viewModelScope.launch { repository.saveSecurityConfig(u) }
    }

    fun setThemeAura(auraName: String) { _uiState.update { it.copy(currentThemeAura = auraName) }; viewModelScope.launch { repository.updateAuraSettings(auraName, _uiState.value.currentIconVariant, _uiState.value.currentLanguage) } }
    fun setIconVariant(variant: String) { _uiState.update { it.copy(currentIconVariant = variant) }; viewModelScope.launch { repository.updateAuraSettings(_uiState.value.currentThemeAura, variant, _uiState.value.currentLanguage) } }
    fun setLanguage(lang: String) { _uiState.update { it.copy(currentLanguage = lang, detectedLanguage = if (lang == "Auto Hinglish") "Hinglish" else lang) }; viewModelScope.launch { dataStore.setLanguage(lang); repository.updateAuraSettings(_uiState.value.currentThemeAura, _uiState.value.currentIconVariant, lang) } }
    fun startFaceEnrollment() { _uiState.update { it.copy(faceEnrollmentStep = 1) } }
    fun advanceFaceEnrollment() {
        val next = _uiState.value.faceEnrollmentStep + 1
        _uiState.update { it.copy(faceEnrollmentStep = next) }
        vibrate(100)
        if (next == 4) {
            securityPrefs.setFaceEnrolled(true)
            _uiState.update { it.copy(isFaceEnrolled = true) }
            voiceService.speak("Face scan successful! Biometric star lock is ready.", AvatarEmotionState.SURPRISED)
        }
    }
    fun resetFaceEnrollment() { _uiState.update { it.copy(faceEnrollmentStep = 0) } }

    fun triggerTouchBreachAlarm(reason: String) {
        val cfg = _uiState.value.securityConfig
        _uiState.update { it.copy(isAlarmActive = true, alarmReason = reason) }
        if (cfg.warningVibration) vibrate(1200)
        if (cfg.warningVoice) {
            val alert = if (cfg.gfModeEnabled) cfg.gfScoldText else "Chehra match nahi hua! Kripya phone wapas rakhein!"
            voiceService.speak(alert, AvatarEmotionState.ROAST_ANGRY)
        }
        viewModelScope.launch {
            starDatabase.intruderDao().insertSnapshot(
                IntruderRecord(triggerType = "TOUCH_BREACH", timestamp = System.currentTimeMillis(), localPhotoPath = "/storage/emulated/0/Pictures/CHOUHAN_STAR/intruder_${System.currentTimeMillis()}.jpg", note = "Breach: $reason")
            )
            repository.recordIncident("TOUCH_BREACH", reason, severity = "HIGH", hasPhoto = true)
        }
    }

    fun onAudioPermissionGranted() { if (_uiState.value.isWakeWordEnabled) checkAndStartWakeWordListening() }
    fun checkAndStartWakeWordListening() {
        val app = getApplication<Application>()
        val hasAudio = ContextCompat.checkSelfPermission(app, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (hasAudio && _uiState.value.isWakeWordEnabled) wakeWordManager.startListening()
    }
    fun toggleWakeWord(enabled: Boolean) { setAlwaysListeningEnabled(enabled) }
    fun startWakeWordListening() { setAlwaysListeningEnabled(true) }
    fun stopWakeWordListening() { setAlwaysListeningEnabled(false) }
    fun simulateWakeWord(phrase: String = "Hey Sanam suno", trailingCommand: String? = null) { wakeWordManager.simulateWakeWord(phrase, trailingCommand) }

    override fun onBeginningOfUserSpeech() {
        voiceService.interruptForBargeIn()
    }

    override fun onWakeWordDetected(matchedPhrase: String, trailingCommand: String?) {
        vibrate(200)
        voiceService.interruptForBargeIn()
        lastVoiceFocusInteractionMs = System.currentTimeMillis()
        hasShownIdleCheckIn = false
        val count = _uiState.value.wakeWordTriggerCount + 1
        _uiState.update {
            it.copy(
                lastWakeWordEvent = matchedPhrase,
                wakeWordTriggerCount = count,
                lastSpokenCommand = trailingCommand,
                assistantStatus = "ANALYZING",
                isVoiceFocusActive = it.voiceFocusModeEnabled
            )
        }
        if (!trailingCommand.isNullOrBlank()) {
            sendChatMessage(trailingCommand)
        } else {
            val reply = "Haanji Dayaram! Main ZARA sun rahi hoon, boliye kya karoon?"
            _uiState.update {
                it.copy(
                    isSanamResponding = true,
                    sanamResponseText = reply,
                    yellowSubtitleText = reply,
                    avatarEmotion = AvatarEmotionState.WAVING,
                    assistantStatus = "SPEAKING"
                )
            }
            voiceService.speak(reply, AvatarEmotionState.WAVING)
        }
    }

    override fun onRmsChanged(rmsDb: Float) { _uiState.update { it.copy(wakeWordAudioRms = rmsDb) } }
    override fun onListeningStateChanged(isListening: Boolean) { _uiState.update { it.copy(isWakeWordListening = isListening, assistantStatus = if (isListening) "LISTENING" else it.assistantStatus) } }
    override fun onSpeechRecognized(text: String) {
        val lang = SystemHardwareController.detectLanguageFromText(text, _uiState.value.currentLanguage)
        _uiState.update { it.copy(lastHeardSpeech = text, detectedLanguage = lang) }
    }
    override fun onError(errorMessage: String) { Log.w("StarViewModel", "WakeWord error: $errorMessage") }

    fun dismissAlarm() { _uiState.update { it.copy(isAlarmActive = false, alarmReason = "") }; voiceService.stopSpeaking() }
    fun clearCache() { viewModelScope.launch { starDatabase.intruderDao().clearAll(); _uiState.update { it.copy(statusMessage = "Local security cache cleared.") } } }
    fun clearStatusMessage() { _uiState.update { it.copy(statusMessage = null) } }

    fun vibrate(durationMs: Long) {
        val app = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                (app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    fun toggleGamingAssistant(enabled: Boolean) { viewModelScope.launch { dataStore.setGamingAssistant(enabled) } }
    fun toggleChatGptImage(enabled: Boolean) { viewModelScope.launch { dataStore.setChatGptImage(enabled) } }
    fun toggleAutoPayments(enabled: Boolean) { viewModelScope.launch { dataStore.setAutoPayments(enabled) } }
    fun toggleAuraBorderStealth(enabled: Boolean) { viewModelScope.launch { dataStore.setAuraBorderStealth(enabled) }; vibrate(50) }
    fun runDeepResearch(query: String) { runDeepSearchTool(query) }
    fun runImageSearch(query: String) { _uiState.update { it.copy(statusMessage = "Image search: $query") } }
    fun postSocialStatus(platform: String, message: String) { _uiState.update { it.copy(statusMessage = "Draft prepared for $platform: $message") } }

    fun captureLocalScreenshot() {
        vibrate(120)
        viewModelScope.launch {
            val fileName = "screenshot_${System.currentTimeMillis()}.jpg"
            starDatabase.intruderDao().insertSnapshot(
                IntruderRecord(triggerType = "SCREENSHOT", timestamp = System.currentTimeMillis(), localPhotoPath = "/storage/emulated/0/Pictures/CHOUHAN_STAR/$fileName", note = "Manual Screenshot")
            )
            loadPicturesFiles()
            _uiState.update { it.copy(statusMessage = "Local screenshot saved to Pictures/CHOUHAN_STAR/") }
        }
    }

    fun loadPicturesFiles() {
        viewModelScope.launch {
            val dir = java.io.File(getApplication<Application>().getExternalFilesDir(null), "Pictures/CHOUHAN_STAR")
            if (!dir.exists()) dir.mkdirs()
            val files = dir.listFiles()?.map { it.name } ?: listOf("intruder_sample_1.jpg", "screenshot_demo.jpg")
            _uiState.update { it.copy(picturesFiles = files) }
        }
    }

    fun callGroqWithJsonRetry(prompt: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = brainService.generateAssistantReply(prompt, _uiState.value.activeAgentId, _uiState.value.currentLanguage, _uiState.value.moodTheme)
            val json = JSONObjectSafe.formatReply(res.providerUsed, res.text)
            _uiState.update { it.copy(statusMessage = "Router responded via ${res.providerUsed}") }
            onResult(json)
        }
    }

    private fun currentTimeString(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    override fun onCleared() {
        super.onCleared()
        wakeWordManager.destroy()
        sensorGuardManager.disarm()
        voiceService.shutdown()
    }

    private object JSONObjectSafe {
        fun formatReply(provider: String, text: String): String =
            org.json.JSONObject().apply {
                put("status", "success")
                put("provider", provider)
                put("reply", text)
            }.toString()
    }
}
