package com.example

import com.example.data.model.SanamCatalog
import com.example.util.SystemHardwareController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SanamCatalogUnitTest {

    @Test
    fun `verify catalog counts match spec`() {
        assertEquals(14, SanamCatalog.fourteenAgents.size)
        assertEquals(24, SanamCatalog.twentyFourTools.size)
        assertEquals(5, SanamCatalog.aiConnectors.size)
        assertEquals(4, SanamCatalog.systemModes.size)
        assertEquals(10, SanamCatalog.moodThemes.size)
        assertEquals(3, SanamCatalog.coreThemes.size)
        assertEquals(12, SanamCatalog.phone12Grid.size)
        assertEquals(12, SanamCatalog.pc12Grid.size)
        assertEquals(9, SanamCatalog.os9Grid.size)
        assertEquals(9, SanamCatalog.go9Grid.size)
        assertEquals(10, SanamCatalog.ghost10Grid.size)
        assertEquals(12, SanamCatalog.umiExpressions.size)
        assertEquals(9, SanamCatalog.marketplaceItems.size)
        assertEquals(12, SanamCatalog.face12HudGrid.size)
    }

    @Test
    fun `verify multi language auto detection`() {
        assertEquals("Hinglish", SystemHardwareController.detectLanguageFromText("Hey Sanam kya haal hai", "Auto Hinglish"))
        assertEquals("Hindi", SystemHardwareController.detectLanguageFromText("नमस्ते ज़ारा", "Auto Hinglish"))
        assertEquals("Urdu", SystemHardwareController.detectLanguageFromText("سلام جناب", "Auto Hinglish"))
        assertEquals("Roman Urdu", SystemHardwareController.detectLanguageFromText("Janab shukriya zaroor", "Auto Hinglish"))
        assertEquals("Sindhi", SystemHardwareController.detectLanguageFromText("Keendo aahe tawhan", "Auto Hinglish"))
        assertEquals("Dhatki", SystemHardwareController.detectLanguageFromText("Khamma ghani tharo", "Auto Hinglish"))
        assertEquals("Punjabi", SystemHardwareController.detectLanguageFromText("Ki haal tussi changa", "Auto Hinglish"))
        assertTrue(SanamCatalog.supportedLanguages.size >= 8)
    }
}
