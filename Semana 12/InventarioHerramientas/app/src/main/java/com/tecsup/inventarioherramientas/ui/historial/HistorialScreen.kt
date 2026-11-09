package com.tecsup.inventarioherramientas.ui.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.domain.model.Movimiento
import com.tecsup.inventarioherramientas.ui.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(
    herramientaId: String,
    estado: UiState<List<Movimiento>>,
    onActualizar: () -> Unit,
    onVolver: () -> Unit
) {
    val formatoFecha = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "PE")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onVolver) {
                    Text("Volver al detalle")
                }

                Text(
                    text = "Historial de movimientos",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text("Herramienta: $herramientaId")

                Button(
                    onClick = onActualizar,
                    enabled = estado !is UiState.Cargando
                ) {
                    Text("Actualizar historial")
                }
            }
        }

        when (estado) {
            UiState.Cargando -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = estado.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onActualizar) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    item {
                        Text(
                            "Esta herramienta todavía no tiene movimientos."
                        )
                    }
                } else {
                    items(
                        items = estado.datos,
                        key = { it.id }
                    ) { movimiento ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = when (movimiento.tipo) {
                                        "SALIDA" -> "Salida por préstamo"
                                        "ENTRADA" -> "Entrada por devolución"
                                        else -> movimiento.tipo
                                    },
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "${movimiento.codigoHerramienta} — " +
                                            movimiento.nombreHerramienta
                                )

                                Text(
                                    "Fecha: " + formatoFecha.format(
                                        Date(movimiento.fechaMillis)
                                    )
                                )

                                Text(
                                    "Responsable del préstamo: " +
                                            movimiento.responsable
                                )

                                Text(
                                    "Condición: ${movimiento.condicion}"
                                )

                                if (movimiento.observacion.isNotBlank()) {
                                    Text(
                                        "Observación: " +
                                                movimiento.observacion
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}