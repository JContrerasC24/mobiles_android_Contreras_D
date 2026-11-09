package com.contreras.saludplus.citas.ui.screens.resultados

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la pantalla de resultados médicos ficticios.
@Composable
fun ResultadosScreen(navController: NavHostController) {
    // TODO: Crear un modelo propio y mostrar resultados ficticios.
    PantallaEnConstruccion(
        titulo = "Resultados",
        descripcion = "Resultados de demostración pendientes",
        acciones = listOf(
            // Regresa a la pantalla anterior.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}