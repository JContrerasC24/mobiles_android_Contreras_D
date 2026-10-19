package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.ReglasCitas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MisCitasViewModel(private val repo: VetRepository, private val dueno: Usuario) : ViewModel() {
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private val _cancelando = MutableStateFlow(false)
    val cancelando = _cancelando.asStateFlow()
    val ahora = flow {
        while (true) { emit(LocalDateTime.now()); delay(1000) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now())

    private val grupos = combine(repo.observarCitasDeDueno(dueno.id), ahora) { citas, momento ->
        val (proximas, historial) = citas.partition { ReglasCitas.proxima(it.cita, momento) }
        proximas.sortedBy { ReglasCitas.momento(it.cita) } to
            historial.sortedByDescending { ReglasCitas.momento(it.cita) }
    }.catch { _mensaje.value = "No se pudieron cargar tus citas." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
            Pair(emptyList<CitaConDetalle>(), emptyList<CitaConDetalle>()))

    val proximas = grupos.map { it.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val historial = grupos.map { it.second }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cancelar(cita: CitaConDetalle) {
        if (_cancelando.value) return
        if (cita.mascota.mascota.duenoId != dueno.id ||
            !ReglasCitas.puedeCancelar(cita.cita, LocalDateTime.now())) {
            _mensaje.value = "Ya no puedes cancelar esta cita"
            return
        }
        _cancelando.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                repo.cancelarCitaDeDueno(cita.cita.id, dueno.id)
                _mensaje.value = "Cita cancelada"
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                _mensaje.value = e.message ?: "Ya no puedes cancelar esta cita"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cancelar la cita. Inténtalo nuevamente."
            } finally { _cancelando.value = false }
        }
    }
}
