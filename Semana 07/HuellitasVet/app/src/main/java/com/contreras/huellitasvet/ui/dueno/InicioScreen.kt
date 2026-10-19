package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.MascotaAvatar
import com.contreras.huellitasvet.ui.components.VetButton
import com.contreras.huellitasvet.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    vm: InicioViewModel,
    onMascota: (Int) -> Unit,
    onAgregar: () -> Unit,
    onPedirCita: () -> Unit,
    onVerMascotas: () -> Unit,
    onCerrarSesion: () -> Unit,
    cerrandoSesion: Boolean,
    errorSesion: String?
) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proxima by vm.proximaCita.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("Hola, ${vm.dueno.nombre}", style = MaterialTheme.typography.headlineMedium)
            Text("Cuidemos juntos a tus mascotas", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Próxima cita", style = MaterialTheme.typography.titleLarge)
                    val cita = proxima
                    if (cita == null) {
                        Text("Aún no tienes citas")
                    } else {
                        Text(cita.mascota.mascota.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(cita.servicio.nombre)
                        Text("${cita.cita.fecha} · ${cita.cita.hora}")
                        Text(cita.cita.estado.lowercase().replaceFirstChar { it.titlecase() })
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Mis mascotas", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onAgregar) { Text("Agregar") }
            }
        }
        if (mascotas.isEmpty()) {
            item { Text("Todavía no tienes mascotas registradas.") }
        } else {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(mascotas, key = { it.id }) { mascota ->
                        Card(onClick = { onMascota(mascota.id) }, modifier = Modifier.width(156.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                MascotaAvatar(especie = mascota.especie)
                                Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(mascota.especie.lowercase().replaceFirstChar { it.titlecase() })
                            }
                        }
                    }
                }
            }
        }
        item { TextButton(onClick = onVerMascotas) { Text("Ver todas mis mascotas") } }
        item { VetButton("Pedir una cita", onPedirCita, Modifier.fillMaxWidth()) }
        error?.let { mensaje -> item { Text(mensaje, color = MaterialTheme.colorScheme.error) } }
        errorSesion?.let { mensaje -> item { Text(mensaje, color = MaterialTheme.colorScheme.error) } }
        item {
            TextButton(onClick = onCerrarSesion, enabled = !cerrandoSesion) { Text("Cerrar sesión") }
        }
    }
}
