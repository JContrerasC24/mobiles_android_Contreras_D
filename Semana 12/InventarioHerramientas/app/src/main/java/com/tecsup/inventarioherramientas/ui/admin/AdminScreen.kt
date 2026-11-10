package com.tecsup.inventarioherramientas.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun AdminScreen(
    estado: UiState<ResumenAdmin>,
    ocupado: Boolean,
    mensaje: String?,
    onSincronizar: () -> Unit,
    onLimpiarCache: () -> Unit,
    onRegistrarHerramienta: () -> Unit,
    onVolver: () -> Unit
) {
    var confirmarLimpieza by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onVolver,
            enabled = !ocupado
        ) {
            Text("Volver al inventario")
        }

        com.tecsup.inventarioherramientas.ui.Cabecera("Administración", "Control del taller")

        when (estado) {
            UiState.Cargando -> {
                CircularProgressIndicator()
                Text("Consultando totales...")
            }

            is UiState.Error -> {
                Text(
                    text = estado.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Exito -> {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    com.tecsup.inventarioherramientas.ui.Indicador(estado.datos.totalGuardado.toString(), "Herramientas", Modifier.weight(1f))
                    com.tecsup.inventarioherramientas.ui.Indicador(estado.datos.totalFavoritos.toString(), "Favoritos", Modifier.weight(1f), destacado = true)
                }
            }
        }

        Button(
            onClick = onRegistrarHerramienta,
            enabled = !ocupado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar herramienta")
        }

        Button(
            onClick = onSincronizar,
            enabled = !ocupado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sincronizar inventario")
        }

        OutlinedButton(
            onClick = { confirmarLimpieza = true },
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            enabled = !ocupado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpiar caché")
        }

        if (ocupado) {
            CircularProgressIndicator()
            Text("Procesando...")
        }

        if (mensaje != null) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (confirmarLimpieza) {
        AlertDialog(
            onDismissRequest = {
                confirmarLimpieza = false
            },
            title = {
                Text("Limpiar caché local")
            },
            text = {
                Text(
                    "Se eliminarán las herramientas descargadas y " +
                            "los favoritos de este dispositivo. " +
                            "Los datos de Firestore se conservarán."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarLimpieza = false
                        onLimpiarCache()
                    },
                    enabled = !ocupado
                ) {
                    Text("Limpiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmarLimpieza = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}