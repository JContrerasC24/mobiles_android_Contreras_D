package com.contreras.saludplus.citas.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*

// Presenta cuatro campos y conserva las validaciones del repositorio.
@Composable
fun RegistroScreen(navController: NavHostController) {
    var nombres by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var registrado by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().imePadding()) {
        CabeceraSalud("Crear cuenta", { navController.popBackStack() })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Regístrate para agendar tus citas", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            CampoRegistro("Nombre completo", nombres, { nombres = it; error = null }, Icons.Outlined.Person)
            CampoRegistro("Teléfono", telefono, {
                telefono = it.filter(Char::isDigit).take(9)
                error = null
            }, Icons.Outlined.Phone, KeyboardType.Phone)
            CampoRegistro("Correo electrónico", correo, { correo = it; error = null },
                Icons.Outlined.Email, KeyboardType.Email)
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it; error = null },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(10.dp),
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            if (visible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )
            Text("Contraseña de al menos 6 caracteres", Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            BotonSalud("Registrarme", habilitado = !registrado) {
                // Valida antes de crear la sesión del paciente.
                error = when {
                    nombres.isBlank() -> "Ingresa tu nombre completo."
                    telefono.length != 9 -> "El teléfono debe tener 9 dígitos."
                    !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Ingresa un correo válido."
                    contrasena.isBlank() || contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
                    else -> null
                }
                if (error == null && !registrado) {
                    if (Repositorio.registrarUsuario(nombres, correo, telefono, contrasena)) {
                        registrado = true
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else error = "Este correo ya está registrado."
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Al registrarte aceptas nuestros", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                TextButton(onClick = { navController.navigate(Rutas.TERMINOS) { launchSingleTop = true } }) {
                    Text("Términos y condiciones", style = MaterialTheme.typography.labelMedium)
                }
            }
            TextButton(onClick = {
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.SPLASH)
                    launchSingleTop = true
                }
            }) {
                Text("¿Ya tienes cuenta? Iniciar sesión", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// Reutiliza el aspecto de los campos del registro.
@Composable
private fun CampoRegistro(
    etiqueta: String,
    valor: String,
    cambiar: (String) -> Unit,
    icono: ImageVector,
    teclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor, onValueChange = cambiar,
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        label = { Text(etiqueta) }, shape = RoundedCornerShape(10.dp),
        leadingIcon = { Icon(icono, null, tint = MaterialTheme.colorScheme.primary) },
        keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = ImeAction.Next)
    )
}
