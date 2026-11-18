package com.contreras.campusgps.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.contreras.campusgps.model.Recorrido
import com.contreras.campusgps.util.Trayectoria
import com.contreras.campusgps.viewmodel.Reproduccion
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AutomovilMapa(recorrido: Recorrido, reproduccion: StateFlow<Reproduccion>) {
    // Solo este marcador observa cada fotograma; el catálogo y el mapa no se reconstruyen.
    val estado by reproduccion.collectAsState()
    val trayectoria = remember(recorrido) { Trayectoria(recorrido.puntos) }
    val punto = trayectoria.punto(estado.progreso)
    Marker(state = rememberUpdatedMarkerState(LatLng(punto.posicion.latitud, punto.posicion.longitud)),
        icon = remember { IconosMapa.automovil() }, title = "Automóvil de demostración",
        anchor = Offset(0.5f, 0.5f), flat = true, rotation = punto.rumbo, zIndex = 5f)
}
