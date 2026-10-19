package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.EstadoCita
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal fun elegirProximaCita(
    citas: List<CitaConDetalle>,
    ahora: LocalDateTime
): CitaConDetalle? = citas.asSequence()
    .filter { it.cita.estado in setOf(EstadoCita.PENDIENTE.name, EstadoCita.CONFIRMADA.name) }
    .map { it to LocalDateTime.of(LocalDate.parse(it.cita.fecha), LocalTime.parse(it.cita.hora)) }
    .filter { (_, momento) -> !momento.isBefore(ahora) }
    .minByOrNull { (_, momento) -> momento }?.first

class InicioViewModel(repo: VetRepository, val dueno: Usuario) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val mascotas = repo.observarMascotasDeDueno(dueno.id)
        .catch { _error.value = "No se pudieron cargar tus mascotas." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Actualiza la próxima cita aunque Room no emita cambios al pasar el tiempo.
    private val reloj = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(30_000)
        }
    }

    val proximaCita = combine(repo.observarCitasDeDueno(dueno.id), reloj) { citas, ahora ->
        elegirProximaCita(citas, ahora)
    }.catch { _error.value = "No se pudo cargar tu próxima cita." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
