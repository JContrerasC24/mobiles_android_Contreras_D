package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la pantalla de condiciones de uso.
@Composable
fun TerminosScreen(navController: NavHostController) {
    // TODO: Mostrar términos completos mediante contenido desplazable.
    PantallaEnConstruccion(
        titulo = "Términos y condiciones",
        descripcion = "Contenido pendiente de implementación",
        acciones = listOf(
            // Regresa al formulario de registro.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}