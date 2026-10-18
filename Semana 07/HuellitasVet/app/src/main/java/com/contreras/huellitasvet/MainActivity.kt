package com.contreras.huellitasvet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.contreras.huellitasvet.ui.dueno.SplashScreen
import com.contreras.huellitasvet.ui.theme.HuellitasVetTheme
import com.contreras.huellitasvet.ui.veterinario.ServiciosScreen
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import com.contreras.huellitasvet.viewmodel.VetViewModelFactory
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as VetApp

        val serviciosViewModel = ViewModelProvider(
            this,
            VetViewModelFactory(app.repository)
        )[ServiciosViewModel::class.java]

        setContent {
            var mostrarSplash by rememberSaveable {
                mutableStateOf(true)
            }

            LaunchedEffect(mostrarSplash) {
                if (mostrarSplash) {
                    delay(1500)
                    mostrarSplash = false
                }
            }

            HuellitasVetTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val contenidoModifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)

                    if (mostrarSplash) {
                        SplashScreen(
                            modifier = contenidoModifier
                        )
                    } else {
                        ServiciosScreen(
                            vm = serviciosViewModel,
                            onEditar = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "El formulario se implementará en el paso 13",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = contenidoModifier
                        )
                    }
                }
            }
        }
    }
}