package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import java.util.Locale

@Composable
fun CitaCard(
    detalle: CitaConDetalle,
    puedeCancelar: Boolean,
    puedeReprogramar: Boolean = puedeCancelar,
    ocupado: Boolean,
    onCancelar: () -> Unit,
    onReprogramar: (() -> Unit)? = null
) {
    VetCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(detalle.mascota.mascota.nombre, style = MaterialTheme.typography.titleLarge)
            Text(detalle.servicio.nombre)
            Text("${detalle.cita.fecha} · ${detalle.cita.hora}")
            Text("S/ ${String.format(Locale.US, "%.2f", detalle.cita.precio)}")
            EstadoCitaChip(detalle.cita.estado)
            if (puedeCancelar || (puedeReprogramar && onReprogramar != null)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (puedeReprogramar && onReprogramar != null) {
                        Button(onClick = onReprogramar, enabled = !ocupado) {
                            Text("Reprogramar")
                        }
                    }
                    if (puedeCancelar) {
                        OutlinedButton(onClick = onCancelar, enabled = !ocupado) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}
