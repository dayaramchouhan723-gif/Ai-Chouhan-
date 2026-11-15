package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

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

    private val vaultAesKeyBytes: ByteArray by lazy {
        val existing = prefs.getString(KEY_VAULT_SECRET_B64, null)
        if (existing != null) {
            try {
                Base64.decode(existing, Base64.NO_WRAP)
            } catch (_: Exception) {
                generateAndPersistAesKey()
            }
        } else {
            generateAndPersistAesKey()
        }
    }

    private fun generateAndPersistAesKey(): ByteArray {
        val key = ByteArray(32)
        SecureRandom().nextBytes(key)
        prefs.edit().putString(KEY_VAULT_SECRET_B64, Base64.encodeToString(key, Base64.NO_WRAP)).apply()
        return key
    }

    fun encryptText(plain: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv)
            val keySpec = SecretKeySpec(vaultAesKeyBytes, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(128, iv))
            val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            val combined = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (_: Exception) {
            Base64.encodeToString(plain.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    fun decryptText(cipherB64: String): String {
        if (cipherB64.isBlank()) return ""
        return try {
            val combined = Base64.decode(cipherB64, Base64.NO_WRAP)
            if (combined.size <= 12) return String(combined, Charsets.UTF_8)
            val iv = combined.copyOfRange(0, 12)
            val encrypted = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(vaultAesKeyBytes, "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(128, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (_: Exception) {
            try {
                String(Base64.decode(cipherB64, Base64.NO_WRAP), Charsets.UTF_8)
            } catch (_: Exception) {
                cipherB64
            }
        }
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val saved = prefs.getString(KEY_PIN_HASH, null) ?: return pin == "1234"
        return saved == hashPin(pin)
    }

    fun isPinConfigured(): Boolean = prefs.contains(KEY_PIN_HASH)

    fun saveApiKey(service: String, key: String) {
        prefs.edit().putString(KEY_API_PREFIX + service, key).apply()
    }

    fun getApiKey(service: String): String {
        return prefs.getString(KEY_API_PREFIX + service, "") ?: ""
    }

    fun setFaceEnrolled(enrolled: Boolean) {
        prefs.edit().putBoolean(KEY_FACE_ENROLLED, enrolled).apply()
    }

    fun isFaceEnrolled(): Boolean = prefs.getBoolean(KEY_FACE_ENROLLED, false)

    fun setVaultLockoutUntil(epochMs: Long) {
        prefs.edit().putLong(KEY_VAULT_LOCKOUT_UNTIL, epochMs).apply()
    }

    fun getVaultLockoutUntil(): Long = prefs.getLong(KEY_VAULT_LOCKOUT_UNTIL, 0L)

    fun setVaultAutoLockLabel(label: String) {
        prefs.edit().putString(KEY_VAULT_AUTOLOCK, label).apply()
    }

    fun getVaultAutoLockLabel(): String = prefs.getString(KEY_VAULT_AUTOLOCK, "1m") ?: "1m"

    fun setVaultBlurEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VAULT_BLUR, enabled).apply()
    }

    fun isVaultBlurEnabled(): Boolean = prefs.getBoolean(KEY_VAULT_BLUR, true)

    fun setFakeVaultMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FAKE_VAULT, enabled).apply()
    }

    fun isFakeVaultMode(): Boolean = prefs.getBoolean(KEY_FAKE_VAULT, false)

    fun clearAllSecrets() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_PIN_HASH = "star_pin_hash"
        private const val KEY_API_PREFIX = "api_key_"
        private const val KEY_FACE_ENROLLED = "face_enrolled"
        private const val KEY_VAULT_SECRET_B64 = "vault_aes_256_secret"
        private const val KEY_VAULT_LOCKOUT_UNTIL = "vault_lockout_until"
        private const val KEY_VAULT_AUTOLOCK = "vault_auto_lock_label"
        private const val KEY_VAULT_BLUR = "vault_blur_thumbnails"
        private const val KEY_FAKE_VAULT = "vault_fake_decoy_mode"
    }
}
