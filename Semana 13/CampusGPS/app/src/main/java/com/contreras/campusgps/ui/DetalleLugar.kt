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
    val contexto = androidx.compose.ui.platform.LocalContext.current
    Card(Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("DESTINO SELECCIONADO", style = MaterialTheme.typography.labelMedium)
            Text(destino.lugar.nombre, style = MaterialTheme.typography.titleLarge)
            Text(destino.lugar.categoria)
            Text(destino.metros?.let { String.format(Locale.getDefault(), "%.0f m en línea recta", it) }
                ?: "Obtén tu ubicación para calcular la distancia")
            TextButton(onClick = {
                val uri = android.net.Uri.parse("https://www.google.com/maps/search/").buildUpon()
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter("query", "${destino.lugar.latitud},${destino.lugar.longitud}").build()
                try {
                    contexto.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                } catch (_: android.content.ActivityNotFoundException) {
                    android.widget.Toast.makeText(contexto, "No hay una aplicación para abrir el mapa", android.widget.Toast.LENGTH_LONG).show()
                }
            }) { Text("Abrir en Google Maps") }
        }
    }
}
