package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será el formulario de creación de cuentas.
@Composable
fun RegistroScreen(navController: NavHostController) {
    // TODO: Agregar campos, validaciones y registro mediante Repositorio.
    PantallaEnConstruccion(
        titulo = "Crear cuenta",
        descripcion = "Formulario pendiente de implementación",
        acciones = listOf(
            // Permite probar el flujo sin registrar usuarios todavía.
            "Probar navegación a Inicio" to {
                navController.navigate(Rutas.INICIO) {
                    popUpTo(Rutas.SPLASH) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },

            // Muestra las condiciones de uso.
            "Términos y condiciones" to {
                navController.navigate(Rutas.TERMINOS)
            },

            // Regresa a la pantalla anterior.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}
