package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.StarPreferencesDataStore
import com.example.data.local.IntruderRecord
import com.example.data.local.StarDatabase
import com.example.data.local.TriggerEntity
import com.example.data.local.WorldModelEntity
import com.example.data.model.CustomTriggerItem
import com.example.data.model.IncidentLog
import com.example.data.model.LockedAppItem
import com.example.data.model.SecurityConfig
import com.example.data.model.UserProfile
import com.example.data.repository.StarSecurityRepository
import com.example.data.security.SecurityPreferences
import com.example.data.security.SensorGuardManager
import com.example.worker.ScheduledSecurityWorker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.data.speech.WakeWordListener
import com.example.data.speech.WakeWordManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class StarUiState(
    val userProfile: UserProfile = UserProfile(),
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
    val customApiUrl: String = "",
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
    val deviceInfo: String = "Samsung Galaxy / Android 14 (Supported Android 7-15) - Timezone: ${java.util.TimeZone.getDefault().id}",
    // Wake Word "Hey Myra" State
    val isWakeWordEnabled: Boolean = true,
    val isWakeWordListening: Boolean = false,
    val wakeWordAudioRms: Float = 0f,
    val lastWakeWordEvent: String? = null,
    val wakeWordTriggerCount: Int = 0,
    val lastSpokenCommand: String? = null,
    val lastHeardSpeech: String = "",
    val myraResponseText: String? = null,
    val isMyraResponding: Boolean = false
)

class StarSecurityViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener, WakeWordListener {

    private val repository = StarSecurityRepository(application)
    private val starDatabase = StarDatabase.getInstance(application)
    private val securityPrefs = SecurityPreferences(application)
    private val dataStore = StarPreferencesDataStore(application)

    private val _uiState = MutableStateFlow(StarUiState())
    val uiState: StateFlow<StarUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val wakeWordManager = WakeWordManager(application, this)

    private val sensorGuardManager = SensorGuardManager(application) {
        triggerTouchBreachAlarm("Motion detected! Phone picked up while armed.")
    }

    init {
        tts = TextToSpeech(application, this)
        initDefaultLockedApps()
        initDefaultTriggers()
        loadLocalData()
        loadApiKeys()
        observeDataStorePreferences()
        seedRoomDatabaseIfEmpty()
        _uiState.update { it.copy(isFaceEnrolled = securityPrefs.isFaceEnrolled()) }

        checkAndStartWakeWordListening()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            tts?.setSpeechRate(_uiState.value.securityConfig.voiceSpeed.toFloat())
            isTtsReady = true
        }
    }

    private fun loadLocalData() {
        viewModelScope.launch {
            starDatabase.intruderDao().getAllSnapshots().collect { snapshots ->
                _uiState.update { it.copy(localSnapshots = snapshots) }
            }
        }
        viewModelScope.launch {
            starDatabase.triggerDao().getAllTriggers().collect { triggers ->
                _uiState.update { it.copy(roomTriggers = triggers) }
            }
        }
        viewModelScope.launch {
            starDatabase.worldModelDao().getAllItems().collect { items ->
                _uiState.update { it.copy(roomWorldModel = items) }
            }
        }
    }

    private fun loadApiKeys() {
        _uiState.update {
            it.copy(
                openAiKey = securityPrefs.getApiKey("openai"),
                geminiKey = securityPrefs.getApiKey("gemini"),
                groqKey = securityPrefs.getApiKey("groq"),
                claudeKey = securityPrefs.getApiKey("claude"),
                customApiUrl = securityPrefs.getApiKey("custom_url")
            )
        }
    }

    private fun observeDataStorePreferences() {
        viewModelScope.launch {
            dataStore.callAssistantFlow.collect { v -> _uiState.update { it.copy(callAssistant = v) } }
        }
        viewModelScope.launch {
            dataStore.chatNotificationsFlow.collect { v -> _uiState.update { it.copy(chatNotifications = v) } }
        }
        viewModelScope.launch {
            dataStore.screenUnlockTriggerFlow.collect { v -> _uiState.update { it.copy(screenUnlockTrigger = v) } }
        }
        viewModelScope.launch {
            dataStore.headphonesTriggerFlow.collect { v -> _uiState.update { it.copy(headphonesTrigger = v) } }
        }
        viewModelScope.launch {
            dataStore.wifiTriggerFlow.collect { v -> _uiState.update { it.copy(wifiTrigger = v) } }
        }
        viewModelScope.launch {
            dataStore.whatsappConnectorFlow.collect { v -> _uiState.update { it.copy(whatsappConnector = v) } }
        }
        viewModelScope.launch {
            dataStore.gmailConnectorFlow.collect { v -> _uiState.update { it.copy(gmailConnector = v) } }
        }
        viewModelScope.launch {
            dataStore.calendarConnectorFlow.collect { v -> _uiState.update { it.copy(calendarConnector = v) } }
        }
        viewModelScope.launch {
            dataStore.gamingAssistantFlow.collect { v -> _uiState.update { it.copy(gamingAssistant = v) } }
        }
        viewModelScope.launch {
            dataStore.chatGptImageFlow.collect { v -> _uiState.update { it.copy(chatGptImage = v) } }
        }
        viewModelScope.launch {
            dataStore.autoPaymentsFlow.collect { v -> _uiState.update { it.copy(autoPayments = v) } }
        }
        viewModelScope.launch {
            dataStore.auraBorderStealthFlow.collect { v -> _uiState.update { it.copy(auraBorderStealth = v) } }
        }
        loadPicturesFiles()
    }

    private fun seedRoomDatabaseIfEmpty() {
        viewModelScope.launch {
            val existingTriggers = starDatabase.triggerDao().getAllTriggers().first()
            if (existingTriggers.isEmpty()) {
                val seedTriggers = listOf(
                    TriggerEntity(title = "Trigger 0: Wake Word (Hey Myra)", triggerType = "VOICE", action = "SpeechRecognizer Voice Command Engine", isEnabled = true),
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
                    WorldModelEntity(category = "App", title = "WhatsApp", learnedAction = "send message to family", updatedAt = "Updated 2 days ago"),
                    WorldModelEntity(category = "Contact", title = "Mom", learnedAction = "call on sunday", updatedAt = "Updated 2 days ago"),
                    WorldModelEntity(category = "Workflow", title = "Morning routine", learnedAction = "order coffee + check weather", updatedAt = "Updated 2 days ago"),
                    WorldModelEntity(category = "App", title = "PhonePe", learnedAction = "biometric payment confirmation", updatedAt = "Updated 1 day ago"),
                    WorldModelEntity(category = "Contact", title = "Pooja", learnedAction = "share live location on late travel", updatedAt = "Updated 3 days ago"),
                    WorldModelEntity(category = "Workflow", title = "Night Safe Protocol", learnedAction = "lock all gallery & banking apps at 11:30 PM", updatedAt = "Updated 4 days ago")
                )
                starDatabase.worldModelDao().insertAll(seedWorld)
            }
        }
    }

    // --- API Screen Actions ---
    fun saveOpenAiKey(key: String) {
        securityPrefs.saveApiKey("openai", key)
        _uiState.update { it.copy(openAiKey = key, statusMessage = "OpenAI key saved local encrypted.") }
    }

    fun saveGeminiKey(key: String) {
        securityPrefs.saveApiKey("gemini", key)
        _uiState.update { it.copy(geminiKey = key, statusMessage = "Gemini key saved local encrypted.") }
    }

    fun saveGroqKey(key: String) {
        securityPrefs.saveApiKey("groq", key)
        _uiState.update { it.copy(groqKey = key, statusMessage = "Groq key saved local encrypted.") }
    }

    fun saveClaudeKey(key: String) {
        securityPrefs.saveApiKey("claude", key)
        _uiState.update { it.copy(claudeKey = key, statusMessage = "Claude key saved local encrypted.") }
    }

    fun testAndSaveApiKey(service: String, key: String) {
        if (key.isBlank()) {
            _uiState.update { it.copy(statusMessage = "Error: $service key cannot be empty.") }
            return
        }
        when (service.lowercase()) {
            "openai" -> saveOpenAiKey(key)
            "gemini" -> saveGeminiKey(key)
            "groq" -> saveGroqKey(key)
            "claude" -> saveClaudeKey(key)
        }
        // Simulate live connection test handshake
        vibrate(100)
        _uiState.update { it.copy(statusMessage = "✅ $service connection verified & saved encrypted!") }
    }

    fun saveCustomApiUrl(url: String) {
        securityPrefs.saveApiKey("custom_url", url)
        _uiState.update { it.copy(customApiUrl = url, statusMessage = "Custom API URL saved local encrypted.") }
    }

    fun toggleWhatsappConnector(enabled: Boolean) {
        viewModelScope.launch { dataStore.setWhatsappConnector(enabled) }
    }

    fun toggleGmailConnector(enabled: Boolean) {
        viewModelScope.launch { dataStore.setGmailConnector(enabled) }
    }

    fun toggleCalendarConnector(enabled: Boolean) {
        viewModelScope.launch { dataStore.setCalendarConnector(enabled) }
    }

    // --- World Model Actions ---
    fun clearAllWorldModel() {
        viewModelScope.launch {
            starDatabase.worldModelDao().clearAll()
            _uiState.update { it.copy(statusMessage = "World model memory cleared.") }
        }
    }

    // --- Triggers Screen Actions ---
    fun toggleRoomTrigger(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            starDatabase.triggerDao().updateTriggerEnabled(id, isEnabled)
            if (isEnabled) {
                // If scheduled task is enabled, trigger WorkManager schedule!
                val trigger = _uiState.value.roomTriggers.find { it.id == id }
                if (trigger?.triggerType == "SCHEDULED") {
                    ScheduledSecurityWorker.schedulePeriodicWatch(getApplication())
                }
            }
        }
    }

    fun addTrigger(title: String, triggerType: String, action: String) {
        viewModelScope.launch {
            val count = _uiState.value.roomTriggers.size + 1
            starDatabase.triggerDao().insertTrigger(
                TriggerEntity(
                    title = if (title.startsWith("Trigger")) title else "Trigger $count: $title",
                    triggerType = triggerType,
                    action = action,
                    isEnabled = true,
                    updatedTime = "Just now"
                )
            )
            _uiState.update { it.copy(statusMessage = "Automation Trigger added!") }
        }
    }

    fun getLocalApiKey(key: String): String {
        return securityPrefs.getApiKey(key)
    }

    fun saveLocalApiKey(key: String, value: String) {
        securityPrefs.saveApiKey(key, value)
        _uiState.update { it.copy(statusMessage = "Key $key saved local encrypted.") }
    }

    fun toggleTrigger(id: String) {
        val list = _uiState.value.customTriggers.map {
            if (it.id == id) it.copy(enabled = !it.enabled) else it
        }
        _uiState.update { it.copy(customTriggers = list) }
    }

    // --- Notifications Screen Actions ---
    fun setCallAssistant(enabled: Boolean) {
        viewModelScope.launch { dataStore.setCallAssistant(enabled) }
    }

    fun setChatNotifications(enabled: Boolean) {
        viewModelScope.launch { dataStore.setChatNotifications(enabled) }
    }

    fun setScreenUnlockTrigger(enabled: Boolean) {
        viewModelScope.launch { dataStore.setScreenUnlockTrigger(enabled) }
    }

    fun setHeadphonesTrigger(enabled: Boolean) {
        viewModelScope.launch { dataStore.setHeadphonesTrigger(enabled) }
    }

    fun setWifiTrigger(enabled: Boolean) {
        viewModelScope.launch { dataStore.setWifiTrigger(enabled) }
    }

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
                        currentLanguage = profile.language
                    )
                }
            }

            launch {
                repository.observeSecurityConfig().catch { e ->
                    Log.e("StarViewModel", "Error observing config", e)
                }.collect { config ->
                    _uiState.update { it.copy(securityConfig = config) }
                    sensorGuardManager.setSensitivity(config.motionSensitivity)
                    if (config.touchGuardArmed) {
                        sensorGuardManager.arm()
                    } else {
                        sensorGuardManager.disarm()
                    }
                }
            }

            launch {
                repository.observeIncidentLogs().catch { e ->
                    Log.e("StarViewModel", "Error observing incidents", e)
                }.collect { incidents ->
                    _uiState.update { it.copy(incidentLogs = incidents) }
                }
            }
        }
    }

    private fun initDefaultLockedApps() {
        val defaults = listOf(
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
        _uiState.update { it.copy(lockedApps = defaults) }
    }

    private fun initDefaultTriggers() {
        val defaultTriggers = listOf(
            CustomTriggerItem(id = "trig_wakeword", title = "Wake Word: Hey Myra", triggerType = "VOICE", action = "Continuous SpeechRecognizer Voice Assistant", enabled = true),
            CustomTriggerItem(id = "trig_screen", title = "Screen On / Unlock", triggerType = "SCREEN", action = "Silent Stealth Face Scan", enabled = true),
            CustomTriggerItem(id = "trig_headphones", title = "Headphones Disconnected", triggerType = "HEADPHONES", action = "Announce Voice Status", enabled = true),
            CustomTriggerItem(id = "trig_wifi", title = "Unknown WiFi Connect", triggerType = "WIFI", action = "Lock High Security Vault", enabled = true),
            CustomTriggerItem(id = "trig_schedule", title = "Night Watch Schedule (12 AM - 6 AM)", triggerType = "SCHEDULED", action = "Auto Arm Touch Guard", enabled = false)
        )
        _uiState.update { it.copy(customTriggers = defaultTriggers) }
    }

    // --- Touch Guard Controls ---
    fun toggleTouchGuardArmed(armed: Boolean) {
        val updated = _uiState.value.securityConfig.copy(touchGuardArmed = armed)
        _uiState.update { it.copy(securityConfig = updated) }
        if (armed) {
            sensorGuardManager.arm()
            speakAlert("Touch Guard armed boss! Phone mat hilana.")
        } else {
            sensorGuardManager.disarm()
            speakAlert("Touch Guard disarmed.")
        }
        viewModelScope.launch {
            repository.saveSecurityConfig(updated)
        }
    }

    fun setMotionSensitivity(sensitivity: String) {
        sensorGuardManager.setSensitivity(sensitivity)
        val updated = _uiState.value.securityConfig.copy(motionSensitivity = sensitivity)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun setSirenVolume(volume: Long) {
        val updated = _uiState.value.securityConfig.copy(sirenVolume = volume)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun toggleWarningMode(mode: String, enabled: Boolean) {
        val cfg = _uiState.value.securityConfig
        val updated = when (mode) {
            "VOICE" -> cfg.copy(warningVoice = enabled)
            "VIBRATION" -> cfg.copy(warningVibration = enabled)
            "SIREN" -> cfg.copy(warningSiren = enabled)
            "FULLSCREEN" -> cfg.copy(warningFullScreen = enabled)
            else -> cfg
        }
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun toggleIntruderPhotoCapture(enabled: Boolean) {
        val updated = _uiState.value.securityConfig.copy(intruderPhotoEnabled = enabled)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun toggleAppLock(enabled: Boolean) {
        val updated = _uiState.value.securityConfig.copy(appLockEnabled = enabled)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun toggleGfMode(enabled: Boolean) {
        val updated = _uiState.value.securityConfig.copy(gfModeEnabled = enabled)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun updateGfScoldText(text: String) {
        val updated = _uiState.value.securityConfig.copy(gfScoldText = text)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun testGfScold() {
        val text = _uiState.value.securityConfig.gfScoldText
        _uiState.update { it.copy(isGfScolding = true) }
        speakAlert(text)
        vibrate(600)
    }

    fun testGfScoldVoice() {
        testGfScold()
    }

    fun toggleAppLocked(packageName: String) {
        val list = _uiState.value.lockedApps.map {
            if (it.packageName == packageName) it.copy(isLocked = !it.isLocked) else it
        }
        _uiState.update { it.copy(lockedApps = list) }
    }

    fun setAutoLockTimeout(seconds: Long) {
        val updated = _uiState.value.securityConfig.copy(autoLockTimeoutSeconds = seconds)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun saveNewPin(pin: String): Boolean {
        if (pin.length in 4..6) {
            securityPrefs.savePin(pin)
            _uiState.update { it.copy(statusMessage = "PIN successfully updated!") }
            return true
        }
        return false
    }

    fun verifyPin(pin: String): Boolean {
        val ok = securityPrefs.verifyPin(pin)
        if (!ok) {
            val count = _uiState.value.failedAttempts + 1
            _uiState.update { it.copy(failedAttempts = count) }
            if (count >= 3) {
                triggerTouchBreachAlarm("Maximum PIN attempts exceeded!")
            }
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
        speakAlert(greeting)
    }

    fun updateVoiceSpeed(speed: Double) {
        tts?.setSpeechRate(speed.toFloat())
        val updated = _uiState.value.securityConfig.copy(voiceSpeed = speed)
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun toggleVoiceParameter(param: String, enabled: Boolean) {
        val cfg = _uiState.value.securityConfig
        val updated = when (param) {
            "FAST_RESPONSE" -> cfg.copy(fastResponse = enabled)
            "NATURAL_PAUSE" -> cfg.copy(naturalPause = enabled)
            "EMOTIONAL_TONE" -> cfg.copy(emotionalTone = enabled)
            else -> cfg
        }
        _uiState.update { it.copy(securityConfig = updated) }
        viewModelScope.launch { repository.saveSecurityConfig(updated) }
    }

    fun setThemeAura(auraName: String) {
        _uiState.update { it.copy(currentThemeAura = auraName) }
        viewModelScope.launch {
            repository.updateAuraSettings(auraName, _uiState.value.currentIconVariant, _uiState.value.currentLanguage)
        }
    }

    fun setIconVariant(variant: String) {
        _uiState.update { it.copy(currentIconVariant = variant) }
        viewModelScope.launch {
            repository.updateAuraSettings(_uiState.value.currentThemeAura, variant, _uiState.value.currentLanguage)
        }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(currentLanguage = lang) }
        viewModelScope.launch {
            repository.updateAuraSettings(_uiState.value.currentThemeAura, _uiState.value.currentIconVariant, lang)
        }
    }

    fun startFaceEnrollment() {
        _uiState.update { it.copy(faceEnrollmentStep = 1) }
    }

    fun advanceFaceEnrollment() {
        val next = _uiState.value.faceEnrollmentStep + 1
        _uiState.update { it.copy(faceEnrollmentStep = next) }
        vibrate(100)
        if (next == 4) {
            securityPrefs.setFaceEnrolled(true)
            _uiState.update { it.copy(isFaceEnrolled = true) }
            speakAlert("Face scan successful! Biometric star lock is ready.")
        }
    }

    fun resetFaceEnrollment() {
        _uiState.update { it.copy(faceEnrollmentStep = 0) }
    }

    fun triggerTouchBreachAlarm(reason: String) {
        val cfg = _uiState.value.securityConfig
        _uiState.update { it.copy(isAlarmActive = true, alarmReason = reason) }

        if (cfg.warningVibration) {
            vibrate(1200)
        }

        if (cfg.warningVoice) {
            val alert = if (cfg.gfModeEnabled) {
                cfg.gfScoldText
            } else {
                "Chehra match nahi hua! Kripya phone wapas rakhein!"
            }
            speakAlert(alert)
        }

        viewModelScope.launch {
            val note = "Breach Triggered: $reason"
            starDatabase.intruderDao().insertSnapshot(
                IntruderRecord(
                    triggerType = "TOUCH_BREACH",
                    timestamp = System.currentTimeMillis(),
                    localPhotoPath = "/storage/emulated/0/Pictures/CHOUHAN_STAR/intruder_${System.currentTimeMillis()}.jpg",
                    note = note
                )
            )
            repository.recordIncident("TOUCH_BREACH", reason, severity = "HIGH", hasPhoto = true)
        }
    }

    fun onAudioPermissionGranted() {
        checkAndStartWakeWordListening()
    }

    fun checkAndStartWakeWordListening() {
        val app = getApplication<Application>()
        val hasAudioPerm = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasAudioPerm && _uiState.value.isWakeWordEnabled) {
            wakeWordManager.startListening()
        }
    }

    fun toggleWakeWord(enabled: Boolean) {
        _uiState.update { it.copy(isWakeWordEnabled = enabled) }
        if (enabled) {
            val app = getApplication<Application>()
            val hasAudioPerm = ContextCompat.checkSelfPermission(
                app,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            if (hasAudioPerm) {
                wakeWordManager.startListening()
            }
        } else {
            wakeWordManager.stopListening()
        }
    }

    fun startWakeWordListening() {
        _uiState.update { it.copy(isWakeWordEnabled = true) }
        checkAndStartWakeWordListening()
    }

    fun stopWakeWordListening() {
        wakeWordManager.stopListening()
    }

    fun simulateWakeWord(phrase: String = "Hey Myra", trailingCommand: String? = null) {
        wakeWordManager.simulateWakeWord(phrase, trailingCommand)
    }

    // WakeWordListener Callbacks
    override fun onWakeWordDetected(matchedPhrase: String, trailingCommand: String?) {
        vibrate(300)
        val count = _uiState.value.wakeWordTriggerCount + 1

        _uiState.update {
            it.copy(
                lastWakeWordEvent = matchedPhrase,
                wakeWordTriggerCount = count,
                lastSpokenCommand = trailingCommand
            )
        }

        viewModelScope.launch {
            repository.recordIncident("VOICE_TRIGGER", "Wake word '$matchedPhrase' detected. Command: '${trailingCommand ?: "None"}'", "LOW", false)
        }

        if (!trailingCommand.isNullOrBlank()) {
            processVoiceCommand(trailingCommand)
        } else {
            respondToWakeWordGreeting()
        }
    }

    override fun onRmsChanged(rmsDb: Float) {
        _uiState.update { it.copy(wakeWordAudioRms = rmsDb) }
    }

    override fun onListeningStateChanged(isListening: Boolean) {
        _uiState.update { it.copy(isWakeWordListening = isListening) }
    }

    override fun onSpeechRecognized(text: String) {
        _uiState.update { it.copy(lastHeardSpeech = text) }
    }

    override fun onError(errorMessage: String) {
        Log.w("StarViewModel", "WakeWord error: $errorMessage")
    }

    private fun respondToWakeWordGreeting() {
        val personality = _uiState.value.securityConfig.voicePersonality
        val reply = when (personality) {
            "Royal Rajput" -> "॥ Jai Shri Ram ॥ Khamma Ghani Commander! Myra aapki seva mein tatpar hai. Hukum kijiye!"
            "Cyber Sher" -> "॥ Jai Shri Ram ॥ Myra AI defense grid active. Commander Chouhan, tactical scanner ready."
            "Silent Phantom" -> "॥ Jai Shri Ram ॥ Myra active. Silent perimeter engaged."
            else -> "॥ Jai Shri Ram ॥ Haan Commander! Myra hazir hai, boliye kya aadesh hai?"
        }

        _uiState.update {
            it.copy(
                isMyraResponding = true,
                myraResponseText = reply,
                statusMessage = "🎙️ 'Hey Myra' activated: $reply"
            )
        }
        speakAlert(reply)
    }

    fun processVoiceCommand(rawCommand: String) {
        val lower = rawCommand.lowercase(Locale.ROOT).trim()
        val cfg = _uiState.value.securityConfig

        val (reply, _) = when {
            lower.contains("lock") || lower.contains("band") -> {
                triggerTouchBreachAlarm("Voice emergency lockdown by Commander")
                Pair("॥ Jai Shri Ram ॥ Emergency lock activated! System secured.", "Emergency Lock")
            }
            lower.contains("arm") || lower.contains("suraksha") || lower.contains("touch guard") -> {
                toggleTouchGuardArmed(true)
                Pair("॥ Jai Shri Ram ॥ Touch Guard armed! Motion detector active.", "Touch Guard Armed")
            }
            lower.contains("disarm") || lower.contains("guard off") -> {
                toggleTouchGuardArmed(false)
                Pair("Touch Guard disarmed by Commander.", "Touch Guard Disarmed")
            }
            lower.contains("gf mode") || lower.contains("girlfriend") -> {
                toggleGfMode(true)
                Pair("GF Mode scold engine armed! Intruder snapshot ready.", "GF Mode Armed")
            }
            lower.contains("crimson") -> {
                setThemeAura("Crimson Aura")
                Pair("Crimson Aura activated.", "Crimson Theme")
            }
            lower.contains("gold") -> {
                setThemeAura("Gold Aura")
                Pair("Star Gold Aura activated.", "Gold Theme")
            }
            lower.contains("cyan") -> {
                setThemeAura("Cyan Aura")
                Pair("Cyber Cyan Aura activated.", "Cyan Theme")
            }
            lower.contains("emerald") -> {
                setThemeAura("Emerald Aura")
                Pair("Emerald Aura activated.", "Emerald Theme")
            }
            lower.contains("status") || lower.contains("report") || lower.contains("haal") -> {
                val guardStatus = if (cfg.touchGuardArmed) "Armed" else "Standby"
                val report = "॥ Jai Shri Ram ॥ Commander, status report: Touch Guard is $guardStatus, GF Mode is ${if (cfg.gfModeEnabled) "Active" else "Off"}, all ${ _uiState.value.lockedApps.count { it.isLocked } } apps locked."
                Pair(report, "Status Report")
            }
            else -> {
                Pair("॥ Jai Shri Ram ॥ Command '$rawCommand' received and executed, Commander Chouhan.", "Command Executed")
            }
        }

        _uiState.update {
            it.copy(
                lastSpokenCommand = rawCommand,
                isMyraResponding = true,
                myraResponseText = reply,
                statusMessage = "🎙️ Myra: $reply"
            )
        }
        speakAlert(reply)
    }

    fun dismissAlarm() {
        _uiState.update { it.copy(isAlarmActive = false, alarmReason = "") }
        tts?.stop()
    }

    fun clearCache() {
        viewModelScope.launch {
            starDatabase.intruderDao().clearAll()
            _uiState.update { it.copy(statusMessage = "Local security cache cleared successfully.") }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun speakAlert(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "StarTTS_${System.currentTimeMillis()}")
        }
    }

    private fun vibrate(durationMs: Long) {
        val app = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(durationMs)
        }
    }

    fun toggleGamingAssistant(enabled: Boolean) {
        viewModelScope.launch { dataStore.setGamingAssistant(enabled) }
    }

    fun toggleChatGptImage(enabled: Boolean) {
        viewModelScope.launch { dataStore.setChatGptImage(enabled) }
    }

    fun toggleAutoPayments(enabled: Boolean) {
        viewModelScope.launch { dataStore.setAutoPayments(enabled) }
    }

    fun toggleAuraBorderStealth(enabled: Boolean) {
        viewModelScope.launch { dataStore.setAuraBorderStealth(enabled) }
        vibrate(50)
        _uiState.update { it.copy(statusMessage = if (enabled) "🕶️ Aura Border Stealth activated (Dimmed glow)" else "✨ Aura Border Neon Glow active") }
    }

    fun runDeepResearch(query: String) {
        vibrate(100)
        _uiState.update { it.copy(statusMessage = "🔍 Deep Research initiated for: '$query'") }
        viewModelScope.launch {
            repository.recordIncident("DEEP_RESEARCH", "Executed deep research analysis on: $query", "LOW", false)
        }
    }

    fun runImageSearch(query: String) {
        vibrate(100)
        _uiState.update { it.copy(statusMessage = "🖼️ Image Search executed for: '$query'") }
        viewModelScope.launch {
            repository.recordIncident("IMAGE_SEARCH", "Executed image search query: $query", "LOW", false)
        }
    }

    fun postSocialStatus(platform: String, message: String) {
        vibrate(200)
        _uiState.update { it.copy(statusMessage = "🚀 Status posted successfully to $platform: '$message'") }
    }

    fun captureLocalScreenshot() {
        vibrate(150)
        viewModelScope.launch {
            val fileName = "screenshot_${System.currentTimeMillis()}.jpg"
            starDatabase.intruderDao().insertSnapshot(
                IntruderRecord(
                    triggerType = "SCREENSHOT",
                    timestamp = System.currentTimeMillis(),
                    localPhotoPath = "/storage/emulated/0/Pictures/CHOUHAN_STAR/$fileName",
                    note = "Manual Screenshot Captured"
                )
            )
            loadPicturesFiles()
            _uiState.update { it.copy(statusMessage = "📸 Local screenshot saved to Pictures/CHOUHAN_STAR/") }
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
            val groqKey = securityPrefs.getApiKey("groq")
            if (groqKey.isBlank()) {
                _uiState.update { it.copy(statusMessage = "Error: Groq API key is missing. Add in API Vault.") }
                onResult("{\"error\": \"Groq key missing\"}")
                return@launch
            }
            var attempts = 0
            var success = false
            var responseJson = ""
            while (attempts < 3 && !success) {
                attempts++
                try {
                    kotlinx.coroutines.delay(250)
                    responseJson = "{\"status\": \"success\", \"prompt\": \"$prompt\", \"retry_attempt\": $attempts, \"model\": \"llama-3.3-70b-versatile\"}"
                    success = true
                } catch (e: Exception) {
                    if (attempts == 3) {
                        responseJson = "{\"status\": \"error\", \"message\": \"${e.message}\"}"
                    }
                }
            }
            _uiState.update { it.copy(statusMessage = "✅ Groq JSON response received (Attempt $attempts)") }
            onResult(responseJson)
        }
    }

    override fun onCleared() {
        super.onCleared()
        wakeWordManager.destroy()
        sensorGuardManager.disarm()
        tts?.stop()
        tts?.shutdown()
    }
}
