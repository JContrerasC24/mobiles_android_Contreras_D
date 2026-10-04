package com.contreras.saludplus.citas.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*
import com.contreras.saludplus.citas.ui.theme.*

// Distribuye los cuatro accesos como en el diseño de referencia.
@Composable
fun HomeScreen(navController: NavHostController) {
    val nombre = Repositorio.usuarioActual?.nombres?.trim()?.substringBefore(" ") ?: "Paciente"
    val destacadas = Repositorio.especialidadesDestacadas()
    var menuAbierto by remember { mutableStateOf(false) }
    PantallaPrincipal(navController, Rutas.INICIO) {
        LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box {
                        IconButton(onClick = { menuAbierto = true }) { Icon(Icons.Filled.Menu, "Abrir menú") }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            DropdownMenuItem(text = { Text("Mi perfil") }, onClick = {
                                menuAbierto = false
                                navController.navigate(Rutas.PERFIL) { launchSingleTop = true }
                            })
                            DropdownMenuItem(text = { Text("Términos y condiciones") }, onClick = {
                                menuAbierto = false
                                navController.navigate(Rutas.TERMINOS) { launchSingleTop = true }
                            })
                        }
                    }
                    IconButton(onClick = { navController.navigate(Rutas.NOTIFICACIONES) { launchSingleTop = true } }) {
                        Icon(Icons.Outlined.Notifications, "Ver notificaciones")
                    }
                }
                Text("¡Hola, $nombre!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("¿Qué deseas hacer hoy?", Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoReferencia("Agendar cita", Icons.Filled.CalendarMonth, AzulClaroSaludPlus,
                        AzulSaludPlus, Modifier.weight(1f)) { navController.navigate(Rutas.ESPECIALIDADES) }
                    AccesoReferencia("Mis citas", Icons.Filled.EventAvailable, FondoCitas,
                        ColorCitas, Modifier.weight(1f)) { navController.navigate(Rutas.MIS_CITAS) }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoReferencia("Mis datos", Icons.Filled.Person, FondoPerfil,
                        ColorPerfil, Modifier.weight(1f)) { navController.navigate(Rutas.PERFIL) }
                    AccesoReferencia("Resultados", Icons.Filled.Description, FondoResultados,
                        ColorResultados, Modifier.weight(1f)) { navController.navigate(Rutas.RESULTADOS) }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Especialidades destacadas", Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { navController.navigate(Rutas.ESPECIALIDADES) }) {
                        Text("Ver todas", style = MaterialTheme.typography.labelSmall)
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(destacadas, key = { it.id }) { especialidad ->
                        OutlinedCard(
                            onClick = { navController.navigate(Rutas.medicos(especialidad.id)) },
                            modifier = Modifier.width(108.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFECF0F5)),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconoEspecialidad(especialidad.id)
                                Text(especialidad.nombre, style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center, minLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Construye una tarjeta pastel con icono y título centrados.
@Composable
private fun AccesoReferencia(
    titulo: String, icono: ImageVector, fondo: Color, color: Color,
    modifier: Modifier = Modifier, accion: () -> Unit
) {
    Card(onClick = accion, modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icono, null, Modifier.size(36.dp), tint = color)
            Text(titulo, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}
