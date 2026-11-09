package com.contreras.saludplus.citas.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Presenta los accesos principales del paciente.
@Composable
fun HomeScreen(navController: NavHostController) {
    // TODO: Agregar saludo, tarjetas, especialidades destacadas y NavigationBar.
    PantallaEnConstruccion(
        titulo = "Inicio",
        descripcion = "Bienvenido a Clínica SaludPlus",
        acciones = listOf(
            // Inicia el recorrido de agendamiento.
            "Agendar cita" to {
                navController.navigate(Rutas.ESPECIALIDADES)
            },

            // Abre las citas del paciente.
            "Mis citas" to {
                navController.navigate(Rutas.MIS_CITAS)
            },

            // Abre los resultados médicos ficticios.
            "Resultados" to {
                navController.navigate(Rutas.RESULTADOS)
            },

            // Abre los datos del paciente.
            "Perfil" to {
                navController.navigate(Rutas.PERFIL)
            },

            // Abre las notificaciones del paciente.
            "Notificaciones" to {
                navController.navigate(Rutas.NOTIFICACIONES)
            }
        )
    )
}