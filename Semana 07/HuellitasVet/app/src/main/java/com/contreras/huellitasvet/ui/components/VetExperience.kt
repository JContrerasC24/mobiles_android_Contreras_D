package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VetBrandHeader(title: String, subtitle: String, onProfile: (() -> Unit)? = null) {
    val c = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))) {
        Canvas(Modifier.matchParentSize()) {
            drawRect(c.primaryContainer)
            drawOval(c.surface, Offset(-size.width * .25f, -size.height * .65f), Size(size.width * 1.5f, size.height * 1.6f))
        }
        Column(Modifier.padding(22.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Surface(shape=CircleShape,color=c.primary) {
                    Icon(Icons.Default.Pets,null,Modifier.padding(10.dp).size(22.dp),tint=c.onPrimary)
                }
                Text("huellitas" ,Modifier.weight(1f).padding(start=9.dp),style=MaterialTheme.typography.titleMedium,color=c.primary)
                if(onProfile!=null) IconButton(onClick=onProfile) { Icon(Icons.Default.AccountCircle,"Mi perfil",tint=c.primary) }
            }
            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Text(title,style=MaterialTheme.typography.headlineMedium)
                Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun VetSection(title:String, action:String?=null, onClick:()->Unit={}) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
        if(action!=null) TextButton(onClick=onClick) { Text(action) }
    }
}

@Composable
fun VetDetail(label:String,value:String) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(label,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value,Modifier.weight(1f),style=MaterialTheme.typography.bodyMedium,fontWeight=FontWeight.SemiBold)
    }
}

@Composable
fun VetEmpty(title:String,subtitle:String) {
    VetCard {
        Icon(Icons.Default.EventAvailable,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(32.dp))
        Text(title,style=MaterialTheme.typography.titleMedium)
        Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun VetDay(date:LocalDate,selected:Boolean,onClick:()->Unit,enabled:Boolean=true) {
    val c=MaterialTheme.colorScheme
    Card(onClick=onClick,enabled=enabled,modifier=Modifier.width(68.dp),shape=RoundedCornerShape(20.dp),
        colors=CardDefaults.cardColors(containerColor=if(selected)c.primary else c.surface,contentColor=if(selected)c.onPrimary else c.onSurface),
        border=BorderStroke(1.dp,if(selected)c.primary else c.outlineVariant)) {
        Column(Modifier.fillMaxWidth().padding(vertical=13.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Text(date.format(DateTimeFormatter.ofPattern("EEE",Locale.forLanguageTag("es-PE"))),style=MaterialTheme.typography.labelMedium)
            Text(date.dayOfMonth.toString(),style=MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun VetQuickAction(title:String,subtitle:String,icon:ImageVector,onClick:()->Unit,modifier:Modifier=Modifier) {
    Card(onClick=onClick,modifier=modifier,colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),shape=RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=RoundedCornerShape(12.dp)) {
                Icon(icon,null,Modifier.padding(10.dp).size(22.dp),tint=MaterialTheme.colorScheme.primary)
            }
            Text(title,style=MaterialTheme.typography.titleSmall)
            Text(subtitle,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun VetSteps(current:Int,labels:List<String>) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { index,label ->
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(progress={if(index<=current)1f else 0f},modifier=Modifier.fillMaxWidth().height(4.dp),
                    color=MaterialTheme.colorScheme.primary,trackColor=MaterialTheme.colorScheme.outlineVariant)
                Text("${index+1}. $label",style=MaterialTheme.typography.labelMedium,
                    color=if(index==current)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
