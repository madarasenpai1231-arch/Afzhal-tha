package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// RizzX Brand Colors
val DarkBgPrimary = Color(0xFF08080B)
val DarkSurfaceSecondary = Color(0xFF101116)
val DarkSurfaceTertiary = Color(0xFF171820)
val DarkCardElevated = Color(0xFF1D1F28)
val DarkCardBorder = Color(0x338B5CF6)
val GlassSurface = Color(0xCC171820)
val GlassBorder = Color(0x2AFFFFFF)

// Accents
val ElectricViolet = Color(0xFF8B5CF6)
val ElectricVioletLight = Color(0xFFA78BFA)
val ElectricVioletDark = Color(0xFF6D28D9)
val WarmPink = Color(0xFFFF4D8D)
val WarmPinkLight = Color(0xFFFF75A0)
val WarmPinkDark = Color(0xFFD91B5C)

// Neutral Text
val TextWhite = Color(0xFFF5F5F7)
val TextSoftWhite = Color(0xFFF7F7FA)
val TextSecondary = Color(0xFFA7A8B3)
val TextMuted = Color(0xFF6F7280)

// Feedback
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val DestructiveRed = Color(0xFFEF4444)
val SparkleYellow = Color(0xFFFFD269)

// Gradients
val RizzGradient = Brush.horizontalGradient(
    colors = listOf(ElectricViolet, WarmPink)
)
val RizzGradientVertical = Brush.verticalGradient(
    colors = listOf(ElectricViolet, WarmPink)
)
val SelectedPillGradient = Brush.horizontalGradient(
    colors = listOf(ElectricViolet, WarmPink)
)
