package com.contreras.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Muestra las reservas del paciente con navegación al detalle.
@Composable
fun MisCitasScreen(navController: NavHostController) {
    val citas = Repositorio.citasDelUsuario()

    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.MIS_CITAS
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Mis citas",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "Consulta tus reservas y sus detalles.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Presenta un mensaje cuando no existen reservas.
            if (citas.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Todavía no tienes citas",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Agenda tu primera consulta desde aquí.")
                        }
                    }
                }
            }

            items(
                items = citas,
                key = { it.id }
            ) { cita ->
                val medico = Repositorio.obtenerMedico(cita.medicoId)

                val especialidad = medico?.let {
                    Repositorio.obtenerEspecialidad(it.especialidadId)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(
                            Rutas.detalleCita(cita.id)
                        ) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                            text = "Ver detalle",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(Rutas.ESPECIALIDADES) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Agendar nueva cita")
                }
            }
        }
    }
}