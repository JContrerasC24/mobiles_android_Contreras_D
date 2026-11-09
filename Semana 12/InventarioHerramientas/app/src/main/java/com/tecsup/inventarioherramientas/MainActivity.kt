package com.tecsup.inventarioherramientas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.inventarioherramientas.ui.login.LoginScreen
import com.tecsup.inventarioherramientas.ui.login.LoginViewModel

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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.safeDrawingPadding()
                    ) {
                        LoginScreen(
                            estado = estado,
                            onIngresar = loginViewModel::iniciarSesion,
                            onCerrarSesion = loginViewModel::cerrarSesion
                        )
                    }
                }
            }
        }
    }
}