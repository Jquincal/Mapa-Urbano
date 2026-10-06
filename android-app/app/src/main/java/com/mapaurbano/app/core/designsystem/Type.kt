package com.mapaurbano.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mapaurbano.app.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

private fun muniText(
    weight: FontWeight,
    size: Int,
    lineHeight: Int,
    letterSpacing: Double = 0.0,
) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
)

val MuniTypography = Typography(
    displayLarge = muniText(FontWeight.Bold, 40, 48, -0.8),
    displaySmall = muniText(FontWeight.Bold, 32, 40, -0.32),
    headlineLarge = muniText(FontWeight.Bold, 28, 36, -0.28),
    headlineMedium = muniText(FontWeight.SemiBold, 24, 32),
    headlineSmall = muniText(FontWeight.SemiBold, 20, 28),
    titleLarge = muniText(FontWeight.SemiBold, 18, 24),
    titleMedium = muniText(FontWeight.SemiBold, 16, 22),
    titleSmall = muniText(FontWeight.SemiBold, 14, 20, 0.14),
    bodyLarge = muniText(FontWeight.Normal, 16, 24),
    bodyMedium = muniText(FontWeight.Normal, 14, 20),
    bodySmall = muniText(FontWeight.Normal, 12, 16),
    labelLarge = muniText(FontWeight.SemiBold, 14, 20, 0.14),
    labelMedium = muniText(FontWeight.SemiBold, 12, 16, 0.24),
    labelSmall = muniText(FontWeight.Bold, 11, 14, 0.33),
)
