package com.contreras.saludplus.citas.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Aplica colores claros inspirados en la referencia.
private val ColoresClaros = lightColorScheme(
    primary = AzulSaludPlus,
    onPrimary = Color.White,
    primaryContainer = AzulClaroSaludPlus,
    onPrimaryContainer = TextoSaludPlus,
    secondary = ColorCitas,
    onSecondary = Color.White,
    secondaryContainer = FondoCitas,
    onSecondaryContainer = ColorCitas,
    tertiary = ColorPerfil,
    onTertiary = Color.White,
    tertiaryContainer = FondoPerfil,
    onTertiaryContainer = ColorPerfil,
    background = FondoSaludPlus,
    onBackground = TextoSaludPlus,
    surface = Color.White,
    onSurface = TextoSaludPlus,
    surfaceVariant = AzulClaroSaludPlus,
    onSurfaceVariant = TextoSecundarioSaludPlus,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    outline = BordeSaludPlus,
    outlineVariant = BordeSaludPlus,
    error = Color(0xFFB91C1C),
    onError = Color.White
)

// Mantiene una alternativa oscura cuando se solicita explícitamente.
private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF93B4FF),
    onPrimary = Color(0xFF082F78),
    primaryContainer = Color(0xFF163D80),
    onPrimaryContainer = Color(0xFFDBEAFE),
    background = Color(0xFF101827),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF182235),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF26364F),
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceContainerHighest = Color(0xFF26364F),
    outline = Color(0xFF64748B)
)

// Unifica las esquinas de los componentes.
private val FormasSaludPlus = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Aplica el tema visual a toda la aplicación.
@Composable
fun SaludPlusCitasTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colores = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexto = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(contexto)
            } else {
                dynamicLightColorScheme(contexto)
            }
        }

        darkTheme -> ColoresOscuros

        else -> ColoresClaros
    }

    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        shapes = FormasSaludPlus,
        content = content
    )
}