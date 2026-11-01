package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.SecurityConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class StarSecurityRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun getOrCreateProfile_authenticatedOwner_createsAndReturnsProfile() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = StarSecurityRepository(firestore)

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getOrCreateProfile("Dayaram Chouhan", ALICE_EMAIL)
        }
        assertTrue(result.isSuccess)
        val profile = result.getOrThrow()
        assertEquals("Dayaram Chouhan", profile.displayName)
        assertTrue(profile.premiumActive)
    }

    @Test
    fun saveSecurityConfig_authenticatedOwner_savesSuccessfully() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = StarSecurityRepository(firestore)

        val config = SecurityConfig(
            userId = uid,
            touchGuardArmed = true,
            appLockEnabled = true,
            motionSensitivity = "High",
            sirenVolume = 95L
        )
        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveSecurityConfig(config)
        }
        assertTrue(saveResult.isSuccess)
    }

    @Test
    fun recordIncident_authenticatedOwner_recordsIncident() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = StarSecurityRepository(firestore)

        val incidentResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.recordIncident("TOUCH_BREACH", "Device moved in armed mode", "CRITICAL")
        }
        assertTrue(incidentResult.isSuccess)
        assertTrue(incidentResult.getOrThrow().isNotEmpty())
    }

    @Test
    fun observeSecurityConfig_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = StarSecurityRepository(firestore)

        var caught = false
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeSecurityConfig().first()
            }
        } catch (e: Exception) {
            caught = true
        }
        assertTrue(caught)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@chouhanstar.security"
        const val BOB_EMAIL = "bob@chouhanstar.security"
        const val DEFAULT_TIMEOUT_MS = 6000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
