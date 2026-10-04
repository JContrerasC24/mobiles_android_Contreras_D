package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Recibe el identificador para mostrar la cita agendada.
@Composable
fun CitaExitosaScreen(
    navController: NavHostController,
    citaId: Int
) {
    // TODO: Consultar la cita guardada y mostrar su resumen.
    PantallaEnConstruccion(
        titulo = "Cita agendada",
        descripcion = "Vista de prueba. Identificador recibido: $citaId",
        acciones = listOf(
            // Abre Mis citas y retira la pantalla de éxito.
            "Ver mis citas" to {
                navController.navigate(Rutas.MIS_CITAS) {
                    popUpTo(Rutas.INICIO) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },

            // Regresa al Inicio conservado en el historial.
            "Volver a Inicio" to {
                navController.popBackStack(
                    route = Rutas.INICIO,
                    inclusive = false
                )
            }
        )
    )
}