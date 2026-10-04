package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.util.CalendarioCitas
import java.time.LocalDate

// Presenta cinco días hábiles y sus horarios disponibles.
@Composable
fun FechaHoraScreen(
    navController: NavHostController,
    medicoId: Int
) {
    val hoy = LocalDate.now()
    val medico = Repositorio.obtenerMedico(medicoId)

    // Conserva el desplazamiento semanal durante cambios de configuración.
    var semanasAdelante by rememberSaveable(medicoId) {
        mutableIntStateOf(0)
    }

    val inicio = hoy.plusWeeks(semanasAdelante.toLong())
    val dias = CalendarioCitas.proximosDiasHabiles(inicio)

    // Selecciona inicialmente el primer día de cada ventana.
    var fechaSeleccionada by rememberSaveable(
        medicoId,
        hoy.toString(),
        semanasAdelante
    ) {
        mutableStateOf(dias.first().toString())
    }

    // Evita conservar una fecha fuera de los días mostrados.
    val fechaActual = fechaSeleccionada.takeIf { seleccion ->
        dias.any { it.toString() == seleccion }
    } ?: dias.first().toString()

    // Reinicia la hora cuando cambia la fecha efectiva.
    var horaSeleccionada by rememberSaveable(
        medicoId,
        fechaActual
    ) {
        mutableStateOf<String?>(null)
    }

    var error by remember(medicoId, fechaActual) {
        mutableStateOf<String?>(null)
    }

    // Consulta nuevamente los horarios durante cada recomposición.
    val horarios = Repositorio.horariosDisponibles(
        medicoId = medicoId,
        fecha = fechaActual
    )

    val horaValida = horaSeleccionada?.takeIf {
        it in horarios
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Fecha y hora",
            style = MaterialTheme.typography.headlineMedium
        )

        if (medico == null) {
            Text(
                text = "No se encontró el médico seleccionado.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = medico.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(medico.descripcion)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    enabled = semanasAdelante > 0,
                    onClick = {
                        // Impide retroceder antes de la ventana inicial.
                        if (semanasAdelante > 0) {
                            semanasAdelante -= 1
                            horaSeleccionada = null
                            error = null
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Semana anterior"
                    )
                }

                Text(
                    text = CalendarioCitas.mesYAnio(dias.first()),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(
                    onClick = {
                        semanasAdelante += 1
                        horaSeleccionada = null
                        error = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Semana siguiente"
                    )
                }
            }

            Text(
                text = "Próximos 5 días hábiles",
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                dias.forEach { dia ->
                    val fechaIso = dia.toString()
                    val seleccionado = fechaIso == fechaActual

                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            fechaSeleccionada = fechaIso
                            horaSeleccionada = null
                            error = null
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (seleccionado) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = CalendarioCitas.diaCorto(dia),
                                style = MaterialTheme.typography.labelMedium
                            )

                            // Incluye el mes para aclarar los cambios mensuales.
                            Text(
                                text = "${dia.dayOfMonth}/${dia.monthValue}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }

            Text(
                text = CalendarioCitas.fechaLarga(fechaActual),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.titleMedium
            )

            if (horarios.isEmpty()) {
                Text("No quedan horarios para este día. Selecciona otro.")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = horarios,
                    key = { it }
                ) { hora ->
                    val seleccionada = hora == horaValida

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            horaSeleccionada = hora
                            error = null
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (seleccionada) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    ) {
                        Text(
                            text = hora,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Text(
                text = horaValida?.let {
                    "Hora seleccionada: $it"
                } ?: "Selecciona un horario."
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = horaValida != null,
                onClick = {
                    val hora = horaSeleccionada

                    // Verifica disponibilidad antes de abrir la confirmación.
                    if (
                        hora != null &&
                        hora in Repositorio.horariosDisponibles(
                            medicoId,
                            fechaActual
                        )
                    ) {
                        navController.navigate(
                            Rutas.confirmarCita(
                                medicoId,
                                fechaActual,
                                hora
                            )
                        ) {
                            launchSingleTop = true
                        }
                    } else {
                        horaSeleccionada = null
                        error = "El horario ya no está disponible."
                    }
                }
            ) {
                Text("Continuar")
            }
        }
    }
}