
package com.contreras.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta los datos personales y permite cerrar la sesión.
@Composable
fun PerfilScreen(navController: NavHostController) {
    val usuario = Repositorio.usuarioActual
    val cantidadCitas = Repositorio.citasDelUsuario().size

    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.PERFIL
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Mi perfil",
                style = MaterialTheme.typography.headlineMedium
            )

            if (usuario == null) {
                Text("No existe una sesión activa.")
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DatoPerfil("Nombres", usuario.nombres)
                        DatoPerfil("Correo", usuario.correo)
                        DatoPerfil("Teléfono", usuario.telefono)
                    }
                }

                Text(
                    text = "Citas registradas: $cantidadCitas",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(Rutas.MIS_CITAS) {
                            popUpTo(Rutas.INICIO) {
                                inclusive = false
                            }

                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Ver mis citas")
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Repositorio.cerrarSesion()

                    // Borra el historial para impedir regresar a la sesión.
                    navController.navigate(Rutas.SPLASH) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

// Presenta una etiqueta y su dato correspondiente.
@Composable
private fun DatoPerfil(
    etiqueta: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}