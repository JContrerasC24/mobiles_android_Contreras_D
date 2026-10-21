package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.Horarios
import com.contreras.huellitasvet.model.ReglasCitas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MisCitasViewModel(private val repo: VetRepository, private val dueno: Usuario) : ViewModel() {
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private val _cancelando = MutableStateFlow(false)
    val cancelando = _cancelando.asStateFlow()
    private val _reprogramando = MutableStateFlow(false)
    val reprogramando = _reprogramando.asStateFlow()

    private val _citaAReprogramar = MutableStateFlow<CitaConDetalle?>(null)
    val citaAReprogramar = _citaAReprogramar.asStateFlow()

    private val _fechaReprogramar = MutableStateFlow(LocalDate.now())
    val fechaReprogramar = _fechaReprogramar.asStateFlow()

    private val _horaReprogramar = MutableStateFlow<String?>(null)
    val horaReprogramar = _horaReprogramar.asStateFlow()

    val horasOcupadasReprogramar = _citaAReprogramar.flatMapLatest { cita ->
        if (cita == null) flowOf(emptyList())
        else _fechaReprogramar.flatMapLatest { dia ->
            repo.observarHorasOcupadasExcluyendo(dia.toString(), cita.cita.id)
        }
    }.catch { _mensaje.value = "No se pudieron consultar los horarios." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        if (_cancelando.value || _reprogramando.value) return
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

    fun iniciarReprogramacion(cita: CitaConDetalle) {
        if (_reprogramando.value || _cancelando.value) return
        if (cita.mascota.mascota.duenoId != dueno.id ||
            !ReglasCitas.puedeReprogramar(cita.cita, LocalDateTime.now())) {
            _mensaje.value = "Ya no puedes reprogramar esta cita"
            return
        }
        _citaAReprogramar.value = cita
        _fechaReprogramar.value = LocalDate.parse(cita.cita.fecha)
        _horaReprogramar.value = cita.cita.hora
        _mensaje.value = null
    }

    fun elegirFechaReprogramacion(dia: LocalDate) {
        if (_reprogramando.value) return
        _fechaReprogramar.value = dia
        _horaReprogramar.value = null
    }

    fun elegirHoraReprogramacion(hora: String) {
        if (_reprogramando.value) return
        _horaReprogramar.value = hora
    }

    fun cancelarReprogramacion() {
        if (_reprogramando.value) return
        _citaAReprogramar.value = null
        _horaReprogramar.value = null
    }

    fun confirmarReprogramacion() {
        val cita = _citaAReprogramar.value ?: return
        val nuevaFecha = _fechaReprogramar.value
        val nuevaHora = _horaReprogramar.value
        val ocupadas = horasOcupadasReprogramar.value

        if (nuevaHora == null ||
            !Horarios.disponible(nuevaFecha, nuevaHora, ocupadas, LocalDateTime.now())) {
            _mensaje.value = "Selecciona un horario disponible."
            return
        }

        if (cita.mascota.mascota.duenoId != dueno.id ||
            !ReglasCitas.puedeReprogramar(cita.cita, LocalDateTime.now())) {
            _mensaje.value = "Ya no puedes reprogramar esta cita"
            return
        }

        _reprogramando.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                repo.reprogramarCitaDeDueno(
                    cita.cita.id,
                    dueno.id,
                    nuevaFecha.toString(),
                    nuevaHora
                )
                _mensaje.value = "Cita reprogramada exitosamente"
                _citaAReprogramar.value = null
                _horaReprogramar.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                _mensaje.value = e.message ?: "Ya no puedes reprogramar esta cita"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo reprogramar la cita. Inténtalo nuevamente."
            } finally {
                _reprogramando.value = false
            }
        }
    }
}
