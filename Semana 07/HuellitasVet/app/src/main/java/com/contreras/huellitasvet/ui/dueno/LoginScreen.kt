package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Rol
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm:SesionViewModel) {
    val rol by vm.rolSeleccionado.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(22.dp)) {
        VetBrandHeader("Bienvenido a HuellitasVet", "Un espacio para cuidar a quienes más quieres")
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.Bottom) {
            MascotaAvatar(especie="GATO",tamano=90.dp)
            Spacer(Modifier.width(16.dp))
            MascotaAvatar(especie="PERRO",tamano=118.dp)
        }
        VetCard {
            Text("Qué bueno verte",style=MaterialTheme.typography.titleLarge)
            Text("Elige tu perfil para continuar",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                VetChoice("Dueño",rol==Rol.CLIENTE,{vm.seleccionarRol(Rol.CLIENTE)},Modifier.weight(1f),!ocupado)
                VetChoice("Veterinario",rol==Rol.ADMIN,{vm.seleccionarRol(Rol.ADMIN)},Modifier.weight(1f),!ocupado)
            }
            VetField(correo,{correo=it;vm.limpiarError()},"Correo electrónico",enabled=!ocupado,keyboard=KeyboardType.Email)
            VetTextField(value=clave,onValueChange={clave=it;vm.limpiarError()},label="Contraseña",modifier=Modifier.fillMaxWidth(),
                enabled=!ocupado,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),visualTransformation=PasswordVisualTransformation())
            error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(4.dp))
            VetButton(if(ocupado)"Ingresando…" else "Ingresar",{vm.login(correo,clave,rol)},Modifier.fillMaxWidth(),!ocupado)
        }
        Text("Sus citas, su historia y tus cuidados. Todo en un solo lugar.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
