package com.contreras.centrotareas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NotionDarkAccent,
    onPrimary = NotionDarkBackground,
    primaryContainer = NotionDarkSurfaceVariant,
    onPrimaryContainer = NotionDarkTextPrimary,
    secondary = NotionDarkTextSecondary,
    onSecondary = NotionDarkSurface,
    background = NotionDarkBackground,
    onBackground = NotionDarkTextPrimary,
    surface = NotionDarkSurface,
    onSurface = NotionDarkTextPrimary,
    surfaceVariant = NotionDarkSurfaceVariant,
    onSurfaceVariant = NotionDarkTextSecondary,
    outline = NotionDarkBorder,
    error = NotionDarkError,
    errorContainer = NotionDarkErrorContainer,
    onError = NotionDarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = NotionAccent,
    onPrimary = NotionSurface,
    primaryContainer = NotionSurfaceVariant,
    onPrimaryContainer = NotionTextPrimary,
    secondary = NotionTextSecondary,
    onSecondary = NotionSurface,
    background = NotionBackground,
    onBackground = NotionTextPrimary,
    surface = NotionSurface,
    onSurface = NotionTextPrimary,
    surfaceVariant = NotionSurfaceVariant,
    onSurfaceVariant = NotionTextSecondary,
    outline = NotionBorder,
    error = NotionError,
    errorContainer = NotionErrorContainer,
    onError = NotionSurface
)

@Composable
fun CentroTareasTheme(
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
