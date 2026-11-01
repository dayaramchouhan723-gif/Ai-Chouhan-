package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.CustomTriggerItem
import com.example.data.model.IncidentLog
import com.example.data.model.SecurityConfig
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class StarSecurityRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth get() = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile ---
    suspend fun getOrCreateProfile(displayName: String, email: String): Result<UserProfile> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java) ?: UserProfile(userId = uid)
                Result.success(profile)
            } else {
                val newProfile = UserProfile(
                    userId = uid,
                    displayName = displayName.ifBlank { "Sovereign Commander" },
                    email = email,
                    premiumActive = true,
                    buildType = "Personal Build",
                    themeAura = "Crimson Aura",
                    iconVariant = "Crimson Wireframe Star",
                    language = "Auto Hinglish"
                )
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to newProfile.displayName,
                    "email" to newProfile.email,
                    "premiumActive" to true,
                    "buildType" to "Personal Build",
                    "themeAura" to newProfile.themeAura,
                    "iconVariant" to newProfile.iconVariant,
                    "language" to newProfile.language,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                Result.success(newProfile)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun updateAuraSettings(themeAura: String, iconVariant: String, language: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            docRef.update(
                mapOf(
                    "themeAura" to themeAura,
                    "iconVariant" to iconVariant,
                    "language" to language,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- Security Config ---
    fun observeSecurityConfig(): Flow<SecurityConfig> = flow {
        val uid = requireUserId()
        val path = "users/$uid/securityConfig/current"
        val docRef = db.collection("users").document(uid).collection("securityConfig").document("current")
        docRef.snapshots().map { snapshot ->
            if (snapshot.exists()) {
                snapshot.toObject(SecurityConfig::class.java) ?: SecurityConfig(userId = uid)
            } else {
                SecurityConfig(userId = uid)
            }
        }.catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
            throw error
        }.collect { emit(it) }
    }

    suspend fun saveSecurityConfig(config: SecurityConfig): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/securityConfig/current"
        val docRef = db.collection("users").document(uid).collection("securityConfig").document("current")
        return try {
            val snapshot = docRef.get().await()
            val payload = mapOf(
                "userId" to uid,
                "touchGuardArmed" to config.touchGuardArmed,
                "motionSensitivity" to config.motionSensitivity,
                "sirenVolume" to config.sirenVolume,
                "warningVoice" to config.warningVoice,
                "warningVibration" to config.warningVibration,
                "warningSiren" to config.warningSiren,
                "warningFullScreen" to config.warningFullScreen,
                "appLockEnabled" to config.appLockEnabled,
                "gfModeEnabled" to config.gfModeEnabled,
                "gfScoldText" to config.gfScoldText,
                "biometricEnabled" to config.biometricEnabled,
                "autoLockTimeoutSeconds" to config.autoLockTimeoutSeconds,
                "voicePersonality" to config.voicePersonality,
                "voiceSpeed" to config.voiceSpeed,
                "fastResponse" to config.fastResponse,
                "naturalPause" to config.naturalPause,
                "emotionalTone" to config.emotionalTone,
                "stealthFaceLock" to config.stealthFaceLock,
                "intruderPhotoEnabled" to config.intruderPhotoEnabled
            ).let { base ->
                if (!snapshot.exists()) {
                    base + ("createdAt" to FieldValue.serverTimestamp())
                } else {
                    base + ("updatedAt" to FieldValue.serverTimestamp())
                }
            }
            docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- Incident Logs ---
    suspend fun recordIncident(type: String, details: String, severity: String = "HIGH", hasPhoto: Boolean = false): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/incidentLogs"
        val colRef = db.collection("users").document(uid).collection("incidentLogs")
        return try {
            val payload = mapOf(
                "userId" to uid,
                "incidentType" to type,
                "details" to details,
                "severity" to severity,
                "hasLocalPhoto" to hasPhoto,
                "timestamp" to FieldValue.serverTimestamp()
            )
            val doc = colRef.add(payload).await()
            Result.success(doc.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeIncidentLogs(): Flow<List<IncidentLog>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/incidentLogs"
        val colRef = db.collection("users").document(uid).collection("incidentLogs")
        colRef.snapshots().map { snapshot ->
            snapshot.toObjects(IncidentLog::class.java)
        }.catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
            throw error
        }.collect { emit(it) }
    }

    // --- Custom Triggers ---
    suspend fun saveCustomTrigger(trigger: CustomTriggerItem): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/customTriggers"
        val colRef = db.collection("users").document(uid).collection("customTriggers")
        return try {
            val payload = mapOf(
                "userId" to uid,
                "title" to trigger.title,
                "triggerType" to trigger.triggerType,
                "action" to trigger.action,
                "enabled" to trigger.enabled,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = if (trigger.id.isNotBlank()) colRef.document(trigger.id) else colRef.document()
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeCustomTriggers(): Flow<List<CustomTriggerItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/customTriggers"
        val colRef = db.collection("users").document(uid).collection("customTriggers")
        colRef.snapshots().map { snapshot ->
            snapshot.toObjects(CustomTriggerItem::class.java)
        }.catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
            throw error
        }.collect { emit(it) }
    }
}
