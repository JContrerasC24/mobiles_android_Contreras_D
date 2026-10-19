package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun HistorialItem(detalle: CitaConDetalle) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(detalle.servicio.nombre, style = MaterialTheme.typography.titleMedium)
            Text("${detalle.cita.fecha} · ${detalle.cita.hora}")
            Text("Diagnóstico: ${detalle.cita.diagnostico ?: "Sin registro"}")
            Text("Tratamiento: ${detalle.cita.tratamiento ?: "Sin registro"}")
            detalle.cita.pesoRegistrado?.let { Text("Peso registrado: $it kg") }
        }
    }
}
