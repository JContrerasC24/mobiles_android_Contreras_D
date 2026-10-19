package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.graficos.GraficoDona
import com.contreras.huellitasvet.ui.components.graficos.GraficoBarras
import com.contreras.huellitasvet.viewmodel.PanelViewModel
import java.util.Locale

@Composable
fun PanelScreen(vm: PanelViewModel, nombre: String, onAgenda: () -> Unit,
    onPacientes: () -> Unit, onServicios: () -> Unit, onCerrarSesion: () -> Unit, ocupado: Boolean, errorSesion: String?) {
    val hoy by vm.citasHoy.collectAsStateWithLifecycle()
    val pendientes by vm.pendientes.collectAsStateWithLifecycle()
    val atendidas by vm.atendidas.collectAsStateWithLifecycle()
    val ingresos by vm.ingresosMes.collectAsStateWithLifecycle()
    val estados by vm.porEstado.collectAsStateWithLifecycle()
    val servicios by vm.porServicio.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Panel de la clínica", style = MaterialTheme.typography.headlineMedium)
            Text("Hola, $nombre")
        }
        item {
            val contadores = listOf(Triple("Citas hoy", hoy, colores.primaryContainer),
                Triple("Pendientes", pendientes, colores.tertiaryContainer),
                Triple("Atendidas", atendidas, colores.secondaryContainer))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                contadores.forEach { (etiqueta, cantidad, color) ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(etiqueta); Text(cantidad.toString(), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = colores.primaryContainer, contentColor = colores.onPrimaryContainer)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Ingresos del mes")
                    Text("S/ ${String.format(Locale.US, "%.2f", ingresos)}", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
        if (pendientes > 0) item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tienes $pendientes citas pendientes")
                    TextButton(onClick = onAgenda) { Text("Ver") }
                }
            }
        }
        item { Button(onClick = onAgenda) { Text("Ver agenda") } }
        item { Text("Citas por estado", style = MaterialTheme.typography.titleLarge); GraficoDona(estados) }
        item { Text("Citas por servicio", style = MaterialTheme.typography.titleLarge); GraficoBarras(servicios) }
        error?.let { item { Text(it, color = colores.error) } }
        errorSesion?.let { item { Text(it, color = colores.error) } }
        item {
            TextButton(onClick = onPacientes) { Text("Pacientes") }
            TextButton(onClick = onServicios) { Text("Servicios") }
            TextButton(onClick = onCerrarSesion, enabled = !ocupado) { Text("Cerrar sesión") }
        }
    }
}
