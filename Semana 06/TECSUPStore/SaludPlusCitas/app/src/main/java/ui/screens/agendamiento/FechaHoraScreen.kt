package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*
import com.contreras.saludplus.citas.util.CalendarioCitas
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.delay

// Conserva el calendario dinámico dentro del diseño compacto.
@Composable
fun FechaHoraScreen(navController: NavHostController, medicoId: Int) {
    // Actualiza fechas y disponibilidad si la pantalla permanece abierta.
    val momento by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(30_000)
            value = System.currentTimeMillis()
        }
    }
    val hoy = Instant.ofEpochMilli(momento).atZone(ZoneId.systemDefault()).toLocalDate()
    var semanasAdelante by rememberSaveable(medicoId) { mutableIntStateOf(0) }
    val dias = CalendarioCitas.proximosDiasHabiles(hoy.plusWeeks(semanasAdelante.toLong()))
    var fechaSeleccionada by rememberSaveable(medicoId, hoy.toString(), semanasAdelante) {
        mutableStateOf(dias.first().toString())
    }
    val fechaActual = fechaSeleccionada.takeIf { seleccion ->
        dias.any { it.toString() == seleccion }
    } ?: dias.first().toString()
    var horaSeleccionada by rememberSaveable(medicoId, fechaActual) { mutableStateOf<String?>(null) }
    var error by remember(medicoId, fechaActual) { mutableStateOf<String?>(null) }
    val medico = Repositorio.obtenerMedico(medicoId)
    val horarios = Repositorio.horariosDisponibles(medicoId, fechaActual)
    val horaValida = horaSeleccionada?.takeIf { it in horarios }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CabeceraSalud("Seleccionar fecha y hora", { navController.popBackStack() }) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall) }
                    BotonSalud("Continuar", habilitado = medico != null && horaValida != null) {
                        val hora = horaSeleccionada
                        // Comprueba nuevamente el horario antes de avanzar.
                        if (hora != null && hora in Repositorio.horariosDisponibles(medicoId, fechaActual)) {
                            navController.navigate(Rutas.confirmarCita(medicoId, fechaActual, hora)) {
                                launchSingleTop = true
                            }
                        } else {
                            horaSeleccionada = null
                            error = "Este horario ya no está disponible. Elige otro."
                        }
                    }
                }
            }
        }
    ) { margen ->
        // Permite desplazar toda la selección en pantallas pequeñas.
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(margen),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                if (medico != null) FichaMedico(medico) else Text("Médico no disponible.")
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(enabled = semanasAdelante > 0, onClick = {
                        if (semanasAdelante > 0) {
                            semanasAdelante -= 1
                            horaSeleccionada = null
                            error = null
                        }
                    }) { Icon(Icons.Filled.ChevronLeft, "Semana anterior") }
                    Text(CalendarioCitas.mesYAnio(dias.first()), style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = {
                        semanasAdelante += 1
                        horaSeleccionada = null
                        error = null
                    }) { Icon(Icons.Filled.ChevronRight, "Semana siguiente") }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dias.forEach { dia ->
                        val iso = dia.toString()
                        val activo = iso == fechaActual
                        Card(
                            onClick = { fechaSeleccionada = iso; horaSeleccionada = null; error = null },
                            modifier = Modifier.weight(1f).semantics {
                                selected = activo
                                contentDescription = CalendarioCitas.fechaLarga(iso)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (activo) MaterialTheme.colorScheme.primary else Color(0xFFF5F7FB),
                                contentColor = if (activo) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(CalendarioCitas.diaCorto(dia), style = MaterialTheme.typography.labelSmall)
                                Text("${dia.dayOfMonth}", style = MaterialTheme.typography.titleMedium)
                                if (dias.first().month != dias.last().month) {
                                    Text("/${dia.monthValue}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(if (horarios.isEmpty()) "Sin horarios disponibles. Selecciona otro día." else "Horarios disponibles",
                    Modifier.padding(top = 4.dp), style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(horarios, key = { it }) { hora ->
                val activo = hora == horaValida
                Card(onClick = { horaSeleccionada = hora; error = null },
                    modifier = Modifier.fillMaxWidth().semantics { selected = activo },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activo) MaterialTheme.colorScheme.primary else Color(0xFFF5F7FB),
                        contentColor = if (activo) Color.White else MaterialTheme.colorScheme.onSurface
                    )) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = Alignment.Center) {
                        Text(hora, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
