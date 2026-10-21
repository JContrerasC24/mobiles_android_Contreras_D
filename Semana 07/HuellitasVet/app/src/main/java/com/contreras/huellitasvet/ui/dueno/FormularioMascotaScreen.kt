package com.contreras.huellitasvet.ui.dueno

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Especie
import com.contreras.huellitasvet.model.Sexo
import com.contreras.huellitasvet.ui.components.*
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
    var paso by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(finalizado) { if(finalizado) onVolver() }
    LaunchedEffect(errores) { if(errores.containsKey("nombre") || errores.containsKey("especie")) paso=0 }
    val enabled=!ocupado && !cargando && !finalizado && (id==null || mascota!=null)
    val volver:()->Unit={if(!ocupado) {if(paso>0) paso-- else onVolver()}}
    BackHandler(enabled=paso>0 || ocupado) { volver() }
    Column(Modifier.fillMaxSize().imePadding()) {
        Column(Modifier.padding(horizontal=20.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            VetHeader(if(id==null)"Su nuevo perfil" else "Editar su perfil",onVolver=volver)
            VetSteps(paso,listOf("Conocernos","Sus datos"))
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            if(cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
            if(paso==0) {
                Text("Cuéntanos sobre tu compañero",style=MaterialTheme.typography.headlineMedium)
                Text("Empecemos con su nombre y especie.",color=MaterialTheme.colorScheme.onSurfaceVariant)
                VetField(f.nombre,{vm.actualizar(f.copy(nombre=it))},"¿Cómo se llama?",enabled=enabled,error=errores["nombre"])
                Text("¿Qué tipo de mascota es?",style=MaterialTheme.typography.titleMedium)
                Especie.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                        row.forEach { e ->
                            val selected=f.especie==e.name
                            Card(onClick={vm.actualizar(f.copy(especie=e.name))},enabled=enabled,modifier=Modifier.weight(1f),shape=RoundedCornerShape(24.dp),
                                border=BorderStroke(if(selected)2.dp else 1.dp,if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                                Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)) {
                                    MascotaAvatar(especie=e.name,tamano=80.dp)
                                    Text(tituloValor(e.name),style=MaterialTheme.typography.titleMedium)
                                    Text(if(selected)"Seleccionado" else "Elegir",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        if(row.size==1) Spacer(Modifier.weight(1f))
                    }
                }
                errores["especie"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            } else {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                    MascotaAvatar(especie=f.especie,tamano=80.dp)
                    Column { Text(f.nombre,style=MaterialTheme.typography.headlineMedium);Text("Completemos su ficha",color=MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                VetCard {
                    VetField(f.raza,{vm.actualizar(f.copy(raza=it))},"Raza",enabled=enabled)
                    VetField(f.anio,{vm.actualizar(f.copy(anio=it))},"Año de nacimiento",enabled=enabled,error=errores["anio"],keyboard=KeyboardType.Number)
                    VetField(f.peso,{vm.actualizar(f.copy(peso=it))},"Peso (kg)",enabled=enabled,error=errores["peso"],keyboard=KeyboardType.Decimal)
                    Text("Sexo",style=MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        Sexo.entries.forEach { s -> VetChoice(tituloValor(s.name),f.sexo==s.name,{vm.actualizar(f.copy(sexo=s.name))},Modifier.weight(1f),enabled) }
                    }
                    errores["sexo"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
                }
                VetNotice("Estos datos acompañarán su historial clínico y ayudarán a personalizar su atención.")
            }
            mensaje?.let { Text(it,color=MaterialTheme.colorScheme.error) }
        }
        Surface(shadowElevation=8.dp) {
            Column(Modifier.padding(20.dp)) {
                VetButton(if(ocupado)"Guardando…" else if(paso==0)"Continuar" else "Guardar mascota",
                    {if(paso==0) paso=1 else vm.guardarFormulario()},Modifier.fillMaxWidth(),enabled && (paso==1 || (f.nombre.isNotBlank() && f.especie.isNotBlank())))
            }
        }
    }
}
