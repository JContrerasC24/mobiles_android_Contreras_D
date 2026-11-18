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
    primary = ColorPrincipalDark,
    onPrimary = SuperficieDark,
    primaryContainer = ColorPrincipalSuaveDark,
    onPrimaryContainer = TextoPrincipalDark,
    secondary = TextoSecundarioDark,
    onSecondary = SuperficieDark,
    background = FondoDark,
    onBackground = TextoPrincipalDark,
    surface = SuperficieDark,
    onSurface = TextoPrincipalDark,
    surfaceVariant = ColorPrincipalSuaveDark,
    onSurfaceVariant = TextoSecundarioDark,
    outline = BordesDark,
    error = RojoIndicadoresDark,
    errorContainer = RojoSuaveDark,
    onError = FondoDark
)

private val LightColorScheme = lightColorScheme(
    primary = ColorPrincipal,
    onPrimary = SuperficieTarjetas,
    primaryContainer = ColorPrincipalSuave,
    onPrimaryContainer = TextoPrincipal,
    secondary = TextoSecundario,
    onSecondary = SuperficieTarjetas,
    background = FondoPrincipal,
    onBackground = TextoPrincipal,
    surface = SuperficieTarjetas,
    onSurface = TextoPrincipal,
    surfaceVariant = ColorPrincipalSuave,
    onSurfaceVariant = TextoSecundario,
    outline = Bordes,
    error = RojoIndicadores,
    errorContainer = RojoSuave,
    onError = SuperficieTarjetas
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
