package com.tecsup.inventarioherramientas.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import com.tecsup.inventarioherramientas.domain.model.Movimiento
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.ui.*
@Composable
fun DetalleScreen(
    consultarPrestamo: suspend (String) -> Result<Movimiento?>,
    finalizar: suspend (String, String) -> Result<String>,
    estado: UiState<Item>,
    aviso: String?,
    onReintentar: () -> Unit,
    esAdmin: Boolean,
    onSolicitar: (String) -> Unit,
    onMovimiento: (String, String) -> Unit,
    onVerHistorial: (String) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BotonVolver("Volver", onVolver)

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

                Surface(shape = RoundedCornerShape(24.dp), shadowElevation = 2.dp, color = Color.White) {
                    Column {
                        Box {
                            Foto(herramienta.nombre, Modifier.fillMaxWidth().height(195.dp))
                            Box(Modifier.align(Alignment.TopEnd).padding(12.dp)) { EstadoItem(herramienta) }
                        }
                        Column(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Petroleo, Turquesa)))
                            .padding(horizontal = 20.dp, vertical = 15.dp)) {
                            Text(herramienta.codigo, color = Ambar, letterSpacing = 1.8.sp,
                                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text(herramienta.nombre, style = MaterialTheme.typography.headlineMedium,
                                color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DatoFicha("Ubicación", herramienta.ubicacion, Modifier.weight(1f))
                            DatoFicha("Condición", herramienta.condicion.lowercase().replaceFirstChar { it.uppercase() }, Modifier.weight(1f))
                        }
                        if (herramienta.descripcion.isNotBlank()) {
                            HorizontalDivider()
                            DatoFicha("Observaciones", herramienta.descripcion)
                        }
                    }
                }
                Text(com.tecsup.inventarioherramientas.domain.model.soles(com.tecsup.inventarioherramientas.domain.model.tarifaSugerida(herramienta.nombre)) + " / día · tarifa sugerida", style = MaterialTheme.typography.titleLarge)
                val prestada = herramienta.disponibilidad == "PRESTADA"
                val permitePrestamo = herramienta.listaParaPrestamo
                if (esAdmin && prestada) {
                    var resumen by remember(herramienta.prestamoActivoId) { mutableStateOf("Consultando préstamo…") }
                    LaunchedEffect(herramienta.prestamoActivoId) {
                        val movimiento = herramienta.prestamoActivoId?.let { consultarPrestamo(it).getOrNull() }
                        resumen = if (movimiento == null) "Datos del préstamo no disponibles. Consulta el historial."
                        else "Prestado a ${movimiento.responsable} · desde ${java.text.DateFormat.getDateInstance().format(java.util.Date(movimiento.fechaMillis))}"
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(resumen, Modifier.fillMaxWidth().padding(16.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
                if (!prestada && !permitePrestamo) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFEDC6)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("Préstamo bloqueado", fontWeight = FontWeight.Bold, color = Color(0xFF5A3700))
                            Text(if (herramienta.condicion == "MANTENIMIENTO") "La herramienta está en mantenimiento."
                                else "La condición o la disponibilidad actual impiden registrar una salida.",
                                color = Color(0xFF5A3700), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                if (esAdmin && herramienta.condicion == "MANTENIMIENTO" && herramienta.disponibilidad == "DISPONIBLE" && herramienta.prestamoActivoId.isNullOrBlank()) {
                    CierreMantenimiento(finalizar)
                }
                if (!esAdmin && permitePrestamo) {
                    Button(onClick = { onSolicitar(herramienta.id) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Ambar, contentColor = Petroleo)) {
                        Text("Solicitar alquiler  →", fontWeight = FontWeight.Bold)
                    }
                    Text("Envía tu solicitud. El administrador la revisará antes de registrar el pago.")
                }
                if (esAdmin && (prestada || permitePrestamo)) {
                    Button(onClick = { onMovimiento(herramienta.id, if (prestada) "ENTRADA" else "SALIDA") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Ambar, contentColor = Petroleo)) {
                        Text(if (prestada) "Registrar devolución" else "Registrar préstamo", fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(onClick = { onVerHistorial(herramienta.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver historial de movimientos  →")
                }
            }
        }
    }
}

@Composable
private fun DatoFicha(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiqueta, color = Turquesa, style = MaterialTheme.typography.labelMedium)
        Text(valor, color = Petroleo, style = MaterialTheme.typography.bodyLarge)
    }
}
@Composable
private fun CierreMantenimiento(finalizar: suspend (String, String) -> Result<String>) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    var condicion by rememberSaveable { mutableStateOf("BUENA") }
    var nota by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Button(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) { Text("Finalizar mantenimiento") }
    if (abierto) AlertDialog(
        onDismissRequest = { if (!ocupado) abierto = false },
        title = { Text("Finalizar mantenimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Condición después de la revisión")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for ((valor, nombre) in listOf("BUENA" to "Buena", "DANADA" to "Dañada")) {
                        FilterChip(condicion == valor, { condicion = valor }, { Text(nombre) }, enabled = !ocupado)
                    }
                }
                Text(if (condicion == "BUENA") "Quedará lista para préstamo." else "El préstamo seguirá bloqueado.")
                OutlinedTextField(nota, { if (it.length <= 500) nota = it }, label = { Text("Trabajo realizado") }, enabled = !ocupado)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { Button(enabled = !ocupado && nota.isNotBlank(), onClick = {
            ocupado = true
            error = null
            scope.launch {
                try {
                    finalizar(condicion, nota).onSuccess { abierto = false }
                        .onFailure { error = it.localizedMessage ?: "No se pudo guardar. Inténtalo nuevamente." }
                } finally { ocupado = false }
            }
        }) { Text(if (ocupado) "Guardando…" else "Guardar cierre") } },
        dismissButton = { TextButton(enabled = !ocupado, onClick = { abierto = false }) { Text("Cancelar") } }
    )
}
