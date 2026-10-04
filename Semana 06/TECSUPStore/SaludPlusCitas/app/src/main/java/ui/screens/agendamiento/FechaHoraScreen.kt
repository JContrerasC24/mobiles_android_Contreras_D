package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Recibe el médico para seleccionar su fecha y horario.
@Composable
fun FechaHoraScreen(
    navController: NavHostController,
    medicoId: Int
) {
    // TODO: Agregar selección de fecha y LazyVerticalGrid con horarios disponibles.
    PantallaEnConstruccion(
        titulo = "Fecha y hora",
        descripcion = "Médico recibido: $medicoId",
        acciones = listOf(
            // Envía valores de prueba sin reservar ningún horario.
            "Probar horario: 10 octubre, 09:00" to {
                navController.navigate(
                    Rutas.confirmarCita(
                        medicoId = medicoId,
                        fecha = "2026-10-10",
                        hora = "09:00"
                    )
                )
            },

            // Regresa a la selección del médico.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}