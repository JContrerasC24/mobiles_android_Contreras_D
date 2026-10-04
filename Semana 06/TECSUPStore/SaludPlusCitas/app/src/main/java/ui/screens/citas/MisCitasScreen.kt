package com.contreras.saludplus.citas.ui.screens.citas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta Citas dentro del menú principal.
@Composable
fun MisCitasScreen(navController: NavHostController) {
    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.MIS_CITAS
    ) {
        // TODO: Mostrar citas mediante LazyColumn y controlar la lista vacía.
        PantallaEnConstruccion(
            titulo = "Mis citas",
            descripcion = "Listado de citas pendiente",
            acciones = listOf(
                // Envía una cita ficticia para probar el detalle.
                "Probar detalle de cita" to {
                    navController.navigate(
                        Rutas.detalleCita(citaId = 1)
                    )
                }
            )
        )
    }
}