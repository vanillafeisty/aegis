package com.example.aegis.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TerracottaAccent,
    onPrimary = SoftCreamSurface,
    primaryContainer = TerracottaSoft,
    onPrimaryContainer = TerracottaDark,
    secondary = CharcoalPrimary,
    onSecondary = SoftCreamSurface,
    secondaryContainer = MutedBeigeCard,
    onSecondaryContainer = CharcoalPrimary,
    tertiary = DarkSage,
    onTertiary = SoftCreamSurface,
    tertiaryContainer = PastelSage,
    onTertiaryContainer = DarkSage,
    background = WarmBeigeBackground,
    onBackground = CharcoalPrimary,
    surface = SoftCreamSurface,
    onSurface = CharcoalPrimary,
    surfaceVariant = MutedBeigeCard,
    onSurfaceVariant = CharcoalMuted,
    outline = WarmStoneBorder,
    outlineVariant = SubtleBeigeHover,
    error = DarkCrimson,
    onError = SoftCreamSurface,
    errorContainer = PastelCrimson,
    onErrorContainer = DarkCrimson
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaAccent,
    onPrimary = DarkWarmBg,
    primaryContainer = DarkWarmCard,
    onPrimaryContainer = TerracottaSoft,
    secondary = DarkTextPrimary,
    onSecondary = DarkWarmBg,
    secondaryContainer = DarkWarmSurface,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = PastelSage,
    onTertiary = DarkWarmBg,
    tertiaryContainer = DarkWarmCard,
    onTertiaryContainer = DarkSage,
    background = DarkWarmBg,
    onBackground = DarkTextPrimary,
    surface = DarkWarmSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkWarmCard,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkWarmBorder,
    outlineVariant = DarkWarmCard,
    error = DarkCrimson,
    onError = DarkWarmBg,
    errorContainer = DarkWarmCard,
    onErrorContainer = PastelCrimson
)

@Composable
fun AegisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

