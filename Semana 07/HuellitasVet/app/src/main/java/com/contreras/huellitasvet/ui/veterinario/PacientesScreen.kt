package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.ui.components.HistorialItem
import com.contreras.huellitasvet.ui.components.graficos.GraficoLineas
import com.contreras.huellitasvet.viewmodel.PacientesViewModel
import java.time.LocalDate

@Composable
fun PacientesScreen(vm: PacientesViewModel, onVolver: () -> Unit, onFicha: (Int) -> Unit) {
    val pacientes by vm.pacientes.collectAsStateWithLifecycle()
    val total by vm.totalPacientes.collectAsStateWithLifecycle()
    val texto by vm.texto.collectAsStateWithLifecycle()
    val especie by vm.especie.collectAsStateWithLifecycle()
    val visitas by vm.ultimasVisitas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            VetBrandHeader("Tus pacientes", "Encuentra su ficha y acompaña su evolución")
        }
        item {
            OutlinedTextField(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                value = texto,
                onValueChange = { vm.texto.value = it },
                label = { Text("Buscar mascota o dueño") },
                singleLine = true,
                trailingIcon = if (texto.isNotEmpty()) {
                    {
                        IconButton(onClick = vm::limpiarTexto) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todos" to null, "Perros" to "PERRO", "Gatos" to "GATO", "Otros" to "OTRO")) { (nombre, valor) ->
                    FilterChip(
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        selected = especie == valor,
                        onClick = { vm.especie.value = valor },
                        label = { Text(nombre) }
                    )
                }
            }
        }
        item {
            val filtroActivo = texto.isNotBlank() || especie != null
            val textoCantidad = if (filtroActivo) {
                "Mostrando ${pacientes.size} de $total pacientes"
            } else {
                "$total pacientes registrados"
            }
            Text(
                text = textoCantidad,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        if (pacientes.isEmpty()) {
            item {
                val mensajeVacio = if (total == 0) {
                    "No hay pacientes registrados en el sistema."
                } else {
                    "No hay coincidencias con la búsqueda."
                }
                Text(
                    text = mensajeVacio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(pacientes, key = { it.mascota.id }) { paciente ->
            Card(
                onClick = { onFicha(paciente.mascota.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MascotaAvatar(especie = paciente.mascota.especie, tamano = 64.dp)
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(paciente.mascota.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${paciente.mascota.raza} · ${LocalDate.now().year - paciente.mascota.anioNacimiento} años",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Dueño: ${paciente.dueno.nombre}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            visitas[paciente.mascota.id]?.let { "Última visita: ${fechaCorta(it)}" } ?: "Sin atenciones",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FichaPacienteScreen(vm: PacientesViewModel, onVolver: () -> Unit) {
    val paciente by vm.paciente.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val registrosPeso by vm.historialPeso.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LazyColumn(
        Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { VetHeader("Ficha del paciente", onVolver = onVolver) }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        paciente?.let { p ->
            item {
                VetCard {
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                        MascotaAvatar(especie=p.mascota.especie,tamano=88.dp)
                        Column {
                            Text(p.mascota.nombre,style=MaterialTheme.typography.headlineMedium)
                            Text(p.mascota.raza,style=MaterialTheme.typography.bodyMedium)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical=8.dp))
                    VetDetail("Dueño",p.dueno.nombre)
                    VetDetail("Edad","${LocalDate.now().year-p.mascota.anioNacimiento} años")
                    VetDetail("Sexo",tituloValor(p.mascota.sexo))
                    VetDetail("Peso","${p.mascota.peso} kg")
                }
            }
            item {
                VetCard {
                    Text("Evolución del peso", style = MaterialTheme.typography.titleMedium)
                    GraficoLineas(registros = registrosPeso)
                }
            }
        }
        item { Text("Historial clínico", style = MaterialTheme.typography.titleLarge) }
        if (historial.isEmpty()) item { Text("Aún no tiene atenciones") }
        items(historial, key = { it.cita.id }) { HistorialItem(it) }
    }
}
