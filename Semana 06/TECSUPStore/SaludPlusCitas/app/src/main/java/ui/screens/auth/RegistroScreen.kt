package com.contreras.saludplus.citas.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Registra pacientes y valida sus datos antes de guardarlos.
@Composable
fun RegistroScreen(navController: NavHostController) {
    // Conserva los datos generales cuando la pantalla se recrea.
    var nombres by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    // Mantiene las contraseñas únicamente en memoria.
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }

    // Controla los errores y evita repetir registros exitosos.
    var error by remember { mutableStateOf<String?>(null) }
    var registrado by remember { mutableStateOf(false) }

    // Permite acceder a todos los campos con el teclado abierto.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Regresa a la pantalla anterior.
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Completa tus datos para agendar citas.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Captura los nombres del paciente.
        OutlinedTextField(
            value = nombres,
            onValueChange = {
                nombres = it
                error = null
            },
            label = { Text("Nombres y apellidos") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Captura el correo utilizado para iniciar sesión.
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

        // Conserva el teléfono como texto.
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                error = null
            },
            label = { Text("Teléfono") },
            supportingText = { Text("Ingresa 9 dígitos") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Oculta la contraseña mientras se escribe.
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                error = null
            },
            label = { Text("Contraseña") },
            supportingText = { Text("Mínimo 6 caracteres") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Solicita repetir la contraseña.
        OutlinedTextField(
            value = confirmacion,
            onValueChange = {
                confirmacion = it
                error = null
            },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Solicita aceptar las condiciones de uso.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = {
                    aceptaTerminos = it
                    error = null
                }
            )

            Text("Acepto los términos y condiciones")
        }

        // Abre los términos sin registrar al paciente.
        TextButton(
            onClick = {
                navController.navigate(Rutas.TERMINOS) {
                    launchSingleTop = true
                }
            }
        ) {
            Text("Leer términos y condiciones")
        }

        // Muestra el primer problema encontrado.
        error?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            enabled = !registrado,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Valida los campos antes de consultar el repositorio.
                val problema = when {
                    nombres.trim().isBlank() ->
                        "Ingresa tus nombres y apellidos."

                    !Patterns.EMAIL_ADDRESS
                        .matcher(correo.trim())
                        .matches() ->
                        "Ingresa un correo válido."

                    telefono.trim().length != 9 ||
                            !telefono.trim().all { it.isDigit() } ->
                        "El teléfono debe tener 9 dígitos."

                    contrasena.length < 6 || contrasena.isBlank() ->
                        "La contraseña debe tener al menos 6 caracteres."

                    contrasena != confirmacion ->
                        "Las contraseñas no coinciden."

                    !aceptaTerminos ->
                        "Debes aceptar los términos y condiciones."

                    else -> null
                }

                if (problema != null) {
                    error = problema
                } else {
                    // Guarda al paciente cuando todos los campos son válidos.
                    val creado = Repositorio.registrarUsuario(
                        nombres = nombres,
                        correo = correo,
                        telefono = telefono,
                        contrasena = contrasena
                    )

                    if (creado) {
                        registrado = true

                        // Abre Inicio y elimina las pantallas de acceso.
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.SPLASH) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        error = "No se pudo registrar. El correo ya está registrado."
                    }
                }
            }
        ) {
            Text("Registrarme")
        }
    }
}