package com.contreras.huellitasvet.ui.veterinario
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

import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import java.util.Locale
@Composable
fun ServiciosScreen(vm:ServiciosViewModel,onEditar:(Int)->Unit,modifier:Modifier=Modifier) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    var eliminarId by rememberSaveable { mutableStateOf<Int?>(null) }
    var menuId by rememberSaveable { mutableStateOf<Int?>(null) }
    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(start=20.dp,end=20.dp,top=20.dp,bottom=100.dp),
            verticalArrangement=Arrangement.spacedBy(12.dp)) {
            item { VetHeader("Servicios") }
            error?.let { item{Text(it,color=MaterialTheme.colorScheme.error)} }
            if(servicios.isEmpty())item{VetNotice("Agrega el primer servicio con el botón +.")}
            items(servicios,key={it.id}) { s ->
                VetCard {
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Surface(shape=RoundedCornerShape(12.dp),color=MaterialTheme.colorScheme.primaryContainer) {
                            Box(Modifier.size(42.dp),contentAlignment=Alignment.Center){Icon(iconoServicio(s.icono),null,tint=MaterialTheme.colorScheme.primary)}
                        }
                        Column(Modifier.weight(1f)) {
                            Text(s.nombre,style=MaterialTheme.typography.titleSmall)
                            Text("${s.duracionMin} min · S/ ${String.format(Locale.US,"%.2f",s.precio)}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(s.activo,{vm.alternar(s)},enabled=!ocupado)
                        Box {
                            IconButton(onClick={menuId=s.id},enabled=!ocupado){Icon(Icons.Default.MoreVert,"Opciones de ${s.nombre}",Modifier.size(20.dp))}
                            DropdownMenu(expanded=menuId==s.id,onDismissRequest={menuId=null}) {
                                DropdownMenuItem(text={Text("Editar")},leadingIcon={Icon(Icons.Default.Edit,null)},
                                    onClick={menuId=null;onEditar(s.id)})
                                DropdownMenuItem(text={Text("Eliminar")},leadingIcon={Icon(Icons.Default.DeleteOutline,null)},
                                    onClick={menuId=null;eliminarId=s.id})
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(onClick={if(!ocupado)onEditar(0)},modifier=Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary) {
            Icon(Icons.Default.Add,"Nuevo servicio")
        }
    }
    if(eliminarId!=null)AlertDialog(onDismissRequest={eliminarId=null},title={Text("Eliminar servicio")},
        text={Text("Solo se eliminará si no tiene citas registradas.")},
        confirmButton={TextButton(enabled=!ocupado,onClick={servicios.firstOrNull{it.id==eliminarId}?.let(vm::eliminar);eliminarId=null}){Text("Eliminar")}},
        dismissButton={TextButton(onClick={eliminarId=null}){Text("Volver")}})
}
