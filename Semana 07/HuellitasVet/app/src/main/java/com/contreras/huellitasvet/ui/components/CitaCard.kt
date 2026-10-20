package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import java.util.Locale

@Composable
fun CitaCard(detalle: CitaConDetalle, puedeCancelar: Boolean,
    ocupado: Boolean, onCancelar: () -> Unit) {
    VetCard {
        Column( verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(detalle.mascota.mascota.nombre, style = MaterialTheme.typography.titleLarge)
            Text(detalle.servicio.nombre)
            Text("${detalle.cita.fecha} · ${detalle.cita.hora}")
            Text("S/ ${String.format(Locale.US, "%.2f", detalle.cita.precio)}")
            EstadoCitaChip(detalle.cita.estado)
            if (puedeCancelar) {
                OutlinedButton(onClick = onCancelar, enabled = !ocupado) { Text("Cancelar") }
            }
        }
    }
}
