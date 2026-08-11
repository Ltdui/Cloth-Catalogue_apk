package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.model.AccentColorTheme

fun createThemeColorScheme(accentColorTheme: AccentColorTheme, isDark: Boolean): ColorScheme {
    val primary = Color(if (isDark) accentColorTheme.darkPrimaryHex else accentColorTheme.primaryHex)
    val secondary = Color(if (isDark) accentColorTheme.primaryHex else accentColorTheme.darkPrimaryHex)

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.Black,
            primaryContainer = primary.copy(alpha = 0.25f),
            onPrimaryContainer = primary,
            secondary = secondary,
            onSecondary = Color.Black,
            secondaryContainer = secondary.copy(alpha = 0.2f),
            onSecondaryContainer = Color.White,
            background = Color(0xFF0F172A),
            onBackground = Color(0xFFF8FAFC),
            surface = Color(0xFF1E293B),
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = Color(0xFF334155),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF475569)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFCCE8E8),
            onPrimaryContainer = Color(0xFF051F1F),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCCE8E8),
            onSecondaryContainer = Color(0xFF051F1F),
            background = Color(0xFFF4FBF9),
            onBackground = Color(0xFF191C1C),
            surface = Color.White,
            onSurface = Color(0xFF191C1C),
            surfaceVariant = Color(0xFFE9EFEE),
            onSurfaceVariant = Color(0xFF3F4948),
            outline = Color(0xFFE0E3E2)
        )
    }
}
