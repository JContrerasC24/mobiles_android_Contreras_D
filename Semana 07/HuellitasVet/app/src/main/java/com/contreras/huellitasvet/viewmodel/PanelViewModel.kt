package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PanelViewModel(private val repo: VetRepository) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val dias = flow {
        while (true) { emit(LocalDate.now()); delay(30_000) }
    }.distinctUntilChanged()
    private fun <T> Flow<T>.estado(inicial: T): StateFlow<T> =
        catch { _error.value = "No se pudieron cargar algunos datos del panel." }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), inicial)
    val citasHoy = dias.flatMapLatest { repo.observarCitasDeFecha(it.toString()) }.estado(0)
    val pendientes = repo.observarCantidadPorEstado(EstadoCita.PENDIENTE).estado(0)
    val atendidas = repo.observarCantidadPorEstado(EstadoCita.ATENDIDA).estado(0)
    val ingresosMes = dias.map { it.withDayOfMonth(1).toString() }.distinctUntilChanged()
        .flatMapLatest { repo.observarIngresosDesde(it) }.estado(0.0)
    val porEstado = combine(EstadoCita.entries.map { estado ->
        repo.observarCantidadPorEstado(estado).map {
            DatoGrafico(estado.name.lowercase().replaceFirstChar { c -> c.titlecase() }, it)
        }
    }) { it.toList() }.estado(emptyList())
    val porServicio = repo.observarCitasPorServicio()
        .map { lista -> lista.map { DatoGrafico(it.nombre, it.cantidad) } }.estado(emptyList())
}
