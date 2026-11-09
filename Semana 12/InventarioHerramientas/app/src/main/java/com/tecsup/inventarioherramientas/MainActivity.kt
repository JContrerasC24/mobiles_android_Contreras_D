package com.tecsup.inventarioherramientas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.inventarioherramientas.ui.UiState
import com.tecsup.inventarioherramientas.ui.login.LoginScreen
import com.tecsup.inventarioherramientas.ui.login.LoginViewModel
import com.tecsup.inventarioherramientas.ui.navigation.AppNav

class MainActivity : ComponentActivity() {


    private val contenedor by lazy {
        AppContainer(applicationContext)
    }

    private val loginFactory by lazy {
        LoginViewModel.Factory(contenedor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = loginFactory
                )

                val estado by loginViewModel.estado
                    .collectAsStateWithLifecycle()

                val sesion = (estado as? UiState.Exito)?.datos

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                        if (sesion == null) {
                            LoginScreen(
                                estado = estado,
                                onIngresar =
                                    loginViewModel::iniciarSesion,
                                onCerrarSesion =
                                    loginViewModel::cerrarSesion
                            )
                        } else {
                            key(sesion.uid) {
                                AppNav(
                                    contenedor = contenedor,
                                    sesion = sesion,
                                    onCerrarSesion =
                                        loginViewModel::cerrarSesion
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}