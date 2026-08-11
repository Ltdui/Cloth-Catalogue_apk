package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.model.AccentColorTheme
import com.example.model.ThemeMode

@Composable
fun FabricCollectionTheme(
    accentColorTheme: AccentColorTheme = AccentColorTheme.TEAL,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> createThemeColorScheme(accentColorTheme, darkTheme)
    }

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
