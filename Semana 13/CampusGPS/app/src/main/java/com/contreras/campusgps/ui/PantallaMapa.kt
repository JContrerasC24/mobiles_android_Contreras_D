package com.contreras.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

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

import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel


/**
 * Pantalla principal de CampusGPS.
 *
 * Funciones:
 * 1. Mostrar la interfaz principal.
 * 2. Solicitar permisos de ubicación.
 * 3. Consultar la ubicación mediante el ViewModel.
 * 4. Mostrar los resultados y errores.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // =====================================================
    // ESTADO DEL VIEWMODEL
    // =====================================================

    // Observa los cambios de ubicación.
    val estado by vm.estado.collectAsState()

    // Obtiene el contexto actual de Android.
    val contexto = LocalContext.current

    // Almacena un mensaje cuando el usuario rechaza el permiso.
    var mensajePermiso by remember {
        mutableStateOf<String?>(null)
    }


    // =====================================================
    // SOLICITUD DE PERMISOS
    // =====================================================

    /*
     * Este componente permite solicitar permisos
     * y recibir la respuesta del usuario.
     *
     * Android mostrará automáticamente el diálogo
     * correspondiente.
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

        /*
         * Basta con que el usuario autorice uno de los permisos
         * para intentar obtener su ubicación.
         */
        if (permisoPreciso || permisoAproximado) {

            // Elimina cualquier mensaje anterior.
            mensajePermiso = null

            // Solicita la ubicación mediante el ViewModel.
            vm.buscar()

        } else {

            // Informa al usuario si rechazó los permisos.
            mensajePermiso =
                "Permiso de ubicación denegado. " +
                        "Debes autorizar el acceso para utilizar esta función."
        }
    }


    // =====================================================
    // INTERFAZ PRINCIPAL
    // =====================================================

    Scaffold(

        // Barra superior.
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
                .padding(16.dp),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            // =================================================
            // TÍTULO DE LA PANTALLA
            // =================================================

            Text(

                text = "Localización Geográfica",

                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // BOTÓN PARA OBTENER LA UBICACIÓN
            // =================================================

            Button(

                onClick = {

                    /*
                     * Primero comprobamos si la aplicación
                     * ya tiene acceso a la ubicación precisa.
                     */
                    val tienePermisoPreciso =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_FINE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    /*
                     * También comprobamos el permiso
                     * de ubicación aproximada.
                     */
                    val tienePermisoAproximado =
                        ContextCompat.checkSelfPermission(

                            contexto,

                            Manifest.permission.ACCESS_COARSE_LOCATION

                        ) == PackageManager.PERMISSION_GRANTED


                    // =========================================
                    // DECISIÓN SEGÚN LOS PERMISOS
                    // =========================================

                    if (
                        tienePermisoPreciso ||
                        tienePermisoAproximado
                    ) {

                        /*
                         * Si ya existen permisos,
                         * buscamos directamente la ubicación.
                         */
                        mensajePermiso = null

                        vm.buscar()

                    } else {

                        /*
                         * Si todavía no existen permisos,
                         * solicitamos ambos al usuario.
                         */
                        solicitarPermisos.launch(

                            arrayOf(

                                Manifest.permission.ACCESS_FINE_LOCATION,

                                Manifest.permission.ACCESS_COARSE_LOCATION

                            )
                        )
                    }
                },

                // Evita solicitudes repetidas durante la búsqueda.
                enabled = estado != UbicacionUi.Buscando

            ) {

                Text("Obtener mi ubicación")
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // MENSAJE SI SE RECHAZA EL PERMISO
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
            // ESTADOS DEL VIEWMODEL
            // =================================================

            /*
             * La interfaz se actualiza automáticamente
             * según el estado de la búsqueda.
             */
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
                        text = "Ubicación obtenida correctamente"
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Latitud: ${actual.posicion.latitud}"
                    )

                    Text(
                        text = "Longitud: ${actual.posicion.longitud}"
                    )
                }


                // ---------------------------------------------
                // ERROR DE UBICACIÓN
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