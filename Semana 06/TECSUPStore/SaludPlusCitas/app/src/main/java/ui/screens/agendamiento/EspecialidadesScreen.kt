package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaEnConstruccion

// Será la lista de especialidades disponibles.
@Composable
fun EspecialidadesScreen(navController: NavHostController) {
    // TODO: Agregar buscador y LazyColumn usando las especialidades del repositorio.
    PantallaEnConstruccion(
        titulo = "Especialidades",
        descripcion = "Selección de especialidad pendiente",
        acciones = listOf(
            // Envía una especialidad ficticia para probar los parámetros.
            "Probar Medicina General" to {
                navController.navigate(
                    Rutas.medicos(especialidadId = 1)
                )
            },

            // Regresa a Inicio.
            "Volver" to {
                navController.popBackStack()
            }
        )
    )
}