package com.contreras.huellitasvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Rol
import com.contreras.huellitasvet.ui.navigation.NavVet
import com.contreras.huellitasvet.ui.theme.VetTheme
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import com.contreras.huellitasvet.viewmodel.SesionViewModel
import com.contreras.huellitasvet.viewmodel.VetViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as VetApp
        val provider = ViewModelProvider(
            this,
            VetViewModelFactory(app.repository, app.preferencias, app.inicializacion)
        )
        val sesion = provider[SesionViewModel::class.java]
        val servicios = provider[ServiciosViewModel::class.java]

        setContent {
            val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
            val seleccionado by sesion.rolSeleccionado.collectAsStateWithLifecycle()
            val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
            val rol = usuario?.rol?.let { nombre ->
                Rol.entries.firstOrNull { it.name == nombre }
            } ?: seleccionado

            VetTheme(rol = rol, darkTheme = oscuro) {
                Scaffold(Modifier.fillMaxSize()) { padding ->
                    NavVet(
                        sesion = sesion,
                        servicios = servicios,
                        inicioFactory = { dueno ->
                            VetViewModelFactory(app.repository, app.preferencias, app.inicializacion, dueno)
                        },
                        modifier = Modifier.fillMaxSize().padding(padding)
                    )
                }
            }
        }
    }
}
