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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

// Componentes de Google Maps para Compose.
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

// Modelos y ViewModel.
import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel


/**
 * Pantalla principal de CampusGPS.
 *
 * Permite:
 * - Solicitar permisos de ubicación.
 * - Obtener la ubicación actual.
 * - Visualizar Google Maps.
 * - Mostrar el marcador del usuario.
 * - Mostrar los cinco lugares del campus.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // ESTADOS DEL VIEWMODEL
    // =====================================================

    // Observa el estado de la ubicación.
    val estado by vm.estado.collectAsState()

    /*
     * Obtiene los lugares registrados en el repositorio.
     *
     * Los datos llegan mediante el StateFlow
     * definido dentro de MapaViewModel.
     */
    val lugares by vm.lugares.collectAsState()

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
            // TÍTULO PRINCIPAL
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

                    // Comprueba el permiso de ubicación precisa.
                    val tienePermisoPreciso =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_FINE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    // Comprueba el permiso de ubicación aproximada.
                    val tienePermisoAproximado =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_COARSE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    if (
                        tienePermisoPreciso ||
                        tienePermisoAproximado
                    ) {

                        // Si hay permisos, solicita la ubicación.
                        mensajePermiso = null

                        vm.buscar()

                    } else {

                        // Si faltan permisos, solicita autorización.
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

                    Text(

                        text = "Ubicación obtenida correctamente",

                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // GOOGLE MAPS
                    // =========================================

                    /*
                     * Enviamos:
                     *
                     * 1. La ubicación del usuario.
                     * 2. Los lugares del campus.
                     *
                     * El mapa se encargará de dibujar
                     * los marcadores correspondientes.
                     */
                    MapaUbicacionActual(

                        latitud = actual.posicion.latitud,

                        longitud = actual.posicion.longitud,

                        lugares = lugares
                    )


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // COORDENADAS DEL USUARIO
                    // =========================================

                    Text(
                        text = "Latitud: ${actual.posicion.latitud}"
                    )

                    Text(
                        text = "Longitud: ${actual.posicion.longitud}"
                    )


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================================
                    // TOTAL DE LUGARES REGISTRADOS
                    // =========================================

                    Text(

                        text = "Lugares registrados: ${lugares.size}",

                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                // ---------------------------------------------
                // ERROR
                // ---------------------------------------------

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
// COMPONENTE DE GOOGLE MAPS
// =========================================================

/**
 * Muestra un mapa interactivo.
 *
 * Incluye:
 * - Marcador de ubicación actual.
 * - Marcadores de los lugares del campus.
 *
 * Los lugares se reciben desde el ViewModel.
 */
@Composable
private fun MapaUbicacionActual(

    latitud: Double,

    longitud: Double,

    lugares: List<LugarConDistancia>

) {

    // =====================================================
    // UBICACIÓN DEL USUARIO
    // =====================================================

    val ubicacionUsuario = LatLng(
        latitud,
        longitud
    )


    // =====================================================
    // CÁMARA DE GOOGLE MAPS
    // =====================================================

    /*
     * Coloca inicialmente la cámara sobre
     * la ubicación actual del dispositivo.
     */
    val estadoCamara = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(

            ubicacionUsuario,

            16f
        )
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

    /*
     * Convierte los lugares almacenados en el repositorio
     * en marcadores que Google Maps puede representar.
     *
     * remember evita recrearlos innecesariamente
     * mientras la lista de lugares no cambie.
     */
    val marcadoresCampus = remember(lugares) {

        lugares.map { elemento ->

            val lugar = elemento.lugar

            val posicionLugar = LatLng(

                lugar.latitud,

                lugar.longitud
            )

            // Relacionamos cada lugar con su marcador.
            lugar to MarkerState(
                position = posicionLugar
            )
        }
    }


    // =====================================================
    // MOSTRAR GOOGLE MAPS
    // =====================================================

    GoogleMap(

        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),

        cameraPositionState = estadoCamara

    ) {

        // =================================================
        // MARCADOR DEL USUARIO
        // =================================================

        Marker(

            state = marcadorUsuario,

            title = "Mi ubicación",

            snippet = "Ubicación actual del dispositivo"
        )


        // =================================================
        // MARCADORES DE LOS LUGARES DEL CAMPUS
        // =================================================

        /*
         * Recorremos los lugares obtenidos
         * desde LugaresRepositoryLocal.
         *
         * Por cada lugar creamos un marcador
         * dentro del mapa.
         */
        marcadoresCampus.forEach { (lugar, marcador) ->

            Marker(

                state = marcador,

                title = lugar.nombre,

                snippet = lugar.categoria
            )
        }
    }
}