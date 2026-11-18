package com.contreras.campusgps.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.campusgps.model.LugarConDistancia
import java.util.Locale

@Composable
fun DetalleLugar(destino: LugarConDistancia?) {
    if (destino == null) return
    Card(Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("DESTINO SELECCIONADO", style = MaterialTheme.typography.labelMedium)
            Text(destino.lugar.nombre, style = MaterialTheme.typography.titleLarge)
            Text(destino.lugar.categoria)
            Text(destino.metros?.let { String.format(Locale.getDefault(), "%.0f m en línea recta", it) }
                ?: "Obtén tu ubicación para calcular la distancia")
        }
    }
}
