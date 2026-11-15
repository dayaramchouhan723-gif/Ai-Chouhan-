package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.datastore.StarPreferencesDataStore
import com.example.data.local.StarDatabase
import com.example.data.local.VaultItemEntity
import com.example.data.model.AvatarEmotionState
import com.example.data.model.SanamCatalog
import com.example.data.security.SecurityPreferences
import com.example.service.BackupService
import com.example.service.BrainService
import com.example.service.ControlService
import com.example.service.MemoryService
import com.example.service.TeacherService
import com.example.service.VaultService
import com.example.service.VoiceService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ChouhanStarBackendRobolectricTest {

    private lateinit var context: Context
    private lateinit var database: StarDatabase
    private lateinit var securityPrefs: SecurityPreferences
    private lateinit var dataStore: StarPreferencesDataStore

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, StarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        securityPrefs = SecurityPreferences(context)
        securityPrefs.clearAllSecrets()
        dataStore = StarPreferencesDataStore(context)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `test 32 voices catalog and VoiceService emotion analyzer`() {
        assertTrue(SanamCatalog.thirtyTwoVoices.size >= 32)
        assertTrue(SanamCatalog.thirtyTwoVoices.any { it.group == "Hindi Female" })
        assertTrue(SanamCatalog.thirtyTwoVoices.any { it.group == "Urdu Female" })
        assertTrue(SanamCatalog.thirtyTwoVoices.any { it.group == "Sindhi" })
        assertTrue(SanamCatalog.thirtyTwoVoices.any { it.group == "Dhatki" })
        assertEquals(16, SanamCatalog.avatarGalleryItems.size)

        assertEquals(
            AvatarEmotionState.SHY_BLUSH,
            VoiceService.detectEmotionForLiveHumanEngine("I love you ZARA")
        )
        assertEquals(
            AvatarEmotionState.ROAST_ANGRY,
            VoiceService.detectEmotionForLiveHumanEngine("Roast karo mujhe")
        )
        assertEquals(
            AvatarEmotionState.SAD_CARING,
            VoiceService.detectEmotionForLiveHumanEngine("Bahut udaas hoon aaj")
        )
        assertEquals(
            AvatarEmotionState.WAVING,
            VoiceService.detectEmotionForLiveHumanEngine("Hey Sanam suno")
        )
    }

    @Test
    fun `test BrainService offline intent JSON fallback and zero forbidden brand strings`() = runBlocking {
        val brain = BrainService(context, securityPrefs)
        val result = brain.generateAssistantReply(
            prompt = "I love you ZARA",
            agentId = "zara",
            languagePref = "Auto Hinglish",
            moodTheme = "Romantic Pink"
        )
        assertTrue(result.isOfflineFallback)
        assertTrue(result.text.isNotBlank())
        assertFalse(result.text.contains("Myra", ignoreCase = true))
        assertFalse(result.text.contains("Iris", ignoreCase = true))

        val connOffline = brain.testConnection("offline", "")
        assertTrue(connOffline.first)
        assertTrue(connOffline.second.startsWith("GREEN"))
    }

    @Test
    fun `test VaultService AES-256 roundtrip and 3 attempt 30s lockout`() = runBlocking {
        val vault = VaultService(database, securityPrefs)
        val secretPlain = "Dayaram-Sovereign-Master-Key-228"
        val encrypted = securityPrefs.encryptText(secretPlain)
        assertNotEquals(secretPlain, encrypted)
        assertEquals(secretPlain, vault.decryptItem(VaultItemEntity(folder = "Notes", title = "T", encryptedPayload = encrypted)))

        val a1 = vault.recordFailedPinAttempt(0, nowMs = 1_000L)
        assertEquals(1 to 0, a1)
        val a2 = vault.recordFailedPinAttempt(a1.first, nowMs = 2_000L)
        assertEquals(2 to 0, a2)
        val a3 = vault.recordFailedPinAttempt(a2.first, nowMs = 3_000L)
        assertEquals(0 to 30, a3)
        assertEquals(30, vault.getRemainingLockoutSeconds(nowMs = 3_000L))

        vault.clearLockout()
        assertEquals(0, vault.getRemainingLockoutSeconds(nowMs = 4_000L))
    }

    @Test
    fun `test MemoryService and BackupService JSON export clear and import roundtrip`() = runBlocking {
        val memoryService = MemoryService(context, database, dataStore)
        val backupService = BackupService(memoryService)

        memoryService.saveNote("TODO", "Ship CHOUHAN STAR v2.2.8", "Verify 32 voices & Voice Focus")
        memoryService.saveNote("BIRTHDAY", "Commander Celebration", "14 Nov")

        val json = backupService.createJsonBackup("CHOUHAN STAR", "Kotlin, Compose")
        assertTrue(json.contains("Ship CHOUHAN STAR v2.2.8"))
        assertTrue(json.contains("2.2.8"))

        memoryService.clearMemoryOnly()
        assertEquals(0, database.memoryDao().getAllMemoryOnce().size)

        val restored = backupService.restoreJsonBackup(json)
        assertTrue(restored >= 2)
        val itemsAfterRestore = database.memoryDao().getAllMemoryOnce()
        assertTrue(itemsAfterRestore.any { it.title == "Ship CHOUHAN STAR v2.2.8" })
    }

    @Test
    fun `test TeacherService 5 of 5 perfect quiz evaluation`() {
        val teacher = TeacherService()
        val questions = teacher.getQuestions()
        assertEquals(5, questions.size)

        val allCorrect = questions.associate { it.id to it.correctIndex }
        val (score, msg) = teacher.evaluateQuiz(allCorrect)
        assertEquals(5, score)
        assertTrue(msg.contains("5/5"))
    }

    @Test
    fun `test ControlService Safety Confirmation detector gates PAY SEND DELETE`() {
        val control = ControlService(context)

        val payReq = control.detectSensitiveConfirmation("Pay 500 UPI to Rahul")
        assertNotNull(payReq)
        assertEquals("PAY", payReq?.actionType)

        val sendReq = control.detectSensitiveConfirmation("Send email to manager")
        assertNotNull(sendReq)
        assertEquals("SEND", sendReq?.actionType)

        val delReq = control.detectSensitiveConfirmation("Delete all chat notes")
        assertNotNull(delReq)
        assertEquals("DELETE", delReq?.actionType)

        val safeQuery = control.detectSensitiveConfirmation("Aaj ka mausam kaisa hai")
        assertNull(safeQuery)
    }

    @Test
    fun `test AvatarGallery 4x4 grid has Neon agents and DataStore persists UI personality`() = runBlocking {
        assertEquals(16, SanamCatalog.avatarGalleryItems.size)
        val neonBlue = SanamCatalog.avatarGalleryItems.find { it.id == "neon_blue" }
        val neonPurple = SanamCatalog.avatarGalleryItems.find { it.id == "neon_purple_hips" }
        assertNotNull(neonBlue)
        assertNotNull(neonPurple)
        assertEquals("Neon Blue", neonBlue?.title)
        assertEquals("Neon Purple", neonPurple?.title)

        // Simulate selecting Neon Purple in AvatarGallery and reading back via fresh DataStore wrapper
        dataStore.setSelectedAvatarId(neonPurple!!.id)
        dataStore.setActiveAgentId(neonPurple.agentId)
        dataStore.setMoodTheme(neonPurple.moodTheme)

        val reloadedDataStore = StarPreferencesDataStore(context)
        assertEquals("neon_purple_hips", reloadedDataStore.selectedAvatarIdFlow.first())
        assertEquals("ultron", reloadedDataStore.activeAgentIdFlow.first())
        assertEquals("JEALOUS Purple", reloadedDataStore.moodThemeFlow.first())
    }
}
