package com.contreras.saludplus.citas.ui.screens.notificaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Genera avisos a partir de las reservas del paciente.
@Composable
fun NotificacionesScreen(navController: NavHostController) {

    // Transforma cada cita en un identificador y mensaje.
    val notificaciones = Repositorio.citasDelUsuario().map { cita ->
        val medico = Repositorio.obtenerMedico(cita.medicoId)

        val mensaje = "Consulta con ${medico?.nombre ?: "tu médico"} " +
                "el ${cita.fecha} a las ${cita.hora}."

        cita.id to mensaje
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Volver")
            }
        }

        item {
            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        if (notificaciones.isEmpty()) {
            item {
                Text("No tienes avisos de citas por el momento.")
            }
        }

        items(
            items = notificaciones,
            key = { it.first }
        ) { notificacion ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(
                        Rutas.detalleCita(notificacion.first)
                    ) {
                        launchSingleTop = true
                    }
                }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Cita registrada",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(notificacion.second)

                    Text(
                        text = "Consultar detalle",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}