package com.contreras.huellitasvet.ui.veterinario
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

import androidx.compose.ui.graphics.Color
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
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { VetHeader("Panel de la clínica",subtitulo="Hoy · ${fechaCorta(LocalDate.now().toString())}",onAccion=onAgenda) }
        item {
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                listOf(Triple("Citas hoy",hoy,Color(0xFFDBEAFE)),Triple("Pendientes",pendientes,Color(0xFFFEF3C7)),
                    Triple("Atendidas",atendidas,Color(0xFFE2E8F0))).forEach { (label,count,color) ->
                    Card(Modifier.weight(1f),colors=CardDefaults.cardColors(containerColor=color,contentColor=Color(0xFF243B64))) {
                        Column(Modifier.fillMaxWidth().padding(vertical=14.dp,horizontal=4.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Text(count.toString(),style=MaterialTheme.typography.headlineMedium)
                            Text(label,style=MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary)) {
                Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text("Ingresos del mes",style=MaterialTheme.typography.bodySmall)
                    Text("S/ ${String.format(Locale.US,"%,.2f",ingresos)}",style=MaterialTheme.typography.headlineSmall)
                }
            }
        }
        item { Text("Citas por estado",style=MaterialTheme.typography.titleSmall) }
        item { VetCard { GraficoDona(estados) } }
        item { Text("Citas por servicio",style=MaterialTheme.typography.titleSmall) }
        item { VetCard { GraficoBarras(servicios) } }
        if(pendientes>0) item {
            VetCard {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning,null,tint=MaterialTheme.colorScheme.tertiary)
                    Column(Modifier.weight(1f).padding(horizontal=10.dp)) {
                        Text("$pendientes citas por confirmar",style=MaterialTheme.typography.titleSmall)
                        Text("Revisa tu agenda",style=MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick=onAgenda){Text("Ver")}
                }
            }
        }
        error?.let { item{Text(it,color=MaterialTheme.colorScheme.error)} }
        errorSesion?.let { item{Text(it,color=MaterialTheme.colorScheme.error)} }
    }
}
