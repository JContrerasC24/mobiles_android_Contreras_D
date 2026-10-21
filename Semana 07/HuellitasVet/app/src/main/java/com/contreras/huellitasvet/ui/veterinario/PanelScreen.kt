package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.contreras.huellitasvet.ui.components.graficos.*
import com.contreras.huellitasvet.viewmodel.PanelViewModel
import java.time.LocalDate
import java.util.Locale

@Composable
fun PanelScreen(vm:PanelViewModel,nombre:String,onAgenda:()->Unit,onAjustes:()->Unit,
    onPacientes:()->Unit,onServicios:()->Unit,onCerrarSesion:()->Unit,ocupado:Boolean,errorSesion:String?) {
    val hoy by vm.citasHoy.collectAsStateWithLifecycle()
    val pendientes by vm.pendientes.collectAsStateWithLifecycle()
    val atendidas by vm.atendidas.collectAsStateWithLifecycle()
    val ingresos by vm.ingresosMes.collectAsStateWithLifecycle()
    val estados by vm.porEstado.collectAsStateWithLifecycle()
    val servicios by vm.porServicio.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
        item { VetBrandHeader("Hola, $nombre", "Tu clínica, conectada con cada paciente",onAjustes) }
        item {
            Card(shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary)) {
                Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("HOY · ${fechaCorta(LocalDate.now().toString()).uppercase()}",style=MaterialTheme.typography.labelMedium)
                            Text("$hoy citas en agenda",style=MaterialTheme.typography.headlineMedium)
                        }
                        Icon(Icons.Default.CalendarMonth,null,Modifier.size(40.dp))
                    }
                    Text("Acompaña cada visita desde la confirmación hasta su atención.",style=MaterialTheme.typography.bodyMedium)
                    Button(onClick=onAgenda,modifier=Modifier.fillMaxWidth().heightIn(min=50.dp),shape=RoundedCornerShape(16.dp),
                        colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.surface,contentColor=MaterialTheme.colorScheme.primary)) {
                        Text("Abrir mi agenda");Spacer(Modifier.width(12.dp));Icon(Icons.Default.ArrowForward,null)
                    }
                }
            }
        }
        item { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            VetQuickAction("Pacientes","Fichas e historial",Icons.Default.Pets,onPacientes,Modifier.weight(1f))
            VetQuickAction("Servicios","Gestiona los cuidados",Icons.Default.MedicalServices,onServicios,Modifier.weight(1f))
        } }
        item { VetSection("Actividad de la clínica") }
        item { VetCard {
            Text("INGRESOS DEL MES",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("S/ ${String.format(Locale.US,"%,.2f",ingresos)}",style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.primary)
            HorizontalDivider(Modifier.padding(vertical=8.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(20.dp)) {
                Column(Modifier.weight(1f)) { Text(pendientes.toString(),style=MaterialTheme.typography.titleLarge);Text("Pendientes · total",style=MaterialTheme.typography.bodySmall) }
                Column(Modifier.weight(1f)) { Text(atendidas.toString(),style=MaterialTheme.typography.titleLarge);Text("Atendidas · total",style=MaterialTheme.typography.bodySmall) }
            }
        } }
        if(pendientes>0) item { OutlinedCard(onClick=onAgenda,colors=CardDefaults.outlinedCardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
            Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.NotificationsActive,null,tint=MaterialTheme.colorScheme.primary)
                Text("$pendientes citas esperan confirmación",Modifier.weight(1f),style=MaterialTheme.typography.titleSmall)
                Icon(Icons.Default.ChevronRight,null)
            }
        } }
        item { VetCard { Text("Estado de las citas",style=MaterialTheme.typography.titleMedium);Text("Historial completo",style=MaterialTheme.typography.bodySmall);GraficoDona(estados) } }
        item { VetCard { Text("Servicios solicitados",style=MaterialTheme.typography.titleMedium);GraficoBarras(servicios) } }
        error?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
        errorSesion?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
    }
}
