package com.tecsup.inventarioherramientas.ui.login

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.domain.model.Sesion
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun LoginScreen(
    estado: UiState<Sesion?>,
    onIngresar: (String, String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val sesion = (estado as? UiState.Exito)?.datos

    if (sesion != null) {
        SesionIniciadaScreen(
            sesion = sesion,
            onCerrarSesion = onCerrarSesion
        )
    } else {
        FormularioLogin(
            estado = estado,
            onIngresar = onIngresar
        )
    }
}

@Composable
private fun FormularioLogin(
    estado: UiState<Sesion?>,
    onIngresar: (String, String) -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }

    // No se guarda la contraseña en el estado persistente.
    var clave by remember { mutableStateOf("") }

    var visible by remember { mutableStateOf(false) }
    val cargando = estado is UiState.Cargando

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        com.tecsup.inventarioherramientas.ui.Cabecera("Acceso al taller", "Inventario de herramientas")

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = !cargando,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !cargando,
            visualTransformation = if (visible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                androidx.compose.material3.TextButton(onClick = { visible = !visible }) {
                    Text(if (visible) "Ocultar" else "Mostrar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (estado is UiState.Error) {
            Text(
                text = estado.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = { onIngresar(correo, clave) },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "Procesando..." else "Ingresar")
        }

        if (cargando) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SesionIniciadaScreen(
    sesion: Sesion,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sesión iniciada",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Correo: ${sesion.correo}")

        Text(
            text = if (sesion.rol == Rol.ADMIN) {
                "Rol: Administrador"
            } else {
                "Rol: Usuario"
            }
        )

        Text("La conexión con Authentication y Firestore funciona.")

        Button(onClick = onCerrarSesion) {
            Text("Cerrar sesión")
        }
    }
}