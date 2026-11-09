package com.contreras.saludplus.citas.ui.screens.perfil

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la pantalla de datos del paciente conectado.
@Composable
fun PerfilScreen(navController: NavHostController) {
    // TODO: Mostrar usuarioActual y cerrar sesión mediante Repositorio.
    PantallaEnConstruccion(
        titulo = "Mi perfil",
        descripcion = "Datos del paciente pendientes",
        acciones = listOf(
            // Prueba el regreso a Splash sin una sesión real.
            "Probar salida" to {
                navController.navigate(Rutas.SPLASH) {
                    // Elimina Inicio y las pantallas abiertas posteriormente.
                    popUpTo(Rutas.INICIO) {
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