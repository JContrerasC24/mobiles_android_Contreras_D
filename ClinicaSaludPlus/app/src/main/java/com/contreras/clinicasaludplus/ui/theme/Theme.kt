package com.contreras.clinicasaludplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PurpleLight,
    background = FondoClaro,
    surface = androidx.compose.ui.graphics.Color.White
)

private val DarkColors = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White
)

@Composable
fun ClinicaSaludPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}