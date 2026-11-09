package com.tecsup.inventarioherramientas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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

                var mostrarSolicitud by rememberSaveable {
                    mutableStateOf(true)
                }

                val solicitarPermiso =
                    rememberLauncherForActivityResult(
                        contract =
                            ActivityResultContracts.RequestPermission()
                    ) {
                        mostrarSolicitud = false
                    }

                val necesitaPermiso =
                    Build.VERSION.SDK_INT >=
                            Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                        if (
                            sesion != null &&
                            necesitaPermiso &&
                            mostrarSolicitud
                        ) {
                            TextButton(
                                onClick = {
                                    solicitarPermiso.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    "Permitir avisos de sincronización"
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
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
}