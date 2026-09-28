package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentAmber,
    onPrimary = Color(0xFF14171C),
    primaryContainer = Color(0xFF2A2E38),
    onPrimaryContainer = AccentAmber,
    secondary = AccentTeal,
    onSecondary = Color(0xFF14171C),
    secondaryContainer = Color(0xFF203233),
    onSecondaryContainer = Color(0xFF80E2D6),
    tertiary = PigmentRose,
    background = PaperCreamDark,
    onBackground = InkPrimaryDark,
    surface = PaperCardDark,
    onSurface = InkPrimaryDark,
    surfaceVariant = Color(0xFF252B36),
    onSurfaceVariant = InkSecondaryDark,
    outline = Color(0xFF3E4654)
)

private val LightColorScheme = lightColorScheme(
    primary = PigmentOlive,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4EDE1),
    onPrimaryContainer = PigmentOlive,
    secondary = PigmentTerracotta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBECE8),
    onSecondaryContainer = PigmentTerracotta,
    tertiary = PigmentDusk,
    background = PaperCream,
    onBackground = InkPrimaryLight,
    surface = PaperCardLight,
    onSurface = InkPrimaryLight,
    surfaceVariant = Color(0xFFEFECE4),
    onSurfaceVariant = InkSecondaryLight,
    outline = SketchGridLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
