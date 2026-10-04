package com.contreras.saludplus.citas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.navigation.Rutas

// Comparte el menú inferior entre las cuatro pantallas principales.
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    destinoActual: String,
    contenido: @Composable () -> Unit
) {
    // Define nombre, ruta y símbolo de cada destino.
    val destinos = listOf(
        Triple("Inicio", Rutas.INICIO, "⌂"),
        Triple("Citas", Rutas.MIS_CITAS, "▦"),
        Triple("Resultados", Rutas.RESULTADOS, "≡"),
        Triple("Perfil", Rutas.PERFIL, "●")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        // MainActivity ya proporciona los márgenes del sistema.
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ),

        bottomBar = {
            NavigationBar(
                // Evita repetir los márgenes aplicados por MainActivity.
                windowInsets = androidx.compose.foundation.layout.WindowInsets(
                    left = 0,
                    top = 0,
                    right = 0,
                    bottom = 0
                )
            ) {
                destinos.forEach { (nombre, ruta, simbolo) ->
                    NavigationBarItem(
                        selected = destinoActual == ruta,
                        onClick = {
                            // Evita navegar nuevamente al destino activo.
                            if (destinoActual != ruta) {
                                navController.navigate(ruta) {
                                    // Mantiene Inicio como base del historial.
                                    popUpTo(Rutas.INICIO) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Text(simbolo)
                        },
                        label = {
                            Text(nombre)
                        }
                    )
                }
            }
        }
    ) { padding ->
        // Reserva espacio para que el menú no cubra el contenido.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            contenido()
        }
    }
}