package com.contreras.saludplus.citas.ui.screens.resultados

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta Resultados dentro del menú principal.
@Composable
fun ResultadosScreen(navController: NavHostController) {
    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.RESULTADOS
    ) {
        // TODO: Crear un modelo propio y mostrar resultados ficticios.
        PantallaEnConstruccion(
            titulo = "Resultados",
            descripcion = "Resultados de demostración pendientes"
        )
    }
}