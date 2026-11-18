package com.contreras.campusgps.ui

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.campusgps.viewmodel.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PanelReproduccion(vm: MapaViewModel) {
    val estado by vm.reproduccion.collectAsStateWithLifecycle()
    val texto = when (estado.estado) {
        EstadoReproduccion.LISTO -> "Listo para iniciar"
        EstadoReproduccion.REPRODUCIENDO -> "Recorriendo la demostración"
        EstadoReproduccion.PAUSADO -> "En pausa"
        EstadoReproduccion.DETENIDO -> "Detenido"
        EstadoReproduccion.FINALIZADO -> "Llegaste al destino de la demostración"
    }
    Text(texto, style = MaterialTheme.typography.titleMedium)
    LinearProgressIndicator(progress = { estado.progreso }, modifier = Modifier.fillMaxWidth())
    Text("${(estado.progreso * 100).toInt()} %", style = MaterialTheme.typography.labelMedium)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (estado.estado) {
            EstadoReproduccion.REPRODUCIENDO -> Button(onClick = vm::pausarRecorrido) { Text("Pausar") }
            EstadoReproduccion.PAUSADO -> Button(onClick = vm::iniciarRecorrido) { Text("Reanudar") }
            EstadoReproduccion.LISTO, EstadoReproduccion.DETENIDO -> Button(onClick = vm::iniciarRecorrido) { Text("Iniciar") }
            EstadoReproduccion.FINALIZADO -> Unit
        }
        OutlinedButton(onClick = vm::reiniciarRecorrido) { Text("Reiniciar") }
        OutlinedButton(onClick = vm::detenerRecorrido,
            enabled = estado.estado != EstadoReproduccion.DETENIDO && estado.estado != EstadoReproduccion.LISTO) { Text("Detener") }
    }
}
