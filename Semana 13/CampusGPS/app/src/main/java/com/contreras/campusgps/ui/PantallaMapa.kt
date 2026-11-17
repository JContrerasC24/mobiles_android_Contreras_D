package com.contreras.campusgps.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.contreras.campusgps.model.UbicacionUi
import com.contreras.campusgps.viewmodel.MapaViewModel

/**
 * Pantalla principal de CampusGPS.
 *
 * Observa los estados del ViewModel y muestra
 * la información correspondiente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    // Observa los cambios del estado de ubicación.
    val estado by vm.estado.collectAsState()

    Scaffold(

        // Barra superior de la aplicación.
        topBar = {
            TopAppBar(
                title = {
                    Text("CampusGPS")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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

            Text(
                text = "Localización Geográfica",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Inicia la búsqueda mediante el ViewModel.
            Button(
                onClick = {
                    vm.buscar()
                },

                // Evita realizar solicitudes repetidas mientras busca.
                enabled = estado != UbicacionUi.Buscando
            ) {
                Text("Obtener mi ubicación")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // La interfaz cambia según el estado recibido.
            when (val actual = estado) {

                UbicacionUi.Inicial -> {
                    Text("Presiona el botón para obtener tu ubicación")
                }

                UbicacionUi.Buscando -> {
                    CircularProgressIndicator()
                    Text("Buscando ubicación...")
                }

                is UbicacionUi.Lista -> {

                    Text(
                        text = "Latitud: ${actual.posicion.latitud}"
                    )

                    Text(
                        text = "Longitud: ${actual.posicion.longitud}"
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