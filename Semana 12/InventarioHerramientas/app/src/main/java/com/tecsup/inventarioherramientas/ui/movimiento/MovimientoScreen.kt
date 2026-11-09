package com.tecsup.inventarioherramientas.ui.movimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun MovimientoScreen(
    herramientaId: String,
    tipo: String,
    estado: UiState<String?>,
    onGuardar: (String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var responsable by rememberSaveable { mutableStateOf("") }
    var observacion by rememberSaveable { mutableStateOf("") }
    var condicion by rememberSaveable { mutableStateOf("BUENA") }

    val salida = tipo == "SALIDA"
    val cargando = estado is UiState.Cargando
    val confirmado = (estado as? UiState.Exito)?.datos
    val editable = !cargando && confirmado == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onVolver,
            enabled = !cargando
        ) {
            Text("Volver al detalle")
        }

        Text(
            text = if (salida) {
                "Registrar préstamo"
            } else {
                "Registrar devolución"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Herramienta: $herramientaId")

        if (salida) {
            OutlinedTextField(
                value = responsable,
                onValueChange = { responsable = it },
                label = { Text("Persona que recibe la herramienta") },
                enabled = editable,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Condición al devolver")

            listOf(
                "BUENA" to "Buena",
                "DANADA" to "Dañada",
                "MANTENIMIENTO" to "Requiere mantenimiento"
            ).forEach { (valor, etiqueta) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = condicion == valor,
                        onClick = { condicion = valor },
                        enabled = editable
                    )

                    Text(etiqueta)
                }
            }
        }

        OutlinedTextField(
            value = observacion,
            onValueChange = { observacion = it },
            label = { Text("Observaciones") },
            enabled = editable,
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        if (estado is UiState.Error) {
            Text(
                text = estado.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (confirmado != null) {
            Text(confirmado)
        }

        Button(
            onClick = {
                onGuardar(responsable, condicion, observacion)
            },
            enabled = editable,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (cargando) {
                    "Registrando..."
                } else if (salida) {
                    "Confirmar préstamo"
                } else {
                    "Confirmar devolución"
                }
            )
        }

        if (cargando) {
            CircularProgressIndicator()
        }
    }
}