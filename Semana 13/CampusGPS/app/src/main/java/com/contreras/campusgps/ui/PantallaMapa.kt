package com.contreras.campusgps.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Surface
import androidx.compose.material3.ButtonDefaults
import androidx.compose.animation.animateContentSize
import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.core.content.ContextCompat

// Google Maps.
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

// Google Maps Compose.
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

// Modelos y ViewModel.
import com.contreras.campusgps.model.Recorrido
import com.google.maps.android.compose.Polyline
import com.google.android.gms.maps.model.LatLngBounds
import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel


/**
 * Pantalla principal de CampusGPS.
 *
 * Integra:
 *
 * 1. Solicitud de permisos.
 * 2. Ubicación actual del dispositivo.
 * 3. Google Maps.
 * 4. Marcadores del campus.
 * 5. Distancias calculadas.
 * 6. Lista interactiva de lugares.
 * 7. Selección y movimiento de cámara.
 * 8. Identificación del lugar más cercano.
 * 9. Manejo de errores.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // 1. ESTADOS DEL VIEWMODEL
    // =====================================================

    // Estado actual de la ubicación.
    androidx.compose.runtime.DisposableEffect(vm) {
        onDispose { vm.cancelarAnimacion() }
    }
    val recorrido by vm.recorrido.collectAsState()
    val estado by vm.estado.collectAsState()

    // Lugares del campus ordenados por distancia.
    val lugares by vm.resultados.collectAsState()
    val consulta by vm.consulta.collectAsState()
    val categoria by vm.categoria.collectAsState()

    // Identificador del lugar seleccionado.
    val seleccionado by vm.seleccionado.collectAsState()

    // Contexto de Android.
    val contexto = LocalContext.current

    /*
     * Permite repetir el movimiento de cámara
     * aunque se seleccione nuevamente el mismo lugar.
     */
    var versionNavegacion by remember {
        mutableStateOf(0)
    }


    // =====================================================
    // 2. SOLICITUD DE PERMISOS
    // =====================================================

    /*
     * Solicita los permisos de ubicación.
     *
     * Android mostrará automáticamente
     * el diálogo correspondiente.
     */
    val solicitarPermisos = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestMultiplePermissions()

    ) { permisos ->

        // Comprueba el permiso de ubicación precisa.
        val permisoPreciso =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

        // Comprueba el permiso de ubicación aproximada.
        val permisoAproximado =
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true


        if (permisoPreciso || permisoAproximado) {

            // El usuario autorizó la ubicación.
            vm.buscar()

        } else {

            /*
             * El usuario rechazó los permisos.
             *
             * El ViewModel mostrará el estado
             * de permiso denegado.
             */
            vm.permisoDenegado()
        }
    }


    // =====================================================
    // 3. COMPROBAR PERMISOS Y BUSCAR
    // =====================================================

    /*
     * Centralizamos la comprobación de permisos.
     *
     * Esta función será utilizada:
     *
     * - Por el botón Obtener mi ubicación.
     * - Por el botón Reintentar de MensajeError.
     *
     * Así evitamos repetir la misma lógica.
     */
    val buscarConPermisos: () -> Unit = {

        val tienePermisoPreciso =
            ContextCompat.checkSelfPermission(

                contexto,

                Manifest.permission.ACCESS_FINE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED


        val tienePermisoAproximado =
            ContextCompat.checkSelfPermission(

                contexto,

                Manifest.permission.ACCESS_COARSE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED


        if (
            tienePermisoPreciso ||
            tienePermisoAproximado
        ) {

            // Ya tenemos autorización.
            vm.buscar()

        } else {

            // Solicitamos permisos.
            solicitarPermisos.launch(

                arrayOf(

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_COARSE_LOCATION

                )
            )
        }
    }


    // =====================================================
    // 4. INTERFAZ PRINCIPAL
    // =====================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Column {
 Text("CampusGPS", style = MaterialTheme.typography.titleLarge)
 Text("Explora tu campus", style = MaterialTheme.typography.labelMedium)
 }
                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor =
                        MaterialTheme.colorScheme.primary,

                    titleContentColor =
                        MaterialTheme.colorScheme.onPrimary
                )
            )
        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            verticalArrangement = Arrangement.Top,

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            // =================================================
            // 5. TÍTULO DE LA PANTALLA
            // =================================================

            Text(

                text = "Localización Geográfica",

                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // 6. BOTÓN DE UBICACIÓN
            // =================================================

            Button(

                onClick = buscarConPermisos,

                // Evita solicitudes repetidas.
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                enabled = estado != UbicacionUi.Buscando

            ) {

                Text("Obtener mi ubicación")
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // 7. ESTADOS DEL VIEWMODEL
            // =================================================

            BuscadorLugares(consulta, categoria, vm.categorias, vm::cambiarConsulta, vm::cambiarCategoria)
            if (lugares.isEmpty()) {
                Text("No hay resultados. Cambia el nombre o la categoría.")
            }

            when (val actual = estado) {


                // ---------------------------------------------
                // ESTADO INICIAL
                // ---------------------------------------------

                UbicacionUi.Inicial -> {

                    Text(
                        "Presiona el botón para obtener tu ubicación"
                    )
                }


                // ---------------------------------------------
                // BUSCANDO UBICACIÓN
                // ---------------------------------------------

                UbicacionUi.Buscando -> {

                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        "Buscando ubicación..."
                    )
                }


                // ---------------------------------------------
                // UBICACIÓN ENCONTRADA
                // ---------------------------------------------

                is UbicacionUi.Lista -> {


                    // =========================================
                    // 8. IDENTIFICAR LUGAR MÁS CERCANO
                    // =========================================

                    /*
                     * La lista ya llega ordenada
                     * desde MapaViewModel.
                     */
                    val masCercano = lugares.firstOrNull { elemento ->

                        elemento.metros != null
                    }


                    // =========================================
                    // 9. MENSAJE DE UBICACIÓN CORRECTA
                    // =========================================

                    Text(

                        text = "Ubicación obtenida correctamente",

                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // 10. LUGAR MÁS CERCANO
                    // =========================================

                    MapaUbicacionActual(

                        reproduccion = vm.reproduccion,
                        recorrido = recorrido,
                        latitud = actual.posicion.latitud,

                        longitud = actual.posicion.longitud,

                        lugares = lugares,

                        seleccionadoId = seleccionado,

                        versionNavegacion = versionNavegacion,

                        onSeleccionarLugar = { id ->

                            vm.seleccionarLugar(id)

                            versionNavegacion++
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // 12. COORDENADAS DEL USUARIO
                    // =========================================

                    Text(

                        text = "Latitud: ${actual.posicion.latitud}"
                    )

                    Text(

                        text = "Longitud: ${actual.posicion.longitud}"
                    )


                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )


                    // =========================================
                    // 13. LISTA DE LUGARES
                    // =========================================

                    if (masCercano != null) {

                        Text(

                            text = "Más cercano: ${masCercano.lugar.nombre}",

                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        // =====================================
                        // BOTÓN IR AL LUGAR MÁS CERCANO
                        // =====================================

                        Button(

                            onClick = {

                                /*
                                 * Selecciona automáticamente
                                 * el lugar más cercano.
                                 */
                                vm.seleccionarMasCercano()

                                /*
                                 * Permite volver a mover la cámara
                                 * incluso si ese lugar ya estaba
                                 * seleccionado.
                                 */
                                versionNavegacion++
                            }

                        ) {

                            Text("Ir al lugar más cercano")
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }


                    // =========================================
                    // 11. GOOGLE MAPS
                    // =========================================

                    DetalleLugar(lugares.firstOrNull { it.lugar.id == seleccionado })
                    if (seleccionado != null && recorrido == null) {
                        Button(onClick = vm::verRecorrido) { Text("Ver recorrido") }
                    }
                    recorrido?.let { ruta ->
                        Text("Demostración en línea recta · no es una ruta por calles",
                            style = MaterialTheme.typography.bodyMedium)
                        Text("Origen: mi ubicación · Destino: ${ruta.destino.nombre}")
                        Text("Recorrido listo · ${ruta.metros.toInt()} m geográficos")
                        PanelReproduccion(vm)
                        Button(onClick = vm::cerrarRecorrido) { Text("Cerrar recorrido") }
                    }

                    ListaLugares(

                        lugares = lugares,

                        seleccionadoId = seleccionado,

                        masCercanoId = masCercano?.lugar?.id,

                        onSeleccionar = { id ->

                            vm.seleccionarLugar(id)

                            versionNavegacion++
                        }
                    )
                }


                // ---------------------------------------------
                // ERROR DE UBICACIÓN
                // ---------------------------------------------

                is UbicacionUi.Error -> {

                    // =========================================
                    // 14. MOSTRAR MENSAJE DE ERROR
                    // =========================================

                    /*
                     * Utilizamos el componente MensajeError
                     * para mostrar el problema y permitir
                     * que el usuario pueda solucionarlo.
                     */
                    AnimatedContent(targetState = actual, label = "mensajeError") { error ->
                    MensajeError(

                        mensaje = error.mensaje,

                        tipo = error.tipo,

                        onReintentar = buscarConPermisos
                    )
                    }
                }
            }
        }
    }
}


// =========================================================
// 15. COMPONENTE DE GOOGLE MAPS
// =========================================================

/**
 * Muestra Google Maps y los lugares del campus.
 *
 * Permite:
 * - Visualizar la ubicación actual.
 * - Mostrar los cinco marcadores.
 * - Seleccionar lugares.
 * - Desplazar la cámara hasta el lugar elegido.
 */
@Composable
private fun MapaUbicacionActual(
    reproduccion: kotlinx.coroutines.flow.StateFlow<com.contreras.campusgps.viewmodel.Reproduccion>,
    recorrido: Recorrido?,

    latitud: Double,

    longitud: Double,

    lugares: List<LugarConDistancia>,

    seleccionadoId: Int?,

    versionNavegacion: Int,

    onSeleccionarLugar: (Int) -> Unit

) {

    // =====================================================
    // 16. UBICACIÓN DEL USUARIO
    // =====================================================

    /*
     * Convierte las coordenadas actuales
     * al formato utilizado por Google Maps.
     */
    val ubicacionUsuario = LatLng(

        latitud,

        longitud
    )


    // =====================================================
    // 17. ESTADO DE LA CÁMARA
    // =====================================================

    /*
     * Inicialmente centra la cámara
     * sobre la ubicación del dispositivo.
     */
    val estadoCamara = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(

            ubicacionUsuario,

            16f
        )
    }


    // =====================================================
    // 18. ESTADO DE CARGA DEL MAPA
    // =====================================================

    /*
     * Nos permite saber cuándo el mapa
     * terminó de cargar correctamente.
     */
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

    /*
     * Convierte los lugares del repositorio
     * en marcadores de Google Maps.
     */
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

    /*
     * Busca el lugar cuyo identificador
     * coincide con la selección actual.
     */
    val lugarSeleccionado = lugares.firstOrNull { elemento ->

        elemento.lugar.id == seleccionadoId

    }?.lugar


    // =====================================================
    // 22. NAVEGAR HACIA EL LUGAR
    // =====================================================

    /*
     * Observa los cambios de selección.
     *
     * Cuando el usuario selecciona un lugar,
     * la cámara se desplaza automáticamente.
     */
    LaunchedEffect(

        seleccionadoId,

        mapaCargado,

        versionNavegacion

    ) {

        if (
            mapaCargado &&
            lugarSeleccionado != null
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
            estadoCamara.animate(CameraUpdateFactory.newLatLngBounds(limites.build(), 80), 600)
        }
    }

    GoogleMap(

        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
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
        Marker(
            state = marcadorUsuario,

            title = "Mi ubicación",

            snippet = "Ubicación actual del dispositivo"
        )


        // =================================================
        // 25. MARCADORES DEL CAMPUS
        // =================================================

        /*
         * Recorre los lugares registrados
         * y crea un marcador para cada uno.
         */
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

                    /*
                     * Comunica la selección
                     * a la pantalla principal.
                     */
                    onSeleccionarLugar(lugar.id)

                    /*
                     * Permite mostrar la ventana
                     * de información del marcador.
                     */
                    false
                }
            )
        }
    }
}
