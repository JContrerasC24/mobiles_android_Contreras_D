package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Horarios
import com.contreras.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PedirCitaScreen(vm: PedirCitaViewModel, onVolver: () -> Unit, onCreada: () -> Unit) {
    val aceptar by vm.aceptarCitas.collectAsStateWithLifecycle()
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mascotaId by vm.mascota.collectAsStateWithLifecycle()
    val servicioId by vm.servicio.collectAsStateWithLifecycle()
    val fecha by vm.fecha.collectAsStateWithLifecycle()
    val hora by vm.hora.collectAsStateWithLifecycle()
    val nota by vm.nota.collectAsStateWithLifecycle()
    val disponibilidad by vm.disponibilidad.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val creada by vm.creada.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val mascota = mascotas.firstOrNull { it.id == mascotaId }
    val servicio = servicios.firstOrNull { it.id == servicioId }
    val horas = disponibilidad.horas
    val listo = disponibilidad.fecha == fecha && horas != null
    val valido = aceptar && mascota != null && servicio != null && hora != null &&
        listo && Horarios.disponible(fecha, hora!!, horas.orEmpty(), ahora)
    LaunchedEffect(creada) { if (creada != null) onCreada() }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            TextButton(onClick = onVolver, enabled = !ocupado) { Text("Volver") }
            Text("Pedir una cita", style = MaterialTheme.typography.headlineMedium)
        }
        if (!aceptar) item { Text("La clínica no está recibiendo citas", color = MaterialTheme.colorScheme.error) }
        item { Text("Mascota", style = MaterialTheme.typography.titleLarge) }
        if (mascotas.isEmpty()) item { Text("Primero registra una mascota para pedir una cita.") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mascotas, key = { it.id }) { m ->
                    FilterChip(selected = mascotaId == m.id, enabled = !ocupado,
                        onClick = { vm.mascota.value = m.id }, label = { Text(m.nombre) })
                }
            }
        }
        item { Text("Servicio", style = MaterialTheme.typography.titleLarge) }
        if (servicios.isEmpty()) item { Text("No hay servicios disponibles.") }
        items(servicios, key = { "servicio-${it.id}" }) { s ->
            OutlinedCard(onClick = { vm.servicio.value = s.id }, enabled = !ocupado,
                modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp)) {
                    RadioButton(selected = servicioId == s.id, onClick = null)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(s.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${s.duracionMin} min · S/ ${String.format(Locale.US, "%.2f", s.precio)}")
                    }
                }
            }
        }
        item { Text("Fecha", style = MaterialTheme.typography.titleLarge) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((0..7).map { ahora.toLocalDate().plusDays(it.toLong()) }) { dia ->
                    FilterChip(selected = dia == fecha, enabled = !ocupado,
                        onClick = { vm.elegirFecha(dia) },
                        label = { Text(dia.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "PE")))) })
                }
            }
        }
        item { Text("Horario", style = MaterialTheme.typography.titleLarge) }
        if (!listo) item { Text("Consultando disponibilidad…") }
        items(Horarios.todos.chunked(3)) { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { turno ->
                    val libre = listo && Horarios.disponible(fecha, turno, horas.orEmpty(), ahora)
                    FilterChip(selected = hora == turno && libre, enabled = libre && !ocupado,
                        onClick = { vm.hora.value = turno },
                        label = { Text(turno, textDecoration = if (libre) TextDecoration.None else TextDecoration.LineThrough) })
                }
            }
        }
        item {
            OutlinedTextField(value = nota, onValueChange = { vm.nota.value = it },
                enabled = !ocupado, label = { Text("Nota (opcional)") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleLarge)
                    Text("Mascota: ${mascota?.nombre ?: "Sin seleccionar"}")
                    Text("Servicio: ${servicio?.nombre ?: "Sin seleccionar"}")
                    Text("Fecha: $fecha · Hora: ${hora ?: "Sin seleccionar"}")
                    Text("Precio: ${servicio?.let { "S/ " + String.format(Locale.US, "%.2f", it.precio) } ?: "—"}")
                }
            }
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        item {
            Button(onClick = vm::confirmar, enabled = valido && !ocupado && creada == null,
                modifier = Modifier.fillMaxWidth()) { Text(if (ocupado) "Reservando…" else "Confirmar cita") }
        }
    }
}
