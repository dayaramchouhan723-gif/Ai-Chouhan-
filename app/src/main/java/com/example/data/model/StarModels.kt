package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Sovereign Commander",
    val email: String = "",
    val premiumActive: Boolean = true,
    val buildType: String = "Personal Build",
    val themeAura: String = "Crimson Aura",
    val iconVariant: String = "Crimson Wireframe Star",
    val language: String = "Auto Hinglish",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SecurityConfig(
    @DocumentId val id: String = "current",
    val userId: String = "",
    val touchGuardArmed: Boolean = false,
    val motionSensitivity: String = "Medium",
    val sirenVolume: Long = 85L,
    val warningVoice: Boolean = true,
    val warningVibration: Boolean = true,
    val warningSiren: Boolean = true,
    val warningFullScreen: Boolean = true,
    val appLockEnabled: Boolean = true,
    val gfModeEnabled: Boolean = true,
    val gfScoldText: String = "Khabardar phone chhua toh! Seedha report jayegi madam ke paas! Chupchap rakh de phone!",
    val biometricEnabled: Boolean = true,
    val autoLockTimeoutSeconds: Long = 0L,
    val voicePersonality: String = "Dabang Guardian",
    val voiceSpeed: Double = 1.0,
    val fastResponse: Boolean = true,
    val naturalPause: Boolean = true,
    val emotionalTone: Boolean = true,
    val stealthFaceLock: Boolean = true,
    val intruderPhotoEnabled: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class IncidentLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val incidentType: String = "TOUCH_GUARD",
    val details: String = "",
    val severity: String = "HIGH",
    val hasLocalPhoto: Boolean = false,
    @ServerTimestamp val timestamp: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomTriggerItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val triggerType: String = "SCREEN",
    val action: String = "",
    val enabled: Boolean = true,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class LockedAppItem(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = false,
    val category: String = "Social"
)
