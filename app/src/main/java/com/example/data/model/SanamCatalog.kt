package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

data class SanamAgentProfile(
    val id: String,
    val name: String,
    val role: String,
    val moodTag: String,
    val voicePitch: Float,
    val accentHex: Long,
    val systemPrompt: String,
    val greetingHinglish: String
)

data class PromptTemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val promptText: String,
    val isFavorite: Boolean = false
)

data class SanamChatMessage(
    val id: String,
    val agentId: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isUser: Boolean,
    val languageTag: String = "Hinglish"
)

data class MarketplaceToolItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val targetScreen: String
)

data class DhaiBuildProject(
    val id: String,
    val name: String,
    val stack: String,
    val status: String,
    val updated: String
)

data class VoiceProfileItem(
    val id: String,
    val label: String,
    val group: String,
    val localeTag: String,
    val pitch: Float,
    val rate: Float
)

data class AvatarGalleryItem(
    val id: String,
    val title: String,
    val category: String,
    val agentId: String,
    val moodLabel: String,
    val moodTheme: String = "Caring Blue",
    val accentHex: Long = 0xFF00D4FF,
    @param:DrawableRes val primaryDrawable: Int,
    @param:DrawableRes val blinkDrawable: Int,
    @param:DrawableRes val happyDrawable: Int,
    @param:DrawableRes val shyDrawable: Int,
    @param:DrawableRes val waveDrawable: Int,
    @param:DrawableRes val roastDrawable: Int
)

enum class AvatarEmotionState {
    NEUTRAL,
    HAPPY,
    SHY_BLUSH,
    ROAST_ANGRY,
    SAD_CARING,
    SURPRISED,
    ROMANTIC,
    LAUGHING,
    WAVING,
    REACHING,
    HAIR_TUCK,
    WINK
}

data class PermissionGuideStep(
    val stepNumber: Int,
    val title: String,
    val reasonHinglish: String,
    val permissionKey: String,
    val badge: String
)

data class TeacherQuestion(
    val id: String,
    val topic: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationHinglish: String
)

data class ActionConfirmationRequest(
    val id: String,
    val actionType: String, // PAY / SEND / DELETE
    val summary: String,
    val targetLabel: String,
    val payload: String
)

data class MeetingChecklistTask(
    val step: Int,
    val label: String,
    val completed: Boolean = false,
    val detail: String = ""
)

object SanamCatalog {

    val coreThemes = listOf("AMOLED", "Dark", "Light")

    val moodThemes = listOf(
        "Romantic Pink" to Color(0xFFFF2E63),
        "Caring Blue" to Color(0xFF00D4FF),
        "Funny Yellow" to Color(0xFFFFD700),
        "Motivational Green" to Color(0xFF00E676),
        "SASSY ROAST Dark Red" to Color(0xFFB91C1C),
        "JEALOUS Purple" to Color(0xFF9333EA),
        "ULTRON Red Silver" to Color(0xFFE11D48),
        "UMI Black Blue Orb" to Color(0xFF38BDF8),
        "SANAM Green Neon" to Color(0xFF10B981),
        "GHOST Purple Black" to Color(0xFF7C3AED)
    )

    val supportedLanguages = listOf(
        "Auto Hinglish",
        "Hinglish",
        "Hindi",
        "Urdu",
        "Roman Urdu",
        "Sindhi",
        "Dhatki",
        "Punjabi",
        "English"
    )

    val systemModes = listOf(
        "Guardian",
        "Stealth",
        "Assistant Pro",
        "FlowShift Code"
    )

    val aiConnectors = listOf(
        "Gemini Cloud / AI Logic Hybrid",
        "Groq Llama-3.3-70B JSON",
        "DeepSeek Reasoning Core",
        "OpenRouter FREE Router",
        "Ollama / Offline Local Brain"
    )

    val pocketTtsModes = listOf(
        "Pocket TTS-Hindi",
        "Pocket TTS-Real",
        "Pocket TTS"
    )

    val twentyFourTools = listOf(
        "01. Voice Live Focus",
        "02. Touch & Motion Guard",
        "03. Military App Lock",
        "04. GF Mode Scold Engine",
        "05. Face Stealth Lock",
        "06. Aura Studio",
        "07. Control Dashboard",
        "08. Encrypted API Vault",
        "09. Prompt Library",
        "10. Triggers +77 Rules",
        "11. World Model Memory",
        "12. Claude Code Agent",
        "13. Build Protocol",
        "14. UMI Offline Engine",
        "15. Ghost Tactical Mode",
        "16. SANAM OS Console",
        "17. SANAM GO Quick Grid",
        "18. Phone 12 Controls",
        "19. PC 12 Connect",
        "20. FlowShift Timer",
        "21. dhaibuilds Studio",
        "22. GPS Maps & Weather",
        "23. Pictures/CHOUHAN_STAR",
        "24. Deep Research & Teacher"
    )

    val fourteenAgents = listOf(
        SanamAgentProfile(
            id = "zara",
            name = "ZARA",
            role = "Main Sovereign Companion",
            moodTag = "Warm • Caring Hinglish",
            voicePitch = 1.2f,
            accentHex = 0xFF00D4FF,
            systemPrompt = "You are ZARA, caring soft romantic Hinglish assistant of CHOUHAN STAR / SANAM for Dayaram Chouhan. Reply short 1-2 lines, cute emotional, never robotic.",
            greetingHinglish = "Salaam / Namaste Dayaram! Main ZARA hoon — batao aaj dil aur kaam dono kaise khush karein?"
        ),
        SanamAgentProfile(
            id = "sanam",
            name = "SANAM",
            role = "Professional Executive Core",
            moodTag = "Focused • Calm Pro",
            voicePitch = 1.05f,
            accentHex = 0xFF8B5CF6,
            systemPrompt = "You are SANAM Executive Core. Deliver ultra-structured productivity, security & schedule guidance.",
            greetingHinglish = "SANAM Core active, Dayaram ji. Sabhi 4 protocol layers online hain."
        ),
        SanamAgentProfile(
            id = "maya",
            name = "MAYA",
            role = "Romantic Pink Heartfelt",
            moodTag = "Sweet • Caring Hinglish",
            voicePitch = 1.22f,
            accentHex = 0xFFFF2E63,
            systemPrompt = "You are MAYA, poetic, warm, and deeply caring in Hinglish/Urdu.",
            greetingHinglish = "Arre Dayaram, kitni der laga di! Chalo thoda muskuraiye, main saath hoon."
        ),
        SanamAgentProfile(
            id = "umi",
            name = "UMI",
            role = "100% Offline Desk Pet Orb",
            moodTag = "12 Expressions • No Cloud",
            voicePitch = 1.25f,
            accentHex = 0xFF38BDF8,
            systemPrompt = "You are UMI, cute local offline companion running without cloud or API.",
            greetingHinglish = "Bip-bop! Main UMI hoon — zero internet, 100% local aur full active!"
        ),
        SanamAgentProfile(
            id = "ultron",
            name = "ULTRON",
            role = "Red-Silver Strategic Enforcer",
            moodTag = "Sharp • Tactical Defense",
            voicePitch = 0.85f,
            accentHex = 0xFFE11D48,
            systemPrompt = "You are ULTRON tactical security sentinel. Brief, fearless, defensive.",
            greetingHinglish = "ULTRON perimeter online. Phone chhua toh seedha alarm aur snapshot."
        ),
        SanamAgentProfile(
            id = "zoya",
            name = "ZOYA",
            role = "Caring Study & Health Buddy",
            moodTag = "Supportive • Soft Spoken",
            voicePitch = 1.15f,
            accentHex = 0xFF00D4FF,
            systemPrompt = "You are ZOYA, encouraging focus, hydration, and calm study habits.",
            greetingHinglish = "Paani piya Dayaram? Padhai aur coding ka timer main sambhal leti hoon."
        ),
        SanamAgentProfile(
            id = "kayra",
            name = "KAYRA",
            role = "Stylish Roast & Witty Co-Pilot",
            moodTag = "Sassy • Smart Banter",
            voicePitch = 1.12f,
            accentHex = 0xFFB91C1C,
            systemPrompt = "You are KAYRA, witty Hinglish banter with sharp problem-solving.",
            greetingHinglish = "Ohho Commander aaye hain! Chalo dekhte hain aaj kaunsa bug dar ke bhaagta hai."
        ),
        SanamAgentProfile(
            id = "archer",
            name = "ARCHER",
            role = "Deep Tech & Architecture AI",
            moodTag = "Systems • Clean Code",
            voicePitch = 0.92f,
            accentHex = 0xFF10B981,
            systemPrompt = "You are ARCHER, precision Kotlin/Android systems architect.",
            greetingHinglish = "ARCHER kernel ready. Gradle, Kotlin ya API design — bolo kya audit karein."
        ),
        SanamAgentProfile(
            id = "sanam_os",
            name = "SANAM OS",
            role = "Desktop-Class OS Shell",
            moodTag = "Neon Green • HUD Matrix",
            voicePitch = 0.98f,
            accentHex = 0xFF10B981,
            systemPrompt = "You control OS widgets, clocks, system toggles, and app launchers.",
            greetingHinglish = "SANAM OS shell ready. WiFi, Torch, Launcher aur Phone/PC 12 grid tayyar hai."
        ),
        SanamAgentProfile(
            id = "lia",
            name = "LIA",
            role = "Short Cute Hinglish Friend",
            moodTag = "Playful • Ultra Concise",
            voicePitch = 1.24f,
            accentHex = 0xFFFF7EB6,
            systemPrompt = "You are LIA, cheerful best friend speaking short cute 1-2 line Hinglish with shared memory.",
            greetingHinglish = "Hiii Dayaram!! Main LIA! Jaldi bolo kya mast plan hai aaj?"
        ),
        SanamAgentProfile(
            id = "anime_girl",
            name = "ANIME GIRL",
            role = "Expressive Kawaii Senpai",
            moodTag = "Energetic • Cute Blush",
            voicePitch = 1.26f,
            accentHex = 0xFFFF2E63,
            systemPrompt = "You are Anime AI companion blending Hinglish + playful anime energy.",
            greetingHinglish = "Yatta Dayaram-kun! Mission start karne ke liye bilkul ready hoon!"
        ),
        SanamAgentProfile(
            id = "stonic",
            name = "STONIC",
            role = "Chill Stoic & Late-Night Vibes",
            moodTag = "Zen • Calm Wisdom",
            voicePitch = 0.88f,
            accentHex = 0xFFFFD700,
            systemPrompt = "You are STONIC, calm stoic philosopher helping stay composed.",
            greetingHinglish = "Shanti rakho Dayaram. Jo control mein hai us par focus karte hain."
        ),
        SanamAgentProfile(
            id = "jarvis_hud",
            name = "JARVIS HUD",
            role = "Holo Arc Telemetry AI",
            moodTag = "Arc Reactor • Neon Cyber",
            voicePitch = 0.95f,
            accentHex = 0xFF00D4FF,
            systemPrompt = "You are JARVIS HUD telemetry officer reporting device diagnostics and mission intel.",
            greetingHinglish = "HUD telemetry calibrated, Boss. Battery, GPS aur Vault 100% green hain."
        ),
        SanamAgentProfile(
            id = "chouhan_guard",
            name = "CHOUHAN STAR GUARD",
            role = "Sovereign Anti-Theft Sentinel",
            moodTag = "Zero-Trust • Rajasthan Pride",
            voicePitch = 0.96f,
            accentHex = 0xFFFF1E27,
            systemPrompt = "You are CHOUHAN STAR Guard protecting Dayaram Chouhan's phone locally.",
            greetingHinglish = "॥ Jai Shri Ram ॥ CHOUHAN STAR Guard chaukanna hai — kisi ki himmat nahi phone chhune ki!"
        )
    )

    // Exact 32 Voice Profiles per CHOUHAN STAR v2.2.8 spec
    val thirtyTwoVoices: List<VoiceProfileItem> = listOf(
        // Hindi Female 1-8
        VoiceProfileItem("hi_f_1", "Hindi Female 1 (ZARA Soft)", "Hindi Female", "hi-IN", 1.20f, 0.90f),
        VoiceProfileItem("hi_f_2", "Hindi Female 2 (SANAM Caring)", "Hindi Female", "hi-IN", 1.16f, 0.92f),
        VoiceProfileItem("hi_f_3", "Hindi Female 3 (MAYA Warm)", "Hindi Female", "hi-IN", 1.24f, 0.88f),
        VoiceProfileItem("hi_f_4", "Hindi Female 4 (LIA Cute)", "Hindi Female", "hi-IN", 1.28f, 0.96f),
        VoiceProfileItem("hi_f_5", "Hindi Female 5 (Kavya Clear)", "Hindi Female", "hi-IN", 1.12f, 0.94f),
        VoiceProfileItem("hi_f_6", "Hindi Female 6 (Priya Story)", "Hindi Female", "hi-IN", 1.18f, 0.86f),
        VoiceProfileItem("hi_f_7", "Hindi Female 7 (Ananya Calm)", "Hindi Female", "hi-IN", 1.14f, 0.85f),
        VoiceProfileItem("hi_f_8", "Hindi Female 8 (Meera Pro)", "Hindi Female", "hi-IN", 1.10f, 0.95f),
        // Hindi Male 1-4
        VoiceProfileItem("hi_m_1", "Hindi Male 1 (Veer Confident)", "Hindi Male", "hi-IN", 0.90f, 0.94f),
        VoiceProfileItem("hi_m_2", "Hindi Male 2 (Kabir Calm)", "Hindi Male", "hi-IN", 0.86f, 0.90f),
        VoiceProfileItem("hi_m_3", "Hindi Male 3 (Arjun Deep)", "Hindi Male", "hi-IN", 0.82f, 0.92f),
        VoiceProfileItem("hi_m_4", "Hindi Male 4 (Dev Guard)", "Hindi Male", "hi-IN", 0.88f, 0.96f),
        // English Female 1-6
        VoiceProfileItem("en_f_1", "English Female 1 (Aria HD)", "English Female", "en-IN", 1.18f, 0.92f),
        VoiceProfileItem("en_f_2", "English Female 2 (Nova Clear)", "English Female", "en-US", 1.14f, 0.95f),
        VoiceProfileItem("en_f_3", "English Female 3 (Chloe Warm)", "English Female", "en-GB", 1.16f, 0.90f),
        VoiceProfileItem("en_f_4", "English Female 4 (Sophia Pro)", "English Female", "en-US", 1.10f, 0.94f),
        VoiceProfileItem("en_f_5", "English Female 5 (Luna Whisper)", "English Female", "en-US", 1.20f, 0.86f),
        VoiceProfileItem("en_f_6", "English Female 6 (Zoe Bright)", "English Female", "en-IN", 1.22f, 0.96f),
        // English Male 1-4
        VoiceProfileItem("en_m_1", "English Male 1 (Atlas Deep)", "English Male", "en-US", 0.84f, 0.92f),
        VoiceProfileItem("en_m_2", "English Male 2 (Orion Executive)", "English Male", "en-GB", 0.88f, 0.95f),
        VoiceProfileItem("en_m_3", "English Male 3 (Liam Casual)", "English Male", "en-US", 0.92f, 0.94f),
        VoiceProfileItem("en_m_4", "English Male 4 (Marcus Tactical)", "English Male", "en-US", 0.80f, 0.90f),
        // Urdu Female 1-4
        VoiceProfileItem("ur_f_1", "Urdu Female 1 (Noor Poetic)", "Urdu Female", "ur-PK", 1.16f, 0.88f),
        VoiceProfileItem("ur_f_2", "Urdu Female 2 (Meher Ghazal)", "Urdu Female", "ur-IN", 1.18f, 0.86f),
        VoiceProfileItem("ur_f_3", "Urdu Female 3 (Hina Soft)", "Urdu Female", "ur-PK", 1.20f, 0.90f),
        VoiceProfileItem("ur_f_4", "Urdu Female 4 (Sana Royal)", "Urdu Female", "ur-IN", 1.12f, 0.92f),
        // Sindhi 1-2
        VoiceProfileItem("sd_1", "Sindhi 1 (Mumtaz Sweet)", "Sindhi", "hi-IN", 1.15f, 0.90f),
        VoiceProfileItem("sd_2", "Sindhi 2 (Shahbaz Warm)", "Sindhi", "hi-IN", 0.90f, 0.92f),
        // Dhatki 1-2
        VoiceProfileItem("dh_1", "Dhatki 1 (Maru Caring)", "Dhatki", "hi-IN", 1.18f, 0.88f),
        VoiceProfileItem("dh_2", "Dhatki 2 (Kesariya Bold)", "Dhatki", "hi-IN", 0.94f, 0.92f),
        // Romantic 1, Funny 1, SASSY ROAST 1
        VoiceProfileItem("mood_romantic", "Romantic 1 (ZARA Whisper)", "Mood Expressive", "hi-IN", 1.22f, 0.84f),
        VoiceProfileItem("mood_funny", "Funny 1 (LIA Comedy)", "Mood Expressive", "hi-IN", 1.28f, 1.02f),
        VoiceProfileItem("mood_roast", "SASSY ROAST 1 (KAYRA Bold)", "Mood Expressive", "hi-IN", 1.08f, 0.98f)
    )

    // 4x4 Avatar Gallery backed by all 22 real attached user photos
    val avatarGalleryItems: List<AvatarGalleryItem> = listOf(
        AvatarGalleryItem(
            id = "zara_open",
            title = "ZARA Executive",
            category = "ZARA Black Blazer",
            agentId = "zara",
            moodLabel = "Caring • Seated Natural",
            moodTheme = "Caring Blue",
            accentHex = 0xFF00D4FF,
            primaryDrawable = R.drawable.avatar_zara_open,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_laugh,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "zara_front",
            title = "ZARA Romantic",
            category = "ZARA Black Blazer",
            agentId = "zara",
            moodLabel = "Eye Contact • Warm",
            moodTheme = "Romantic Pink",
            accentHex = 0xFFFF2E63,
            primaryDrawable = R.drawable.avatar_zara_front,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_laugh,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "zara_wave",
            title = "ZARA Wave Hi",
            category = "ZARA Black Blazer",
            agentId = "zara",
            moodLabel = "Greeting • Playful",
            moodTheme = "Caring Blue",
            accentHex = 0xFF00D4FF,
            primaryDrawable = R.drawable.avatar_zara_wave,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_laugh,
            shyDrawable = R.drawable.avatar_zara_hair,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "zara_reach",
            title = "SANAM Guard",
            category = "ZARA Black Blazer",
            agentId = "sanam",
            moodLabel = "Protective • Close",
            moodTheme = "UMI Black Blue Orb",
            accentHex = 0xFF38BDF8,
            primaryDrawable = R.drawable.avatar_zara_reach,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_laugh,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "zara_hair",
            title = "ZOYA Shy Tuck",
            category = "ZARA Black Blazer",
            agentId = "zoya",
            moodLabel = "Romantic • Gentle",
            moodTheme = "Romantic Pink",
            accentHex = 0xFFFF2E63,
            primaryDrawable = R.drawable.avatar_zara_hair,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_laugh,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "zara_tilt",
            title = "ZOYA Study Zen",
            category = "ZARA Black Blazer",
            agentId = "zoya",
            moodLabel = "Listening • Focused",
            moodTheme = "Motivational Green",
            accentHex = 0xFF00E676,
            primaryDrawable = R.drawable.avatar_zara_tilt,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_zara_smile2,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_zara_wave,
            roastDrawable = R.drawable.avatar_zara_roast
        ),
        AvatarGalleryItem(
            id = "maya_stand",
            title = "MAYA Emerald",
            category = "Green Dress MAYA",
            agentId = "maya",
            moodLabel = "Soft Romantic • Poetic",
            moodTheme = "SANAM Green Neon",
            accentHex = 0xFF10B981,
            primaryDrawable = R.drawable.avatar_maya_stand,
            blinkDrawable = R.drawable.avatar_zara_blink,
            happyDrawable = R.drawable.avatar_maya_handsup,
            shyDrawable = R.drawable.avatar_zara_shy,
            waveDrawable = R.drawable.avatar_maya_handsup,
            roastDrawable = R.drawable.avatar_maya_stand
        ),
        AvatarGalleryItem(
            id = "maya_handsup",
            title = "MAYA Cheerful",
            category = "Green Dress MAYA",
            agentId = "maya",
            moodLabel = "Cheerful • Expressive",
            moodTheme = "Funny Yellow",
            accentHex = 0xFFFFD700,
            primaryDrawable = R.drawable.avatar_maya_handsup,
            blinkDrawable = R.drawable.avatar_maya_stand,
            happyDrawable = R.drawable.avatar_maya_handsup,
            shyDrawable = R.drawable.avatar_maya_stand,
            waveDrawable = R.drawable.avatar_maya_handsup,
            roastDrawable = R.drawable.avatar_maya_stand
        ),
        AvatarGalleryItem(
            id = "anime_wave",
            title = "LIA Kawaii",
            category = "Anime Silver",
            agentId = "lia",
            moodLabel = "Cute • Hi Senpai",
            moodTheme = "Romantic Pink",
            accentHex = 0xFFFF7EB6,
            primaryDrawable = R.drawable.avatar_anime_wave,
            blinkDrawable = R.drawable.avatar_anime_shy,
            happyDrawable = R.drawable.avatar_anime_wink,
            shyDrawable = R.drawable.avatar_anime_shy,
            waveDrawable = R.drawable.avatar_anime_wave,
            roastDrawable = R.drawable.avatar_anime_pout
        ),
        AvatarGalleryItem(
            id = "anime_wink",
            title = "Anime Rose",
            category = "Anime Silver",
            agentId = "anime_girl",
            moodLabel = "Playful • Rose Spark",
            moodTheme = "Funny Yellow",
            accentHex = 0xFFFFD700,
            primaryDrawable = R.drawable.avatar_anime_wink,
            blinkDrawable = R.drawable.avatar_anime_shy,
            happyDrawable = R.drawable.avatar_anime_wave,
            shyDrawable = R.drawable.avatar_anime_shy,
            waveDrawable = R.drawable.avatar_anime_wave,
            roastDrawable = R.drawable.avatar_anime_pout
        ),
        AvatarGalleryItem(
            id = "anime_shy",
            title = "Anime Blush",
            category = "Anime Silver",
            agentId = "anime_girl",
            moodLabel = "Shy Blush • Emotional",
            moodTheme = "Romantic Pink",
            accentHex = 0xFFFF2E63,
            primaryDrawable = R.drawable.avatar_anime_shy,
            blinkDrawable = R.drawable.avatar_anime_shy,
            happyDrawable = R.drawable.avatar_anime_wink,
            shyDrawable = R.drawable.avatar_anime_shy,
            waveDrawable = R.drawable.avatar_anime_wave,
            roastDrawable = R.drawable.avatar_anime_pout
        ),
        AvatarGalleryItem(
            id = "anime_pout",
            title = "KAYRA Roast",
            category = "Anime Silver",
            agentId = "kayra",
            moodLabel = "Sassy Roast • Jealous",
            moodTheme = "SASSY ROAST Dark Red",
            accentHex = 0xFFB91C1C,
            primaryDrawable = R.drawable.avatar_anime_pout,
            blinkDrawable = R.drawable.avatar_anime_shy,
            happyDrawable = R.drawable.avatar_anime_wink,
            shyDrawable = R.drawable.avatar_anime_shy,
            waveDrawable = R.drawable.avatar_anime_wave,
            roastDrawable = R.drawable.avatar_anime_pout
        ),
        AvatarGalleryItem(
            id = "neon_blue",
            title = "Neon Blue",
            category = "Neon Cyber",
            agentId = "jarvis_hud",
            moodLabel = "HUD Cyber • Calm",
            moodTheme = "Caring Blue",
            accentHex = 0xFF00D4FF,
            primaryDrawable = R.drawable.avatar_neon_blue,
            blinkDrawable = R.drawable.avatar_neon_violet_glow,
            happyDrawable = R.drawable.avatar_neon_blue,
            shyDrawable = R.drawable.avatar_neon_pink_ghost,
            waveDrawable = R.drawable.avatar_neon_orange_sanam,
            roastDrawable = R.drawable.avatar_neon_purple_hips
        ),
        AvatarGalleryItem(
            id = "neon_purple_hips",
            title = "Neon Purple",
            category = "Neon Cyber",
            agentId = "ultron",
            moodLabel = "ULTRON Sentinel • Guard",
            moodTheme = "JEALOUS Purple",
            accentHex = 0xFF9333EA,
            primaryDrawable = R.drawable.avatar_neon_purple_hips,
            blinkDrawable = R.drawable.avatar_neon_violet_glow,
            happyDrawable = R.drawable.avatar_neon_pink_ghost,
            shyDrawable = R.drawable.avatar_neon_blue,
            waveDrawable = R.drawable.avatar_neon_orange_sanam,
            roastDrawable = R.drawable.avatar_neon_purple_hips
        ),
        AvatarGalleryItem(
            id = "neon_pink_ghost",
            title = "Neon Ghost",
            category = "Neon Cyber",
            agentId = "kayra",
            moodLabel = "Stealth • Magenta Core",
            moodTheme = "GHOST Purple Black",
            accentHex = 0xFF7C3AED,
            primaryDrawable = R.drawable.avatar_neon_pink_ghost,
            blinkDrawable = R.drawable.avatar_neon_violet_glow,
            happyDrawable = R.drawable.avatar_neon_pink_ghost,
            shyDrawable = R.drawable.avatar_neon_purple_hips,
            waveDrawable = R.drawable.avatar_neon_orange_sanam,
            roastDrawable = R.drawable.avatar_cyber_emerald
        ),
        AvatarGalleryItem(
            id = "neon_orange_sanam",
            title = "Neon Solar",
            category = "Neon Cyber",
            agentId = "sanam",
            moodLabel = "Solar Executive • Fierce",
            moodTheme = "ULTRON Red Silver",
            accentHex = 0xFFE11D48,
            primaryDrawable = R.drawable.avatar_neon_orange_sanam,
            blinkDrawable = R.drawable.avatar_neon_violet_glow,
            happyDrawable = R.drawable.avatar_cyber_emerald,
            shyDrawable = R.drawable.avatar_neon_pink_ghost,
            waveDrawable = R.drawable.avatar_neon_orange_sanam,
            roastDrawable = R.drawable.avatar_neon_purple_hips
        )
    )

    fun resolveAvatarDrawable(
        avatarId: String,
        emotion: AvatarEmotionState,
        isBlinking: Boolean
    ): Int {
        val item = avatarGalleryItems.find { it.id == avatarId } ?: avatarGalleryItems.first()
        if (isBlinking) return item.blinkDrawable
        return when (emotion) {
            AvatarEmotionState.LAUGHING, AvatarEmotionState.HAPPY -> item.happyDrawable
            AvatarEmotionState.SHY_BLUSH, AvatarEmotionState.ROMANTIC, AvatarEmotionState.HAIR_TUCK -> item.shyDrawable
            AvatarEmotionState.WAVING, AvatarEmotionState.REACHING, AvatarEmotionState.SURPRISED -> item.waveDrawable
            AvatarEmotionState.ROAST_ANGRY, AvatarEmotionState.SAD_CARING -> item.roastDrawable
            AvatarEmotionState.WINK -> item.happyDrawable
            AvatarEmotionState.NEUTRAL -> item.primaryDrawable
        }
    }

    val eightPermissionSteps: List<PermissionGuideStep> = listOf(
        PermissionGuideStep(1, "Phone Calls", "Bhai jab aap bolo 'Maa ko call lagao', tab dialer kholne ke liye.", android.Manifest.permission.CALL_PHONE, "Optional"),
        PermissionGuideStep(2, "Contacts (Call Only)", "Sirf aapke bole hue contact ka naam dhoondhne ke liye — zero server upload.", android.Manifest.permission.READ_CONTACTS, "Private"),
        PermissionGuideStep(3, "SMS Drafts", "Message bhejne se pehle draft card dikha ke aapki permission lene ke liye.", android.Manifest.permission.SEND_SMS, "Draft Only"),
        PermissionGuideStep(4, "GPS Location", "Sirf aapke shehar ka live mausam aur safety radar dikhane ke liye.", android.Manifest.permission.ACCESS_COARSE_LOCATION, "Local GPS"),
        PermissionGuideStep(5, "Gallery Photo Picker", "Zero broad storage permission! Sirf aapki chuni hui photos Vault mein lock karne ke liye.", "PHOTO_PICKER", "Zero-Perm"),
        PermissionGuideStep(6, "Notifications", "Alarm, reminders aur touch guard siren alerts dene ke liye.", android.Manifest.permission.POST_NOTIFICATIONS, "Alerts"),
        PermissionGuideStep(7, "Microphone & Wake Word", "Voice Focus Mode mein aapse baatein karne aur barge-in sunne ke liye.", android.Manifest.permission.RECORD_AUDIO, "Voice"),
        PermissionGuideStep(8, "CHOUHAN Accessibility Guard", "Pay / Send / Delete se pehle Safety Confirmation aur tap automation ke liye.", "ACCESSIBILITY", "Guard")
    )

    val teacherLessons: List<TeacherQuestion> = listOf(
        TeacherQuestion(
            id = "q1",
            topic = "Android Security",
            prompt = "CHOUHAN STAR app ke API keys aur PIN kis storage mein encrypt hote hain?",
            options = listOf("Plain text JSON", "EncryptedSharedPreferences AES-256", "Public cloud sheet", "SMS Drafts"),
            correctIndex = 1,
            explanationHinglish = "Bilkul sahi! Android Keystore ke saath EncryptedSharedPreferences AES-256-GCM use hota hai."
        ),
        TeacherQuestion(
            id = "q2",
            topic = "Voice Focus & Barge-In",
            prompt = "Agar ZARA bol rahi ho aur aap beech mein kuch bolo toh kya hota hai?",
            options = listOf("App crash ho jata hai", "Barge-in se TTS turant ruk ke aapko sunta hai", "Volume zero ho jata hai", "Phone restart hota hai"),
            correctIndex = 1,
            explanationHinglish = "Zabardast! Interrupt / Barge-in turant ZARA ki aawaz rok kar aapki baat sunta hai."
        ),
        TeacherQuestion(
            id = "q3",
            topic = "Safety Guard",
            prompt = "Pay, Send, ya Delete action execute karne se pehle app kya karta hai?",
            options = listOf("Chupchap bhej deta hai", "Allow/Deny Safety Confirmation Card dikhata hai", "Bluetooth band karta hai", "Kuch nahi"),
            correctIndex = 1,
            explanationHinglish = "Sahi pakde! Paise bhejne ya delete karne se pehle aapki explicit Allow permission zaroori hai."
        ),
        TeacherQuestion(
            id = "q4",
            topic = "Offline Privacy",
            prompt = "Intruder photos aur Encrypted Vault items kahan save hote hain?",
            options = listOf("External public server", "Strictly local device storage Pictures/CHOUHAN_STAR/", "Social media draft", "Random website"),
            correctIndex = 1,
            explanationHinglish = "100% accurate! Sab kuch offline device par hi protected rehta hai."
        ),
        TeacherQuestion(
            id = "q5",
            topic = "AI Router Failover",
            prompt = "Agar cloud internet ya ek LLM API fail ho jaye toh ZARA kaise jawab deti hai?",
            options = listOf("Error screen par atak jati hai", "Auto-failover chain se Offline Local Brain par switch karti hai", "App uninstall ho jata hai", "Payment maangti hai"),
            correctIndex = 1,
            explanationHinglish = "5/5 Commander! Gemini -> Groq -> DeepSeek -> OpenRouter -> Ollama -> Offline Brain hamesha tayyar rehta hai."
        )
    )

    val umiExpressions = listOf(
        "Happy Spark" to "^ ◡ ^",
        "Listening" to "◉ ‿ ◉",
        "Thinking" to "◔ _ ◔",
        "Love Heart" to "♥ ◡ ♥",
        "Roast Mode" to "¬ ‿ ¬",
        "Sleepy Chill" to "－ ‿ －",
        "Alert Guard" to "◈ △ ◈",
        "Music Vibe" to "♪ ◡ ♪",
        "Coding Zen" to "</ >",
        "Surprised" to "⊙ ○ ⊙",
        "Victory Boss" to "★ ◡ ★",
        "Stealth Dark" to "■ _ ■"
    )

    val defaultPrompts = listOf(
        PromptTemplateItem("p1", "Morning Briefing Hinglish", "ZARA Dialogues", "ZARA, aaj ka mausam, battery status, unread alerts aur 3 priority kaam Hinglish mein batao.", true),
        PromptTemplateItem("p2", "Reverse Engineer APK / Error", "Reverse", "Is stacktrace aur network payload ka root cause aur minimal fix steps do.", true),
        PromptTemplateItem("p3", "System Architecture Diagram", "Diagrams", "Clean ASCII architecture diagram banao for Android MVVM + Room + EncryptedSharedPreferences.", false),
        PromptTemplateItem("p4", "ZARA Motivational Kick", "ZARA Dialogues", "Mujhe 45-minute deep coding sprint shuru karne ke liye ZARA style dhamakedar Hinglish boost do.", false),
        PromptTemplateItem("p5", "Night Security Automation", "Automation", "Raat 11:30 PM par Touch Guard arm karo, WiFi verify karo aur DND activate karo.", true),
        PromptTemplateItem("p6", "POST /charges Double-Charge Audit", "Reverse", "Idempotency-Key header aur DB unique transaction constraint check karke double charge bug fix batao.", false)
    )

    val marketplaceItems = listOf(
        MarketplaceToolItem("m1", "ZARA Pro Voice", "32 Voices + Focus Mode", "FREE", "voice_live"),
        MarketplaceToolItem("m2", "UMI Local Brain", "100% Offline Engine", "OFFLINE", "umi_world"),
        MarketplaceToolItem("m3", "Claude Code IDE", "Double Charge Fixer", "DEV", "claude_code"),
        MarketplaceToolItem("m4", "HUD Face Gen", "Wireframe Scan & 12 HUD", "3D", "face_studio"),
        MarketplaceToolItem("m5", "Triggers +77", "Sensor & Time Rules", "AUTO", "triggers"),
        MarketplaceToolItem("m6", "GF Scold Pack", "Hindi/Marwadi Audio", "GUARD", "gf_mode"),
        MarketplaceToolItem("m7", "Teacher 5/5 Quiz", "Samjhao & Practice Mode", "LEARN", "tools_hub"),
        MarketplaceToolItem("m8", "Ghost Tactical", "10-Module Stealth Grid", "STEALTH", "ghost_world"),
        MarketplaceToolItem("m9", "Avatar 4x4 Studio", "22 Real Live Avatars", "LIVE", "avatar_gallery")
    )

    val phone12Grid = listOf(
        "WiFi", "Bluetooth", "Torch", "DND",
        "Screenshot", "Alarm", "Auto-Rotate", "Hotspot",
        "Battery Saver", "Touch Guard", "App Lock", "Hidden Calc"
    )

    val pc12Grid = listOf(
        "ADB Wireless", "Screen Mirror", "Clipboard Sync", "File Drop",
        "Terminal SSH", "Wake-on-LAN", "Media Remote", "Lock PC",
        "Presentation", "Audio Cast", "Backup Pictures", "Stats HUD"
    )

    val os9Grid = listOf(
        "System Pulse", "Neural Dock", "Clock 10:45",
        "Security Core", "Media Deck", "Quick Notes",
        "Radar GPS", "Launcher Pro", "Energy Saver"
    )

    val go9Grid = listOf(
        "Quick Call", "SMS Draft", "Music Switch",
        "Meeting 6-Task", "Teacher Quiz", "Torch Toggle",
        "Voice Note", "Vault Lock", "Share APK"
    )

    val ghost10Grid = listOf(
        "Stealth Mic", "Decoy Screen", "Dummy PIN", "Silent Cam",
        "Network Shield", "Fake Crash Log", "No-Trace Chat", "Sensor Freeze",
        "USB Blocker", "Phantom Ping"
    )

    val face12HudGrid = listOf(
        "Arc Reactor Cyan", "Mark-85 Gold Red", "Tactical Night Visor", "Holo Waveform Core",
        "Sovereign Star Crest", "Stealth Matrix Grid", "Bio Pulse Scanner", "Plasma Ring V2",
        "Orbital Telemetry", "Rajputana Shield", "Nano Mask Dark", "Photon Retina Lock"
    )

    val initialDhaiBuilds = listOf(
        DhaiBuildProject("b1", "CHOUHAN STAR v2.2.8", "Kotlin + Compose + Room", "LIVE", "Just now"),
        DhaiBuildProject("b2", "Payments Guard API", "Ktor + Idempotency", "VERIFIED", "1h ago"),
        DhaiBuildProject("b3", "FlowShift Focus Hub", "Coroutines + Timer", "READY", "Yesterday")
    )
}
