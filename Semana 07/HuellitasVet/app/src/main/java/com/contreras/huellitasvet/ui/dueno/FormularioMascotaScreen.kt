package com.contreras.huellitasvet.ui.dueno
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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import com.contreras.huellitasvet.model.Especie
import com.contreras.huellitasvet.model.Sexo
import com.contreras.huellitasvet.viewmodel.MascotaViewModel
@Composable
fun FormularioMascotaScreen(vm:MascotaViewModel,id:Int?=null,onVolver:()->Unit) {
    val f by vm.formulario.collectAsStateWithLifecycle()
    val errores by vm.errores.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val finalizado by vm.finalizado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    LaunchedEffect(finalizado){if(finalizado)onVolver()}
    val enabled=!ocupado && !cargando && !finalizado && (id==null || mascota!=null)
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        VetHeader(if(id==null)"Nueva mascota" else "Editar mascota",onVolver={if(!ocupado)onVolver()})
        Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center) { MascotaAvatar(especie=f.especie,tamano=80.dp) }
        VetField(f.nombre,{vm.actualizar(f.copy(nombre=it))},"Nombre",enabled=enabled,error=errores["nombre"])
        Text("Especie",style=MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Especie.entries.forEach { e -> VetChoice(tituloValor(e.name),f.especie==e.name,
                {vm.actualizar(f.copy(especie=e.name))},Modifier.weight(1f),enabled) }
        }
        errores["especie"]?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        VetField(f.raza,{vm.actualizar(f.copy(raza=it))},"Raza",enabled=enabled)
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            VetField(f.anio,{vm.actualizar(f.copy(anio=it))},"Año de nacimiento",Modifier.weight(1f),enabled,errores["anio"],KeyboardType.Number)
            VetField(f.peso,{vm.actualizar(f.copy(peso=it))},"Peso (kg)",Modifier.weight(1f),enabled,errores["peso"],KeyboardType.Decimal)
        }
        Text("Sexo",style=MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Sexo.entries.forEach { s -> VetChoice(tituloValor(s.name),f.sexo==s.name,
                {vm.actualizar(f.copy(sexo=s.name))},Modifier.weight(1f),enabled) }
        }
        errores["sexo"]?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        mensaje?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        VetButton(if(ocupado)"Guardando…" else "Guardar mascota",vm::guardarFormulario,Modifier.fillMaxWidth(),enabled)
        OutlinedButton(onClick=onVolver,enabled=!ocupado,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Cancelar")}
    }
}
