package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

class SecurityPreferences(private val context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "chouhan_star_vault",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.w("SecurityPreferences", "EncryptedSharedPreferences fallback: ${e.message}")
        context.getSharedPreferences("chouhan_star_vault_fallback", Context.MODE_PRIVATE)
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val saved = prefs.getString(KEY_PIN_HASH, null) ?: return pin == "1234" // default pin
        return saved == hashPin(pin)
    }

    fun isPinConfigured(): Boolean {
        return prefs.contains(KEY_PIN_HASH)
    }

    fun saveApiKey(service: String, key: String) {
        prefs.edit().putString(KEY_API_PREFIX + service, key).apply()
    }

    fun getApiKey(service: String): String {
        return prefs.getString(KEY_API_PREFIX + service, "") ?: ""
    }

    fun setFaceEnrolled(enrolled: Boolean) {
        prefs.edit().putBoolean(KEY_FACE_ENROLLED, enrolled).apply()
    }

    fun isFaceEnrolled(): Boolean {
        return prefs.getBoolean(KEY_FACE_ENROLLED, false)
    }

    companion object {
        private const val KEY_PIN_HASH = "star_pin_hash"
        private const val KEY_API_PREFIX = "api_key_"
        private const val KEY_FACE_ENROLLED = "face_enrolled"
    }
}
