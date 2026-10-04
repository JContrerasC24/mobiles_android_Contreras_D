package com.contreras.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Presenta una reserva y permite cancelarla mediante confirmación.
@Composable
fun DetalleCitaScreen(
    navController: NavHostController,
    citaId: Int
) {
    val cita = Repositorio.obtenerCita(citaId)

    val medico = cita?.let {
        Repositorio.obtenerMedico(it.medicoId)
    }

    val especialidad = medico?.let {
        Repositorio.obtenerEspecialidad(it.especialidadId)
    }

    var mostrarDialogo by rememberSaveable(citaId) {
        mutableStateOf(false)
    }

    var error by remember(citaId) {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Detalle de cita",
            style = MaterialTheme.typography.headlineMedium
        )

        if (cita == null) {
            Text("Esta cita ya no está disponible.")
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Reserva #${cita.id}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = medico?.nombre ?: "Médico no disponible",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = especialidad?.nombre
                            ?: "Especialidad no disponible"
                    )

                    Text("Fecha: ${cita.fecha}")
                    Text("Hora: ${cita.hora}")

                    Text(
                        text = if (cita.motivo.isBlank()) {
                            "Motivo: no especificado"
                        } else {
                            "Motivo: ${cita.motivo}"
                        }
                    )
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { mostrarDialogo = true }
            ) {
                Text(
                    text = "Cancelar cita",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Solicita confirmación antes de eliminar la reserva.
    if (mostrarDialogo && cita != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Cancelar esta cita?") },
            text = {
                Text(
                    "Se eliminará la reserva del ${cita.fecha} a las ${cita.hora}."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cancelada = Repositorio.cancelarCita(citaId)
                        mostrarDialogo = false

                        if (cancelada) {
                            // Regresa a la lista después de cancelar.
                            val regreso = navController.popBackStack()

                            if (!regreso) {
                                navController.navigate(Rutas.MIS_CITAS) {
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            error = "No se pudo cancelar la cita."
                        }
                    }
                ) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Conservar cita")
                }
            }
        )
    }
}