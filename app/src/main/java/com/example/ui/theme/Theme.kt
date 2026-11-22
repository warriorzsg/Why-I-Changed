package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.manuscript.ReaderThemeMode

private val DarkEditorialScheme = darkColorScheme(
    primary = EditorialDarkGold,
    onPrimary = Color(0xFF1A1505),
    primaryContainer = EditorialDarkGoldContainer,
    onPrimaryContainer = EditorialDarkGold,
    secondary = Color(0xFFC9B37E),
    onSecondary = Color(0xFF1E1A12),
    secondaryContainer = EditorialDarkSurfaceVariant,
    onSecondaryContainer = EditorialDarkTextPrimary,
    background = EditorialDarkBg,
    onBackground = EditorialDarkTextPrimary,
    surface = EditorialDarkSurface,
    onSurface = EditorialDarkTextPrimary,
    surfaceVariant = EditorialDarkSurfaceVariant,
    onSurfaceVariant = EditorialDarkTextSecondary,
    outline = EditorialDarkOutline
)

private val LightEditorialScheme = lightColorScheme(
    primary = EditorialLightGold,
    onPrimary = Color.White,
    primaryContainer = EditorialLightGoldContainer,
    onPrimaryContainer = Color(0xFF3E2D04),
    secondary = Color(0xFF6B5B3E),
    onSecondary = Color.White,
    secondaryContainer = EditorialLightSurfaceVariant,
    onSecondaryContainer = EditorialLightTextPrimary,
    background = EditorialLightBg,
    onBackground = EditorialLightTextPrimary,
    surface = EditorialLightSurface,
    onSurface = EditorialLightTextPrimary,
    surfaceVariant = EditorialLightSurfaceVariant,
    onSurfaceVariant = EditorialLightTextSecondary,
    outline = EditorialLightOutline
)

private val SepiaEditorialScheme = lightColorScheme(
    primary = EditorialSepiaGold,
    onPrimary = Color(0xFFFFF8EC),
    primaryContainer = EditorialSepiaGoldContainer,
    onPrimaryContainer = Color(0xFF3B2406),
    secondary = Color(0xFF6E5233),
    onSecondary = Color(0xFFFFF8EC),
    secondaryContainer = EditorialSepiaSurfaceVariant,
    onSecondaryContainer = EditorialSepiaTextPrimary,
    background = EditorialSepiaBg,
    onBackground = EditorialSepiaTextPrimary,
    surface = EditorialSepiaSurface,
    onSurface = EditorialSepiaTextPrimary,
    surfaceVariant = EditorialSepiaSurfaceVariant,
    onSurfaceVariant = EditorialSepiaTextSecondary,
    outline = EditorialSepiaOutline
)

@Composable
fun WhyIChangedTheme(
    themeMode: ReaderThemeMode = ReaderThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ReaderThemeMode.DARK -> DarkEditorialScheme
        ReaderThemeMode.LIGHT -> LightEditorialScheme
        ReaderThemeMode.SEPIA -> SepiaEditorialScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards compatibility alias for any existing tests
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WhyIChangedTheme(
        themeMode = if (darkTheme) ReaderThemeMode.DARK else ReaderThemeMode.LIGHT,
        content = content
    )
}
