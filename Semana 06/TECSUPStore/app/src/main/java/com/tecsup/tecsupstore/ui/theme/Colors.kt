package com.contreras.myapplication.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Color morado oscuro principal
val PurpleDark = Color(0xFF3D2266)

// Color morado medio para barras y acentos
val PurpleMid = Color(0xFF6A4C9C)

// Color morado claro para resaltados
val PurpleLight = Color(0xFFB39DDB)

// Degradado institucional de 3 tonos
val BrandGradient = Brush.verticalGradient(
    colors = listOf(PurpleDark, PurpleMid, PurpleLight)
)

// Gris suave para tarjetas de lista
val CardGray = Color(0xFFF2F2F2)

// Gris para tarjetas de detalle
val DetailCardGray = Color(0xFFE0E0E0)

// Fondo lila claro para la aplicación
val BackgroundLilac = Color(0xFFF5F2FB)

// Rojo para acciones de cerrar sesión o error
val ErrorRed = Color(0xFFD32F2F)

// URL de avatar principal de Jose Contreras
const val JOSE_AVATAR_URL = "https://randomuser.me/api/portraits/men/32.jpg"
