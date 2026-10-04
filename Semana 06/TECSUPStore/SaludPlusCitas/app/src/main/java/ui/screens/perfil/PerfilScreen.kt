package com.contreras.saludplus.citas.ui.screens.perfil

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion
import com.contreras.saludplus.citas.data.repository.Repositorio
// Será la pantalla de datos del paciente conectado.
@Composable
fun PerfilScreen(navController: NavHostController) {
    // TODO: Mostrar usuarioActual y cerrar sesión mediante Repositorio.
    PantallaEnConstruccion(
        titulo = "Mi perfil",
        descripcion = "Datos del paciente pendientes",
        acciones = listOf(
            // Prueba el regreso a Splash sin una sesión real.
            // Cierra la sesión y elimina el historial privado.
            "Cerrar sesión" to {
                Repositorio.cerrarSesion()

                navController.navigate(Rutas.SPLASH) {
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