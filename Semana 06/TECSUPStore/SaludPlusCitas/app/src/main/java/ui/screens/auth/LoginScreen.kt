package com.contreras.saludplus.citas.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Permite ingresar mediante credenciales registradas en memoria.
@Composable
fun LoginScreen(navController: NavHostController) {
    // Conserva el correo cuando la pantalla se recrea.
    var correo by rememberSaveable { mutableStateOf("") }

    // Mantiene la contraseña únicamente en memoria.
    var contrasena by remember { mutableStateOf("") }

    // Controla los errores y las pulsaciones posteriores al acceso.
    var error by remember { mutableStateOf<String?>(null) }
    var ingresoExitoso by remember { mutableStateOf(false) }

    // Permite desplazarse cuando aparece el teclado.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Regresa a la pantalla anterior.
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Ingresa con tu cuenta de SaludPlus.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Captura el correo con un teclado apropiado.
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                error = null
            },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Oculta visualmente la contraseña.
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                error = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Muestra el error cuando existe.
        error?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            enabled = !ingresoExitoso,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Comprueba los campos antes de validar las credenciales.
                when {
                    !Patterns.EMAIL_ADDRESS
                        .matcher(correo.trim())
                        .matches() -> {
                        error = "Ingresa un correo válido."
                    }

                    contrasena.isBlank() -> {
                        error = "Ingresa tu contraseña."
                    }

                    Repositorio.iniciarSesion(
                        correo = correo,
                        contrasena = contrasena
                    ) -> {
                        ingresoExitoso = true

                        // Elimina el flujo de acceso del historial.
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.SPLASH) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    else -> {
                        error = "Correo o contraseña incorrectos."
                    }
                }
            }
        ) {
            Text("Ingresar")
        }
    }
}