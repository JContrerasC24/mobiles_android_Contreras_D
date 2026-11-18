package com.contreras.centrotareas.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contreras.centrotareas.ui.navigation.NotionBottomNavBar
import com.contreras.centrotareas.ui.navigation.Screen
import com.contreras.centrotareas.ui.screens.PantallaActividad
import com.contreras.centrotareas.ui.screens.PantallaInicio
import com.contreras.centrotareas.ui.screens.PantallaPrimos
import com.contreras.centrotareas.ui.screens.PantallaProcesos
import com.contreras.centrotareas.ui.screens.PantallaRendimiento
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla principal y contenedor de navegación de CentroTareas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Inicio.route

    val currentScreenTitle = Screen.items.find { it.route == currentRoute }?.title ?: "CentroTareas"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentScreenTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NotionBottomNavBar(
                currentRoute = currentRoute,
                onNavigateToRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Inicio.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Inicio.route) {
                PantallaInicio(
                    vm = vm,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Inicio.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Procesos.route) {
                PantallaProcesos(vm = vm)
            }

            composable(Screen.Primos.route) {
                PantallaPrimos(vm = vm)
            }

            composable(Screen.Rendimiento.route) {
                PantallaRendimiento(vm = vm)
            }

            composable(Screen.Actividad.route) {
                PantallaActividad(vm = vm)
            }
        }
    }
}
