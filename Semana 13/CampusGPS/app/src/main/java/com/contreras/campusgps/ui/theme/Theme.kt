package com.contreras.campusgps.ui.theme

import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93D3DF),
    onPrimary = Color(0xFF003640),
    primaryContainer = Color(0xFF174C58),
    onPrimaryContainer = Color(0xFFC1EFF7),
    background = Color(0xFF10191C),
    surface = Color(0xFF10191C),
    onSurface = Color(0xFFE1ECEF),
    surfaceVariant = Color(0xFF293A3E),
    onSurfaceVariant = Color(0xFFB8CBCF),
    secondary = Color(0xFF77D7CB),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF175049),
    onSecondaryContainer = Color(0xFFB4F1E7),
    tertiary = Color(0xFFB8C9A1)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF164E60),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8EEF3),
    onPrimaryContainer = Color(0xFF103B49),
    background = Color(0xFFF5F8FA),
    surface = Color(0xFFF5F8FA),
    onSurface = Color(0xFF172D35),
    surfaceVariant = Color(0xFFE5EFF1),
    onSurfaceVariant = Color(0xFF48616A),
    secondary = Color(0xFF087F79),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5F2EC),
    onSecondaryContainer = Color(0xFF064D47),
    tertiary = Color(0xFF516B3C)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}