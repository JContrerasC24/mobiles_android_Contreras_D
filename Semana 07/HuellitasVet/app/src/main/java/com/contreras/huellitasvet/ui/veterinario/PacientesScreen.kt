package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.MascotaAvatar
import com.contreras.huellitasvet.ui.components.HistorialItem
import com.contreras.huellitasvet.viewmodel.PacientesViewModel
import java.time.LocalDate

@Composable
fun PacientesScreen(vm: PacientesViewModel, onVolver: () -> Unit, onFicha: (Int) -> Unit) {
    val pacientes by vm.pacientes.collectAsStateWithLifecycle()
    val texto by vm.texto.collectAsStateWithLifecycle()
    val especie by vm.especie.collectAsStateWithLifecycle()
    val visitas by vm.ultimasVisitas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            TextButton(onClick = onVolver) { Text("Volver al panel") }
            Text("Pacientes", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            OutlinedTextField(value = texto, onValueChange = { vm.texto.value = it },
                label = { Text("Buscar mascota o dueño") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todos" to null, "Perros" to "PERRO", "Gatos" to "GATO", "Otros" to "OTRO")) { (nombre, valor) ->
                    FilterChip(selected = especie == valor, onClick = { vm.especie.value = valor }, label = { Text(nombre) })
                }
            }
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        if (pacientes.isEmpty()) item { Text("No hay pacientes que coincidan con la búsqueda.") }
        items(pacientes, key = { it.mascota.id }) { paciente ->
            Card(onClick = { onFicha(paciente.mascota.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MascotaAvatar(especie = paciente.mascota.especie)
                    Text(paciente.mascota.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("${paciente.mascota.raza} · ${LocalDate.now().year - paciente.mascota.anioNacimiento} años")
                    Text("Dueño: ${paciente.dueno.nombre}")
                    Text("Última visita: ${visitas[paciente.mascota.id] ?: "Sin atenciones"}")
                }
            }
        }
    }
}

@Composable
fun FichaPacienteScreen(vm: PacientesViewModel, onVolver: () -> Unit) {
    val paciente by vm.paciente.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TextButton(onClick = onVolver) { Text("Volver a pacientes") } }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        paciente?.let { p ->
            item {
                MascotaAvatar(especie = p.mascota.especie)
                Text(p.mascota.nombre, style = MaterialTheme.typography.headlineMedium)
                Text("Dueño: ${p.dueno.nombre}")
                Text("${p.mascota.especie} · ${p.mascota.raza} · ${p.mascota.sexo}")
                Text("${LocalDate.now().year - p.mascota.anioNacimiento} años · ${p.mascota.peso} kg")
            }
        }
        item { Text("Historial clínico", style = MaterialTheme.typography.titleLarge) }
        if (historial.isEmpty()) item { Text("Aún no tiene atenciones") }
        items(historial, key = { it.cita.id }) { HistorialItem(it) }
    }
}
