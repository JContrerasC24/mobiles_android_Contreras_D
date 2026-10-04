package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Muestra la confirmación y los datos de la cita guardada.
@Composable
fun CitaExitosaScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (cita == null) {
            Text(
                text = "Cita no disponible",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "No encontramos esta cita en tu sesión actual."
            )
        } else {
            Text(
                text = "¡Cita agendada!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tu reserva se registró correctamente.",
                style = MaterialTheme.typography.bodyLarge
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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

                    // Muestra el motivo cuando el paciente lo completó.
                    if (cita.motivo.isNotBlank()) {
                        Text("Motivo: ${cita.motivo}")
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Abre Mis citas y elimina la pantalla de confirmación.
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.INICIO) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                }
            ) {
                Text("Ver mis citas")
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Regresa al Inicio existente.
                val regreso = navController.popBackStack(
                    Rutas.INICIO,
                    false
                )

                // Abre Inicio cuando no existe en el historial.
                if (!regreso) {
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }
        ) {
            Text("Volver al inicio")
        }
    }
}