package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.Especie
import com.contreras.huellitasvet.model.RegistroPeso
import com.contreras.huellitasvet.model.Sexo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FormularioMascota(
    val nombre: String = "",
    val raza: String = "",
    val especie: String = "",
    val sexo: String = "",
    val anio: String = "",
    val peso: String = ""
)

internal fun validarMascota(f: FormularioMascota, anioActual: Int): Map<String, String> = buildMap {
    if (f.nombre.trim().length < 2) put("nombre", "Escribe al menos 2 caracteres")
    if (Especie.entries.none { it.name == f.especie }) put("especie", "Selecciona una especie")
    if (Sexo.entries.none { it.name == f.sexo }) put("sexo", "Selecciona el sexo")
    val anio = f.anio.toIntOrNull()
    if (anio == null || anio !in 1990..anioActual) put("anio", "Ingresa un año entre 1990 y $anioActual")
    val peso = f.peso.replace(',', '.').toDoubleOrNull()
    if (peso == null || !peso.isFinite() || peso <= 0) put("peso", "Ingresa un peso mayor que 0")
}

class MascotaViewModel(
    private val repo: VetRepository,
    private val dueno: Usuario,
    private val mascotaId: Int? = null
) : ViewModel() {
    private val _formulario = MutableStateFlow(FormularioMascota())
    val formulario = _formulario.asStateFlow()
    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores = _errores.asStateFlow()
    private val _mascota = MutableStateFlow<Mascota?>(null)
    val mascota = _mascota.asStateFlow()
    private val _cargando = MutableStateFlow(mascotaId != null)
    val cargando = _cargando.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private val _finalizado = MutableStateFlow(false)
    val finalizado = _finalizado.asStateFlow()
    private val historiales = mutableMapOf<Int, StateFlow<List<CitaConDetalle>>>()

    init {
        if (mascotaId != null) viewModelScope.launch {
            var formularioCargado = false
            try {
                repo.observarMascota(mascotaId).collect { m ->
                    _mascota.value = m?.takeIf { it.duenoId == dueno.id }
                    if (_mascota.value == null && !_finalizado.value) {
                        _mensaje.value = "La mascota no está disponible."
                    } else if (m != null && m.duenoId == dueno.id && !formularioCargado) {
                        _formulario.value = FormularioMascota(
                            m.nombre, m.raza, m.especie, m.sexo,
                            m.anioNacimiento.toString(), m.peso.toString()
                        )
                        formularioCargado = true
                    }
                    _cargando.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cargar la mascota."
                _cargando.value = false
            }
        }
    }

    fun actualizar(formulario: FormularioMascota) {
        if (_ocupado.value) return
        _formulario.value = formulario
        _errores.value = emptyMap()
        _mensaje.value = null
    }

    fun guardarFormulario() {
        val f = _formulario.value
        _errores.value = validarMascota(f, LocalDate.now().year)
        if (_errores.value.isNotEmpty()) return
        guardar(Mascota(
            id = mascotaId ?: 0, duenoId = dueno.id,
            nombre = f.nombre.trim(), raza = f.raza.trim(),
            especie = f.especie, sexo = f.sexo,
            anioNacimiento = f.anio.toInt(),
            peso = f.peso.replace(',', '.').toDouble()
        ))
    }

    fun guardar(mascota: Mascota) {
        if (_ocupado.value || _cargando.value || _finalizado.value) return
        val f = FormularioMascota(mascota.nombre, mascota.raza, mascota.especie,
            mascota.sexo, mascota.anioNacimiento.toString(), mascota.peso.toString())
        _errores.value = validarMascota(f, LocalDate.now().year)
        if (_errores.value.isNotEmpty()) return
        if (mascota.duenoId != dueno.id || mascota.id != (mascotaId ?: 0)) return
        _ocupado.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                if (mascotaId != null) {
                    val actual = repo.observarMascota(mascotaId).first()
                    check(actual?.duenoId == dueno.id) { "La mascota no está disponible." }
                }
                repo.guardarMascota(mascota.copy(nombre = mascota.nombre.trim(), raza = mascota.raza.trim()))
                _finalizado.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la mascota. Inténtalo nuevamente."
            } finally {
                _ocupado.value = false
            }
        }
    }

    fun eliminar(mascota: Mascota) {
        if (_ocupado.value || _cargando.value || _finalizado.value) return
        if (mascota.duenoId != dueno.id || mascota.id != mascotaId) return
        _ocupado.value = true
        _mensaje.value = null
        viewModelScope.launch {
            try {
                val actual = repo.observarMascota(mascota.id).first()
                check(actual?.duenoId == dueno.id)
                if (repo.mascotaTieneCitas(mascota.id)) {
                    _mensaje.value = "No se puede eliminar: la mascota tiene citas registradas."
                } else {
                    repo.eliminarMascota(mascota)
                    _finalizado.value = true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar la mascota. Comprueba si tiene citas."
            } finally {
                _ocupado.value = false
            }
        }
    }

    private val historialesPeso = mutableMapOf<Int, StateFlow<List<RegistroPeso>>>()

    fun historial(mascotaId: Int): StateFlow<List<CitaConDetalle>> =
        historiales.getOrPut(mascotaId) {
            repo.observarHistorial(mascotaId)
                .map { citas -> citas.filter { it.mascota.mascota.duenoId == dueno.id } }
                .catch { _mensaje.value = "No se pudo cargar el historial clínico." }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }

    fun historialPeso(mascotaId: Int): StateFlow<List<RegistroPeso>> =
        historialesPeso.getOrPut(mascotaId) {
            repo.observarHistorialPeso(mascotaId)
                .combine(repo.observarMascota(mascotaId)) { registros, m ->
                    if (m?.duenoId == dueno.id) registros else emptyList()
                }
                .catch { _mensaje.value = "No se pudo cargar la evolución del peso." }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }
}
