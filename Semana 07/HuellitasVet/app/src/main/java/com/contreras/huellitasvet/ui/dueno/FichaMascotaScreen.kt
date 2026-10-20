package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.HistorialItem
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.ui.components.graficos.GraficoLineas
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
    val historialPesoFlow = remember(vm, mascotaId) { vm.historialPeso(mascotaId) }
    val registrosPeso by historialPesoFlow.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(finalizado) { if (finalizado) onVolver() }

    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { VetHeader(mascota?.nombre ?: "Ficha de mascota",onVolver={if(!ocupado)onVolver()}) }
        if (cargando) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        mensaje?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        val m = mascota
        if (m != null) {
            item {
                VetCard {
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                        MascotaAvatar(especie=m.especie)
                        Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(m.nombre,style=MaterialTheme.typography.titleLarge)
                            Text("${m.raza} · ${LocalDate.now().year-m.anioNacimiento} años",style=MaterialTheme.typography.bodySmall)
                            Text("${tituloValor(m.sexo)} · ${m.peso} kg",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VetButton("Pedir cita", onPedirCita, Modifier.weight(1f), enabled = !ocupado)
                    OutlinedButton(onClick = onEditar, enabled = !ocupado,modifier=Modifier.weight(1f).heightIn(min=48.dp)) { Text("Editar") }
                }
            }
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    VetChoice("Datos",pestana==0,{pestana=0},Modifier.weight(1f))
                    VetChoice("Historial clínico",pestana==1,{pestana=1},Modifier.weight(1f))
                }
            }
            if (pestana == 0) {
                item {
                    VetCard {
                        Text("Evolución del peso", style = MaterialTheme.typography.titleMedium)
                        GraficoLineas(registros = registrosPeso)
                    }
                }
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
