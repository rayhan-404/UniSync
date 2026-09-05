package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Exact Color Tokens from User Design Reference
// Light Mode Tokens
val LightBg = Color(0xFFF3EEFA)       // --bg: #f3eefa
val LightBg2 = Color(0xFFEDE5F7)      // --bg2: #ede5f7
val LightBg3 = Color(0xFFE4D8F0)      // --bg3: #e4d8f0
val LightNl = Color(0xFFFFFFFF)       // --nl: #ffffff (crisp white specular highlight)
val LightNd = Color(0xFFBFACDA)       // --nd: deep soft lavender shadow for rich 3D embossing
val LightSurface = Color(0xFFF3EEFA)
val LightText = Color(0xFF1C0030)      // --text: #1c0030
val LightText2 = Color(0xFF6D4C80)     // --text2: #6d4c80
val LightText3 = Color(0xFFA07CB8)     // --text3: #a07cb8

// Dark Mode Tokens
val DarkBg = Color(0xFF120820)        // --bg: #120820
val DarkBg2 = Color(0xFF1C1130)       // --bg2: #1c1130
val DarkBg3 = Color(0xFF261640)       // --bg3: #261640
val DarkNl = Color(0xFF33204E)        // --nl: bright highlight for dark neumorphism
val DarkNd = Color(0xFF06020D)        // --nd: rich deep shadow in dark mode
val DarkSurface = Color(0xFF120820)
val DarkText = Color(0xFFF0E6FF)       // --text: #f0e6ff
val DarkText2 = Color(0xFFC9A8F0)      // --text2: #c9a8f0
val DarkText3 = Color(0xFF8A6AAA)      // --text3: #8a6aaa


// Aliases for default light neumorphic theme
val BaseBackground = LightBg
val MainText = LightText
val SecondaryText = LightText2
val MutedText = LightText3
val NeumorphicShadowDark = LightNd
val NeumorphicHighlightLight = LightNl

// Core Accents
val PrimaryPurple = Color(0xFF9C27B0)  // --primary: #9c27b0
val PrimaryPurple2 = Color(0xFFAB47BC) // --primary2: #ab47bc
val AccentMagenta = Color(0xFFE040FB)  // --accent: #e040fb
val SuccessGreen = Color(0xFF4CAF50)   // --success: #4caf50
val WarnOrange = Color(0xFFFF9800)     // --warn: #ff9800
val InfoBlue = Color(0xFF2196F3)       // --info: #2196f3

// Backward-compat aliases
val PrimaryBlue = PrimaryPurple
val PrimaryMediumBlue = PrimaryPurple2
val PrimaryLight = LightBg2
val PrimaryFixedDim = Color(0xFFD1C4E9)
val SecondaryGreen = SuccessGreen
val SecondaryLight = Color(0xFFC8E6C9)
val TertiaryPurple = PrimaryPurple
val PurpleAccent = AccentMagenta
val PurpleLight = LightBg3
val ErrorRed = Color(0xFFF44336)
val ErrorLight = Color(0xFFFFEBEE)

// Surface references
val LightBackground = LightBg
val LightSurfaceVariant = LightBg2
val LightOnSurface = LightText
val LightTextSubtle = LightText2

val DarkBackground = DarkBg
val DarkSurfaceVariant = DarkBg2
val DarkOnSurface = DarkText
val DarkTextSubtle = DarkText2

// Dynamic Color Cycler Presets matching reference Material Colors
data class CustomColorTheme(
    val name: String,
    val primary: Color,
    val primary2: Color,
    val accent: Color
)

val ColorThemePresets = listOf(
    CustomColorTheme("Material Purple", Color(0xFF9C27B0), Color(0xFFAB47BC), Color(0xFFE040FB)),
    CustomColorTheme("Material Indigo", Color(0xFF3F51B5), Color(0xFF5C6BC0), Color(0xFF7986CB)),
    CustomColorTheme("Material Teal", Color(0xFF009688), Color(0xFF26A69A), Color(0xFF80CBC4)),
    CustomColorTheme("Material Deep Orange", Color(0xFFFF5722), Color(0xFFFF7043), Color(0xFFFF8A65)),
    CustomColorTheme("Material Pink", Color(0xFFE91E63), Color(0xFFEC407A), Color(0xFFF48FB1)),
    CustomColorTheme("Material Cyan", Color(0xFF00BCD4), Color(0xFF26C6DA), Color(0xFF80DEEA)),
    CustomColorTheme("Material Amber", Color(0xFFFF9800), Color(0xFFFFA726), Color(0xFFFFD54F)),
    CustomColorTheme("Material Deep Purple", Color(0xFF673AB7), Color(0xFF7E57C2), Color(0xFFB39DDB)),
    CustomColorTheme("Material Green", Color(0xFF4CAF50), Color(0xFF66BB6A), Color(0xFFA5D6A7)),
    CustomColorTheme("Material Blue", Color(0xFF2196F3), Color(0xFF42A5F5), Color(0xFF90CAF9))
)



