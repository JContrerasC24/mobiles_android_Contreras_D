package com.contreras.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contreras.centrotareas.ui.PantallaTareas
import com.contreras.centrotareas.ui.theme.CentroTareasTheme
import com.contreras.centrotareas.viewmodel.TareasViewModel
import com.contreras.centrotareas.viewmodel.TareasViewModelFactory

/**
 * Actividad principal de CentroTareas.
 *
 * Inicia Jetpack Compose y proporciona el ViewModel
 * a la pantalla principal.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CentroTareasTheme {
                val vm: TareasViewModel = viewModel(
                    factory = TareasViewModelFactory()
                )

                PantallaTareas(
                    vm = vm
                )
            }
        }
    }
}
