package com.contreras.saludplus.citas.ui.screens.citas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la lista de citas del paciente conectado.
@Composable
fun MisCitasScreen(navController: NavHostController) {
    // TODO: Mostrar citas mediante LazyColumn y controlar la lista vacía.
    PantallaEnConstruccion(
        titulo = "Mis citas",
        descripcion = "Listado de citas pendiente",
        acciones = listOf(
            // Envía un identificador ficticio para probar el detalle.
            "Probar detalle de cita" to {
                navController.navigate(Rutas.detalleCita(citaId = 1))
            },

            // Regresa a la pantalla anterior.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}
