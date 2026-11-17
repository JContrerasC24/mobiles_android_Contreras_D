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

// Google Maps para Jetpack Compose.
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
 * Funcionalidades:
 *
 * 1. Solicitar permisos de ubicación.
 * 2. Obtener las coordenadas del usuario.
 * 3. Mostrar Google Maps.
 * 4. Representar los lugares del campus.
 * 5. Mostrar las distancias calculadas.
 * 6. Seleccionar lugares desde una lista.
 * 7. Navegar hacia los marcadores seleccionados.
 * 8. Identificar y resaltar el lugar más cercano.
 *
 * Utiliza la arquitectura MVVM.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // 1. ESTADOS DEL VIEWMODEL
    // =====================================================

    /*
     * Observa el estado de la ubicación.
     *
     * Puede ser:
     * Inicial, Buscando, Lista o Error.
     */
    val estado by vm.estado.collectAsState()

    /*
     * Obtiene los lugares del campus.
     *
     * Los lugares ya están ordenados por distancia
     * después de obtener la ubicación.
     */
    val lugares by vm.lugares.collectAsState()

    /*
     * Observa el identificador del lugar
     * seleccionado por el usuario.
     */
    val seleccionado by vm.seleccionado.collectAsState()

    // Obtiene el contexto de Android.
    val contexto = LocalContext.current

    // Mensaje utilizado cuando se deniegan los permisos.
    var mensajePermiso by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Permite volver a centrar el mapa incluso
     * si el usuario selecciona nuevamente
     * un lugar que ya estaba seleccionado.
     */
    var versionNavegacion by remember {
        mutableStateOf(0)
    }


    // =====================================================
    // 2. SOLICITAR PERMISOS DE UBICACIÓN
    // =====================================================

    /*
     * Permite solicitar simultáneamente
     * los permisos de ubicación precisa
     * y ubicación aproximada.
     */
    val solicitarPermisos = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestMultiplePermissions()

    ) { permisos ->

        // Comprueba si se autorizó la ubicación precisa.
        val permisoPreciso =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

        // Comprueba si se autorizó la ubicación aproximada.
        val permisoAproximado =
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permisoPreciso || permisoAproximado) {

            // Elimina los mensajes anteriores.
            mensajePermiso = null

            // Solicita la ubicación al ViewModel.
            vm.buscar()

        } else {

            // Informa que el usuario rechazó los permisos.
            mensajePermiso =
                "Permiso denegado. Autoriza la ubicación " +
                        "para utilizar CampusGPS."
        }
    }


    // =====================================================
    // 3. INTERFAZ PRINCIPAL
    // =====================================================

    Scaffold(

        // Barra superior de CampusGPS.
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
            // 4. TÍTULO PRINCIPAL
            // =================================================

            Text(

                text = "Localización Geográfica",

                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // 5. BOTÓN PARA OBTENER LA UBICACIÓN
            // =================================================

            Button(

                onClick = {

                    /*
                     * Comprueba si la aplicación
                     * tiene permiso de ubicación precisa.
                     */
                    val tienePermisoPreciso =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_FINE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    /*
                     * Comprueba si la aplicación
                     * tiene permiso de ubicación aproximada.
                     */
                    val tienePermisoAproximado =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_COARSE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    // =========================================
                    // COMPROBACIÓN DE PERMISOS
                    // =========================================

                    if (
                        tienePermisoPreciso ||
                        tienePermisoAproximado
                    ) {

                        /*
                         * Ya tenemos autorización.
                         *
                         * Solicitamos la ubicación
                         * directamente al ViewModel.
                         */
                        mensajePermiso = null

                        vm.buscar()

                    } else {

                        /*
                         * Si no tenemos permisos,
                         * Android mostrará la solicitud.
                         */
                        solicitarPermisos.launch(

                            arrayOf(

                                Manifest.permission.ACCESS_FINE_LOCATION,

                                Manifest.permission.ACCESS_COARSE_LOCATION

                            )
                        )
                    }
                },

                // Evita búsquedas repetidas.
                enabled = estado != UbicacionUi.Buscando

            ) {

                Text("Obtener mi ubicación")
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // 6. MENSAJE DE PERMISOS
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
            // 7. ESTADOS DEL VIEWMODEL
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

                    // =========================================
                    // 8. IDENTIFICAR EL LUGAR MÁS CERCANO
                    // =========================================

                    /*
                     * La lista ya está ordenada por distancia
                     * desde MapaViewModel.
                     *
                     * Por lo tanto, el primer lugar
                     * con una distancia calculada
                     * será el más cercano.
                     */
                    val masCercano = lugares.firstOrNull { elemento ->

                        elemento.metros != null
                    }


                    // =========================================
                    // 9. MENSAJE DE UBICACIÓN
                    // =========================================

                    Text(

                        text = "Ubicación obtenida correctamente",

                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================================
                    // 10. MOSTRAR LUGAR MÁS CERCANO
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
                        // BOTÓN PARA IR AL MÁS CERCANO
                        // =====================================

                        Button(

                            onClick = {

                                /*
                                 * Solicita al ViewModel
                                 * seleccionar el lugar
                                 * más cercano.
                                 */
                                vm.seleccionarMasCercano()

                                /*
                                 * Fuerza una nueva navegación
                                 * incluso si ese lugar ya
                                 * estaba seleccionado.
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

                    /*
                     * Enviamos al mapa:
                     *
                     * - La ubicación del usuario.
                     * - Los lugares del campus.
                     * - El lugar seleccionado.
                     * - La versión de navegación.
                     *
                     * Cuando cambia la selección,
                     * la cámara se desplaza hacia el lugar.
                     */
                    MapaUbicacionActual(

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
                    // 13. LISTA INTERACTIVA DE LUGARES
                    // =========================================

                    /*
                     * Muestra los cinco lugares
                     * ordenados por distancia.
                     *
                     * La tarjeta del lugar más cercano
                     * aparecerá resaltada.
                     */
                    ListaLugares(

                        lugares = lugares,

                        seleccionadoId = seleccionado,

                        // Identificador del lugar más cercano.
                        masCercanoId = masCercano?.lugar?.id,

                        onSeleccionar = { id ->

                            /*
                             * Selecciona el lugar
                             * y permite que el mapa
                             * se desplace hasta él.
                             */
                            vm.seleccionarLugar(id)

                            versionNavegacion++
                        }
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
// 14. COMPONENTE DE GOOGLE MAPS
// =========================================================

/**
 * Muestra Google Maps y los marcadores del campus.
 *
 * También permite desplazar la cámara
 * hacia el lugar seleccionado.
 */
@Composable
private fun MapaUbicacionActual(

    latitud: Double,

    longitud: Double,

    lugares: List<LugarConDistancia>,

    seleccionadoId: Int?,

    versionNavegacion: Int,

    onSeleccionarLugar: (Int) -> Unit

) {

    // =====================================================
    // 15. UBICACIÓN ACTUAL DEL USUARIO
    // =====================================================

    /*
     * LatLng representa una posición
     * mediante latitud y longitud.
     */
    val ubicacionUsuario = LatLng(

        latitud,

        longitud
    )


    // =====================================================
    // 16. CONFIGURACIÓN DE LA CÁMARA
    // =====================================================

    /*
     * Inicialmente centramos el mapa
     * sobre la ubicación del dispositivo.
     */
    val estadoCamara = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(

            ubicacionUsuario,

            16f
        )
    }


    // =====================================================
    // 17. ESTADO DE CARGA DEL MAPA
    // =====================================================

    /*
     * Nos permite saber cuándo Google Maps
     * terminó de cargar correctamente.
     */
    var mapaCargado by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // 18. MARCADOR DEL USUARIO
    // =====================================================

    /*
     * Representa las coordenadas actuales
     * mediante un marcador de Google Maps.
     */
    val marcadorUsuario = rememberUpdatedMarkerState(

        position = ubicacionUsuario
    )


    // =====================================================
    // 19. MARCADORES DE LOS LUGARES DEL CAMPUS
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
    // 20. OBTENER EL LUGAR SELECCIONADO
    // =====================================================

    /*
     * Busca en la lista el lugar cuyo identificador
     * coincida con el que seleccionó el usuario.
     */
    val lugarSeleccionado = lugares.firstOrNull { elemento ->

        elemento.lugar.id == seleccionadoId

    }?.lugar


    // =====================================================
    // 21. NAVEGAR HACIA EL LUGAR SELECCIONADO
    // =====================================================

    /*
     * LaunchedEffect se ejecuta cuando cambia:
     *
     * - El lugar seleccionado.
     * - El estado de carga del mapa.
     * - La versión de navegación.
     *
     * Esto permite desplazar la cámara
     * incluso si se vuelve a pulsar el mismo lugar.
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

            // Coordenadas del lugar seleccionado.
            val destino = LatLng(

                lugarSeleccionado.latitud,

                lugarSeleccionado.longitud
            )


            /*
             * Desplaza suavemente la cámara
             * hacia el lugar seleccionado.
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
    // 22. MOSTRAR GOOGLE MAPS
    // =====================================================

    GoogleMap(

        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),

        cameraPositionState = estadoCamara,

        // Confirma que el mapa terminó de cargar.
        onMapLoaded = {

            mapaCargado = true
        }

    ) {

        // =================================================
        // 23. MARCADOR DE UBICACIÓN ACTUAL
        // =================================================

        Marker(

            state = marcadorUsuario,

            title = "Mi ubicación",

            snippet = "Ubicación actual del dispositivo"
        )


        // =================================================
        // 24. MARCADORES DEL CAMPUS
        // =================================================

        /*
         * Dibuja un marcador por cada lugar.
         *
         * También permite seleccionar lugares
         * directamente desde Google Maps.
         */
        marcadoresCampus.forEach { (lugar, marcador) ->

            Marker(

                state = marcador,

                title = lugar.nombre,

                snippet = lugar.categoria,

                onClick = {

                    /*
                     * Notifica la selección al ViewModel
                     * mediante el callback recibido.
                     */
                    onSeleccionarLugar(lugar.id)

                    /*
                     * Permite que Google Maps siga mostrando
                     * la ventana de información del marcador.
                     */
                    false
                }
            )
        }
    }
}