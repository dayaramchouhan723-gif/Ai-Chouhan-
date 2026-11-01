package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    suspend fun setCallAssistant(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_CALL_ASSISTANT] = enabled }
    }

    suspend fun setChatNotifications(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_CHAT_NOTIFICATIONS] = enabled }
    }

    suspend fun setScreenUnlockTrigger(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_SCREEN_UNLOCK_TRIGGER] = enabled }
    }

    suspend fun setHeadphonesTrigger(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_HEADPHONES_TRIGGER] = enabled }
    }

    suspend fun setWifiTrigger(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_WIFI_TRIGGER] = enabled }
    }

    suspend fun setWhatsappConnector(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_WHATSAPP_CONNECTOR] = enabled }
    }

    suspend fun setGmailConnector(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_GMAIL_CONNECTOR] = enabled }
    }

    suspend fun setCalendarConnector(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_CALENDAR_CONNECTOR] = enabled }
    }

    suspend fun setGamingAssistant(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_GAMING_ASSISTANT] = enabled }
    }

    suspend fun setChatGptImage(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_CHATGPT_IMAGE] = enabled }
    }

    suspend fun setAutoPayments(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_AUTO_PAYMENTS] = enabled }
    }

    suspend fun setAuraBorderStealth(enabled: Boolean) {
        context.starDataStore.edit { it[KEY_AURA_BORDER_STEALTH] = enabled }
    }
}
