package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.relation.MascotaConDueno
import com.contreras.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

internal fun filtrarPacientes(lista: List<MascotaConDueno>, texto: String, especie: String?): List<MascotaConDueno> =
    lista.filter {
        (especie == null || it.mascota.especie == especie) &&
            (it.mascota.nombre.contains(texto.trim(), ignoreCase = true) ||
                it.dueno.nombre.contains(texto.trim(), ignoreCase = true))
    }.sortedBy { it.mascota.nombre.lowercase() }

@OptIn(ExperimentalCoroutinesApi::class)
class PacientesViewModel(repo: VetRepository, mascotaId: Int? = null) : ViewModel() {
    val texto = MutableStateFlow("")
    val especie = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val todas = repo.observarTodasLasMascotas()
        .catch { _error.value = "No se pudieron cargar los pacientes." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pacientes = combine(todas, texto, especie, ::filtrarPacientes)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val paciente = todas.map { lista -> lista.firstOrNull { it.mascota.id == mascotaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val historial = (mascotaId?.let(repo::observarHistorial) ?: flowOf(emptyList()))
        .catch { _error.value = "No se pudo cargar el historial." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val ultimasVisitas = todas.flatMapLatest { lista ->
        if (lista.isEmpty()) flowOf(emptyMap<Int, String?>())
        else combine(lista.map { paciente ->
            repo.observarHistorial(paciente.mascota.id).map { atenciones ->
                paciente.mascota.id to atenciones.firstOrNull()?.cita?.let { "${it.fecha} · ${it.hora}" }
            }
        }) { pares -> pares.toMap() }
    }.catch { _error.value = "No se pudieron cargar las últimas visitas." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
