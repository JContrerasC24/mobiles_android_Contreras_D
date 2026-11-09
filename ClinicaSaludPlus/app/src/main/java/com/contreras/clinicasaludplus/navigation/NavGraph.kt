package com.contreras.clinicasaludplus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.contreras.clinicasaludplus.data.Cita
import com.contreras.clinicasaludplus.data.citasIniciales
import com.contreras.clinicasaludplus.data.listaMedicos
import com.contreras.clinicasaludplus.screens.AgendarCitaScreen
import com.contreras.clinicasaludplus.screens.ConfirmacionScreen
import com.contreras.clinicasaludplus.screens.HistorialMedicoScreen
import com.contreras.clinicasaludplus.screens.InicioScreen
import com.contreras.clinicasaludplus.screens.MisCitasScreen
import com.contreras.clinicasaludplus.screens.PerfilMedicoScreen
import com.contreras.clinicasaludplus.screens.PerfilUsuarioScreen

/**
 * Grafo de navegacion principal.
 *
 * IMPORTANTE: esta app NO usa ViewModel/MVVM. El estado que se comparte
 * entre pantallas (la lista de citas y la ultima cita agendada) vive aqui,
 * en el nivel mas alto, con remember/mutableStateOf, y se pasa hacia abajo
 * por parametros a cada pantalla.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Estado compartido simple (sin ViewModel)
    val listaCitas = remember { mutableStateListOf<Cita>().apply { addAll(citasIniciales()) } }
    var ultimaCita by remember { mutableStateOf<Cita?>(null) }

    NavHost(navController = navController, startDestination = Rutas.INICIO) {

        // ---- Destinos principales (accesibles desde el drawer) ----

        composable(Rutas.INICIO) {
            InicioScreen(navController = navController)
        }

        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(citas = listaCitas, navController = navController)
        }

        composable(Rutas.HISTORIAL) {
            HistorialMedicoScreen(navController = navController)
        }

        composable(Rutas.PERFIL_USUARIO) {
            PerfilUsuarioScreen(navController = navController)
        }

        // ---- Flujo secuencial: Perfil medico -> Agendar -> Confirmacion ----

        composable(
            route = Rutas.PERFIL_MEDICO,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            val medico = listaMedicos.first { it.id == medicoId }
            PerfilMedicoScreen(medico = medico, navController = navController)
        }

        composable(
            route = Rutas.AGENDAR_CITA,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            val medico = listaMedicos.first { it.id == medicoId }
            AgendarCitaScreen(
                medico = medico,
                navController = navController,
                onCitaConfirmada = { fecha, hora ->
                    val nuevaCita = Cita(medico = medico, fecha = fecha, hora = hora)
                    listaCitas.add(0, nuevaCita)
                    ultimaCita = nuevaCita
                    navController.navigate(Rutas.CONFIRMACION)
                }
            )
        }

        composable(Rutas.CONFIRMACION) {
            ultimaCita?.let { cita ->
                ConfirmacionScreen(cita = cita, navController = navController)
            }
        }
    }
}