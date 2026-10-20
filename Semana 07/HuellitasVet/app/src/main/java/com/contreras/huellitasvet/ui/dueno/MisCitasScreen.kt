package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.model.Horarios
import com.contreras.huellitasvet.model.ReglasCitas
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.viewmodel.MisCitasViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MisCitasScreen(vm: MisCitasViewModel, onInicio: () -> Unit, onPedir: () -> Unit) {
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()
    val cancelando by vm.cancelando.collectAsStateWithLifecycle()
    val reprogramando by vm.reprogramando.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val citaAReprogramar by vm.citaAReprogramar.collectAsStateWithLifecycle()
    val fechaReprogramar by vm.fechaReprogramar.collectAsStateWithLifecycle()
    val horaReprogramar by vm.horaReprogramar.collectAsStateWithLifecycle()
    val horasOcupadasReprogramar by vm.horasOcupadasReprogramar.collectAsStateWithLifecycle()

    val ocupado = cancelando || reprogramando
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var confirmarId by rememberSaveable { mutableStateOf<Int?>(null) }
    val seleccionada = (proximas + historial).firstOrNull { it.cita.id == confirmarId }
    val citas = if (pestana == 0) proximas else historial

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(onClick = onPedir) {
                Icon(Icons.Default.Add, contentDescription = "Pedir una cita")
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                VetHeader("Mis citas", subtitulo = "Puedes reprogramar o cancelar con más de 2 horas de anticipación")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VetChoice("Próximas", pestana == 0, { pestana = 0 }, Modifier.weight(1f))
                    VetChoice("Historial", pestana == 1, { pestana = 1 }, Modifier.weight(1f))
                }
            }
            mensaje?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
            if (citas.isEmpty()) item {
                Text(if (pestana == 0) "No tienes próximas citas" else "Aún no tienes citas en el historial")
            }
            items(citas, key = { it.cita.id }) { detalle ->
                val puedeAccion = ReglasCitas.puedeReprogramar(detalle.cita, ahora)
                CitaCard(
                    detalle = detalle,
                    puedeCancelar = puedeAccion,
                    puedeReprogramar = puedeAccion,
                    ocupado = ocupado,
                    onCancelar = { confirmarId = detalle.cita.id },
                    onReprogramar = { vm.iniciarReprogramacion(detalle) }
                )
            }
        }
    }

    if (confirmarId != null) {
        val permitido = seleccionada?.let { ReglasCitas.puedeCancelar(it.cita, ahora) } == true
        AlertDialog(
            onDismissRequest = { confirmarId = null },
            title = { Text("Cancelar cita") },
            text = {
                Text(
                    if (permitido)
                        "¿Quieres cancelar la cita de ${seleccionada?.mascota?.mascota?.nombre} del ${seleccionada?.cita?.fecha} a las ${seleccionada?.cita?.hora}?"
                    else "Ya no puedes cancelar esta cita"
                )
            },
            confirmButton = {
                if (permitido) TextButton(enabled = !ocupado, onClick = {
                    seleccionada?.let(vm::cancelar)
                    confirmarId = null
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarId = null }) { Text(if (permitido) "Volver" else "Cerrar") }
            }
        )
    }

    citaAReprogramar?.let { cita ->
        ReprogramarCitaDialog(
            cita = cita,
            fecha = fechaReprogramar,
            hora = horaReprogramar,
            horasOcupadas = horasOcupadasReprogramar,
            ocupado = ocupado,
            ahora = ahora,
            onElegirFecha = vm::elegirFechaReprogramacion,
            onElegirHora = vm::elegirHoraReprogramacion,
            onConfirmar = vm::confirmarReprogramacion,
            onDismiss = vm::cancelarReprogramacion
        )
    }
}

@Composable
fun ReprogramarCitaDialog(
    cita: CitaConDetalle,
    fecha: LocalDate,
    hora: String?,
    horasOcupadas: List<String>,
    ocupado: Boolean,
    ahora: LocalDateTime,
    onElegirFecha: (LocalDate) -> Unit,
    onElegirHora: (String) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reprogramar cita") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "${cita.mascota.mascota.nombre} · ${cita.servicio.nombre}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Fecha", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((0..7).map { ahora.toLocalDate().plusDays(it.toLong()) }) { dia ->
                        FilterChip(
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            selected = dia == fecha,
                            enabled = !ocupado,
                            onClick = { onElegirFecha(dia) },
                            label = { Text(dia.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.forLanguageTag("es-PE")))) }
                        )
                    }
                }

                Text("Horario", style = MaterialTheme.typography.titleSmall)
                Horarios.todos.chunked(3).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { turno ->
                            val libre = Horarios.disponible(fecha, turno, horasOcupadas, ahora)
                            FilterChip(
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                selected = hora == turno && libre,
                                enabled = libre && !ocupado,
                                onClick = { onElegirHora(turno) },
                                label = {
                                    Text(
                                        turno,
                                        textDecoration = if (libre) TextDecoration.None else TextDecoration.LineThrough
                                    )
                                }
                            )
                        }
                    }
                }

                VetCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Resumen de reprogramación", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Anterior: ${cita.cita.fecha} a las ${cita.cita.hora}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Nueva: $fecha a las ${hora ?: "Sin seleccionar"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                enabled = hora != null && Horarios.disponible(fecha, hora, horasOcupadas, ahora) && !ocupado
            ) {
                Text(if (ocupado) "Guardando…" else "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !ocupado) {
                Text("Cancelar")
            }
        }
    )
}
