package com.contreras.campusgps.ui

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

import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel


/**
 * Pantalla principal de CampusGPS.
 *
 * Integra:
 * - Permisos de ubicación.
 * - Google Maps.
 * - Marcadores del campus.
 * - Distancias calculadas.
 * - Lista interactiva de lugares.
 * - Selección de lugares en el mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // ESTADOS DEL VIEWMODEL
    // =====================================================

    val estado by vm.estado.collectAsState()

    val lugares by vm.lugares.collectAsState()

    // Identificador del lugar seleccionado.
    val seleccionado by vm.seleccionado.collectAsState()

    val contexto = LocalContext.current

    var mensajePermiso by remember {
        mutableStateOf<String?>(null)
    }


    // =====================================================
    // SOLICITUD DE PERMISOS
    // =====================================================

    val solicitarPermisos = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestMultiplePermissions()

    ) { permisos ->

        val permisoPreciso =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val permisoAproximado =
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permisoPreciso || permisoAproximado) {

            mensajePermiso = null

            vm.buscar()

        } else {

            mensajePermiso =
                "Permiso denegado. Autoriza la ubicación " +
                        "para utilizar CampusGPS."
        }
    }


    // =====================================================
    // INTERFAZ PRINCIPAL
    // =====================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("CampusGPS")
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
            // TÍTULO
            // =================================================

            Text(

                text = "Localización Geográfica",

                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // BOTÓN DE UBICACIÓN
            // =================================================

            Button(

                onClick = {

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

                        mensajePermiso = null

                        vm.buscar()

                    } else {

                        solicitarPermisos.launch(

                            arrayOf(

                                Manifest.permission.ACCESS_FINE_LOCATION,

                                Manifest.permission.ACCESS_COARSE_LOCATION

                            )
                        )
                    }
                },

                enabled = estado != UbicacionUi.Buscando

            ) {

                Text("Obtener mi ubicación")
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // MENSAJE DE PERMISOS
            // =================================================

            if (mensajePermiso != null) {

                Text(

                    text = mensajePermiso ?: "",

                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }


            // =================================================
            // ESTADOS DE UBICACIÓN
            // =================================================

            when (val actual = estado) {

                UbicacionUi.Inicial -> {

                    Text(
                        "Presiona el botón para obtener tu ubicación"
                    )
                }


                UbicacionUi.Buscando -> {

                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        "Buscando ubicación..."
                    )
                }


                is UbicacionUi.Lista -> {

                    Text(

                        text = "Ubicación obtenida correctamente",

                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // MAPA INTERACTIVO
                    // =========================================

                    MapaUbicacionActual(

                        latitud = actual.posicion.latitud,

                        longitud = actual.posicion.longitud,

                        lugares = lugares,

                        seleccionadoId = seleccionado,

                        onSeleccionarLugar = { id ->

                            /*
                             * Comunica al ViewModel
                             * qué lugar fue seleccionado.
                             */
                            vm.seleccionarLugar(id)
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // COORDENADAS
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
                    // LISTA DE LUGARES
                    // =========================================

                    /*
                     * Muestra los lugares ordenados
                     * por distancia desde el ViewModel.
                     */
                    ListaLugares(

                        lugares = lugares,

                        seleccionadoId = seleccionado,

                        onSeleccionar = { id ->

                            vm.seleccionarLugar(id)
                        }
                    )
                }


                is UbicacionUi.Error -> {

                    Text(

                        text = actual.mensaje,

                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


// =========================================================
// MAPA INTERACTIVO
// =========================================================

/**
 * Muestra Google Maps y permite seleccionar lugares.
 *
 * Cuando cambia seleccionadoId, la cámara se desplaza
 * hacia las coordenadas del lugar seleccionado.
 */
@Composable
private fun MapaUbicacionActual(

    latitud: Double,

    longitud: Double,

    lugares: List<LugarConDistancia>,

    seleccionadoId: Int?,

    onSeleccionarLugar: (Int) -> Unit

) {

    // =====================================================
    // UBICACIÓN DEL USUARIO
    // =====================================================

    val ubicacionUsuario = LatLng(
        latitud,
        longitud
    )


    // =====================================================
    // CÁMARA DEL MAPA
    // =====================================================

    val estadoCamara = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(

            ubicacionUsuario,

            16f
        )
    }


    // =====================================================
    // ESTADO DE CARGA DEL MAPA
    // =====================================================

    /*
     * Nos permite saber cuándo el mapa terminó de cargar.
     *
     * Solo moveremos la cámara después de ese momento.
     */
    var mapaCargado by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // MARCADOR DEL USUARIO
    // =====================================================

    val marcadorUsuario = rememberUpdatedMarkerState(

        position = ubicacionUsuario
    )


    // =====================================================
    // MARCADORES DEL CAMPUS
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
    // BUSCAR LUGAR SELECCIONADO
    // =====================================================

    /*
     * Busca las coordenadas correspondientes
     * al lugar seleccionado desde la lista.
     */
    val lugarSeleccionado = lugares.firstOrNull { elemento ->

        elemento.lugar.id == seleccionadoId

    }?.lugar


    // =====================================================
    // NAVEGAR HACIA EL LUGAR SELECCIONADO
    // =====================================================

    /*
     * LaunchedEffect observa los cambios de selección.
     *
     * Cuando el usuario selecciona un lugar:
     *
     * 1. Obtiene sus coordenadas.
     * 2. Espera que el mapa esté cargado.
     * 3. Desplaza la cámara hasta el marcador.
     */
    LaunchedEffect(
        seleccionadoId,
        mapaCargado
    ) {

        if (
            mapaCargado &&
            lugarSeleccionado != null
        ) {

            val destino = LatLng(

                lugarSeleccionado.latitud,

                lugarSeleccionado.longitud
            )

            /*
             * Desplaza suavemente la cámara
             * hasta el lugar seleccionado.
             */
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
    // GOOGLE MAPS
    // =====================================================

    GoogleMap(

        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),

        cameraPositionState = estadoCamara,

        /*
         * Android notificará cuando el mapa termine
         * de cargar y pueda recibir movimientos de cámara.
         */
        onMapLoaded = {

            mapaCargado = true
        }

    ) {

        // =================================================
        // MARCADOR DE UBICACIÓN ACTUAL
        // =================================================

        Marker(

            state = marcadorUsuario,

            title = "Mi ubicación",

            snippet = "Ubicación actual del dispositivo"
        )


        // =================================================
        // MARCADORES DE LOS LUGARES DEL CAMPUS
        // =================================================

        marcadoresCampus.forEach { (lugar, marcador) ->

            Marker(

                state = marcador,

                title = lugar.nombre,

                snippet = lugar.categoria,

                /*
                 * También permitimos seleccionar lugares
                 * directamente desde los marcadores.
                 */
                onClick = {

                    onSeleccionarLugar(lugar.id)

                    /*
                     * false permite conservar
                     * el comportamiento normal del marcador,
                     * incluida su ventana de información.
                     */
                    false
                }
            )
        }
    }
}