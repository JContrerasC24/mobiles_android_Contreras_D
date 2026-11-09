package com.contreras.clinicasaludplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.contreras.clinicasaludplus.ui.theme.PurpleChipBg
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary

/**
 * Chip de seleccion UNICA para fecha u hora en "Agendar cita".
 * Solo una opcion del grupo puede estar marcada a la vez
 * (la pantalla que lo usa controla eso con un solo mutableStateOf).
 */
@Composable
fun SeleccionChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val fondo = if (seleccionado) PurplePrimary else PurpleChipBg
    val colorTexto = if (seleccionado) Color.White else PurplePrimary

    Text(
        text = texto,
        color = colorTexto,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(end = 10.dp)
            .background(fondo, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    )
}