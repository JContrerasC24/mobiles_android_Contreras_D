package com.contreras.saludplus.citas.ui.screens.notificaciones

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la pantalla de avisos relacionados con las citas.
@Composable
fun NotificacionesScreen(navController: NavHostController) {
    // TODO: Generar notificaciones mediante map sobre las citas del paciente.
    PantallaEnConstruccion(
        titulo = "Notificaciones",
        descripcion = "Avisos de citas pendientes",
        acciones = listOf(
            // Regresa a la pantalla anterior.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}