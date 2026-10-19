package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.model.EstadoCita

@Composable
fun EstadoCitaChip(estado: String) {
    val colores = MaterialTheme.colorScheme
    val (fondo, texto) = when (estado) {
        EstadoCita.CANCELADA.name -> colores.errorContainer to colores.onErrorContainer
        EstadoCita.CONFIRMADA.name -> colores.primaryContainer to colores.onPrimaryContainer
        EstadoCita.PENDIENTE.name -> colores.tertiaryContainer to colores.onTertiaryContainer
        else -> colores.surfaceVariant to colores.onSurfaceVariant
    }
    Surface(color = fondo, contentColor = texto, shape = RoundedCornerShape(16.dp)) {
        Text(estado.lowercase().replaceFirstChar { it.titlecase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge)
    }
}
