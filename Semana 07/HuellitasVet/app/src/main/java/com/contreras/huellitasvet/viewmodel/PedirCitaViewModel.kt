package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.Horarios
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class Disponibilidad(val fecha: LocalDate, val horas: List<String>? = null)

@OptIn(ExperimentalCoroutinesApi::class)
class PedirCitaViewModel(
    private val repo: VetRepository, private val dueno: Usuario,
    mascotaInicial: Int? = null
) : ViewModel() {
    val mascota = MutableStateFlow(mascotaInicial)
    val servicio = MutableStateFlow<Int?>(null)
    val fecha = MutableStateFlow(LocalDate.now())
    val hora = MutableStateFlow<String?>(null)
    val nota = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _creada = MutableStateFlow<Long?>(null)
    val creada = _creada.asStateFlow()
    val mascotas = repo.observarMascotasDeDueno(dueno.id)
        .catch { _error.value = "No se pudieron cargar las mascotas." }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val servicios = repo.observarServiciosActivos()
        .catch { _error.value = "No se pudieron cargar los servicios." }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val disponibilidad = fecha.flatMapLatest { dia ->
        repo.observarHorasOcupadas(dia.toString())
            .map { Disponibilidad(dia, it) }
            .onStart { emit(Disponibilidad(dia)) }
            .catch { _error.value = "No se pudieron consultar los horarios." }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Disponibilidad(fecha.value))
    val horasOcupadas = disponibilidad.map { it.horas.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val ahora = flow {
        while (true) { emit(LocalDateTime.now()); delay(30_000) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now())

    fun elegirFecha(dia: LocalDate) {
        if (_ocupado.value) return
        fecha.value = dia
        hora.value = null
        _error.value = null
    }

    fun confirmar() {
        if (_ocupado.value || _creada.value != null) return
        val mascotaId = mascota.value
        val servicioId = servicio.value
        val dia = fecha.value
        val turno = hora.value
        val ocupadas = disponibilidad.value
        if (mascotaId == null || servicioId == null || turno == null ||
            mascotas.value.none { it.id == mascotaId } ||
            servicios.value.none { it.id == servicioId } ||
            ocupadas.fecha != dia || ocupadas.horas == null ||
            !Horarios.disponible(dia, turno, ocupadas.horas, LocalDateTime.now())) {
            _error.value = "Selecciona tu mascota, un servicio activo y un horario disponible."
            return
        }
        _ocupado.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                check(repo.observarMascotasDeDueno(dueno.id).first().any { it.id == mascotaId }) {
                    "La mascota ya no está disponible."
                }
                check(repo.observarServiciosActivos().first().any { it.id == servicioId }) {
                    "El servicio ya no está disponible."
                }
                val actuales = repo.observarHorasOcupadas(dia.toString()).first()
                check(Horarios.disponible(dia, turno, actuales, LocalDateTime.now())) {
                    "El horario ya no está disponible. Elige otro."
                }
                _creada.value = repo.crearCita(mascotaId, servicioId, dia.toString(), turno, nota.value.trim())
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                hora.value = null
                _error.value = e.message ?: "No se pudo reservar el horario."
            } catch (e: Exception) {
                _error.value = "No se pudo guardar la cita. Inténtalo nuevamente."
            } finally { _ocupado.value = false }
        }
    }
}
