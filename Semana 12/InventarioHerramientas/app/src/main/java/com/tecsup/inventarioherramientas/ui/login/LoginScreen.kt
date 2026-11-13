package com.tecsup.inventarioherramientas.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.inventarioherramientas.R
import com.tecsup.inventarioherramientas.domain.model.*
import com.tecsup.inventarioherramientas.ui.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CancellationException
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

    var recuperar by rememberSaveable { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }
    var aviso by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val oro = Color(0xFFFFBD00)
    val colores = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color(0xFFEDF1F5), focusedContainerColor = Color(0xFFF2F6F7),
        unfocusedBorderColor = Color(0xFF9BA6B1), focusedBorderColor = Petroleo)
    Box(Modifier.fillMaxSize().background(Color.White).drawBehind {
        val radio = size.width * 0.60f
        for (y in listOf(0f, size.height)) {
            for ((x, tono) in listOf(0f to Color(0xFFFFE49A), size.width to Color(0xFF9FE8E5))) {
                val centro = Offset(x, y)
                drawCircle(Brush.radialGradient(listOf(tono, tono.copy(alpha = 0f)), centro, radio), radio, centro)
            }
        }
    }.imePadding(), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 440.dp).fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.login_marca), null, Modifier.size(66.dp))
                Text("Tool", color = Color(0xFFECA800), fontWeight = FontWeight.Bold, fontSize = 43.sp)
                Text("Rent", color = Petroleo, fontWeight = FontWeight.Bold, fontSize = 43.sp)
            }
            Spacer(Modifier.height(34.dp))
            Text("¡Hola de nuevo!", fontSize = 33.sp, fontWeight = FontWeight.Bold, color = Color(0xFF161319))
            Text("Inicia sesión para continuar", color = Color.DarkGray, fontSize = 18.sp)
            Spacer(Modifier.height(28.dp))
            OutlinedTextField(correo, { correo = it }, Modifier.fillMaxWidth(), singleLine = true,
                shape = CircleShape, colors = colores, enabled = !cargando,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(painterResource(R.drawable.login_correo), null, tint = Petroleo) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(clave, { clave = it }, Modifier.fillMaxWidth(), singleLine = true,
                shape = CircleShape, colors = colores, enabled = !cargando, label = { Text("Contraseña") },
                leadingIcon = { Icon(painterResource(R.drawable.login_candado), null, tint = Petroleo) },
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconToggleButton(visible, { visible = it }) {
                    Icon(painterResource(R.drawable.login_ojo), if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = if (visible) Petroleo else Color.Gray)
                } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
            TextButton(onClick = { aviso = ""; recuperar = true }, enabled = !cargando && !enviando,
                modifier = Modifier.align(Alignment.End)) { Text("¿Olvidaste tu contraseña?", fontWeight = FontWeight.Bold) }
            if (estado is UiState.Error) Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
            if (aviso.isNotEmpty()) Text(aviso, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { onIngresar(correo, clave) }, enabled = !cargando,
                modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp), shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = oro, contentColor = Petroleo)) {
                if (cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                else Text("Ingresar  →", fontWeight = FontWeight.Bold, fontSize = 23.sp)
            }
            Spacer(Modifier.height(26.dp))
            Text("Administra tus herramientas en un solo lugar", textAlign = TextAlign.Center,
                color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Icon(painterResource(R.drawable.login_caja_taller), null, Modifier.size(32.dp), tint = Petroleo)
        }
    }
    if (recuperar) {
        var destino by rememberSaveable { mutableStateOf(correo.trim()) }
        AlertDialog(onDismissRequest = { if (!enviando) recuperar = false },
            title = { Text("Recuperar contraseña") },
            text = { Column {
                Text("Ingresa el correo con el que accedes al taller.")
                OutlinedTextField(destino, { destino = it }, singleLine = true, enabled = !enviando,
                    label = { Text("Correo electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            } },
            confirmButton = { TextButton(enabled = !enviando && android.util.Patterns.EMAIL_ADDRESS.matcher(destino.trim()).matches(),
                onClick = {
                    enviando = true
                    scope.launch {
                        try {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(destino.trim()).await()
                            aviso = "Si existe una cuenta con ese correo, recibirás un enlace de recuperación."
                        } catch (e: CancellationException) { throw e }
                        catch (e: Exception) { aviso = "No se pudo enviar la solicitud. Revisa tu conexión e inténtalo de nuevo." }
                        finally { enviando = false; recuperar = false }
                    }
                }) { Text(if (enviando) "Enviando…" else "Enviar enlace") } },
            dismissButton = { TextButton(onClick = { recuperar = false }, enabled = !enviando) { Text("Cancelar") } })
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