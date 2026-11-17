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

// Google Maps para Jetpack Compose.
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel


/**
 * Pantalla principal de CampusGPS.
 *
 * Responsabilidades:
 * - Solicitar permisos de ubicación.
 * - Obtener la ubicación mediante el ViewModel.
 * - Mostrar las coordenadas actuales.
 * - Visualizar Google Maps.
 * - Colocar un marcador en la ubicación del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // ESTADO DEL VIEWMODEL
    // =====================================================

    // Observa el estado de ubicación.
    val estado by vm.estado.collectAsState()

    // Contexto de Android.
    val contexto = LocalContext.current

    // Mensaje mostrado cuando se deniega un permiso.
    var mensajePermiso by remember {
        mutableStateOf<String?>(null)
    }


    // =====================================================
    // SOLICITUD DE PERMISOS
    // =====================================================

    /*
     * Solicita acceso a la ubicación del dispositivo.
     *
     * Android permite que el usuario elija entre
     * ubicación precisa y aproximada.
     */
    val solicitarPermisos = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestMultiplePermissions()

    ) { permisos ->

        val permisoPreciso =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val permisoAproximado =
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permisoPreciso || permisoAproximado) {

            // Si existe autorización, buscamos la ubicación.
            mensajePermiso = null

            vm.buscar()

        } else {

            // Mensaje mostrado cuando se rechaza la solicitud.
            mensajePermiso =
                "Permiso denegado. Autoriza la ubicación " +
                        "para poder utilizar CampusGPS."
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
            // BOTÓN PARA OBTENER LA UBICACIÓN
            // =================================================

            Button(

                onClick = {

                    /*
                     * Comprueba si Android ya concedió
                     * acceso a la ubicación precisa.
                     */
                    val tienePermisoPreciso =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_FINE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    /*
                     * Comprueba si Android concedió
                     * acceso a la ubicación aproximada.
                     */
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
                        mensajePermiso = null

                        vm.buscar()

                    } else {

                        // Solicitamos autorización al usuario.
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
                // UBICACIÓN OBTENIDA
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
                    // MAPA INTERACTIVO
                    // =========================================

                    /*
                     * Enviamos la latitud y longitud obtenidas
                     * por el ViewModel al mapa de Google.
                     */
                    MapaUbicacionActual(

                        latitud = actual.posicion.latitud,

                        longitud = actual.posicion.longitud
                    )


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // COORDENADAS
                    // =========================================

                    Text(

                        text =
                            "Latitud: ${actual.posicion.latitud}"
                    )

                    Text(

                        text =
                            "Longitud: ${actual.posicion.longitud}"
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
 * Muestra un mapa centrado en las coordenadas del usuario.
 *
 * @param latitud Coordenada geográfica de latitud.
 * @param longitud Coordenada geográfica de longitud.
 */
@Composable
private fun MapaUbicacionActual(
    latitud: Double,
    longitud: Double
) {

    // =====================================================
    // CONVERTIR COORDENADAS
    // =====================================================

    /*
     * Google Maps utiliza LatLng para representar
     * una posición geográfica.
     */
    val ubicacion = LatLng(
        latitud,
        longitud
    )


    // =====================================================
    // CONFIGURAR CÁMARA
    // =====================================================

    /*
     * Establece la posición inicial de la cámara.
     *
     * 16f representa el nivel de acercamiento
     * con el que se mostrará el mapa.
     */
    val estadoCamara = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(

            ubicacion,

            16f
        )
    }


    // =====================================================
    // CONFIGURAR MARCADOR
    // =====================================================

    /*
     * Representa la ubicación del usuario.
     *
     * El marcador se actualizará si cambian
     * las coordenadas recibidas.
     */
    val marcadorUsuario = rememberUpdatedMarkerState(
        position = ubicacion
    )


    // =====================================================
    // MOSTRAR GOOGLE MAPS
    // =====================================================

    GoogleMap(

        // Tamaño que ocupará el mapa en la pantalla.
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),

        // Centra el mapa en la ubicación configurada.
        cameraPositionState = estadoCamara

    ) {

        // =================================================
        // MARCADOR DE UBICACIÓN ACTUAL
        // =================================================

        Marker(

            state = marcadorUsuario,

            title = "Mi ubicación",

            snippet = "Ubicación actual del dispositivo"
        )
    }
}