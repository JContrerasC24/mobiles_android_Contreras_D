package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.ReglasCitas
import com.contreras.huellitasvet.ui.components.*
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

    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        VetHeader("Registro de atención",onVolver={if(!ocupado)onVolver()},subtitulo="Cada detalle forma parte de su historia")
        detalle?.let { d ->
            VetCard {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    MascotaAvatar(especie=d.mascota.mascota.especie)
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        Text("${d.mascota.mascota.nombre} · ${d.mascota.mascota.raza}",style=MaterialTheme.typography.titleSmall)
                        Text("Dueño: ${d.mascota.dueno.nombre}",style=MaterialTheme.typography.bodySmall)
                        Text("${d.servicio.nombre} · ${fechaCorta(d.cita.fecha)} · ${d.cita.hora}",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
                        EstadoCitaChip(d.cita.estado)
                    }
                }
            }
        }
        Text("Registro clínico",style=MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.CenterVertically) {
            VetField(peso,{peso=it},"Peso (kg)",Modifier.weight(1f),editable,errores["peso"],KeyboardType.Decimal)
            VetCard(Modifier.weight(1f)) {
                Text("Peso anterior",style=MaterialTheme.typography.bodySmall)
                Text("${detalle?.mascota?.mascota?.peso ?: "—"} kg",style=MaterialTheme.typography.titleSmall)
            }
        }
        VetField(diagnostico,{diagnostico=it},"Diagnóstico",enabled=editable,error=errores["diagnostico"],lines=3)
        VetField(tratamiento,{tratamiento=it},"Tratamiento",enabled=editable,lines=3)
        detalle?.let { VetNotice("Se actualizará el peso de ${it.mascota.mascota.nombre} al registrar la atención.") }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (detalle != null && !ReglasCitas.activa(detalle!!.cita)) Text("Esta cita ya no permite registrar atención.")
        Button(shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),enabled = editable, onClick = {
            vm.registrar(diagnostico, tratamiento, peso.replace(',', '.').toDoubleOrNull() ?: Double.NaN)
        }, modifier = Modifier.fillMaxWidth().heightIn(min=54.dp)) { Text(if (ocupado) "Guardando…" else "Registrar atención") }
        VetDanger("Cancelar cita",{confirmarCancelar=true},editable)
    }
    if (confirmarCancelar) AlertDialog(
        onDismissRequest = { confirmarCancelar = false },
        title = { Text("Cancelar cita") }, text = { Text("¿Quieres cancelar esta cita sin registrar una atención?") },
        confirmButton = { TextButton(shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),enabled = editable, onClick = { confirmarCancelar = false; vm.cancelar() }) { Text("Sí, cancelar") } },
        dismissButton = { TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") } })
}
