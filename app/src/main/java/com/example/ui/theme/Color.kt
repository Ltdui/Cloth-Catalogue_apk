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
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.30f),
            onPrimaryContainer = Color(0xFFFFF0F2),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.25f),
            onSecondaryContainer = Color.White,
            background = Color(0xFF141A21),
            onBackground = Color(0xFFF8FAFC),
            surface = Color(0xFF1E2630),
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = Color(0xFF2B3644),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF475569)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFDE8EB),
            onPrimaryContainer = Color(0xFF42000B),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF5EBE8),
            onSecondaryContainer = Color(0xFF381A16),
            background = Color(0xFFFAF6F5),
            onBackground = Color(0xFF1F1A1C),
            surface = Color.White,
            onSurface = Color(0xFF1F1A1C),
            surfaceVariant = Color(0xFFF3EBE9),
            onSurfaceVariant = Color(0xFF524345),
            outline = Color(0xFFE5D8DA)
        )
    }
}
