package com.example.aegis.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = PureWhite,
    primaryContainer = LightSage,
    onPrimaryContainer = ForestGreen,
    secondary = SageGreen,
    onSecondary = PureWhite,
    secondaryContainer = UltraLightSage,
    onSecondaryContainer = DarkGreen,
    tertiary = EmeraldAccent,
    onTertiary = PureWhite,
    background = OffWhite,
    onBackground = ForestGreen,
    surface = PureWhite,
    onSurface = ForestGreen,
    surfaceVariant = UltraLightSage,
    onSurfaceVariant = MutedText,
    outline = BorderColor,
    error = CrimsonError,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = LightSage,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = LightSage,
    secondary = SageGreen,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = LightSage,
    tertiary = EmeraldAccent,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = CrimsonError,
    onError = PureWhite
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
