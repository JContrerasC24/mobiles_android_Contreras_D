package com.contreras.campusgps

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.lifecycle.viewmodel.compose.viewModel

import com.contreras.campusgps.ui.PantallaMapa
import com.contreras.campusgps.ui.theme.CampusGPSTheme
import com.contreras.campusgps.viewmodel.CampusViewModelFactory
import com.contreras.campusgps.viewmodel.MapaViewModel

/**
 * Actividad principal de CampusGPS.
 *
 * Inicia la interfaz de Jetpack Compose
 * y proporciona el ViewModel a la pantalla.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            CampusGPSTheme {

                // Crea u obtiene el ViewModel mediante su Factory.
                val vm: MapaViewModel = viewModel(
                    factory = CampusViewModelFactory(
                        applicationContext
                    )
                )

                // Muestra la pantalla principal.
                PantallaMapa(vm = vm)
            }
        }
    }
}