package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import java.util.Locale

@Composable
fun CitaCard(detalle:CitaConDetalle,puedeCancelar:Boolean,puedeReprogramar:Boolean=puedeCancelar,
    ocupado:Boolean,onCancelar:()->Unit,onReprogramar:(()->Unit)?=null) {
    VetCard {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            MascotaAvatar(especie=detalle.mascota.mascota.especie,tamano=60.dp)
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Text(detalle.mascota.mascota.nombre,style=MaterialTheme.typography.titleLarge)
                Text(detalle.servicio.nombre,style=MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(4.dp))
        Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=RoundedCornerShape(14.dp)) {
            Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth,null,tint=MaterialTheme.colorScheme.primary)
                Text("${fechaCorta(detalle.cita.fecha)} · ${detalle.cita.hora}",style=MaterialTheme.typography.titleSmall)
            }
        }
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
            EstadoCitaChip(detalle.cita.estado)
            Text("S/ ${String.format(Locale.US,"%.2f",detalle.cita.precio)}",style=MaterialTheme.typography.titleSmall)
        }
        if(puedeReprogramar && onReprogramar!=null) VetButton("Reprogramar visita",onReprogramar,Modifier.fillMaxWidth(),!ocupado)
        if(puedeCancelar) TextButton(onClick=onCancelar,enabled=!ocupado,modifier=Modifier.fillMaxWidth()) { Text("Cancelar cita",color=MaterialTheme.colorScheme.error) }
    }
}
