package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Light Mode Tokens (Clean Warm Tone for Material Deep Orange)
val LightBg = Color(0xFFFBF9F8)       // Soft clean background
val LightBg2 = Color(0xFFF7F1EE)      // Inset / card variant
val LightBg3 = Color(0xFFF0E5DF)      // Secondary card variant
val LightNl = Color(0xFFFFFFFF)       // Crisp white highlight
val LightNd = Color(0xFFDCC8BE)       // Soft warm shadow for embossing
val LightSurface = Color(0xFFFBF9F8)
val LightText = Color(0xFF261208)      // Deep warm text
val LightText2 = Color(0xFF6B4533)     // Subtitle text
val LightText3 = Color(0xFFA57D68)     // Muted text

// Dark Mode Tokens (Rich Dark Warm Tone)
val DarkBg = Color(0xFF1A110E)        // Dark warm background
val DarkBg2 = Color(0xFF241814)       // Dark card variant
val DarkBg3 = Color(0xFF33221C)       // Dark inset variant
val DarkNl = Color(0xFF422C24)        // Specular highlight for dark neumorphism
val DarkNd = Color(0xFF0D0806)        // Deep dark shadow
val DarkSurface = Color(0xFF1A110E)
val DarkText = Color(0xFFFDEEE9)       // Crisp light text
val DarkText2 = Color(0xFFDDB5A5)      // Subtitle text
val DarkText3 = Color(0xFFA88270)      // Muted text

// Aliases for default light neumorphic theme
val BaseBackground = LightBg
val MainText = LightText
val SecondaryText = LightText2
val MutedText = LightText3
val NeumorphicShadowDark = LightNd
val NeumorphicHighlightLight = LightNl

// Material Deep Orange Accent Tokens
val DeepOrangePrimary = Color(0xFFFF5722)     // Material Deep Orange Primary
val DeepOrangeSecondary = Color(0xFFFF7043)   // Deep Orange Light Variant
val DeepOrangeAccent = Color(0xFFFF8A65)      // Deep Orange Accent
val DeepOrangeContainer = Color(0xFFFFEBE5)   // Light Tinted Container

// Core Accents & Backward-compat aliases mapped to Deep Orange
val PrimaryPurple = DeepOrangePrimary
val PrimaryPurple2 = DeepOrangeSecondary
val AccentMagenta = DeepOrangeAccent
val PrimaryBlue = DeepOrangePrimary
val PrimaryMediumBlue = DeepOrangeSecondary
val PrimaryLight = LightBg2
val PrimaryFixedDim = Color(0xFFFFCCBC)
val SecondaryGreen = Color(0xFF4CAF50)
val SecondaryLight = Color(0xFFE8F5E9)
val TertiaryPurple = DeepOrangePrimary
val PurpleAccent = DeepOrangeAccent
val PurpleLight = LightBg3
val ErrorRed = Color(0xFFE53935)
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

// Only Material Deep Orange Theme Preset
data class CustomColorTheme(
    val name: String,
    val primary: Color,
    val primary2: Color,
    val accent: Color
)

val ColorThemePresets = listOf(
    CustomColorTheme(
        name = "Material Deep Orange",
        primary = DeepOrangePrimary,
        primary2 = DeepOrangeSecondary,
        accent = DeepOrangeAccent
    )
)