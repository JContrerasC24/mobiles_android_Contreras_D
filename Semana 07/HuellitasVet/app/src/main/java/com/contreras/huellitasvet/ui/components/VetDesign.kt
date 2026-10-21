package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun fechaCorta(fecha:String):String = runCatching {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("d MMM",Locale("es","PE")))
}.getOrDefault(fecha)
fun tituloValor(valor:String)=valor.lowercase().replaceFirstChar { it.titlecase() }
fun iconoServicio(valor:String):ImageVector = when(valor.lowercase()) {
    "pets" -> Icons.Default.Pets
    "syringe","vaccines" -> Icons.Default.Vaccines
    "scissors","contentcut" -> Icons.Default.ContentCut
    else -> Icons.Default.Favorite
}
@Composable
fun VetHeader(titulo:String,onVolver:(()->Unit)?=null,subtitulo:String?=null,
    onAccion:(()->Unit)?=null,icono:ImageVector=Icons.Default.Notifications) {
    Row(Modifier.fillMaxWidth().padding(vertical=8.dp).heightIn(min=56.dp),verticalAlignment=Alignment.CenterVertically) {
        if(onVolver!=null) IconButton(onClick=onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack,"Volver") }
        Column(Modifier.weight(1f)) {
            Text(titulo,style=if(onVolver==null)MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge)
            subtitulo?.let { Text(it,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        if(onAccion!=null)IconButton(onClick=onAccion){Icon(icono,"Ver novedades")}
    }
}
@Composable
fun VetCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit) {
    Card(modifier.fillMaxWidth(),shape=RoundedCornerShape(24.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
        elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=content)
    }
}
@Composable
fun VetField(value:String,onChange:(String)->Unit,label:String,modifier:Modifier=Modifier,
    enabled:Boolean=true,error:String?=null,keyboard:KeyboardType=KeyboardType.Text,lines:Int=1) {
    OutlinedTextField(value,onChange,label={Text(label)},modifier=modifier.fillMaxWidth(),enabled=enabled,
        singleLine=lines==1,minLines=lines,shape=RoundedCornerShape(16.dp),
        keyboardOptions=KeyboardOptions(keyboardType=keyboard),isError=error!=null,
        supportingText=if(error==null)null else ({Text(error)}),
        colors=OutlinedTextFieldDefaults.colors(unfocusedContainerColor=MaterialTheme.colorScheme.surface,
            focusedContainerColor=MaterialTheme.colorScheme.surface,
            unfocusedBorderColor=MaterialTheme.colorScheme.outlineVariant))
}
@Composable
fun VetChoice(text:String,selected:Boolean,onClick:()->Unit,modifier:Modifier=Modifier,enabled:Boolean=true) {
    FilterChip(selected,onClick,modifier=modifier.heightIn(min=48.dp),enabled=enabled,
        label={Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center){Text(text)}},
        shape=RoundedCornerShape(16.dp),
        colors=FilterChipDefaults.filterChipColors(containerColor=MaterialTheme.colorScheme.surface,
            selectedContainerColor=MaterialTheme.colorScheme.primary,selectedLabelColor=MaterialTheme.colorScheme.onPrimary))
}
@Composable
fun VetNotice(text:String,warning:Boolean=false) {
    val c=MaterialTheme.colorScheme
    Surface(color=if(warning)c.tertiaryContainer else c.primaryContainer,
        contentColor=if(warning)c.onTertiaryContainer else c.onPrimaryContainer,shape=RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Icon(if(warning)Icons.Default.Notifications else Icons.Default.Check,null,Modifier.size(20.dp))
            Text(text,style=MaterialTheme.typography.bodySmall)
        }
    }
}
@Composable
fun VetDanger(text:String,onClick:()->Unit,enabled:Boolean=true,modifier:Modifier=Modifier) {
    OutlinedButton(onClick,modifier.fillMaxWidth().heightIn(min=48.dp),enabled=enabled,
        shape=RoundedCornerShape(16.dp),border=BorderStroke(1.dp,MaterialTheme.colorScheme.error),
        colors=ButtonDefaults.outlinedButtonColors(contentColor=MaterialTheme.colorScheme.error)){Text(text)}
}
