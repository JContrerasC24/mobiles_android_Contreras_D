package com.contreras.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.contreras.saludplus.citas.ui.screens.agendamiento.CitaExitosaScreen
import com.contreras.saludplus.citas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.contreras.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.contreras.saludplus.citas.ui.screens.agendamiento.FechaHoraScreen
import com.contreras.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.contreras.saludplus.citas.ui.screens.auth.LoginScreen
import com.contreras.saludplus.citas.ui.screens.auth.RegistroScreen
import com.contreras.saludplus.citas.ui.screens.auth.SplashScreen
import com.contreras.saludplus.citas.ui.screens.auth.TerminosScreen
import com.contreras.saludplus.citas.ui.screens.citas.DetalleCitaScreen
import com.contreras.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.contreras.saludplus.citas.ui.screens.home.HomeScreen
import com.contreras.saludplus.citas.ui.screens.notificaciones.NotificacionesScreen
import com.contreras.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.contreras.saludplus.citas.ui.screens.resultados.ResultadosScreen

// Centraliza los destinos y sus parámetros de navegación.
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // Conserva el controlador y su historial de navegación.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        // Define las pantallas de acceso.
        composable(Rutas.SPLASH) {
            SplashScreen(navController)
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(navController)
        }

        composable(Rutas.LOGIN) {
            LoginScreen(navController)
        }

        composable(Rutas.TERMINOS) {
            TerminosScreen(navController)
        }

        // Define Inicio y la selección de especialidades.
        composable(Rutas.INICIO) {
            HomeScreen(navController)
        }

        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(navController)
        }

        // Recibe el identificador de la especialidad seleccionada.
        composable(
            route = Rutas.MEDICOS,
            arguments = listOf(
                navArgument("especialidadId") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            val especialidadId = requireNotNull(
                entrada.arguments
            ).getInt("especialidadId")

            MedicosScreen(
                navController = navController,
                especialidadId = especialidadId
            )
        }

        // Recibe el médico para seleccionar su horario.
        composable(
            route = Rutas.FECHA_HORA,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            val medicoId = requireNotNull(
                entrada.arguments
            ).getInt("medicoId")

            FechaHoraScreen(
                navController = navController,
                medicoId = medicoId
            )
        }

        // Recibe médico, fecha y hora para confirmar.
        composable(
            route = Rutas.CONFIRMAR_CITA,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                },
                navArgument("fecha") {
                    type = NavType.StringType
                },
                navArgument("hora") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val argumentos = requireNotNull(entrada.arguments)
            val medicoId = argumentos.getInt("medicoId")
            val fecha = requireNotNull(argumentos.getString("fecha"))
            val hora = requireNotNull(argumentos.getString("hora"))

            ConfirmarCitaScreen(
                navController = navController,
                medicoId = medicoId,
                fecha = fecha,
                hora = hora
            )
        }

        // Recibe la cita para mostrar el resultado del agendamiento.
        composable(
            route = Rutas.CITA_EXITOSA,
            arguments = listOf(
                navArgument("citaId") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            val citaId = requireNotNull(
                entrada.arguments
            ).getInt("citaId")

            CitaExitosaScreen(
                navController = navController,
                citaId = citaId
            )
        }

        // Define la lista de citas del paciente.
        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(navController)
        }

        // Recibe la cita seleccionada desde la lista.
        composable(
            route = Rutas.DETALLE_CITA,
            arguments = listOf(
                navArgument("citaId") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            val citaId = requireNotNull(
                entrada.arguments
            ).getInt("citaId")

            DetalleCitaScreen(
                navController = navController,
                citaId = citaId
            )
        }

        // Define los destinos complementarios del paciente.
        composable(Rutas.PERFIL) {
            PerfilScreen(navController)
        }

        composable(Rutas.RESULTADOS) {
            ResultadosScreen(navController)
        }

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen(navController)
        }
    }
}