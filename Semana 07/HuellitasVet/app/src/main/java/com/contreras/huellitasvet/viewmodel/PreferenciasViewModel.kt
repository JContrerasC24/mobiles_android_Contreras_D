package com.contreras.huellitasvet.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.prefs.PreferenciasStore
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.model.ReglasCitas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PreferenciasViewModel(repo: VetRepository, private val prefs: PreferenciasStore, val usuario: Usuario): ViewModel() {
    private val _error=MutableStateFlow<String?>(null)
    val error=_error.asStateFlow()
    private fun <T> Flow<T>.estado(inicial:T) = catch { _error.value="No se pudieron cargar los datos." }
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),inicial)
    val oscuro=prefs.modoOscuro.estado(false)
    val notificaciones=prefs.notificaciones.estado(true)
    val aceptarCitas=prefs.aceptarCitas.estado(true)
    val mascotas=repo.observarMascotasDeDueno(usuario.id).map { it.size }.estado(0)
    private val reloj=flow { while(true) { emit(LocalDateTime.now()); delay(30_000) } }
    val proximas=combine(repo.observarCitasDeDueno(usuario.id),reloj) { lista,ahora ->
        lista.count { ReglasCitas.proxima(it.cita,ahora) }
    }.estado(0)
    val atenciones=repo.observarCitasDeDueno(usuario.id).map { lista -> lista.count { it.cita.estado=="ATENDIDA" } }.estado(0)
    private fun guardar(accion:suspend ()->Unit) { viewModelScope.launch {
        try { accion(); _error.value=null }
        catch(e:CancellationException){throw e}
        catch(e:Exception){_error.value="No se pudo guardar la preferencia."}
    } }
    fun modoOscuro(valor:Boolean)=guardar { prefs.guardarModoOscuro(valor) }
    fun notificar(valor:Boolean)=guardar { prefs.guardarNotificaciones(valor) }
    fun aceptar(valor:Boolean)=guardar { prefs.guardarAceptarCitas(valor) }
}
