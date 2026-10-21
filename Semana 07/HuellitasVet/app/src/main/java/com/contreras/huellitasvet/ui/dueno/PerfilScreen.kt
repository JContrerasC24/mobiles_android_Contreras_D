package com.contreras.huellitasvet.ui.dueno
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.contreras.huellitasvet.viewmodel.PreferenciasViewModel
@Composable
fun PerfilScreen(vm:PreferenciasViewModel,onVolver:()->Unit,onCambiar:()->Unit,onCerrar:()->Unit,ocupado:Boolean,errorSesion:String?) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val atenciones by vm.atenciones.collectAsStateWithLifecycle()
    PreferenciasContenido(vm,false,onVolver,onCambiar,onCerrar,ocupado,errorSesion) {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly) {
            listOf("Mascotas" to mascotas,"Próximas" to proximas,"Atenciones" to atenciones).forEach { (label,count) ->
                Column(horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(count.toString(),style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.primary)
                    Text(label,style=MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
@Composable
fun PreferenciasContenido(vm:PreferenciasViewModel,veterinario:Boolean,onVolver:()->Unit,onCambiar:()->Unit,
    onCerrar:()->Unit,ocupado:Boolean,errorSesion:String?,contadores:@Composable ()->Unit={}) {
    val oscuro by vm.oscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    val aceptar by vm.aceptarCitas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        VetBrandHeader(if(veterinario)"Tu espacio profesional" else "Tu cuenta", if(veterinario)"Preferencias de la clínica" else "Todo sobre ti y tus mascotas")
        VetCard {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                Surface(shape=RoundedCornerShape(50),color=MaterialTheme.colorScheme.primaryContainer) {
                    Box(Modifier.size(54.dp),contentAlignment=Alignment.Center) {Icon(Icons.Default.PersonOutline,null,tint=MaterialTheme.colorScheme.primary)}
                }
                Column {
                    Text(vm.usuario.nombre,style=MaterialTheme.typography.titleMedium)
                    Text(vm.usuario.correo,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if(veterinario)"Perfil veterinario" else "Perfil dueño",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
                }
            }
            contadores()
        }
        Card(onClick={if(!ocupado)onCambiar()},colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface,contentColor=MaterialTheme.colorScheme.onSurface)) {
            Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                Icon(Icons.Default.SwapHoriz,null)
                Column(Modifier.weight(1f)) {
                    Text("Explorar otro perfil · Demo",style=MaterialTheme.typography.titleSmall)
                    Text(if(veterinario)"Modo práctica: ir a vista Dueño" else "Modo práctica: ir a vista Veterinario",style=MaterialTheme.typography.bodySmall)
                }
                Icon(Icons.Default.ChevronRight,null)
            }
        }
        Text("Preferencias",style=MaterialTheme.typography.titleSmall)
        VetCard {
            PreferenciaSwitch("Modo oscuro","Cambia el tema de la app",oscuro,vm::modoOscuro)
            HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
            PreferenciaSwitch(if(veterinario)"Notificaciones" else "Recordatorios",if(veterinario)"Guardar preferencia de avisos" else "Mostrar la próxima cita en Inicio",notificaciones,vm::notificar)
            if(veterinario) {
                HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
                PreferenciaSwitch("Aceptar citas","Abre o cierra las reservas",aceptar,vm::aceptar)
            }
        }
        VetNotice("Tus preferencias se guardan en este dispositivo.")
        error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        errorSesion?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        VetDanger("Cerrar sesión",onCerrar,!ocupado)
    }
}
@Composable
private fun PreferenciaSwitch(texto:String,descripcion:String,valor:Boolean,onCambio:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Column(Modifier.weight(1f)) {
            Text(texto,style=MaterialTheme.typography.titleSmall)
            Text(descripcion,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(valor,onCambio)
    }
}
