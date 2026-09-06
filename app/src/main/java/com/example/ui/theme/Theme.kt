package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = ElectricVioletDark,
    onPrimaryContainer = TextWhite,
    secondary = WarmPink,
    onSecondary = Color.White,
    secondaryContainer = WarmPinkDark,
    onSecondaryContainer = TextWhite,
    tertiary = SparkleYellow,
    background = DarkBgPrimary,
    onBackground = TextWhite,
    surface = DarkSurfaceSecondary,
    onSurface = TextWhite,
    surfaceVariant = DarkCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    outlineVariant = GlassBorder,
    error = DestructiveRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = WarmPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4E6),
    onSecondaryContainer = Color(0xFF881337),
    tertiary = Color(0xFFD97706),
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFD1D5DB),
    error = DestructiveRed,
    onError = Color.White
)

@Composable
fun RizzXTheme(
    darkTheme: Boolean = true, // Default to brand dark-first experience
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
