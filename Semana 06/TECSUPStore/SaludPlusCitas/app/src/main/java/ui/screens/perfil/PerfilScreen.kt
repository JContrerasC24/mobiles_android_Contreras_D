package com.contreras.saludplus.citas.ui.screens.perfil

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta Perfil y conserva el cierre de sesión.
@Composable
fun PerfilScreen(navController: NavHostController) {
    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.PERFIL
    ) {
        // TODO: Mostrar los datos completos del paciente conectado.
        PantallaEnConstruccion(
            titulo = "Mi perfil",
            descripcion = "Datos del paciente pendientes",
            acciones = listOf(
                // Finaliza la sesión y elimina el historial privado.
                "Cerrar sesión" to {
                    Repositorio.cerrarSesion()

                    navController.navigate(Rutas.SPLASH) {
                        popUpTo(Rutas.INICIO) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        )
    }
}