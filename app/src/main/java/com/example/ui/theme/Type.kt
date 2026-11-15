package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins, FontWeight.Normal),
    Font(R.font.poppins, FontWeight.Medium),
    Font(R.font.poppins, FontWeight.SemiBold),
    Font(R.font.poppins, FontWeight.Bold),
    Font(R.font.poppins, FontWeight.ExtraBold)
)

val InterFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold)
)

fun buildStarTypography(largeFont: Boolean = false): Typography {
    val boost = if (largeFont) 3 else 0
    return Typography(
        headlineMedium = TextStyle(
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (22 + boost).sp,
            lineHeight = (28 + boost).sp
        ),
        titleLarge = TextStyle(
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (22 + boost).sp,
            lineHeight = (28 + boost).sp
        ),
        titleMedium = TextStyle(
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 + boost).sp,
            lineHeight = (22 + boost).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (14 + boost).sp,
            lineHeight = (20 + boost).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (14 + boost).sp,
            lineHeight = (20 + boost).sp
        ),
        bodySmall = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (12 + boost).sp,
            lineHeight = (16 + boost).sp
        ),
        labelSmall = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (12 + boost).sp,
            lineHeight = (16 + boost).sp
        )
    )
}

val Typography = buildStarTypography(largeFont = false)
