package com.contreras.huellitasvet.ui.dueno
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.*

import com.contreras.huellitasvet.viewmodel.InicioViewModel
@Composable
fun InicioScreen(vm: InicioViewModel, onMascota:(Int)->Unit, onAgregar:()->Unit,
    onPedirCita:()->Unit,onVerMascotas:()->Unit,onMisCitas:()->Unit,onPerfil:()->Unit,
    onCerrarSesion:()->Unit,cerrandoSesion:Boolean,errorSesion:String?) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proxima by vm.proximaCita.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val recordatorios by vm.mostrarRecordatorios.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(20.dp),
        verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { VetHeader("Hola, ${vm.dueno.nombre.substringBefore(' ')}",
            subtitulo="¿Cómo están tus mascotas hoy?",onAccion=onMisCitas) }
        item {
            Card(onClick=onMisCitas,colors=CardDefaults.cardColors(
                containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary)) {
                Row(Modifier.fillMaxWidth().padding(18.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                        Text("Próxima cita",style=MaterialTheme.typography.labelMedium)
                        val d=proxima
                        if(d==null) {
                            Text("Un cuidado a tiempo",style=MaterialTheme.typography.titleLarge)
                            Text("Aún no tienes próximas citas.",style=MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("${d.mascota.mascota.nombre} · ${d.servicio.nombre}",style=MaterialTheme.typography.titleMedium)
                            Text("${fechaCorta(d.cita.fecha)} · ${d.cita.hora}",style=MaterialTheme.typography.bodyMedium)
                            EstadoCitaChip(d.cita.estado)
                        }
                    }
                    proxima?.let { MascotaAvatar(especie=it.mascota.mascota.especie) }
                }
            }
        }
        item { Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("Mis mascotas",style=MaterialTheme.typography.titleMedium,modifier=Modifier.weight(1f))
            TextButton(onClick=onVerMascotas){Text("Ver todas")}
        } }
        item {
            LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                items(mascotas,key={it.id}) { m ->
                    Card(onClick={onMascota(m.id)},modifier=Modifier.width(104.dp),
                        border=BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant),
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.fillMaxWidth().padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            MascotaAvatar(especie=m.especie,tamano=48.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(m.nombre,style=MaterialTheme.typography.titleSmall,maxLines=1)
                            Text("Ver ficha",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item {
                    OutlinedCard(onClick=onAgregar,modifier=Modifier.width(104.dp).height(114.dp),
                        border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary)) {
                        Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                            Icon(Icons.Default.Add,"Agregar mascota",tint=MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp));Text("Agregar",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        item { Text("Servicios",style=MaterialTheme.typography.titleMedium) }
        item {
            LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                items(servicios,key={it.id}) { s ->
                    Card(onClick=onPedirCita,modifier=Modifier.width(104.dp),
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally,
                            verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            Icon(iconoServicio(s.icono),null,tint=MaterialTheme.colorScheme.primary)
                            Text(s.nombre,style=MaterialTheme.typography.labelSmall,maxLines=2)
                        }
                    }
                }
            }
            if(servicios.isEmpty()) Text("No hay servicios disponibles.",style=MaterialTheme.typography.bodyMedium)
        }
        proxima?.takeIf { recordatorios }?.let { d -> item { VetNotice("Recordatorio\n${d.mascota.mascota.nombre}: ${d.servicio.nombre}, ${fechaCorta(d.cita.fecha)} a las ${d.cita.hora}.",warning=true) } }
        item { VetButton("Pedir una cita",onPedirCita,Modifier.fillMaxWidth()) }
        error?.let { item{Text(it,color=MaterialTheme.colorScheme.error)} }
        errorSesion?.let { item{Text(it,color=MaterialTheme.colorScheme.error)} }
    }
}
