package com.contreras.centrotareas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionDangerButton
import com.contreras.centrotareas.ui.components.NotionPrimaryButton
import com.contreras.centrotareas.ui.components.NotionSecondaryButton
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.viewmodel.TareasViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Pantalla para la ejecución y gestión de tareas con progreso en segundo plano.
 *
 * Incluye barra de progreso reactiva, tiempo transcurrido en vivo, cancelaciones
 * mediante Job y prueba de reactividad de la interfaz.
 */
@Composable
fun PantallaProcesos(
    vm: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()

    var toques by remember { mutableStateOf(0) }
    var tiempoTranscurridoMs by remember { mutableLongStateOf(0L) }

    val animatedProgress by animateFloatAsState(targetValue = progreso, label = "progressAnim")

    // Temporizador en tiempo real mientras el proceso está activo
    LaunchedEffect(corriendo) {
        if (corriendo) {
            val inicio = System.currentTimeMillis()
            while (corriendo) {
                tiempoTranscurridoMs = System.currentTimeMillis() - inicio
                delay(50)
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Administración de Procesos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Control de progreso y tareas en segundo plano.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // PRUEBA DE RESPUESTA DE INTERFAZ
        item {
            NotionCard(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prueba de Respuesta de UI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Presiona el botón mientras el proceso se ejecuta para verificar que el hilo UI no se bloquee.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    NotionSecondaryButton(
                        text = "Toques: $toques",
                        icon = Icons.Default.TouchApp,
                        onClick = { toques++ }
                    )
                }
            }
        }

        // TARJETA PRINCIPAL DEL PROCESO
        item {
            NotionCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tarea Larga en Segundo Plano",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        NotionStatusBadge(
                            status = if (corriendo) "Ejecutando" else mensaje
                        )
                    }

                    Text(
                        text = "Simulación de una operación distribuida de 100 iteraciones en Dispatchers.Default con actualización de StateFlow a 40ms.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // BARRA DE PROGRESO CON ANIMACIÓN
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso de avance",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${(animatedProgress * 100).roundToInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // MÉTRICAS Y TIEMPOS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Estado actual",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Crossfade(targetState = mensaje, label = "estadoCrossfade") { msg ->
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tiempo transcurrido",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${tiempoTranscurridoMs} ms",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // MENSAJE INFORMATIVO / RESULTADO
                    AnimatedVisibility(
                        visible = !corriendo && mensaje != "Listo",
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (mensaje == "Terminó") Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                                contentDescription = null,
                                tint = if (mensaje == "Terminó") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (mensaje == "Terminó") "Proceso finalizado correctamente al 100%." else "Proceso cancelado por solicitud del usuario.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // BOTONES DE CONTROL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NotionPrimaryButton(
                            text = "Iniciar Proceso",
                            icon = Icons.Default.PlayArrow,
                            onClick = { vm.tareaLarga() },
                            enabled = !corriendo,
                            modifier = Modifier.weight(1f)
                        )

                        NotionDangerButton(
                            text = "Cancelar",
                            icon = Icons.Default.Cancel,
                            onClick = { vm.cancelar() },
                            enabled = corriendo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
