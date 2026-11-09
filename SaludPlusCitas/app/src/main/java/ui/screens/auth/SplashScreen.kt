package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Presenta los accesos iniciales de la aplicación.
@Composable
fun SplashScreen(navController: NavHostController) {
    // TODO: Agregar logo, ilustración y bienvenida según la referencia.
    PantallaEnConstruccion(
        titulo = "Clínica SaludPlus",
        descripcion = "Tu salud, nuestra prioridad",
        acciones = listOf(
            // Abre la pantalla de registro.
            "Crear cuenta" to {
                navController.navigate(Rutas.REGISTRO)
            },

            // Abre la pantalla de inicio de sesión.
            "Iniciar sesión" to {
                navController.navigate(Rutas.LOGIN)
            }
        )
    )
}

