package com.contreras.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.contreras.campusgps.model.*
import com.contreras.campusgps.viewmodel.MapaViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(vm, lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) vm.pausarRecorrido()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer); vm.pausarRecorrido() }
    }
    val estado by vm.estado.collectAsState()
    val lugares by vm.resultados.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val recorrido by vm.recorrido.collectAsState()
    val contexto = LocalContext.current
    val teclado = LocalSoftwareKeyboardController.current
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    var versionNavegacion by remember { mutableIntStateOf(0) }
    val solicitarPermisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (permisos.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }
    val buscar: () -> Unit = {
        val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permisos.any { ContextCompat.checkSelfPermission(contexto, it) == PackageManager.PERMISSION_GRANTED })
            vm.buscar() else solicitarPermisos.launch(permisos)
    }
    val seleccionar: (Int) -> Unit = {
        vm.seleccionarLugar(it); versionNavegacion++; teclado?.hide()
        scope.launch { scroll.animateScrollTo(0) }
    }
    val posicion = (estado as? UbicacionUi.Lista)?.posicion
    Scaffold(topBar = {
        TopAppBar(title = { Column {
            Text("CampusGPS", style = MaterialTheme.typography.titleLarge)
            Text("Explora · descubre · recorre", style = MaterialTheme.typography.labelMedium)
        } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary))
    }) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding).imePadding().padding(12.dp)) {
            val alturaMapa = (maxHeight * .43f).coerceIn(160.dp, 400.dp)
            val mapa: @Composable (Modifier) -> Unit = { modifier ->
                MapaUbicacionActual(vm.reproduccion, recorrido, posicion, modifier, lugares,
                    seleccionado, versionNavegacion, seleccionar)
            }
            val panel: @Composable (Modifier) -> Unit = { modifier ->
                Column(modifier.verticalScroll(scroll), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PanelCampus(vm, estado, lugares, seleccionado, recorrido, buscar, seleccionar)
                }
            }
            if (maxWidth > maxHeight) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    mapa(Modifier.weight(1.1f).fillMaxHeight())
                    panel(Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    mapa(Modifier.fillMaxWidth().height(alturaMapa))
                    panel(Modifier.weight(1f).fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun PanelCampus(vm: MapaViewModel, estado: UbicacionUi, lugares: List<LugarConDistancia>,
    seleccionado: Int?, recorrido: Recorrido?, buscar: () -> Unit, seleccionar: (Int) -> Unit) {
    val consulta by vm.consulta.collectAsState()
    val categoria by vm.categoria.collectAsState()
    val destino = lugares.firstOrNull { it.lugar.id == seleccionado }
    if (recorrido != null) {
        Text(recorrido.destino.nombre, style = MaterialTheme.typography.titleLarge)
        Text("Demostración en línea recta · no es una ruta por calles", style = MaterialTheme.typography.bodySmall)
        Text("Origen: mi ubicación · ${recorrido.metros.toInt()} m geográficos")
        PanelReproduccion(vm)
        TextButton(onClick = vm::cerrarRecorrido) { Text("Cerrar recorrido") }
    } else {
        DetalleLugar(destino)
        if (destino != null) {
            Button(onClick = vm::verRecorrido, enabled = estado is UbicacionUi.Lista,
                modifier = Modifier.fillMaxWidth()) { Text("Ver recorrido") }
        }
    }
    Button(onClick = buscar, enabled = estado != UbicacionUi.Buscando, modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
        Text(if (estado == UbicacionUi.Buscando) "Buscando ubicación…" else "Obtener mi ubicación")
    }
    when (estado) {
        UbicacionUi.Inicial -> Text("Explora el catálogo. Activa tu ubicación para medir distancias y ver recorridos.")
        UbicacionUi.Buscando -> LinearProgressIndicator(Modifier.fillMaxWidth())
        is UbicacionUi.Error -> MensajeError(estado.mensaje, estado.tipo, buscar)
        is UbicacionUi.Lista -> {
            Text("Ubicación del dispositivo disponible", style = MaterialTheme.typography.labelMedium)
            lugares.firstOrNull { it.metros != null }?.let { cercano ->
                TextButton(onClick = { seleccionar(cercano.lugar.id) }) { Text("Ir al lugar más cercano") }
            }
        }
    }
    BuscadorLugares(consulta, categoria, vm.categorias, vm::cambiarConsulta, vm::cambiarCategoria)
    Text("${lugares.size} resultados · catálogo de demostración", style = MaterialTheme.typography.labelMedium)
    if (lugares.isEmpty()) Text("No hay resultados. Cambia el nombre o la categoría.")
    ListaLugares(lugares, seleccionado, lugares.firstOrNull { it.metros != null }?.lugar?.id, seleccionar)
}

@Composable
private fun MapaUbicacionActual(
    reproduccion: kotlinx.coroutines.flow.StateFlow<com.contreras.campusgps.viewmodel.Reproduccion>,
    recorrido: Recorrido?,
    posicion: com.contreras.campusgps.model.Posicion?,
    modifier: Modifier = Modifier,
    lugares: List<LugarConDistancia>,
    seleccionadoId: Int?,
    versionNavegacion: Int,
    onSeleccionarLugar: (Int) -> Unit
) {
    // =====================================================
    // 16. UBICACIÓN DEL USUARIO
    // =====================================================
    val ubicacionUsuario = LatLng(
        posicion?.latitud ?: -12.0460,
        posicion?.longitud ?: -77.0435
    )
    // =====================================================
    // 17. ESTADO DE LA CÁMARA
    // =====================================================
    val estadoCamara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            ubicacionUsuario,
            16f
        )
    }
    // =====================================================
    // 18. ESTADO DE CARGA DEL MAPA
    // =====================================================
    var mapaCargado by remember {
        mutableStateOf(false)
    }
    // =====================================================
    // 19. MARCADOR DE UBICACIÓN ACTUAL
    // =====================================================
    val marcadorUsuario = rememberUpdatedMarkerState(
        position = ubicacionUsuario
    )
    // =====================================================
    // 20. MARCADORES DEL CAMPUS
    // =====================================================
    val marcadoresCampus = remember(lugares) {
        lugares.map { elemento ->
            val lugar = elemento.lugar
            val posicion = LatLng(
                lugar.latitud,
                lugar.longitud
            )
            lugar to MarkerState(
                position = posicion
            )
        }
    }
    // =====================================================
    // 21. OBTENER LUGAR SELECCIONADO
    // =====================================================
    val lugarSeleccionado = lugares.firstOrNull { elemento ->
        elemento.lugar.id == seleccionadoId
    }?.lugar
    // =====================================================
    // 22. NAVEGAR HACIA EL LUGAR
    // =====================================================
    LaunchedEffect(
        seleccionadoId,
        mapaCargado,
        versionNavegacion,
        posicion,
        recorrido
    ) {
        if (
            mapaCargado &&
            lugarSeleccionado != null && recorrido == null
        ) {
            val destino = LatLng(
                lugarSeleccionado.latitud,
                lugarSeleccionado.longitud
            )
            // Desplazamiento animado de la cámara.
            estadoCamara.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    destino,
                    17f
                ),
                durationMs = 800
            )
        }
    }
    // =====================================================
    // 23. MOSTRAR GOOGLE MAPS
    // =====================================================
    LaunchedEffect(recorrido, mapaCargado) {
        if (mapaCargado && recorrido != null) {
            val limites = LatLngBounds.builder()
            recorrido.puntos.forEach { limites.include(LatLng(it.latitud, it.longitud)) }
            if (recorrido.puntos.distinct().size == 1) {
                estadoCamara.animate(CameraUpdateFactory.newLatLngZoom(ubicacionUsuario, 17f), 600)
            } else estadoCamara.animate(CameraUpdateFactory.newLatLngBounds(limites.build(), 80), 600)
        }
    }
    GoogleMap(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp)),
        cameraPositionState = estadoCamara,
        onMapLoaded = {
            mapaCargado = true
        }
    ) {
        // =================================================
        // 24. MARCADOR DEL USUARIO
        // =================================================
        recorrido?.let { ruta ->
            AutomovilMapa(ruta, reproduccion)
            Polyline(points = ruta.puntos.map { LatLng(it.latitud, it.longitud) },
                color = MaterialTheme.colorScheme.primary, width = 10f, geodesic = true)
        }
        if (posicion != null) Marker(
            state = marcadorUsuario,
            title = "Mi ubicación",
            snippet = "Ubicación actual del dispositivo"
        )
        // =================================================
        // 25. MARCADORES DEL CAMPUS
        // =================================================
        marcadoresCampus.forEach { (lugar, marcador) ->
            Marker(
                state = marcador,
                title = lugar.nombre,
                snippet = lugar.categoria,
                icon = remember(lugar.categoria, seleccionadoId == lugar.id) {
                    IconosMapa.categoria(lugar.categoria, seleccionadoId == lugar.id)
                },
                zIndex = if (seleccionadoId == lugar.id) 2f else 1f,
                onClick = {
                    onSeleccionarLugar(lugar.id)
                    false
                }
            )
        }
    }
}
