package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StarThemeTokens(
    val coreMode: String = "AMOLED",
    val moodTheme: String = "Caring Blue",
    val background: Color = AmoledBlack,
    val cardSurface: Color = GlassCardColor,
    val elevatedSurface: Color = SurfaceCard,
    val primaryAccent: Color = NeonBlue,
    val secondaryAccent: Color = NeonPurple,
    val pinkAccent: Color = NeonPink,
    val subtitleYellow: Color = YellowSubtitle,
    val onBackgroundText: Color = TextWhite,
    val mutedText: Color = TextGrey,
    val isLight: Boolean = false
)

val LocalStarThemeTokens = staticCompositionLocalOf { StarThemeTokens() }

fun resolveMoodAccent(moodTheme: String, fallbackAura: String): Color = when (moodTheme) {
    "Romantic Pink" -> MoodRomanticPink
    "Caring Blue" -> MoodCaringBlue
    "Funny Yellow" -> MoodFunnyYellow
    "Motivational Green" -> MoodMotivationalGreen
    "SASSY ROAST Dark Red" -> MoodSassyRoastRed
    "JEALOUS Purple" -> MoodJealousPurple
    "ULTRON Red Silver" -> MoodUltronRedSilver
    "UMI Black Blue Orb" -> MoodUmiBlueOrb
    "SANAM Green Neon" -> MoodNeonGreen
    "GHOST Purple Black" -> MoodGhostPurple
    else -> when (fallbackAura) {
        "Blood Ember" -> BloodEmberPrimary
        "Electric Neon Red" -> NeonRedPrimary
        "Cyber Ruby" -> CyberRubyPrimary
        "Gold Aura" -> StarGold
        "Cyan Aura" -> NeonBlue
        "Emerald Aura" -> MoodMotivationalGreen
        else -> NeonBlue
    }
}

fun getStarColorScheme(
    auraName: String,
    coreThemeMode: String = "AMOLED",
    moodTheme: String = "Caring Blue"
): ColorScheme {
    val accent = resolveMoodAccent(moodTheme, auraName)
    return when (coreThemeMode) {
        "Light" -> lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = accent.copy(alpha = 0.16f),
            onPrimaryContainer = LightTextPrimary,
            secondary = NeonPurple,
            onSecondary = Color.White,
            background = LightBg,
            surface = LightSurface,
            surfaceVariant = LightCard,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary,
            onSurfaceVariant = LightTextSecondary,
            outline = Color(0xFFD0D7E8)
        )
        "Dark" -> darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = Color(0xFF1F2238),
            onPrimaryContainer = Color.White,
            secondary = NeonPurple,
            onSecondary = Color.White,
            background = Color(0xFF121420),
            surface = Color(0xFF181B2C),
            surfaceVariant = Color(0xFF22263D),
            onBackground = TextWhite,
            onSurface = TextWhite,
            onSurfaceVariant = TextGrey,
            outline = BorderDark
        )
        else -> darkColorScheme( // AMOLED default (#0A0A12)
            primary = accent,
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF1A1A2E),
            onPrimaryContainer = Color.White,
            secondary = NeonPurple,
            onSecondary = Color.White,
            tertiary = NeonPink,
            background = AmoledBlack,
            surface = SurfaceDark,
            surfaceVariant = SurfaceCard,
            onBackground = TextWhite,
            onSurface = TextWhite,
            onSurfaceVariant = TextGrey,
            outline = BorderDark
        )
    }
}

@Composable
fun ChouhanStarTheme(
    auraName: String = "Crimson Aura",
    coreThemeMode: String = "AMOLED",
    moodTheme: String = "Caring Blue",
    largeFont: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getStarColorScheme(auraName, coreThemeMode, moodTheme)
    val accent = resolveMoodAccent(moodTheme, auraName)
    val isLight = coreThemeMode == "Light"
    val tokens = StarThemeTokens(
        coreMode = coreThemeMode,
        moodTheme = moodTheme,
        background = colorScheme.background,
        cardSurface = if (isLight) LightCard.copy(alpha = 0.9f) else GlassCardColor,
        elevatedSurface = if (isLight) LightSurface else SurfaceCard,
        primaryAccent = accent,
        secondaryAccent = NeonPurple,
        pinkAccent = NeonPink,
        subtitleYellow = YellowSubtitle,
        onBackgroundText = if (isLight) LightTextPrimary else TextWhite,
        mutedText = if (isLight) LightTextSecondary else TextGrey,
        isLight = isLight
    )

    CompositionLocalProvider(LocalStarThemeTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = buildStarTypography(largeFont = largeFont),
            content = content
        )
    }
}
