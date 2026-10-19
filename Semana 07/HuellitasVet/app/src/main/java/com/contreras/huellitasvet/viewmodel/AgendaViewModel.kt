package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.EstadoCita
import com.contreras.huellitasvet.model.ReglasAgenda
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {
    private val _fecha = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada = _fecha.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    val citas = _fecha.flatMapLatest { fecha ->
        repo.observarCitasPorFecha(fecha.toString())
            .map { it.sortedBy { detalle -> detalle.cita.hora } }
            .onStart { emit(emptyList()) }
            .catch { _mensaje.value = "No se pudo cargar la agenda." }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(fecha: LocalDate) { if (!_ocupado.value) { _fecha.value = fecha; _mensaje.value = null } }
    fun confirmar(cita: CitaConDetalle) = cambiar(cita, EstadoCita.CONFIRMADA)
    fun cancelar(cita: CitaConDetalle) = cambiar(cita, EstadoCita.CANCELADA)

    private fun cambiar(detalle: CitaConDetalle, destino: EstadoCita) {
        if (_ocupado.value) return
        if (!ReglasAgenda.permite(detalle.cita.estado, destino)) {
            _mensaje.value = "La cita ya no permite esta acción."
            return
        }
        _ocupado.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                repo.cambiarEstadoCita(detalle.cita.id, destino)
                _mensaje.value = if (destino == EstadoCita.CONFIRMADA) "Cita confirmada" else "Cita cancelada"
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value = "No se pudo actualizar la cita. Inténtalo nuevamente."
            } finally { _ocupado.value = false }
        }
    }
}
