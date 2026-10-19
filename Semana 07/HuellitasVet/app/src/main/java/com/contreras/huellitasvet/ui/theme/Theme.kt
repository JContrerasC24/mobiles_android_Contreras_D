package com.contreras.huellitasvet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.contreras.huellitasvet.model.Rol

private val DuenoClaro = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    background = Color(0xFFF6FDF9),
    surface = Color(0xFFF6FDF9)
)
private val VetClaro = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    background = Color(0xFFF7F9FF),
    surface = Color(0xFFF7F9FF)
)
private val DuenoOscuro = darkColorScheme(
    primary = Color(0xFF6EE7B7),
    onPrimary = Color(0xFF003828),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5)
)
private val VetOscuro = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF082F65),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE)
)

@Composable
fun VetTheme(
    rol: Rol,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = when {
        rol == Rol.ADMIN && darkTheme -> VetOscuro
        rol == Rol.ADMIN -> VetClaro
        darkTheme -> DuenoOscuro
        else -> DuenoClaro
    }
    MaterialTheme(colorScheme = esquema, typography = Typography, content = content)
}

// Conserva el nombre utilizado por las vistas previas de la plantilla.
@Composable
fun HuellitasVetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    VetTheme(rol = Rol.CLIENTE, darkTheme = darkTheme, content = content)
}
