package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getStarColorScheme(auraName: String) = when (auraName) {
    "Blood Ember" -> darkColorScheme(
        primary = BloodEmberPrimary,
        onPrimary = Color.White,
        primaryContainer = BloodEmberContainer,
        onPrimaryContainer = BloodEmberGlow,
        secondary = WarningGold,
        onSecondary = Color.Black,
        background = ObsidianDark,
        surface = SurfaceDark,
        surfaceVariant = SurfaceCard,
        onBackground = TextWhite,
        onSurface = TextWhite,
        onSurfaceVariant = TextGrey,
        outline = BorderDark
    )
    "Electric Neon Red" -> darkColorScheme(
        primary = NeonRedPrimary,
        onPrimary = Color.White,
        primaryContainer = NeonRedContainer,
        onPrimaryContainer = NeonRedGlow,
        secondary = Color(0xFF00E5FF),
        onSecondary = Color.Black,
        background = ObsidianDark,
        surface = SurfaceDark,
        surfaceVariant = SurfaceCard,
        onBackground = TextWhite,
        onSurface = TextWhite,
        onSurfaceVariant = TextGrey,
        outline = BorderDark
    )
    "Cyber Ruby" -> darkColorScheme(
        primary = CyberRubyPrimary,
        onPrimary = Color.White,
        primaryContainer = CyberRubyContainer,
        onPrimaryContainer = CyberRubyGlow,
        secondary = WarningGold,
        onSecondary = Color.Black,
        background = ObsidianDark,
        surface = SurfaceDark,
        surfaceVariant = SurfaceCard,
        onBackground = TextWhite,
        onSurface = TextWhite,
        onSurfaceVariant = TextGrey,
        outline = BorderDark
    )
    else -> darkColorScheme( // Crimson Aura default
        primary = CrimsonPrimary,
        onPrimary = Color.White,
        primaryContainer = CrimsonContainer,
        onPrimaryContainer = CrimsonGlow,
        secondary = WarningGold,
        onSecondary = Color.Black,
        background = ObsidianDark,
        surface = SurfaceDark,
        surfaceVariant = SurfaceCard,
        onBackground = TextWhite,
        onSurface = TextWhite,
        onSurfaceVariant = TextGrey,
        outline = BorderDark
    )
}

@Composable
fun ChouhanStarTheme(
    auraName: String = "Crimson Aura",
    content: @Composable () -> Unit
) {
    val colorScheme = getStarColorScheme(auraName)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
