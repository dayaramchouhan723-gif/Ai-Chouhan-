package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.starDataStore: DataStore<Preferences> by preferencesDataStore(name = "star_toggle_prefs")

class StarPreferencesDataStore(private val context: Context) {

    companion object {
        val KEY_CALL_ASSISTANT = booleanPreferencesKey("call_assistant")
        val KEY_CHAT_NOTIFICATIONS = booleanPreferencesKey("chat_notifications")
        val KEY_SCREEN_UNLOCK_TRIGGER = booleanPreferencesKey("screen_unlock_trigger")
        val KEY_HEADPHONES_TRIGGER = booleanPreferencesKey("headphones_trigger")
        val KEY_WIFI_TRIGGER = booleanPreferencesKey("wifi_trigger")
        val KEY_WHATSAPP_CONNECTOR = booleanPreferencesKey("whatsapp_connector")
        val KEY_GMAIL_CONNECTOR = booleanPreferencesKey("gmail_connector")
        val KEY_CALENDAR_CONNECTOR = booleanPreferencesKey("calendar_connector")
        val KEY_GAMING_ASSISTANT = booleanPreferencesKey("gaming_assistant")
        val KEY_CHATGPT_IMAGE = booleanPreferencesKey("chatgpt_image")
        val KEY_AUTO_PAYMENTS = booleanPreferencesKey("auto_payments")
        val KEY_AURA_BORDER_STEALTH = booleanPreferencesKey("aura_border_stealth")

        // CHOUHAN STAR v2.2.8 Preferences
        val KEY_CUSTOM_APP_NAME = stringPreferencesKey("custom_app_name")
        val KEY_CORE_THEME_MODE = stringPreferencesKey("core_theme_mode")
        val KEY_MOOD_THEME = stringPreferencesKey("mood_theme")
        val KEY_ACTIVE_AGENT_ID = stringPreferencesKey("active_agent_id")
        val KEY_SELECTED_AVATAR_ID = stringPreferencesKey("selected_avatar_id")
        val KEY_LANGUAGE = stringPreferencesKey("selected_language")
        val KEY_SYSTEM_MODE = stringPreferencesKey("system_mode")
        val KEY_ACCESSIBILITY_LARGE_FONT = booleanPreferencesKey("accessibility_large_font")
        val KEY_VOICE_ONLY_MODE = booleanPreferencesKey("voice_only_mode")
        val KEY_HIDDEN_CALC_MODE = booleanPreferencesKey("hidden_calculator_mode")
        val KEY_FINGERPRINT_LOCK = booleanPreferencesKey("fingerprint_lock_enabled")
        val KEY_SKIPPED_LOGIN = booleanPreferencesKey("skipped_login")

        // Backend Brain, Voice Focus & Memory Controls
        val KEY_ALWAYS_LISTENING = booleanPreferencesKey("always_listening_enabled")
        val KEY_VOICE_FOCUS_MODE = booleanPreferencesKey("voice_focus_mode_enabled")
        val KEY_VOICE_PITCH = floatPreferencesKey("voice_pitch")
        val KEY_VOICE_RATE = floatPreferencesKey("voice_rate")
        val KEY_SELECTED_VOICE_ID = stringPreferencesKey("selected_voice_id")
        val KEY_POCKET_TTS_ENGINE = stringPreferencesKey("pocket_tts_engine")
        val KEY_OWNER_LIKES = stringPreferencesKey("owner_likes")
        val KEY_OWNER_MOOD = stringPreferencesKey("owner_mood")
    }

    val callAssistantFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_CALL_ASSISTANT] ?: true }
    val chatNotificationsFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_CHAT_NOTIFICATIONS] ?: true }
    val screenUnlockTriggerFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_SCREEN_UNLOCK_TRIGGER] ?: true }
    val headphonesTriggerFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_HEADPHONES_TRIGGER] ?: false }
    val wifiTriggerFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_WIFI_TRIGGER] ?: true }
    val whatsappConnectorFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_WHATSAPP_CONNECTOR] ?: true }
    val gmailConnectorFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_GMAIL_CONNECTOR] ?: false }
    val calendarConnectorFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_CALENDAR_CONNECTOR] ?: true }
    val gamingAssistantFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_GAMING_ASSISTANT] ?: false }
    val chatGptImageFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_CHATGPT_IMAGE] ?: false }
    val autoPaymentsFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_AUTO_PAYMENTS] ?: false }
    val auraBorderStealthFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_AURA_BORDER_STEALTH] ?: false }

    val customAppNameFlow: Flow<String> = context.starDataStore.data.map { it[KEY_CUSTOM_APP_NAME] ?: "CHOUHAN STAR" }
    val coreThemeModeFlow: Flow<String> = context.starDataStore.data.map { it[KEY_CORE_THEME_MODE] ?: "AMOLED" }
    val moodThemeFlow: Flow<String> = context.starDataStore.data.map { it[KEY_MOOD_THEME] ?: "Caring Blue" }
    val activeAgentIdFlow: Flow<String> = context.starDataStore.data.map { it[KEY_ACTIVE_AGENT_ID] ?: "zara" }
    val selectedAvatarIdFlow: Flow<String> = context.starDataStore.data.map { it[KEY_SELECTED_AVATAR_ID] ?: "zara_open" }
    val languageFlow: Flow<String> = context.starDataStore.data.map { it[KEY_LANGUAGE] ?: "Auto Hinglish" }
    val systemModeFlow: Flow<String> = context.starDataStore.data.map { it[KEY_SYSTEM_MODE] ?: "Assistant Pro" }
    val largeFontFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_ACCESSIBILITY_LARGE_FONT] ?: false }
    val voiceOnlyModeFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_VOICE_ONLY_MODE] ?: false }
    val hiddenCalcModeFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_HIDDEN_CALC_MODE] ?: false }
    val fingerprintLockFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_FINGERPRINT_LOCK] ?: true }
    val skippedLoginFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_SKIPPED_LOGIN] ?: false }

    // Always listening OFF by default; Voice Focus ON by default; Hindi Female Pitch 1.2 Rate 0.9
    val alwaysListeningFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_ALWAYS_LISTENING] ?: false }
    val voiceFocusModeFlow: Flow<Boolean> = context.starDataStore.data.map { it[KEY_VOICE_FOCUS_MODE] ?: true }
    val voicePitchFlow: Flow<Float> = context.starDataStore.data.map { it[KEY_VOICE_PITCH] ?: 1.2f }
    val voiceRateFlow: Flow<Float> = context.starDataStore.data.map { it[KEY_VOICE_RATE] ?: 0.9f }
    val selectedVoiceIdFlow: Flow<String> = context.starDataStore.data.map { it[KEY_SELECTED_VOICE_ID] ?: "hi_f_1" }
    val pocketTtsEngineFlow: Flow<String> = context.starDataStore.data.map { it[KEY_POCKET_TTS_ENGINE] ?: "Pocket TTS-Hindi" }
    val ownerLikesFlow: Flow<String> = context.starDataStore.data.map { it[KEY_OWNER_LIKES] ?: "Kotlin, Compose, AI Security, Chai" }
    val ownerMoodFlow: Flow<String> = context.starDataStore.data.map { it[KEY_OWNER_MOOD] ?: "Focused" }

    suspend fun setCallAssistant(enabled: Boolean) { context.starDataStore.edit { it[KEY_CALL_ASSISTANT] = enabled } }
    suspend fun setChatNotifications(enabled: Boolean) { context.starDataStore.edit { it[KEY_CHAT_NOTIFICATIONS] = enabled } }
    suspend fun setScreenUnlockTrigger(enabled: Boolean) { context.starDataStore.edit { it[KEY_SCREEN_UNLOCK_TRIGGER] = enabled } }
    suspend fun setHeadphonesTrigger(enabled: Boolean) { context.starDataStore.edit { it[KEY_HEADPHONES_TRIGGER] = enabled } }
    suspend fun setWifiTrigger(enabled: Boolean) { context.starDataStore.edit { it[KEY_WIFI_TRIGGER] = enabled } }
    suspend fun setWhatsappConnector(enabled: Boolean) { context.starDataStore.edit { it[KEY_WHATSAPP_CONNECTOR] = enabled } }
    suspend fun setGmailConnector(enabled: Boolean) { context.starDataStore.edit { it[KEY_GMAIL_CONNECTOR] = enabled } }
    suspend fun setCalendarConnector(enabled: Boolean) { context.starDataStore.edit { it[KEY_CALENDAR_CONNECTOR] = enabled } }
    suspend fun setGamingAssistant(enabled: Boolean) { context.starDataStore.edit { it[KEY_GAMING_ASSISTANT] = enabled } }
    suspend fun setChatGptImage(enabled: Boolean) { context.starDataStore.edit { it[KEY_CHATGPT_IMAGE] = enabled } }
    suspend fun setAutoPayments(enabled: Boolean) { context.starDataStore.edit { it[KEY_AUTO_PAYMENTS] = enabled } }
    suspend fun setAuraBorderStealth(enabled: Boolean) { context.starDataStore.edit { it[KEY_AURA_BORDER_STEALTH] = enabled } }

    suspend fun setCustomAppName(name: String) { context.starDataStore.edit { it[KEY_CUSTOM_APP_NAME] = name.ifBlank { "CHOUHAN STAR" } } }
    suspend fun setCoreThemeMode(mode: String) { context.starDataStore.edit { it[KEY_CORE_THEME_MODE] = mode } }
    suspend fun setMoodTheme(mood: String) { context.starDataStore.edit { it[KEY_MOOD_THEME] = mood } }
    suspend fun setActiveAgentId(agentId: String) { context.starDataStore.edit { it[KEY_ACTIVE_AGENT_ID] = agentId } }
    suspend fun setSelectedAvatarId(avatarId: String) { context.starDataStore.edit { it[KEY_SELECTED_AVATAR_ID] = avatarId } }
    suspend fun setLanguage(lang: String) { context.starDataStore.edit { it[KEY_LANGUAGE] = lang } }
    suspend fun setSystemMode(mode: String) { context.starDataStore.edit { it[KEY_SYSTEM_MODE] = mode } }
    suspend fun setLargeFont(enabled: Boolean) { context.starDataStore.edit { it[KEY_ACCESSIBILITY_LARGE_FONT] = enabled } }
    suspend fun setVoiceOnlyMode(enabled: Boolean) { context.starDataStore.edit { it[KEY_VOICE_ONLY_MODE] = enabled } }
    suspend fun setHiddenCalcMode(enabled: Boolean) { context.starDataStore.edit { it[KEY_HIDDEN_CALC_MODE] = enabled } }
    suspend fun setFingerprintLock(enabled: Boolean) { context.starDataStore.edit { it[KEY_FINGERPRINT_LOCK] = enabled } }
    suspend fun setSkippedLogin(skipped: Boolean) { context.starDataStore.edit { it[KEY_SKIPPED_LOGIN] = skipped } }

    suspend fun setAlwaysListening(enabled: Boolean) { context.starDataStore.edit { it[KEY_ALWAYS_LISTENING] = enabled } }
    suspend fun setVoiceFocusMode(enabled: Boolean) { context.starDataStore.edit { it[KEY_VOICE_FOCUS_MODE] = enabled } }
    suspend fun setVoicePitch(pitch: Float) { context.starDataStore.edit { it[KEY_VOICE_PITCH] = pitch.coerceIn(0.5f, 2.0f) } }
    suspend fun setVoiceRate(rate: Float) { context.starDataStore.edit { it[KEY_VOICE_RATE] = rate.coerceIn(0.5f, 2.0f) } }
    suspend fun setSelectedVoiceId(voiceId: String) { context.starDataStore.edit { it[KEY_SELECTED_VOICE_ID] = voiceId } }
    suspend fun setPocketTtsEngine(engine: String) { context.starDataStore.edit { it[KEY_POCKET_TTS_ENGINE] = engine } }
    suspend fun setOwnerLikes(likes: String) { context.starDataStore.edit { it[KEY_OWNER_LIKES] = likes } }
    suspend fun setOwnerMood(mood: String) { context.starDataStore.edit { it[KEY_OWNER_MOOD] = mood } }

    suspend fun clearPersonalPreferences() {
        context.starDataStore.edit { prefs ->
            prefs.remove(KEY_OWNER_LIKES)
            prefs.remove(KEY_OWNER_MOOD)
            prefs.remove(KEY_CUSTOM_APP_NAME)
        }
    }
}
