package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Standard light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// Standard Dark Scheme (Material Dark)
private val CoreDarkColorScheme = darkColorScheme(
    primary = CosmicCyan,
    secondary = NeonPurple,
    tertiary = GoldYellow,
    background = Color(0xFF0D0F14),
    surface = Color(0xFF151821),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1)
)

// AMOLED Dark Theme: Elegant Dark design system, absolute pitch black base with soft purple-grey details
private val AmoledColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFF4A4458),
    tertiary = Color(0xFF381E72),
    background = Color(0xFF000000),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onSecondary = Color(0xFFE6E1E9),
    onBackground = Color(0xFFE6E1E9),
    onSurface = Color(0xFFE6E1E9),
    onSurfaceVariant = Color(0xFF938F99),
    surfaceVariant = Color(0xFF4A4458),
    outline = Color(0xFF49454F),
    outlineVariant = Color(0x3349454F)
)

// Glassmorphism Scheme (Deep dark space backings with bright, icy primary colors)
private val GlassmorphismColorScheme = darkColorScheme(
    primary = CosmicCyan,
    secondary = NeonPurple,
    tertiary = GoldYellow,
    background = Color(0xFF0A0C16),
    surface = Color(0x1EFFFFFF), // Transparent glass overlay standard
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0x0FFFFFFF)
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "amoled", // light, amoled, glass, dynamic
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()

    val colorScheme = when (themeMode) {
        "light" -> LightColorScheme
        "amoled" -> AmoledColorScheme
        "glass" -> GlassmorphismColorScheme
        "dynamic" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (systemInDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (systemInDark) AmoledColorScheme else LightColorScheme
            }
        }
        else -> AmoledColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
