package com.contreras.saludplus.citas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas

// Describe cada destino del menú inferior.
private data class DestinoPrincipal(
    val titulo: String,
    val ruta: String,
    val icono: ImageVector
)

// Comparte el menú entre las cuatro pantallas principales.
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    destinoActual: String,
    contenido: @Composable () -> Unit
) {
    val destinos = listOf(
        DestinoPrincipal(
            titulo = "Inicio",
            ruta = Rutas.INICIO,
            icono = Icons.Outlined.Home
        ),
        DestinoPrincipal(
            titulo = "Citas",
            ruta = Rutas.MIS_CITAS,
            icono = Icons.Outlined.CalendarMonth
        ),
        DestinoPrincipal(
            titulo = "Resultados",
            ruta = Rutas.RESULTADOS,
            icono = Icons.Outlined.Description
        ),
        DestinoPrincipal(
            titulo = "Perfil",
            ruta = Rutas.PERFIL,
            icono = Icons.Outlined.Person
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,

        // Evita duplicar los espacios gestionados por MainActivity.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        bottomBar = {
            NavigationBar(
                modifier = Modifier.heightIn(min = 64.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = destinoActual == destino.ruta,
                        onClick = {
                            if (destinoActual != destino.ruta) {
                                if (destino.ruta == Rutas.INICIO) {

                                    // Regresa al Inicio existente y elimina las pantallas superiores.
                                    val regreso = navController.popBackStack(
                                        route = Rutas.INICIO,
                                        inclusive = false
                                    )

                                    // Abre Inicio cuando no existe en el historial.
                                    if (!regreso) {
                                        navController.navigate(Rutas.INICIO) {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }

                                            launchSingleTop = true
                                        }
                                    }
                                } else {

                                    // Cambia de sección conservando Inicio como pantalla base.
                                    navController.navigate(destino.ruta) {
                                        popUpTo(Rutas.INICIO) {
                                            inclusive = false
                                        }

                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destino.icono,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(destino.titulo, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { espacioInterior ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacioInterior)
        ) {
            contenido()
        }
    }
}