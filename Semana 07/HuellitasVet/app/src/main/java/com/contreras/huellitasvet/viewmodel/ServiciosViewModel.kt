package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repo: VetRepository
) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> =
        repo.observarServicios().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun alternar(servicio: Servicio) {
        viewModelScope.launch {
            try {
                repo.cambiarActivo(servicio.id, !servicio.activo)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar el servicio"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    // Paso 13: completar guardar y eliminar con sus validaciones.
}