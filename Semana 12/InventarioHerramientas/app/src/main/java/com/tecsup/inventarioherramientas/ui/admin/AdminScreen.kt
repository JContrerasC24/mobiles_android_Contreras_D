package com.tecsup.inventarioherramientas.ui.admin

import com.tecsup.inventarioherramientas.ui.BotonVolver
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.tecsup.inventarioherramientas.R
import com.tecsup.inventarioherramientas.ui.Foto
import com.tecsup.inventarioherramientas.ui.Petroleo
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
    onSolicitudes: () -> Unit,
    onRegistrarHerramienta: () -> Unit,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var importando by remember { mutableStateOf(false) }
    var resultadoLote by rememberSaveable { mutableStateOf("") }
    var limpiando by rememberSaveable { mutableStateOf(false) }
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
        BotonVolver("Volver al inventario", onVolver, enabled = !ocupado)
        Button(onClick = onSolicitudes, modifier = Modifier.fillMaxWidth()) { Text("Revisar solicitudes y pagos  →") }

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

        RegistroCobros()
        AccionTaller("Registrar herramienta", "Crea una ficha para el catálogo.", Petroleo, Color.White,
            habilitada = !ocupado, onClick = onRegistrarHerramienta,
            imagen = { Foto("taladro", Modifier.size(66.dp).clip(RoundedCornerShape(14.dp))) })

        OutlinedButton(onClick = {
            importando = true
            scope.launch {
                try {
                    com.tecsup.inventarioherramientas.data.repository.cargarLoteTaller()
                    resultadoLote = "Lote verificado en el taller."
                    onSincronizar()
                } catch (e: kotlinx.coroutines.CancellationException) { throw e }
                catch (e: Exception) { resultadoLote = e.message ?: "No se pudo cargar el lote." }
                finally { importando = false }
            }
        }, enabled = !ocupado && !importando, modifier = Modifier.fillMaxWidth()) {
            Text(if (importando) "Cargando unidades…" else "Cargar lote de 23 unidades")
        }
        if (resultadoLote.isNotEmpty()) Text(resultadoLote)
        AccionTaller(if (ocupado && !limpiando) "Sincronizando…" else "Sincronizar inventario",
            "Actualiza la copia de este dispositivo.", Color(0xFFDAF1ED), Petroleo,
            habilitada = !ocupado, enCurso = ocupado && !limpiando,
            onClick = { limpiando = false; onSincronizar() },
            imagen = { Icon(painterResource(R.drawable.ic_sincronizar), null, Modifier.size(30.dp)) })

        AccionTaller(if (ocupado && limpiando) "Limpiando…" else "Limpiar caché",
            "Elimina herramientas y favoritos locales.", Color(0xFFFFEFEB), Color(0xFF82392F),
            habilitada = !ocupado, enCurso = ocupado && limpiando, onClick = { confirmarLimpieza = true },
            imagen = { Icon(painterResource(R.drawable.ic_limpiar), null, Modifier.size(28.dp)) })
        if (mensaje != null) {
            Text(
                text = mensaje,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
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
                        limpiando = true
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
@Composable
private fun AccionTaller(
    titulo: String, detalle: String, fondo: Color, tinta: Color,
    habilitada: Boolean, enCurso: Boolean = false,
    onClick: () -> Unit, imagen: @Composable () -> Unit
) {
    val interaccion = remember { MutableInteractionSource() }
    val pulsado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (pulsado) 0.98f else 1f, label = "Pulsación")
    Button(onClick = onClick, enabled = habilitada, interactionSource = interaccion,
        modifier = Modifier.fillMaxWidth().scale(escala), shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = fondo, contentColor = tinta,
            disabledContainerColor = fondo.copy(alpha = 0.78f), disabledContentColor = tinta.copy(alpha = 0.8f)),
        contentPadding = PaddingValues(12.dp)) {
        Box(Modifier.width(66.dp), contentAlignment = Alignment.Center) {
            if (enCurso) CircularProgressIndicator(Modifier.size(28.dp), color = tinta) else imagen()
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(detalle, style = MaterialTheme.typography.bodySmall)
        }
        Icon(painterResource(R.drawable.ic_volver), null,
            Modifier.size(18.dp).scale(scaleX = -1f, scaleY = 1f))
    }
}