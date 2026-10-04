package com.contreras.saludplus.citas.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal
import com.contreras.saludplus.citas.ui.theme.AzulClaroSaludPlus
import com.contreras.saludplus.citas.ui.theme.AzulSaludPlus
import com.contreras.saludplus.citas.ui.theme.ColorCitas
import com.contreras.saludplus.citas.ui.theme.ColorPerfil
import com.contreras.saludplus.citas.ui.theme.ColorResultados
import com.contreras.saludplus.citas.ui.theme.FondoCitas
import com.contreras.saludplus.citas.ui.theme.FondoPerfil
import com.contreras.saludplus.citas.ui.theme.FondoResultados

// Presenta los accesos principales y las especialidades destacadas.
@Composable
fun HomeScreen(navController: NavHostController) {
    val nombre = Repositorio.usuarioActual
        ?.nombres
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Paciente"

    val destacadas = Repositorio.especialidadesDestacadas()

    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.INICIO
    ) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "¡Hola, $nombre!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "¿Qué deseas hacer hoy?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            navController.navigate(Rutas.NOTIFICACIONES) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Ver notificaciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccesoInicio(
                        titulo = "Agendar cita",
                        descripcion = "Elige tu médico",
                        icono = Icons.Outlined.MedicalServices,
                        fondo = AzulClaroSaludPlus,
                        color = AzulSaludPlus,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.ESPECIALIDADES) {
                                launchSingleTop = true
                            }
                        }
                    )

                    AccesoInicio(
                        titulo = "Mis citas",
                        descripcion = "Revisa tus reservas",
                        icono = Icons.Outlined.CalendarMonth,
                        fondo = FondoCitas,
                        color = ColorCitas,
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccesoInicio(
                        titulo = "Mis datos",
                        descripcion = "Consulta tu perfil",
                        icono = Icons.Outlined.Person,
                        fondo = FondoPerfil,
                        color = ColorPerfil,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.PERFIL) {
                                launchSingleTop = true
                            }
                        }
                    )

                    AccesoInicio(
                        titulo = "Resultados",
                        descripcion = "Consulta los ejemplos",
                        icono = Icons.Outlined.Description,
                        fondo = FondoResultados,
                        color = ColorResultados,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Rutas.RESULTADOS) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            item {
                Text(
                    text = "Especialidades destacadas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                // Permite explorar las especialidades horizontalmente.
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = destacadas,
                        key = { it.id }
                    ) { especialidad ->
                        Card(
                            modifier = Modifier.width(180.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            onClick = {
                                navController.navigate(
                                    Rutas.medicos(especialidad.id)
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MedicalServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = especialidad.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = especialidad.descripcion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
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

// Reutiliza una tarjeta para los accesos principales.
@Composable
private fun AccesoInicio(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    fondo: Color,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = fondo
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}