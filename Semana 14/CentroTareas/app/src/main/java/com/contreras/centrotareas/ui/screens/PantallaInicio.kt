package com.contreras.centrotareas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.navigation.Screen
import com.contreras.centrotareas.ui.theme.NaranjaAdvertencias
import com.contreras.centrotareas.ui.theme.VerdeIndicadores
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla de inicio sobria y profesional con resumen dinámico de métricas y accesos rápidos.
 */
@Composable
fun PantallaInicio(
    vm: TareasViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()
    val historial by vm.historial.collectAsState()

    // Cálculo dinámico de métricas reales de la sesión
    val procesosActivos = (if (corriendo) 1 else 0) +
            (if (primos is PrimosUi.Calculando) 1 else 0) +
            (if (carga is CargaUi.Cargando) 1 else 0)

    val completadosCount = historial.count { it.estado.equals("Completado", ignoreCase = true) }
    val canceladosCount = historial.count { it.estado.equals("Cancelado", ignoreCase = true) }
    val ultimaOperacion = historial.firstOrNull()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // A. ENCABEZADO
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "CentroTareas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tu espacio de trabajo y procesamiento concurrente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // B. RESUMEN DE PROCESOS (3 INDICADORES UNIFORMES CON FONDO BLANCO)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Resumen de Procesos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricIndicatorCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Activos",
                        valor = procesosActivos.toString(),
                        icon = Icons.Outlined.Sync,
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                    MetricIndicatorCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Completados",
                        valor = completadosCount.toString(),
                        icon = Icons.Outlined.CheckCircle,
                        accentColor = VerdeIndicadores
                    )
                    MetricIndicatorCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Cancelados",
                        valor = canceladosCount.toString(),
                        icon = Icons.Outlined.Cancel,
                        accentColor = NaranjaAdvertencias
                    )
                }
            }
        }

        // C. ÚLTIMA OPERACIÓN REALIZADA
        item {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Última Operación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                NotionCard(
                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    if (ultimaOperacion != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ultimaOperacion.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${ultimaOperacion.descripcion} • ${ultimaOperacion.horaFormateada}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            NotionStatusBadge(status = ultimaOperacion.estado)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Aún no se han registrado operaciones en esta sesión.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }

        // D. ACCESOS RÁPIDOS
        item {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Accesos Rápidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                QuickAccessCard(
                    title = "Ejecutar tarea",
                    description = "Administra operaciones largas con barra de progreso reactiva.",
                    icon = Icons.Filled.PlayArrow,
                    onClick = { onNavigate(Screen.Procesos.route) }
                )

                QuickAccessCard(
                    title = "Calcular números primos",
                    description = "Ejecuta cálculos pesados en CPU usando Dispatchers.Default.",
                    icon = Icons.Filled.Calculate,
                    onClick = { onNavigate(Screen.Primos.route) }
                )

                QuickAccessCard(
                    title = "Comparar rendimiento",
                    description = "Evalúa cargas secuenciales vs. paralelas con async / await.",
                    icon = Icons.Filled.Speed,
                    onClick = { onNavigate(Screen.Rendimiento.route) }
                )

                QuickAccessCard(
                    title = "Consultar actividad",
                    description = "Revisa el registro en tiempo real de operaciones de la sesión.",
                    icon = Icons.Filled.History,
                    onClick = { onNavigate(Screen.Actividad.route) }
                )
            }
        }
    }
}

@Composable
private fun MetricIndicatorCard(
    titulo: String,
    valor: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    NotionCard(
        modifier = modifier,
        contentPadding = 14.dp,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NotionCard(
        modifier = modifier,
        onClick = onClick,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
