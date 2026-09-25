package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = OrangePrimaryContainer,
    onPrimaryContainer = OrangeLight,
    secondary = CyanTech,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = GoldContainer,
    background = ObsidianDarkBg,
    onBackground = TextPrimaryDark,
    surface = ObsidianCardBg,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = ObsidianCardBorder,
    error = RedAlert,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangePrimaryContainer,
    secondary = CyanTech,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = GoldContainer,
    background = SlateLightBg,
    onBackground = TextPrimaryLight,
    surface = SlateCardBg,
    onSurface = TextPrimaryLight,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = SlateCardBorder,
    error = RedAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark obsidian aesthetic for luxury tech vibe
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
