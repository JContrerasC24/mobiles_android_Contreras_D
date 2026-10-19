package com.contreras.huellitasvet.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal fun validarServicio(s: Servicio): Map<String,String> = buildMap {
    if (s.nombre.isBlank()) put("nombre", "El nombre es obligatorio")
    if (s.duracionMin !in listOf(15,30,45,60)) put("duracion", "Selecciona 15, 30, 45 o 60 minutos")
    if (!s.precio.isFinite() || s.precio <= 0) put("precio", "Ingresa un precio mayor que 0")
    if (s.icono.isBlank()) put("icono", "Selecciona un ícono")
}
class ServiciosViewModel(private val repo: VetRepository) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _errores = MutableStateFlow<Map<String,String>>(emptyMap())
    val errores = _errores.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    private val _guardado = MutableStateFlow(false)
    val guardado = _guardado.asStateFlow()
    val servicios = repo.observarServicios().catch { _error.value = "No se pudieron cargar los servicios" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    fun limpiarError() { _error.value = null }
    fun prepararFormulario() { _guardado.value = false; _errores.value = emptyMap(); limpiarError() }
    private fun ejecutar(accion: suspend () -> Unit) {
        if (_ocupado.value) return
        _ocupado.value = true; limpiarError()
        viewModelScope.launch {
            try { accion() }
            catch (e: CancellationException) { throw e }
            catch (e: IllegalStateException) { _error.value = e.message }
            catch (e: Exception) { _error.value = "No se pudo guardar el cambio. Inténtalo nuevamente." }
            finally { _ocupado.value = false }
        }
    }
    fun alternar(s: Servicio) = ejecutar { repo.cambiarActivo(s.id, !s.activo) }
    fun guardar(s: Servicio) {
        _errores.value = validarServicio(s)
        if (_errores.value.isNotEmpty()) return
        ejecutar { repo.guardarServicio(s.copy(nombre=s.nombre.trim(), descripcion=s.descripcion.trim())); _guardado.value=true }
    }
    fun eliminar(s: Servicio) = ejecutar {
        if (repo.servicioTieneCitas(s.id)) {
            _error.value = "El servicio tiene citas y no se puede eliminar. Puedes desactivarlo."
        } else repo.eliminarServicio(s)
    }
}
