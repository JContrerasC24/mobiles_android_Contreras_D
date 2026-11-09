package com.tecsup.inventarioherramientas.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    aviso: String?,
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onVolver) {
            Text("Volver ")
        }

        Text(
            text = "Detalle de herramienta",
            style = MaterialTheme.typography.headlineMedium
        )

        if (aviso != null) {
            Text(
                text = aviso,
                color = MaterialTheme.colorScheme.error
            )
        }

        when (estado) {
            UiState.Cargando -> {
                CircularProgressIndicator()
                Text("Consultando detalle...")
            }

            is UiState.Error -> {
                Text(
                    text = estado.mensaje,
                    color = MaterialTheme.colorScheme.error
                )

                Button(onClick = onReintentar) {
                    Text("Reintentar")
                }
            }

            is UiState.Exito -> {
                val herramienta = estado.datos

                Text(
                    text = herramienta.nombre,
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Código: ${herramienta.codigo}")

                Text(
                    text = herramienta.descripcion.ifBlank {
                        "Sin descripción."
                    }
                )

                Text("Ubicación: ${herramienta.ubicacion}")
                Text("Condición: ${herramienta.condicion}")
                Text("Disponibilidad: ${herramienta.disponibilidad}")
            }
        }
    }
}