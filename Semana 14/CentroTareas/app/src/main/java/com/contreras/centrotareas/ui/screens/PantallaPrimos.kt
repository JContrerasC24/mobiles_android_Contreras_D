package com.contreras.centrotareas.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.ui.theme.NotionSuccess
import com.contreras.centrotareas.ui.theme.NotionSuccessContainer
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla para el cálculo concurrente de números primos.
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
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

        // PANEL DE CONTROL DEL CÁLCULO
        item {
            NotionCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Parámetros de Entrada",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
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
                                    text = "Por favor ingresa un número entero mayor o igual a 2",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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

        // VISUALIZACIÓN DE RESULTADOS
        item {
            when (val estado = primosState) {
                PrimosUi.Inicial -> {
                    NotionCard(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "Presiona 'Calcular Primos' para iniciar el cómputo en Dispatchers.Default.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                PrimosUi.Calculando -> {
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
                                    text = "Calculando números primos...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "La corrutina verifica divisibilidad en segundo plano.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                is PrimosUi.Listo -> {
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        NotionCard(
                            backgroundColor = NotionSuccessContainer,
                            borderColor = NotionSuccess.copy(alpha = 0.4f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = NotionSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(
                                        text = "Cálculo Finalizado Exitosamente",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NotionSuccess
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Primos encontrados:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${estado.cantidad}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tiempo transcurrido:",
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
                        backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                        borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "Operación cancelada. El bucle de cálculo detectó ensureActive() y finalizó de forma cooperativa.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
