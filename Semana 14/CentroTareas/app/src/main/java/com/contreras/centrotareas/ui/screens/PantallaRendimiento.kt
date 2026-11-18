package com.contreras.centrotareas.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionPrimaryButton
import com.contreras.centrotareas.ui.components.NotionSecondaryButton
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.theme.NotionError
import com.contreras.centrotareas.ui.theme.NotionErrorContainer
import com.contreras.centrotareas.ui.theme.NotionSuccess
import com.contreras.centrotareas.ui.theme.NotionSuccessContainer
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla para comparar el rendimiento de cargas secuenciales vs paralelas.
 */
@Composable
fun PantallaRendimiento(
    vm: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val cargaState by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()

    val estaCargando = cargaState == CargaUi.Cargando

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Comparación de Rendimiento",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Evaluación de ejecución secuencial vs. concurrente con async / await.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // PANEL DE CONTROLES
        item {
            NotionCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Modo de Carga de Datos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        val statusText = when (cargaState) {
                            CargaUi.Inicial -> "Inicial"
                            CargaUi.Cargando -> "Cargando"
                            is CargaUi.Exito -> "Completado"
                            is CargaUi.Error -> "Error"
                        }
                        NotionStatusBadge(status = statusText)
                    }

                    Text(
                        text = "Ejecuta dos peticiones simuladas (Perfil: 2s, Pedidos: 3s) para medir la diferencia de tiempo real.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NotionSecondaryButton(
                            text = "Secuencial (~5s)",
                            icon = Icons.Default.PlayArrow,
                            onClick = { vm.cargarUnaPorUna() },
                            enabled = !estaCargando,
                            modifier = Modifier.weight(1f)
                        )

                        NotionPrimaryButton(
                            text = "Paralela (~3s)",
                            icon = Icons.Default.Speed,
                            onClick = { vm.cargarALaVez() },
                            enabled = !estaCargando,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // CONTROL DE ERROR SIMULADO
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Simular Error de Red",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lanza IOException durante la carga de pedidos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Switch(
                            checked = simularError,
                            onCheckedChange = { vm.cambiarSimularError(it) },
                            enabled = !estaCargando,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }

        // ESTADOS DE CARGA Y RESULTADOS
        item {
            when (val estado = cargaState) {
                CargaUi.Inicial -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "Selecciona 'Secuencial' o 'Paralela' para medir los tiempos de ejecución reales.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                CargaUi.Cargando -> {
                    NotionCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Cargando datos en segundo plano...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Obteniendo perfil y pedidos...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                is CargaUi.Exito -> {
                    NotionCard(
                        backgroundColor = NotionSuccessContainer,
                        borderColor = NotionSuccess.copy(alpha = 0.4f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Resultados de Ejecución",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NotionSuccess
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Modo:", style = MaterialTheme.typography.bodyLarge)
                                Text(estado.modo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Perfil retornado:", style = MaterialTheme.typography.bodyLarge)
                                Text(estado.perfil, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pedidos cargados:", style = MaterialTheme.typography.bodyLarge)
                                Text("${estado.pedidos} pedidos", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tiempo total medido:", style = MaterialTheme.typography.bodyLarge)
                                Text("${estado.milisegundos} ms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // COMPARACIÓN DE TIEMPO CON BARRAS HORIZONTALES
                            Text(
                                text = "Comparativa visual de tiempo",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            // Referencia teórica aproximada max 6000ms
                            val maxTime = 6000f
                            val progressRatio = (estado.milisegundos / maxTime).coerceIn(0.1f, 1.0f)
                            val animWidth by animateFloatAsState(targetValue = progressRatio, label = "barRatio")

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(animWidth)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (estado.modo.contains("Paralela", ignoreCase = true) || estado.modo.contains("A la vez", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                Text(
                                    text = "Tiempo registrado: ${estado.milisegundos} ms (Teórico Paralelo ~3000ms, Secuencial ~5000ms)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                is CargaUi.Error -> {
                    NotionCard(
                        backgroundColor = NotionErrorContainer,
                        borderColor = NotionError.copy(alpha = 0.4f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = NotionError,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Error al Cargar Datos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NotionError
                                )
                            }

                            Text(
                                text = estado.mensaje,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            NotionSecondaryButton(
                                text = "Reintentar Carga Paralela",
                                icon = Icons.Default.Refresh,
                                onClick = { vm.cargarALaVez() },
                                enabled = !estaCargando
                            )
                        }
                    }
                }
            }
        }
    }
}
