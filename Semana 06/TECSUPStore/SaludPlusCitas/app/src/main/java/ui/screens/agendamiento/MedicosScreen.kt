package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Recibe la especialidad seleccionada en la pantalla anterior.
@Composable
fun MedicosScreen(
    navController: NavHostController,
    especialidadId: Int
) {
    // TODO: Buscar médicos por especialidad y ordenarlos por calificación.
    PantallaEnConstruccion(
        titulo = "Médicos",
        descripcion = "Especialidad recibida: $especialidadId",
        acciones = listOf(
            // Envía el médico inicial para probar la navegación.
            "Probar selección de médico" to {
                navController.navigate(
                    Rutas.fechaHora(medicoId = 1)
                )
            },

            // Regresa a la selección de especialidades.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}