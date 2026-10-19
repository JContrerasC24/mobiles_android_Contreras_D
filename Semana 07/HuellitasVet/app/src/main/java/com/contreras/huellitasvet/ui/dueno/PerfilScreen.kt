package com.contreras.huellitasvet.ui.dueno
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.viewmodel.PreferenciasViewModel

@Composable
fun PerfilScreen(vm:PreferenciasViewModel,onVolver:()->Unit,onCambiar:()->Unit,onCerrar:()->Unit,ocupado:Boolean,errorSesion:String?) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val atenciones by vm.atenciones.collectAsStateWithLifecycle()
    PreferenciasContenido(vm,false,onVolver,onCambiar,onCerrar,ocupado,errorSesion) {
        Text("Mascotas: $mascotas"); Text("Citas próximas: $proximas"); Text("Atenciones: $atenciones")
    }
}
@Composable
fun PreferenciasContenido(vm:PreferenciasViewModel,veterinario:Boolean,onVolver:()->Unit,onCambiar:()->Unit,
    onCerrar:()->Unit,ocupado:Boolean,errorSesion:String?,contadores:@Composable ()->Unit={}) {
    val oscuro by vm.oscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    val aceptar by vm.aceptarCitas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement=Arrangement.spacedBy(16.dp)) {
        TextButton(onClick=onVolver){Text("Volver")}
        Text(if(veterinario)"Ajustes" else "Mi perfil",style=MaterialTheme.typography.headlineMedium)
        Text(vm.usuario.nombre); Text(vm.usuario.correo)
        contadores()
        PreferenciaSwitch("Modo oscuro",oscuro,vm::modoOscuro)
        PreferenciaSwitch(if(veterinario)"Notificaciones" else "Recordatorios",notificaciones,vm::notificar)
        if(veterinario)PreferenciaSwitch("Aceptar citas",aceptar,vm::aceptar)
        error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        errorSesion?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        Button(onClick=onCambiar,enabled=!ocupado){Text("Cambiar de perfil")}
        TextButton(onClick=onCerrar,enabled=!ocupado){Text("Cerrar sesión")}
    }
}
@Composable
private fun PreferenciaSwitch(texto:String,valor:Boolean,onCambio:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
        Text(texto,modifier=Modifier.weight(1f)); Switch(valor,onCambio)
    }
}
