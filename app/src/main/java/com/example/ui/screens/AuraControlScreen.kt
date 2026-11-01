package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WireframeStar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextGrey
import com.example.viewmodel.StarSecurityViewModel

@Composable
fun AuraControlScreen(
    viewModel: StarSecurityViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("aura_control_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("aura_control_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Aura Control ⭐",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Visual Identities, Live Wallpaper & Themes",
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Wallpaper Canvas Preview
        Text(
            text = "VIDEO WALLPAPER LIVE PREVIEW",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .testTag("live_wallpaper_preview_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C12)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                WireframeStar(
                    size = 180.dp,
                    variant = uiState.currentIconVariant,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    glowColor = Color(0xFFFF3355),
                    animated = true
                )

                // Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚡ Real-time 3D Wireframe Vector Engine",
                        color = StarGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4 App Icons Switcher
        Text(
            text = "4 APP ICONS SWITCHER",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val icons = listOf(
            "Crimson Wireframe Star" to "Default red wireframe with neon glow",
            "Cyber Ruby Star" to "Faceted quantum diamond ruby star",
            "Obsidian Blood Star" to "Stealth black armor with razor crimson border",
            "Neon Crimson Pulsar" to "Dual concentric orbital rings with pulsar core"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            icons.forEach { (variantName, desc) ->
                val isSelected = uiState.currentIconVariant == variantName
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setIconVariant(variantName) }
                        .testTag("icon_variant_$variantName"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else BorderDark
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WireframeStar(
                            size = 46.dp,
                            variant = variantName,
                            primaryColor = MaterialTheme.colorScheme.primary,
                            glowColor = Color(0xFFFF4D4D),
                            animated = isSelected
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = variantName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = desc, color = TextGrey, fontSize = 11.sp)
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Theme Color Switcher
        Text(
            text = "THEME COLOR AURA",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val themes = listOf(
                "Crimson Aura" to Color(0xFFFF1E27),
                "Blood Ember" to Color(0xFFFF3333),
                "Electric Neon Red" to Color(0xFFFF0055),
                "Cyber Ruby" to Color(0xFFE91E63)
            )

            themes.forEach { (name, color) ->
                val isSelected = uiState.currentThemeAura == name
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SurfaceCard, RoundedCornerShape(12.dp))
                        .border(
                            1.5.dp,
                            if (isSelected) color else BorderDark,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setThemeAura(name) }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(20.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = name.replace(" ", "\n"),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Aura Border Stealth Mode", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Dim neon glow border for privacy and discretion", color = TextGrey, fontSize = 11.sp)
                }
                Switch(
                    checked = uiState.auraBorderStealth,
                    onCheckedChange = { viewModel.toggleAuraBorderStealth(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CrimsonPrimary,
                        uncheckedThumbColor = TextGrey,
                        uncheckedTrackColor = Color(0xFF262626)
                    ),
                    modifier = Modifier.testTag("toggle_aura_border_stealth")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Language Selector
        Text(
            text = "SYSTEM LANGUAGE",
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Auto Hinglish", "Hindi", "English").forEach { lang ->
                val isSelected = uiState.currentLanguage == lang
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else SurfaceCard,
                            RoundedCornerShape(10.dp)
                        )
                        .border(1.dp, if (isSelected) Color.White else BorderDark, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setLanguage(lang) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lang,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
