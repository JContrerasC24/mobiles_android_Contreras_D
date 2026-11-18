package com.contreras.centrotareas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.contreras.centrotareas.model.PrimosUi
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionDangerButton
import com.contreras.centrotareas.ui.components.NotionPrimaryButton
import com.contreras.centrotareas.ui.components.NotionSecondaryButton
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.theme.VerdeIndicadores
import com.contreras.centrotareas.ui.theme.VerdeSuave
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla para el cálculo concurrente de números primos.
 *
 * Utiliza Dispatchers.Default para procesamiento matemático intensivo y
 * permite cancelación cooperativa mediante ensureActive().
 */
@Composable
fun PantallaPrimos(
    vm: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val primosState by vm.primos.collectAsState()

    var numeroTexto by remember { mutableStateOf("1000") }

    val estaCalculando = primosState is PrimosUi.Calculando
    val numeroValido = numeroTexto.toIntOrNull()?.let { it >= 2 } == true

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "Cálculo de Números Primos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Procesamiento intensivo en CPU con Dispatchers.Default y cancelación cooperativa.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // TARJETA DE PARÁMETROS DE ENTRADA
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
                            text = "Parámetros de Entrada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val statusTag = when (primosState) {
                            PrimosUi.Inicial -> "Inicial"
                            PrimosUi.Calculando -> "Calculando"
                            is PrimosUi.Listo -> "Completado"
                            PrimosUi.Cancelado -> "Cancelado"
                        }
                        NotionStatusBadge(status = statusTag)
                    }

                    OutlinedTextField(
                        value = numeroTexto,
                        onValueChange = { input ->
                            numeroTexto = input.filter { it.isDigit() }
                        },
                        label = { Text("Número máximo (N ≥ 2)") },
                        placeholder = { Text("Ej. 1000") },
                        isError = !numeroValido && numeroTexto.isNotEmpty(),
                        supportingText = {
                            if (!numeroValido && numeroTexto.isNotEmpty()) {
                                Text(
                                    text = "Ingresa un número entero mayor o igual a 2",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // CHIPS DE SELECCIÓN RÁPIDA DE VALORES PRESET
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1000", "5000", "20000", "50000").forEach { preset ->
                            NotionSecondaryButton(
                                text = preset,
                                onClick = { numeroTexto = preset },
                                enabled = !estaCalculando,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // BOTONES DE CONTROL DE CÁLCULO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NotionPrimaryButton(
                            text = "Calcular Primos",
                            icon = Icons.Default.Calculate,
                            onClick = {
                                numeroTexto.toIntOrNull()?.let { num ->
                                    vm.contar(num)
                                }
                            },
                            enabled = !estaCalculando && numeroValido,
                            modifier = Modifier.weight(1f)
                        )

                        NotionDangerButton(
                            text = "Cancelar",
                            icon = Icons.Default.Cancel,
                            onClick = { vm.cancelarPrimos() },
                            enabled = estaCalculando,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // TARJETA DE RESULTADOS Y ESTADOS
        item {
            when (val estado = primosState) {
                PrimosUi.Inicial -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Functions,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Ingresa un valor N y presiona 'Calcular Primos' para evaluar divisibilidad en segundo plano.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                PrimosUi.Calculando -> {
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
                                    text = "Calculando números primos hasta $numeroTexto...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ejecutando algoritmo optimizado en Dispatchers.Default.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                is PrimosUi.Listo -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        NotionCard(
                            backgroundColor = VerdeSuave,
                            borderColor = VerdeIndicadores.copy(alpha = 0.3f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = VerdeIndicadores,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Cálculo Completado Exitosamente",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeIndicadores
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Cantidad de números primos:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${estado.cantidad}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tiempo total de ejecución:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${estado.milisegundos} ms",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                PrimosUi.Cancelado -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "El cálculo fue cancelado por el usuario. La corrutina interrumpió el bucle mediante ensureActive().",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
