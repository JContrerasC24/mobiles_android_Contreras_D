package com.contreras.saludplus.citas.ui.screens.citas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Recibe el identificador de la cita seleccionada.
@Composable
fun DetalleCitaScreen(
    navController: NavHostController,
    citaId: Int
) {
    // TODO: Mostrar detalles y confirmar cancelación mediante AlertDialog.
    PantallaEnConstruccion(
        titulo = "Detalle de cita",
        descripcion = "Identificador recibido: $citaId",
        acciones = listOf(
            // Regresa a la lista de citas.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}