package com.contreras.tecsupfit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*
import androidx.compose.ui.unit.dp

// Importaciones obligatorias para NavController State
import androidx.compose.runtime.getValue

sealed class BarraNavegacion(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : BarraNavegacion("inicio", "Inicio", Icons.Default.Home)
    object Reservas : BarraNavegacion("reservas", "Reservas", Icons.Default.DateRange)
    object Rutinas : BarraNavegacion("rutinas", "Rutinas", Icons.Default.List)
    object Perfil : BarraNavegacion("perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun TecsupFitApp() {
    val navController = rememberNavController()

    val items = listOf(
        BarraNavegacion.Inicio,
        BarraNavegacion.Reservas,
        BarraNavegacion.Rutinas,
        BarraNavegacion.Perfil
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        selected = rutaActual?.startsWith(item.ruta) == true,
                        onClick = {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
        // Aplicacion de padding del Scaffold para evitar superposicion con el bottomBar
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BarraNavegacion.Inicio.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BarraNavegacion.Inicio.ruta) { InicioScreen(navController) }
            composable(BarraNavegacion.Reservas.ruta) { Text("Mis Reservas", modifier = Modifier.padding(16.dp)) }
            composable(BarraNavegacion.Rutinas.ruta) { Text("Mis Rutinas", modifier = Modifier.padding(16.dp)) }
            composable(BarraNavegacion.Perfil.ruta) { Text("Mi Perfil", modifier = Modifier.padding(16.dp)) }

            composable("detalle/{claseNombre}/{claseHorario}") { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("claseNombre") ?: ""
                val horario = backStackEntry.arguments?.getString("claseHorario") ?: ""
                DetalleScreen(navController, nombre, horario)
            }
            composable("confirmacion/{claseNombre}/{horarioElegido}") { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("claseNombre") ?: ""
                val horario = backStackEntry.arguments?.getString("horarioElegido") ?: ""
                ConfirmacionScreen(navController, nombre, horario)
            }
        }
    }
}