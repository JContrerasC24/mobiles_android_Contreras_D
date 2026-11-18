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
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionPrimaryButton
import com.contreras.centrotareas.ui.components.NotionSecondaryButton
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.theme.RojoIndicadores
import com.contreras.centrotareas.ui.theme.RojoSuave
import com.contreras.centrotareas.ui.theme.VerdeIndicadores
import com.contreras.centrotareas.ui.theme.VerdeSuave
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla para comparar el rendimiento de cargas secuenciales vs paralelas.
 *
 * Muestra tiempos reales medidos (ms) y barras comparativas horizontales.
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
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

        // CONTROL DE MODO DE CARGA Y ERROR SIMULADO
        item {
            NotionCard(
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Modo de Carga de Datos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                        text = "Perfil requiere ~2s y Pedidos requiere ~3s. En modo secuencial demoran ~5s; en modo paralelo demoran ~3s.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NotionSecondaryButton(
                            text = "Una por una (~5s)",
                            icon = Icons.Default.PlayArrow,
                            onClick = { vm.cargarUnaPorUna() },
                            enabled = !estaCargando,
                            modifier = Modifier.weight(1f)
                        )

                        NotionPrimaryButton(
                            text = "A la vez (~3s)",
                            icon = Icons.Default.Speed,
                            onClick = { vm.cargarALaVez() },
                            enabled = !estaCargando,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // SWITCH DE ERROR SIMULADO CON ESTILO DESATURADO
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Simular Error de Conexión",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Lanza IOException durante la descarga de pedidos",
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

        // TARJETA DE RESULTADOS Y BARRAS COMPARATIVAS
        item {
            when (val estado = cargaState) {
                CargaUi.Inicial -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "Selecciona 'Una por una' o 'A la vez' para iniciar la carga y medir los tiempos de ejecución.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                CargaUi.Cargando -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
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
                                    text = "Ejecutando cargas simuladas...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Obteniendo perfil y pedidos en segundo plano...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                is CargaUi.Exito -> {
                    NotionCard(
                        backgroundColor = VerdeSuave,
                        borderColor = VerdeIndicadores.copy(alpha = 0.3f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Resultados Medidos en Tiempo Real",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerdeIndicadores
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Modo de ejecución:", style = MaterialTheme.typography.bodyLarge)
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
                                Text("Pedidos procesados:", style = MaterialTheme.typography.bodyLarge)
                                Text("${estado.pedidos} pedidos", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tiempo total registrado:", style = MaterialTheme.typography.bodyLarge)
                                Text("${estado.milisegundos} ms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // GRÁFICO COMPARATIVO DE BARRAS HORIZONTALES
                            Text(
                                text = "Visualización Comparativa de Tiempo (ms)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            val maxTheoretical = 6000f
                            val actualRatio = (estado.milisegundos / maxTheoretical).coerceIn(0.1f, 1.0f)
                            val animRatio by animateFloatAsState(targetValue = actualRatio, label = "barAnim")

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Ejecución Actual (${estado.modo}): ${estado.milisegundos} ms",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(animRatio)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }

                                Text(
                                    text = "Benchmark Teórico Paralelo (~3000 ms)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(3000f / maxTheoretical)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(VerdeIndicadores)
                                    )
                                }

                                Text(
                                    text = "Benchmark Teórico Secuencial (~5000 ms)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(5000f / maxTheoretical)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(MaterialTheme.colorScheme.secondary)
                                    )
                                }
                            }
                        }
                    }
                }

                is CargaUi.Error -> {
                    NotionCard(
                        backgroundColor = RojoSuave,
                        borderColor = RojoIndicadores.copy(alpha = 0.4f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = RojoIndicadores,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Error de Carga Simulado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RojoIndicadores
                                )
                            }

                            Text(
                                text = estado.mensaje,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            NotionSecondaryButton(
                                text = "Reintentar Operación",
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
