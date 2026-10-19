package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.ReglasCitas
import com.contreras.huellitasvet.ui.components.MascotaAvatar
import com.contreras.huellitasvet.ui.components.EstadoCitaChip
import com.contreras.huellitasvet.viewmodel.AtenderCitaViewModel

@Composable
fun AtenderCitaScreen(vm: AtenderCitaViewModel, onVolver: () -> Unit) {
    val detalle by vm.cita.collectAsStateWithLifecycle()
    val errores by vm.errores.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val finalizado by vm.finalizado.collectAsStateWithLifecycle()
    var diagnostico by rememberSaveable { mutableStateOf("") }
    var tratamiento by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var inicializado by rememberSaveable { mutableStateOf(false) }
    var confirmarCancelar by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(detalle) {
        if (!inicializado && detalle != null) {
            peso = detalle!!.mascota.mascota.peso.toString()
            inicializado = true
        }
    }
    LaunchedEffect(finalizado) { if (finalizado) onVolver() }
    val editable = detalle?.let { ReglasCitas.activa(it.cita) } == true && !ocupado && !finalizado

    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onVolver, enabled = !ocupado) { Text("Volver a la agenda") }
        Text("Atender cita", style = MaterialTheme.typography.headlineMedium)
        detalle?.let { d ->
            MascotaAvatar(especie = d.mascota.mascota.especie)
            Text(d.mascota.mascota.nombre, style = MaterialTheme.typography.titleLarge)
            Text("Dueño: ${d.mascota.dueno.nombre}")
            Text("${d.servicio.nombre} · ${d.cita.fecha} · ${d.cita.hora}")
            EstadoCitaChip(d.cita.estado)
            Text("Peso anterior: ${d.mascota.mascota.peso} kg")
        }
        OutlinedTextField(value = peso, onValueChange = { peso = it }, enabled = editable,
            label = { Text("Peso actual (kg)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = errores["peso"] != null, supportingText = { errores["peso"]?.let { Text(it) } })
        OutlinedTextField(value = diagnostico, onValueChange = { diagnostico = it }, enabled = editable,
            label = { Text("Diagnóstico") }, modifier = Modifier.fillMaxWidth(), minLines = 3,
            isError = errores["diagnostico"] != null, supportingText = { errores["diagnostico"]?.let { Text(it) } })
        OutlinedTextField(value = tratamiento, onValueChange = { tratamiento = it }, enabled = editable,
            label = { Text("Tratamiento") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        detalle?.let { Text("Se actualizará el peso de ${it.mascota.mascota.nombre}.") }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (detalle != null && !ReglasCitas.activa(detalle!!.cita)) Text("Esta cita ya no permite registrar atención.")
        Button(enabled = editable, onClick = {
            vm.registrar(diagnostico, tratamiento, peso.replace(',', '.').toDoubleOrNull() ?: Double.NaN)
        }, modifier = Modifier.fillMaxWidth()) { Text(if (ocupado) "Guardando…" else "Registrar atención") }
        TextButton(enabled = editable, onClick = { confirmarCancelar = true }) { Text("Cancelar cita") }
    }
    if (confirmarCancelar) AlertDialog(
        onDismissRequest = { confirmarCancelar = false },
        title = { Text("Cancelar cita") }, text = { Text("¿Quieres cancelar esta cita sin registrar una atención?") },
        confirmButton = { TextButton(enabled = editable, onClick = { confirmarCancelar = false; vm.cancelar() }) { Text("Sí, cancelar") } },
        dismissButton = { TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") } })
}
