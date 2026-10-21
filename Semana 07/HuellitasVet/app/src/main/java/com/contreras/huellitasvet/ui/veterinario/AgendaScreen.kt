package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.EstadoCita
import com.contreras.huellitasvet.model.ReglasAgenda
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.ui.components.EstadoCitaChip
import com.contreras.huellitasvet.viewmodel.AgendaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaScreen(vm: AgendaViewModel, onVolver: () -> Unit, onAtender: (Int) -> Unit) {
    val fecha by vm.fechaSeleccionada.collectAsStateWithLifecycle()
    val citas by vm.citas.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var cancelarId by rememberSaveable { mutableStateOf<Int?>(null) }
    val seleccionada = citas.firstOrNull { it.cita.id == cancelarId }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            VetBrandHeader("Tu agenda", "Cada visita, un cuidado importante")
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((0..7).map { LocalDate.now().plusDays(it.toLong()) }) { dia ->
                    VetDay(dia, fecha == dia, { vm.elegirFecha(dia) }, !ocupado)
                }
            }
        }
        mensaje?.let { item { Text(it) } }
        item { Text("${fechaCorta(fecha.toString())} · ${citas.size} citas", style=MaterialTheme.typography.titleMedium) }
        if (citas.isEmpty()) item { VetEmpty("Un día sin citas", "Las reservas de esta fecha aparecerán aquí.") }
        items(citas, key = { it.cita.id }) { detalle ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text(detalle.cita.hora, style = MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
                    VerticalDivider(Modifier.padding(start = 16.dp).weight(1f))
                }
                VetCard(Modifier.weight(1f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.CenterVertically) {
                            MascotaAvatar(especie=detalle.mascota.mascota.especie,tamano=40.dp)
                            Column(Modifier.weight(1f)) {
                                Text("${detalle.mascota.mascota.nombre} · ${detalle.servicio.nombre}",style=MaterialTheme.typography.titleSmall)
                                Text("Dueño: ${detalle.mascota.dueno.nombre}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        EstadoCitaChip(detalle.cita.estado)
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End,verticalAlignment=Alignment.CenterVertically) {
                        if (detalle.cita.estado == EstadoCita.PENDIENTE.name) {
                            Button(enabled = !ocupado, onClick = { vm.confirmar(detalle) }) { Text("Confirmar") }
                        }
                        if (detalle.cita.estado == EstadoCita.CONFIRMADA.name) {
                            Button(enabled = !ocupado, onClick = { onAtender(detalle.cita.id) }) { Text("Atender") }
                        }
                        if (ReglasAgenda.permite(detalle.cita.estado, EstadoCita.CANCELADA)) {
                            TextButton(enabled = !ocupado, onClick = { cancelarId = detalle.cita.id }) { Text("Cancelar") }
                        }
                        }
                    }
                }
            }
        }
    }
    if (cancelarId != null) {
        val permitido = seleccionada?.let { ReglasAgenda.permite(it.cita.estado, EstadoCita.CANCELADA) } == true
        AlertDialog(onDismissRequest = { cancelarId = null },
            title = { Text("Cancelar cita") },
            text = { Text(if (permitido) "¿Quieres cancelar esta cita?" else "La cita ya no permite esta acción.") },
            confirmButton = {
                if (permitido) TextButton(onClick = { seleccionada?.let(vm::cancelar); cancelarId = null }) { Text("Sí, cancelar") }
            },
            dismissButton = { TextButton(onClick = { cancelarId = null }) { Text("Volver") } })
    }
}
