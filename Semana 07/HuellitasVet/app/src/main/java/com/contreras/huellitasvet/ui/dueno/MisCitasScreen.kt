package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.ReglasCitas
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.viewmodel.MisCitasViewModel

@Composable
fun MisCitasScreen(vm: MisCitasViewModel, onInicio: () -> Unit, onPedir: () -> Unit) {
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()
    val ocupado by vm.cancelando.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var confirmarId by rememberSaveable { mutableStateOf<Int?>(null) }
    val seleccionada = (proximas + historial).firstOrNull { it.cita.id == confirmarId }
    val citas = if (pestana == 0) proximas else historial

    Scaffold(
        contentWindowInsets=WindowInsets(0,0,0,0),
        floatingActionButton = {
            FloatingActionButton(onClick = onPedir) {
                Icon(Icons.Default.Add, contentDescription = "Pedir una cita")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                VetHeader("Mis citas",subtitulo="Puedes cancelar con más de 2 horas de anticipación")
            }
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    VetChoice("Próximas",pestana==0,{pestana=0},Modifier.weight(1f))
                    VetChoice("Historial",pestana==1,{pestana=1},Modifier.weight(1f))
                }
            }
            mensaje?.let { item { Text(it) } }
            if (citas.isEmpty()) item {
                Text(if (pestana == 0) "No tienes próximas citas" else "Aún no tienes citas en el historial")
            }
            items(citas, key = { it.cita.id }) { detalle ->
                CitaCard(detalle, ReglasCitas.puedeCancelar(detalle.cita, ahora), ocupado,
                    onCancelar = { confirmarId = detalle.cita.id })
            }
        }
    }
    if (confirmarId != null) {
        val permitido = seleccionada?.let { ReglasCitas.puedeCancelar(it.cita, ahora) } == true
        AlertDialog(
            onDismissRequest = { confirmarId = null },
            title = { Text("Cancelar cita") },
            text = { Text(if (permitido)
                "¿Quieres cancelar la cita de ${seleccionada?.mascota?.mascota?.nombre} del ${seleccionada?.cita?.fecha} a las ${seleccionada?.cita?.hora}?"
                else "Ya no puedes cancelar esta cita") },
            confirmButton = {
                if (permitido) TextButton(enabled = !ocupado, onClick = {
                    seleccionada?.let(vm::cancelar)
                    confirmarId = null
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarId = null }) { Text(if (permitido) "Volver" else "Cerrar") }
            }
        )
    }
}
