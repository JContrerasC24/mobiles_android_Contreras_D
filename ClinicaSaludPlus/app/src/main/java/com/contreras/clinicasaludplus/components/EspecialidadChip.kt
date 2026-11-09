package com.contreras.clinicasaludplus.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.contreras.clinicasaludplus.ui.theme.PurpleChipBg
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary

/**
 * Chip de especialidad usado en la LazyRow de Inicio.
 * Cuando esta seleccionado se pinta de morado (igual que el mockup).
 */
@Composable
fun EspecialidadChip(
    nombre: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(nombre) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = PurpleChipBg,
            selectedContainerColor = PurplePrimary,
            labelColor = PurplePrimary,
            selectedLabelColor = Color.White
        ),
        modifier = Modifier.padding(end = 8.dp)
    )
}