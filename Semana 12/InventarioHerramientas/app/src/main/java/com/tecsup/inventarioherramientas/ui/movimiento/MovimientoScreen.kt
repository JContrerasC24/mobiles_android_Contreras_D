package com.tecsup.inventarioherramientas.ui.movimiento

import com.tecsup.inventarioherramientas.ui.BotonVolver
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
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
    solicitudAprobada: Boolean = false,
    nombreInicial: String = "",
    diasIniciales: Int = 1,
    tarifaInicial: Long?,
    herramientaId: String,
    tipo: String,
    estado: UiState<String?>,
    onGuardar: (String, com.tecsup.inventarioherramientas.domain.model.CobroAlquiler, String, String) -> Unit,
    onTerminar: () -> Unit,
    onVolver: () -> Unit
) {
    var tarifa by rememberSaveable { mutableStateOf("") }
    var dias by rememberSaveable { mutableStateOf(diasIniciales.toString()) }
    var metodo by rememberSaveable { mutableStateOf("EFECTIVO") }
    var referencia by rememberSaveable { mutableStateOf("") }
    var recibido by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(tarifaInicial) {
        if (tarifa.isBlank() && tarifaInicial != null) tarifa = java.math.BigDecimal.valueOf(tarifaInicial, 2).toPlainString()
    }
    val importe = com.tecsup.inventarioherramientas.domain.model.leerCentimos(tarifa)
    val cobro = com.tecsup.inventarioherramientas.domain.model.CobroAlquiler(importe ?: 0, dias.toIntOrNull() ?: 0, metodo, referencia)
    val cobroValido = runCatching { cobro.validar() }.isSuccess && recibido
    var responsable by rememberSaveable { mutableStateOf(nombreInicial) }
    var observacion by rememberSaveable { mutableStateOf("") }
    var condicion by rememberSaveable { mutableStateOf("BUENA") }

    val salida = tipo == "SALIDA"
    val cargando = estado is UiState.Cargando
    val confirmado = (estado as? UiState.Exito)?.datos
    val editable = !cargando && confirmado == null
    if (confirmado != null) {
        ConfirmacionMovimiento(herramientaId, confirmado, onTerminar)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BotonVolver("Volver al detalle", onVolver, enabled = !cargando)

        com.tecsup.inventarioherramientas.ui.Cabecera(if (salida) "Registrar alquiler" else "Registrar devolución", herramientaId)

        if (salida) {
            OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
                value = responsable,
                onValueChange = { responsable = it },
                readOnly = solicitudAprobada,
                label = { Text("Persona que recibe la herramienta") },
                enabled = editable,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Condición al devolver")

            com.tecsup.inventarioherramientas.ui.SelectorCondicion(condicion, editable) { condicion = it }
        }
        if (salida) {
            Text("Tarifa y pago", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White), value = tarifa, onValueChange = { tarifa = it; recibido = false }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal), label = { Text("Tarifa diaria en soles") }, enabled = editable, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White), readOnly = solicitudAprobada, value = dias, onValueChange = { dias = it.filter(Char::isDigit).take(3); recibido = false }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number), label = { Text("Días contratados") }, enabled = editable, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (opcion in listOf("EFECTIVO", "YAPE", "TARJETA")) {
                    FilterChip(metodo == opcion, { metodo = opcion; referencia = ""; recibido = false }, { Text(opcion.lowercase().replaceFirstChar { it.uppercase() }) }, enabled = editable)
                }
            }
            if (metodo != "EFECTIVO") OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White), value = referencia, onValueChange = { referencia = it.take(80); recibido = false }, label = { Text("Número de operación / comprobante") }, enabled = editable, modifier = Modifier.fillMaxWidth())
            Text("Total: " + com.tecsup.inventarioherramientas.domain.model.soles(cobro.totalCentimos), style = MaterialTheme.typography.headlineSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(recibido, { recibido = it }, enabled = editable)
                Text("Confirmo que verifiqué el pago recibido", Modifier.weight(1f))
            }
        }
        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
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

        Button(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
            onClick = {
                onGuardar(responsable, cobro, condicion, observacion)
            },
            enabled = editable && (!salida || cobroValido),
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
@Composable
private fun ConfirmacionMovimiento(id: String, mensaje: String, onTerminar: () -> Unit) {
    val terminar by rememberUpdatedState(onTerminar)
    val teclado = LocalSoftwareKeyboardController.current
    LaunchedEffect(id, mensaje) {
        teclado?.hide()
        delay(3000)
        terminar()
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Text("✓", modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        }
        Text(mensaje, style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Herramienta $id", style = MaterialTheme.typography.titleMedium)
        Text("Volviendo al catálogo…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Button(onClick = { terminar() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text("Ir al catálogo ahora")
        }
    }
}