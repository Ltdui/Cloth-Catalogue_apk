package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.model.AccentColorTheme
import com.example.model.ThemeMode

@Composable
fun FabricCollectionTheme(
    accentColorTheme: AccentColorTheme = AccentColorTheme.TEAL,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = createThemeColorScheme(accentColorTheme, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep legacy alias for compatibility
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    FabricCollectionTheme(
        accentColorTheme = AccentColorTheme.TEAL,
        themeMode = ThemeMode.SYSTEM,
        content = content
    )
}
