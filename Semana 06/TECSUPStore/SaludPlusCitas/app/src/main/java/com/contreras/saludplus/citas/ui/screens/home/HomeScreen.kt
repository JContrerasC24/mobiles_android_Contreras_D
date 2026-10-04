package com.contreras.saludplus.citas.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta el saludo, accesos y especialidades destacadas.
@Composable
fun HomeScreen(navController: NavHostController) {
    // Obtiene el primer nombre del paciente conectado.
    val nombre = Repositorio.usuarioActual
        ?.nombres
        ?.trim()
        ?.substringBefore(" ")
        ?: "Paciente"

    // Consulta las primeras especialidades del repositorio.
    val destacadas = Repositorio.especialidadesDestacadas()

    // Muestra Inicio junto al menú inferior compartido.
    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.INICIO
    ) {
        // Permite recorrer el contenido en pantallas pequeñas.
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¡Hola, $nombre!",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "¿Qué deseas hacer hoy?",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Abre las notificaciones del paciente.
                    TextButton(
                        onClick = {
                            navController.navigate(Rutas.NOTIFICACIONES)
                        }
                    ) {
                        Text("Ver notificaciones")
                    }
                }
            }

            item {
                // Distribuye los primeros accesos en dos columnas.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccesoInicio(
                        titulo = "Agendar cita",
                        descripcion = "Selecciona un especialista",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.ESPECIALIDADES) {
                                launchSingleTop = true
                            }
                        }
                    )

                    AccesoInicio(
                        titulo = "Mis citas",
                        descripcion = "Consulta tus reservas",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.MIS_CITAS) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            item {
                // Distribuye los accesos restantes en otra fila.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccesoInicio(
                        titulo = "Resultados",
                        descripcion = "Consulta tus resultados",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.RESULTADOS) {
                                launchSingleTop = true
                            }
                        }
                    )

                    AccesoInicio(
                        titulo = "Mi perfil",
                        descripcion = "Revisa tus datos",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.PERFIL) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            item {
                Text(
                    text = "Especialidades destacadas",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                // Presenta especialidades con desplazamiento horizontal.
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = destacadas,
                        key = { it.id }
                    ) { especialidad ->
                        // Abre los médicos de la especialidad seleccionada.
                        Card(
                            onClick = {
                                navController.navigate(
                                    Rutas.medicos(especialidad.id)
                                ) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.width(180.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = especialidad.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = especialidad.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Abre la lista completa de especialidades.
                TextButton(
                    onClick = {
                        navController.navigate(Rutas.ESPECIALIDADES) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Ver todas las especialidades")
                }
            }
        }
    }
}

// Reutiliza el diseño de las tarjetas principales.
@Composable
private fun AccesoInicio(
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}