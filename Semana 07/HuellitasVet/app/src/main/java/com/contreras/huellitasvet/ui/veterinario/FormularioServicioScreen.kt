package com.contreras.huellitasvet.ui.veterinario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun FormularioServicioScreen(vm:ServiciosViewModel,id:Int?,onVolver:()->Unit) {
    val lista by vm.servicios.collectAsStateWithLifecycle()
    val errores by vm.errores.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val guardado by vm.guardado.collectAsStateWithLifecycle()
    val servicio=lista.firstOrNull { it.id==id }
    var nombre by rememberSaveable(id){mutableStateOf("")}
    var descripcion by rememberSaveable(id){mutableStateOf("")}
    var precio by rememberSaveable(id){mutableStateOf("")}
    var duracion by rememberSaveable(id){mutableIntStateOf(30)}
    var icono by rememberSaveable(id){mutableStateOf("Pets")}
    var activo by rememberSaveable(id){mutableStateOf(true)}
    var inicializado by rememberSaveable(id){mutableStateOf(id==null)}
    LaunchedEffect(servicio) { if(!inicializado && servicio!=null) {
        nombre=servicio.nombre; descripcion=servicio.descripcion; precio=servicio.precio.toString()
        duracion=servicio.duracionMin; icono=servicio.icono; activo=servicio.activo; inicializado=true
    } }
    LaunchedEffect(guardado){if(guardado)onVolver()}
    val habilitado=inicializado && !ocupado && !guardado && (id==null || servicio!=null)
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        TextButton(onClick=onVolver,enabled=!ocupado){Text("Volver")}
        Text(if(id==null)"Nuevo servicio" else "Editar servicio",style=MaterialTheme.typography.headlineMedium)
        OutlinedTextField(nombre,{nombre=it},enabled=habilitado,label={Text("Nombre")},modifier=Modifier.fillMaxWidth(),
            isError=errores["nombre"]!=null,supportingText={errores["nombre"]?.let{Text(it)}})
        OutlinedTextField(descripcion,{descripcion=it},enabled=habilitado,label={Text("Descripción")},modifier=Modifier.fillMaxWidth())
        Text("Duración")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf(15,30,45,60).forEach { minutos ->
                FilterChip(selected=duracion==minutos,onClick={duracion=minutos},enabled=habilitado,label={Text("$minutos")})
            }
        }
        errores["duracion"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
        OutlinedTextField(precio,{precio=it},enabled=habilitado,label={Text("Precio (S/)")},modifier=Modifier.fillMaxWidth(),
            keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),
            isError=errores["precio"]!=null,supportingText={errores["precio"]?.let{Text(it)}})
        Text("Ícono")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Pets" to "🐾","Vaccines" to "💉","ContentCut" to "✂").forEach { (valor,simbolo) ->
                FilterChip(selected=icono==valor,onClick={icono=valor},enabled=habilitado,label={Text(simbolo)})
            }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text("Servicio activo"); Switch(activo,{activo=it},enabled=habilitado)
        }
        error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        Button(enabled=habilitado,onClick={
            vm.guardar(Servicio(id?:0,nombre,descripcion,duracion,precio.replace(',','.').toDoubleOrNull()?:Double.NaN,activo,icono))
        }){Text(if(ocupado)"Guardando…" else "Guardar servicio")}
    }
}
