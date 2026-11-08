package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun VarsityRoutineTheme(
    darkTheme: Boolean = false,
    themePresetIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val preset = ColorThemePresets.getOrElse(themePresetIndex % ColorThemePresets.size) { ColorThemePresets[0] }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = preset.primary,
            onPrimary = Color.White,
            primaryContainer = preset.primary.copy(alpha = 0.25f),
            onPrimaryContainer = preset.accent,
            secondary = preset.primary2,
            onSecondary = Color.White,
            tertiary = preset.accent,
            background = DarkBg,
            onBackground = DarkText,
            surface = DarkBg,
            onSurface = DarkText,
            surfaceVariant = DarkBg2,
            onSurfaceVariant = DarkText2,
            outline = DarkNd
        )
    } else {
        lightColorScheme(
            primary = preset.primary,
            onPrimary = Color.White,
            primaryContainer = LightBg2,
            onPrimaryContainer = preset.primary,
            secondary = preset.primary2,
            onSecondary = Color.White,
            secondaryContainer = LightBg3,
            onSecondaryContainer = preset.primary,
            tertiary = preset.accent,
            tertiaryContainer = LightBg3,
            onTertiaryContainer = preset.accent,
            error = ErrorRed,
            errorContainer = ErrorLight,
            onErrorContainer = ErrorRed,
            background = LightBg,
            onBackground = LightText,
            surface = LightBg,
            onSurface = LightText,
            surfaceVariant = LightBg2,
            onSurfaceVariant = LightText2,
            outline = LightNd.copy(alpha = 0.5f)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for test compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    VarsityRoutineTheme(darkTheme = darkTheme, content = content)
}