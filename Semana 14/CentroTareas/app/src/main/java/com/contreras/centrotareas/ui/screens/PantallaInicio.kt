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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.navigation.Screen
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla principal de inicio con resumen dinámico y accesos rápidos estilo Notion.
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

    // Cálculo dinámico de métricas reales
    val procesosActivos = (if (corriendo) 1 else 0) +
            (if (primos is PrimosUi.Calculando) 1 else 0) +
            (if (carga is CargaUi.Cargando) 1 else 0)

    val completadosCount = historial.count { it.estado.equals("Completado", ignoreCase = true) }
    val canceladosCount = historial.count { it.estado.equals("Cancelado", ignoreCase = true) }
    val ultimaOperacion = historial.firstOrNull()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ENCABEZADO ESTILO NOTION
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
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

        // RESUMEN DINÁMICO (TARJETAS DE ESTADÍSTICAS)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Resumen de Procesos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Activos",
                        valor = procesosActivos.toString(),
                        icon = Icons.Outlined.Sync,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Completados",
                        valor = completadosCount.toString(),
                        icon = Icons.Outlined.CheckCircle,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        titulo = "Cancelados",
                        valor = canceladosCount.toString(),
                        icon = Icons.Outlined.Cancel,
                        iconTint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // ÚLTIMA OPERACIÓN REALIZADA
        item {
            NotionCard(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Última Operación Realizada",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ultimaOperacion?.titulo ?: "Ninguna operación registrada aún",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (ultimaOperacion != null) {
                            Text(
                                text = "${ultimaOperacion.descripcion} • ${ultimaOperacion.horaFormateada}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (ultimaOperacion != null) {
                        NotionStatusBadge(status = ultimaOperacion.estado)
                    }
                }
            }
        }

        // SECCIÓN DE ACCESOS RÁPIDOS
        item {
            Text(
                text = "Accesos Rápidos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            QuickAccessCard(
                title = "Ejecutar tarea con progreso",
                description = "Monitorea tareas largas con barra de avance e interacción UI.",
                icon = Icons.Filled.PlayArrow,
                onClick = { onNavigate(Screen.Procesos.route) }
            )
        }

        item {
            QuickAccessCard(
                title = "Calcular números primos",
                description = "Ejecuta cálculos matemáticos usando Dispatchers.Default.",
                icon = Icons.Filled.Calculate,
                onClick = { onNavigate(Screen.Primos.route) }
            )
        }

        item {
            QuickAccessCard(
                title = "Comparar rendimiento",
                description = "Evalúa ejecuciones secuenciales vs. paralelas con async/await.",
                icon = Icons.Filled.Speed,
                onClick = { onNavigate(Screen.Rendimiento.route) }
            )
        }

        item {
            QuickAccessCard(
                title = "Consultar actividad",
                description = "Revisa el historial en tiempo real de todas las ejecuciones.",
                icon = Icons.Filled.History,
                onClick = { onNavigate(Screen.Actividad.route) }
            )
        }
    }
}

@Composable
private fun MetricCard(
    titulo: String,
    valor: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    NotionCard(
        modifier = modifier,
        contentPadding = 12.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
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
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
