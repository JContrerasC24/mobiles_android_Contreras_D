package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Recibe los tres datos necesarios para reservar una cita.
@Composable
fun ConfirmarCitaScreen(
    navController: NavHostController,
    medicoId: Int,
    fecha: String,
    hora: String
) {
    // TODO: Mostrar resumen y guardar la cita mediante Repositorio.
    PantallaEnConstruccion(
        titulo = "Confirmar cita",
        descripcion = """
            Médico: $medicoId
            Fecha: $fecha
            Hora: $hora
        """.trimIndent(),
        acciones = listOf(
            // Prueba la pantalla de éxito sin guardar una cita.
            "Probar pantalla de éxito" to {
                navController.navigate(
                    Rutas.citaExitosa(citaId = 1)
                ) {
                    // Elimina las pantallas del flujo posteriores a Inicio.
                    popUpTo(Rutas.INICIO) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },

            // Permite regresar para cambiar el horario.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}