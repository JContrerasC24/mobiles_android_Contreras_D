package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AtenderCitaViewModel(private val repo: VetRepository, private val citaId: Int) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores = _errores.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _finalizado = MutableStateFlow(false)
    val finalizado = _finalizado.asStateFlow()
    val cita = repo.observarCita(citaId)
        .onEach { if (it == null) _error.value = "La cita no está disponible." }
        .catch { _error.value = "No se pudo cargar la cita." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun registrar(diagnostico: String, tratamiento: String, peso: Double) {
        _errores.value = validarAtencion(diagnostico, peso)
        if (_errores.value.isNotEmpty()) return
        ejecutar { repo.registrarAtencion(citaId, diagnostico.trim(), tratamiento.trim(), peso) }
    }
    fun cancelar() = ejecutar { repo.cambiarEstadoCita(citaId, EstadoCita.CANCELADA) }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (_ocupado.value || _finalizado.value) return
        val actual = cita.value
        if (actual == null || !ReglasCitas.activa(actual.cita)) {
            _error.value = "Solo puedes atender o cancelar citas pendientes o confirmadas."
            return
        }
        _ocupado.value = true
        _error.value = null
        viewModelScope.launch {
            try { accion(); _finalizado.value = true }
            catch (e: CancellationException) { throw e }
            catch (e: IllegalStateException) { _error.value = e.message }
            catch (e: Exception) { _error.value = "No se pudo guardar la operación. Inténtalo nuevamente." }
            finally { _ocupado.value = false }
        }
    }
}
