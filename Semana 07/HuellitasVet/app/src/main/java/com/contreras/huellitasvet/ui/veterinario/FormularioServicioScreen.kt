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
import com.contreras.huellitasvet.ui.components.*

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
    var icono by rememberSaveable(id){mutableStateOf("heart")}
    var activo by rememberSaveable(id){mutableStateOf(true)}
    var inicializado by rememberSaveable(id){mutableStateOf(id==null)}
    LaunchedEffect(servicio) { if(!inicializado && servicio!=null) {
        nombre=servicio.nombre; descripcion=servicio.descripcion; precio=servicio.precio.toString()
        duracion=servicio.duracionMin; icono=servicio.icono; activo=servicio.activo; inicializado=true
    } }
    LaunchedEffect(guardado){if(guardado)onVolver()}
    val habilitado=inicializado && !ocupado && !guardado && (id==null || servicio!=null)
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        VetHeader(if(id==null)"Nuevo servicio" else "Editar servicio",onVolver={if(!ocupado)onVolver()})
        VetField(nombre,{nombre=it},"Nombre del servicio",enabled=habilitado,error=errores["nombre"])
        Text("Duración de la visita",style=MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf(15,30,45,60).forEach { minutos ->
                VetChoice("$minutos min",duracion==minutos,{duracion=minutos},Modifier.weight(1f),habilitado)
            }
        }
        errores["duracion"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
        VetField(precio,{precio=it},"Precio (S/)",enabled=habilitado,error=errores["precio"],keyboard=KeyboardType.Decimal)
        VetField(descripcion,{descripcion=it},"Descripción",enabled=habilitado,lines=3)
        Text("Identidad del servicio",style=MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("heart" to "Consulta","Vaccines" to "Vacuna","ContentCut" to "Baño","Pets" to "Mascota").forEach { (valor,simbolo) ->
                FilterChip(selected=iconoServicio(icono)==iconoServicio(valor),onClick={icono=valor},enabled=habilitado,modifier=Modifier.weight(1f),label={Icon(iconoServicio(valor),simbolo,tint=MaterialTheme.colorScheme.primary)})
            }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) { Text("Servicio activo",style=MaterialTheme.typography.titleSmall); Text("Los dueños pueden pedirlo",style=MaterialTheme.typography.bodySmall) }; Switch(activo,{activo=it},enabled=habilitado)
        }
        error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        Button(shape=androidx.compose.foundation.shape.RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth().heightIn(min=54.dp),enabled=habilitado,onClick={
            vm.guardar(Servicio(id?:0,nombre,descripcion,duracion,precio.replace(',','.').toDoubleOrNull()?:Double.NaN,activo,icono))
        }){Text(if(ocupado)"Guardando…" else "Guardar servicio")}
        OutlinedButton(onClick=onVolver,enabled=!ocupado,modifier=Modifier.fillMaxWidth().heightIn(min=54.dp)){Text("Cancelar")}
    }
}
