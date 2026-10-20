package com.contreras.huellitasvet.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
@Composable
fun HistorialItem(detalle:CitaConDetalle) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        Column {
            Icon(Icons.Default.CheckCircle,null,Modifier.size(22.dp),tint=MaterialTheme.colorScheme.primary)
            VerticalDivider(Modifier.padding(start=10.dp).weight(1f),color=MaterialTheme.colorScheme.primaryContainer)
        }
        VetCard(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text(detalle.servicio.nombre,Modifier.weight(1f),style=MaterialTheme.typography.titleSmall)
                Text(fechaCorta(detalle.cita.fecha),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
            }
            Text(detalle.cita.diagnostico?:"Sin diagnóstico registrado",style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            detalle.cita.tratamiento?.takeIf{it.isNotBlank()}?.let { Text(it,style=MaterialTheme.typography.bodySmall) }
            detalle.cita.pesoRegistrado?.let {
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Favorite,null,Modifier.size(14.dp),tint=MaterialTheme.colorScheme.error)
                    Text("Peso: $it kg",style=MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
