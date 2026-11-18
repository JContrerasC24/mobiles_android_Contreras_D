package com.contreras.campusgps.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.contreras.campusgps.viewmodel.*

@Composable
fun PanelReproduccion(vm: MapaViewModel) {
    val estado by vm.reproduccion.collectAsState()
    if (estado.estado == EstadoReproduccion.FINALIZADO) Text("Llegaste al destino de la demostración")
    Button(onClick = vm::iniciarRecorrido, enabled = estado.estado == EstadoReproduccion.LISTO) {
        Text("Iniciar")
    }
}
