package com.contreras.huellitasvet.ui.dueno

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.contreras.huellitasvet.model.Horarios
import com.contreras.huellitasvet.viewmodel.PedirCitaViewModel
import java.util.Locale

@Composable
fun PedirCitaScreen(vm:PedirCitaViewModel,onVolver:()->Unit,onCreada:()->Unit) {
    val aceptar by vm.aceptarCitas.collectAsStateWithLifecycle()
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mascotaId by vm.mascota.collectAsStateWithLifecycle()
    val servicioId by vm.servicio.collectAsStateWithLifecycle()
    val fecha by vm.fecha.collectAsStateWithLifecycle()
    val hora by vm.hora.collectAsStateWithLifecycle()
    val nota by vm.nota.collectAsStateWithLifecycle()
    val disponibilidad by vm.disponibilidad.collectAsStateWithLifecycle()
    val ahora by vm.ahora.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val creada by vm.creada.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var paso by rememberSaveable { mutableIntStateOf(0) }
    val mascota=mascotas.firstOrNull { it.id==mascotaId }
    val servicio=servicios.firstOrNull { it.id==servicioId }
    val horas=disponibilidad.horas
    val listo=disponibilidad.fecha==fecha && horas!=null
    val seleccionValida=mascota!=null && servicio!=null
    val horarioValido=hora!=null && listo && Horarios.disponible(fecha,hora!!,horas.orEmpty(),ahora)
    val valido=aceptar && seleccionValida && horarioValido
    val volver:()->Unit={ if(!ocupado) { if(paso>0) paso-- else onVolver() } }
    BackHandler(enabled=paso>0 || ocupado) { volver() }

    if(creada!=null) {
        Column(Modifier.fillMaxSize().padding(28.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle,null,Modifier.size(72.dp),tint=MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(24.dp))
            Text("¡Solicitud enviada!",style=MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text("Tu cita está pendiente de confirmación por la clínica.",style=MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            VetCard {
                VetDetail("Mascota",mascota?.nombre.orEmpty())
                VetDetail("Fecha",fechaCorta(fecha.toString()))
                VetDetail("Hora",hora.orEmpty())
                VetDetail("Estado","Pendiente")
            }
            Spacer(Modifier.height(24.dp))
            VetButton("Ver mis citas",onCreada,Modifier.fillMaxWidth())
        }
        BackHandler { onCreada() }
        return
    }
    Column(Modifier.fillMaxSize().imePadding()) {
        Column(Modifier.padding(horizontal=20.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            VetHeader("Reservar una cita",onVolver=volver)
            VetSteps(paso,listOf("Cuidado","Horario","Revisión"))
        }
        key(paso) {
            LazyColumn(Modifier.weight(1f),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                if(!aceptar) item { VetNotice("La clínica no está recibiendo nuevas reservas.",warning=true) }
                if(paso==0) {
                    item { Text("¿A quién vamos a cuidar?",style=MaterialTheme.typography.headlineMedium) }
                    if(mascotas.isEmpty()) item { VetEmpty("Primero, su perfil", "Vuelve a Mascotas y registra a tu compañero antes de reservar.") }
                    item { LazyRow(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        items(mascotas,key={it.id}) { m ->
                            Card(onClick={vm.mascota.value=m.id},enabled=!ocupado,modifier=Modifier.width(124.dp),shape=RoundedCornerShape(22.dp),
                                border=BorderStroke(if(m.id==mascotaId)2.dp else 1.dp,if(m.id==mascotaId)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                                Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                                    MascotaAvatar(especie=m.especie,tamano=64.dp)
                                    Text(m.nombre,style=MaterialTheme.typography.titleSmall)
                                    Text(if(m.id==mascotaId)"Seleccionada" else tituloValor(m.especie),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    } }
                    item { VetSection("¿Qué necesita?") }
                    if(servicios.isEmpty()) item { VetNotice("No hay servicios activos disponibles.") }
                    items(servicios,key={it.id}) { s ->
                        Card(onClick={vm.servicio.value=s.id},enabled=!ocupado,shape=RoundedCornerShape(22.dp),
                            border=BorderStroke(if(s.id==servicioId)2.dp else 1.dp,if(s.id==servicioId)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                            Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                                Icon(iconoServicio(s.icono),null,tint=MaterialTheme.colorScheme.primary)
                                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                                    Text(s.nombre,style=MaterialTheme.typography.titleMedium)
                                    Text("${s.duracionMin} min · S/ ${String.format(Locale.US,"%.2f",s.precio)}",style=MaterialTheme.typography.bodySmall)
                                }
                                RadioButton(selected=s.id==servicioId,onClick=null)
                            }
                        }
                    }
                }
                if(paso==1) {
                    item { Text("Elige su próxima visita",style=MaterialTheme.typography.headlineMedium) }
                    item { Text("${mascota?.nombre.orEmpty()} · ${servicio?.nombre.orEmpty()}",color=MaterialTheme.colorScheme.onSurfaceVariant) }
                    item { LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        items((0..7).map { ahora.toLocalDate().plusDays(it.toLong()) }) { dia -> VetDay(dia,dia==fecha,{vm.elegirFecha(dia)},!ocupado) }
                    } }
                    item { VetSection("Horarios disponibles") }
                    if(!listo) item { LinearProgressIndicator(Modifier.fillMaxWidth());Text("Consultando horarios…") }
                    if(listo && Horarios.todos.none { Horarios.disponible(fecha,it,horas.orEmpty(),ahora) }) item { VetNotice("No quedan turnos para este día. Elige otra fecha.",warning=true) }
                    items(Horarios.todos.chunked(3)) { fila ->
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            fila.forEach { turno ->
                                val libre=listo && Horarios.disponible(fecha,turno,horas.orEmpty(),ahora)
                                VetChoice(turno,hora==turno && libre,{vm.hora.value=turno},Modifier.weight(1f),libre && !ocupado)
                            }
                            repeat(3-fila.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    item { Text("Los horarios atenuados ya no están disponibles.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                if(paso==2) {
                    item { Text("Revisa los detalles",style=MaterialTheme.typography.headlineMedium) }
                    item { VetCard {
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                            MascotaAvatar(especie=mascota?.especie.orEmpty(),tamano=64.dp)
                            Column { Text(mascota?.nombre ?: "Sin seleccionar",style=MaterialTheme.typography.titleLarge);Text(servicio?.nombre ?: "Sin seleccionar") }
                        }
                        HorizontalDivider(Modifier.padding(vertical=8.dp))
                        VetDetail("Clínica","HuellitasVet")
                        VetDetail("Fecha",fechaCorta(fecha.toString()))
                        VetDetail("Hora",hora ?: "Elige otro horario")
                        VetDetail("Duración","${servicio?.duracionMin ?: 0} min")
                        VetDetail("Precio",servicio?.let { "S/ ${String.format(Locale.US,"%.2f",it.precio)}" } ?: "—")
                    } }
                    item { VetField(nota,{vm.nota.value=it},"Cuéntanos algo más (opcional)",enabled=!ocupado,lines=3) }
                    item { VetNotice("La clínica confirmará tu solicitud. Puedes reprogramarla o cancelarla con más de 2 horas de anticipación.") }
                    if(!seleccionValida || !horarioValido) item { VetNotice("La selección ya no está disponible. Vuelve y elige un servicio y horario válidos.",warning=true) }
                }
                error?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
            }
        }
        Surface(shadowElevation=8.dp,color=MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                VetButton(if(ocupado)"Enviando solicitud…" else if(paso==2)"Solicitar cita" else "Continuar",
                    { if(paso<2) paso++ else vm.confirmar() },Modifier.fillMaxWidth(),
                    !ocupado && aceptar && when(paso) {0->seleccionValida;1->seleccionValida && horarioValido;else->valido})
            }
        }
    }
}
