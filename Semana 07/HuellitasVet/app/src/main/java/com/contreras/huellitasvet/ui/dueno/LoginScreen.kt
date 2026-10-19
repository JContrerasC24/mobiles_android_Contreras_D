package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Rol
import com.contreras.huellitasvet.ui.components.VetButton
import com.contreras.huellitasvet.ui.components.VetTextField
import com.contreras.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel) {
    val rol by vm.rolSeleccionado.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    var correo by rememberSaveable { mutableStateOf("") }
    // La contraseña no se persiste en el estado guardado de la pantalla.
    var clave by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().imePadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Pets,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("HuellitasVet", style = MaterialTheme.typography.headlineLarge)
        Text("Bienvenido", style = MaterialTheme.typography.titleLarge)
        Text("Selecciona tu perfil e inicia sesión")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = rol == Rol.CLIENTE,
                onClick = { vm.seleccionarRol(Rol.CLIENTE) },
                label = { Text("Dueño") },
                enabled = !ocupado
            )
            FilterChip(
                selected = rol == Rol.ADMIN,
                onClick = { vm.seleccionarRol(Rol.ADMIN) },
                label = { Text("Veterinario") },
                enabled = !ocupado
            )
        }

        VetTextField(
            value = correo,
            onValueChange = { correo = it; vm.limpiarError() },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth(),
            enabled = !ocupado,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        VetTextField(
            value = clave,
            onValueChange = { clave = it; vm.limpiarError() },
            label = "Contraseña",
            modifier = Modifier.fillMaxWidth(),
            enabled = !ocupado,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
        VetButton(
            text = if (ocupado) "Ingresando…" else "Ingresar",
            onClick = { vm.login(correo, clave, rol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !ocupado
        )
    }
}
