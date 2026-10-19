package com.contreras.huellitasvet.ui.veterinario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import java.util.Locale

@Composable
fun ServiciosScreen(vm:ServiciosViewModel,onEditar:(Int)->Unit,modifier:Modifier=Modifier) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    var eliminarId by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyColumn(modifier,contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { Text("Servicios",style=MaterialTheme.typography.headlineMedium)
            Button(onClick={onEditar(0)},enabled=!ocupado){Text("Nuevo servicio")} }
        error?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
        items(servicios,key={it.id}) { s ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(s.nombre,style=MaterialTheme.typography.titleLarge)
                    Text(s.descripcion)
                    Text("${s.duracionMin} min · S/ ${String.format(Locale.US,"%.2f",s.precio)}")
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                        Text(if(s.activo)"Activo" else "Inactivo")
                        Switch(s.activo,{vm.alternar(s)},enabled=!ocupado)
                    }
                    Row {
                        TextButton(onClick={onEditar(s.id)},enabled=!ocupado){Text("Editar")}
                        TextButton(onClick={eliminarId=s.id},enabled=!ocupado){Text("Eliminar")}
                    }
                }
            }
        }
    }
    if(eliminarId!=null)AlertDialog(onDismissRequest={eliminarId=null},
        title={Text("Eliminar servicio")},text={Text("Solo se eliminará si no tiene citas registradas.")},
        confirmButton={TextButton(onClick={
            servicios.firstOrNull{it.id==eliminarId}?.let(vm::eliminar); eliminarId=null
        }){Text("Eliminar")}},
        dismissButton={TextButton(onClick={eliminarId=null}){Text("Volver")}})
}
