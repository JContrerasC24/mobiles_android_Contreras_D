package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será el formulario de acceso para pacientes registrados.
@Composable
fun LoginScreen(navController: NavHostController) {
    // TODO: Validar credenciales y establecer sesión mediante Repositorio.
    PantallaEnConstruccion(
        titulo = "Iniciar sesión",
        descripcion = "Inicio de sesión pendiente de implementación",
        acciones = listOf(
            // Permite probar la navegación sin autenticar todavía.
            "Probar navegación a Inicio" to {
                navController.navigate(Rutas.INICIO) {
                    popUpTo(Rutas.SPLASH) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },

            // Regresa a la pantalla anterior.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}
