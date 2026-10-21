package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
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
import java.util.Locale

@Composable
fun InicioScreen(vm:InicioViewModel,onMascota:(Int)->Unit,onAgregar:()->Unit,
    onPedirCita:()->Unit,onVerMascotas:()->Unit,onMisCitas:()->Unit,onPerfil:()->Unit,
    onCerrarSesion:()->Unit,cerrandoSesion:Boolean,errorSesion:String?) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proxima by vm.proximaCita.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val recordatorios by vm.mostrarRecordatorios.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
        item { VetBrandHeader("Hola, ${vm.dueno.nombre.substringBefore(' ')}", "Su bienestar empieza contigo", onPerfil) }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary),shape=RoundedCornerShape(28.dp)) {
                Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            Text("HUELLITASVET · TU CLÍNICA",style=MaterialTheme.typography.labelSmall)
                            Text("Más momentos\njuntos y saludables",style=MaterialTheme.typography.headlineMedium)
                            Text("Programa su próxima visita en unos pasos.",style=MaterialTheme.typography.bodyMedium)
                        }
                        MascotaAvatar(especie="PERRO",tamano=82.dp)
                    }
                    Button(onClick=onPedirCita,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp),shape=RoundedCornerShape(16.dp),
                        colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.surface,contentColor=MaterialTheme.colorScheme.primary)) {
                        Icon(Icons.Default.AddCircleOutline,null,Modifier.size(20.dp));Spacer(Modifier.width(8.dp));Text("Reservar una cita")
                    }
                }
            }
        }
        item { VetSection("Su próxima visita","Ver citas",onMisCitas) }
        item {
            val d=proxima
            if(d==null) VetEmpty("Todo listo para su próxima visita", "Cuando reserves, encontrarás aquí la fecha y el estado de tu cita.")
            else Card(onClick=onMisCitas,colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),shape=RoundedCornerShape(24.dp)) {
                Row(Modifier.padding(18.dp),horizontalArrangement=Arrangement.spacedBy(14.dp),verticalAlignment=Alignment.CenterVertically) {
                    Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(13.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CalendarMonth,null,tint=MaterialTheme.colorScheme.primary)
                            Text(fechaCorta(d.cita.fecha),style=MaterialTheme.typography.labelLarge)
                            Text(d.cita.hora,style=MaterialTheme.typography.bodySmall)
                        }
                    }
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                        Text(d.mascota.mascota.nombre,style=MaterialTheme.typography.titleMedium)
                        Text(d.servicio.nombre,style=MaterialTheme.typography.bodyMedium)
                        EstadoCitaChip(d.cita.estado)
                    }
                    Icon(Icons.Default.ChevronRight,null,tint=MaterialTheme.colorScheme.primary)
                }
            }
        }
        item { VetSection("Tu familia de cuatro patas","Ver todas",onVerMascotas) }
        item {
            LazyRow(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                items(mascotas,key={it.id}) { m ->
                    Card(onClick={onMascota(m.id)},modifier=Modifier.width(132.dp),shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            MascotaAvatar(especie=m.especie,tamano=72.dp)
                            Text(m.nombre,style=MaterialTheme.typography.titleMedium,maxLines=2)
                            Text("Ver su perfil",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                item { OutlinedCard(onClick=onAgregar,modifier=Modifier.width(110.dp).heightIn(min=158.dp),shape=RoundedCornerShape(24.dp),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.AddCircleOutline,"Agregar mascota",Modifier.size(40.dp),tint=MaterialTheme.colorScheme.primary)
                        Text("Agregar\nmascota",style=MaterialTheme.typography.labelLarge)
                    }
                } }
            }
        }
        item { VetSection("Cuidados para cada etapa") }
        items(servicios.chunked(2)) { pair ->
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                pair.forEach { s -> VetQuickAction(s.nombre,"${s.duracionMin} min · S/ ${String.format(Locale.US,"%.2f",s.precio)}",iconoServicio(s.icono),onPedirCita,Modifier.weight(1f)) }
                if(pair.size==1) Spacer(Modifier.weight(1f))
            }
        }
        if(servicios.isEmpty()) item { VetNotice("Los servicios disponibles aparecerán aquí.") }
        proxima?.takeIf { recordatorios }?.let { d -> item { VetNotice("Recuerda: ${d.mascota.mascota.nombre} tiene una visita el ${fechaCorta(d.cita.fecha)} a las ${d.cita.hora}.") } }
        error?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
        errorSesion?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
    }
}
