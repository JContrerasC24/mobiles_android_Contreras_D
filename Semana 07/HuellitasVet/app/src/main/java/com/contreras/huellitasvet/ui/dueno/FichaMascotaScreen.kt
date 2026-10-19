package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.HistorialItem
import com.contreras.huellitasvet.ui.components.MascotaAvatar
import com.contreras.huellitasvet.ui.components.VetButton
import com.contreras.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate

@Composable
fun FichaMascotaScreen(
    vm: MascotaViewModel, mascotaId: Int,
    onVolver: () -> Unit, onEditar: () -> Unit, onPedirCita: () -> Unit
) {
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val finalizado by vm.finalizado.collectAsStateWithLifecycle()
    val historial = remember(vm, mascotaId) { vm.historial(mascotaId) }
    val atenciones by historial.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(finalizado) { if (finalizado) onVolver() }

    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TextButton(onClick = onVolver, enabled = !ocupado) { Text("Volver") } }
        if (cargando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        mensaje?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        val m = mascota
        if (m != null) {
            item {
                MascotaAvatar(especie = m.especie)
                Text(m.nombre, style = MaterialTheme.typography.headlineMedium)
                Text("${m.especie.lowercase().replaceFirstChar { it.titlecase() }} · ${m.raza}")
                val edad = LocalDate.now().year - m.anioNacimiento
                Text("$edad ${if (edad == 1) "año" else "años"} · ${m.peso} kg")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VetButton("Pedir cita", onPedirCita, enabled = !ocupado)
                    OutlinedButton(onClick = onEditar, enabled = !ocupado) { Text("Editar") }
                }
            }
            item {
                TabRow(selectedTabIndex = pestana) {
                    Tab(selected = pestana == 0, onClick = { pestana = 0 }, text = { Text("Datos") })
                    Tab(selected = pestana == 1, onClick = { pestana = 1 }, text = { Text("Historial clínico") })
                }
            }
            if (pestana == 0) {
                item {
                    Text("Sexo: ${m.sexo.lowercase().replaceFirstChar { it.titlecase() }}")
                    Text("Año de nacimiento: ${m.anioNacimiento}")
                    Text("Peso actual: ${m.peso} kg")
                }
                item {
                    TextButton(onClick = { confirmarEliminar = true }, enabled = !ocupado) {
                        Text("Eliminar mascota", color = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                if (atenciones.isEmpty()) item { Text("Aún no tiene atenciones") }
                items(atenciones, key = { it.cita.id }) { HistorialItem(it) }
            }
        }
    }
    if (confirmarEliminar && mascota != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar mascota") },
            text = { Text("¿Quieres eliminar a ${mascota?.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    mascota?.let(vm::eliminar)
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
