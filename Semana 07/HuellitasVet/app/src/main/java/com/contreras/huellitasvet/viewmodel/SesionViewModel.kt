package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.prefs.PreferenciasStore
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.model.Rol
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: VetRepository,
    private val preferencias: PreferenciasStore,
    private val inicializacion: Deferred<Unit>
) : ViewModel() {
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _inicializando = MutableStateFlow(true)
    val inicializando = _inicializando.asStateFlow()

    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()

    private val _rolSeleccionado = MutableStateFlow(Rol.CLIENTE)
    val rolSeleccionado = _rolSeleccionado.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = preferencias.modoOscuro.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    init {
        viewModelScope.launch {
            try {
                // Evita consultar usuarios antes de terminar la primera siembra.
                inicializacion.await()
                val id = preferencias.usuarioId.first()
                val rol = preferencias.perfilActivo.first()
                if (id > 0 && rol.isNotEmpty()) {
                    val usuario = repo.obtenerUsuario(id)
                    if (usuario != null && usuario.rol == rol &&
                        Rol.entries.any { it.name == rol }
                    ) {
                        _rolSeleccionado.value = Rol.valueOf(rol)
                        _usuarioActual.value = usuario
                    } else {
                        preferencias.cerrarSesion()
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo recuperar la sesión. Intenta ingresar nuevamente."
            } finally {
                _inicializando.value = false
            }
        }
    }

    fun seleccionarRol(rol: Rol) {
        if (_ocupado.value) return
        _rolSeleccionado.value = rol
        limpiarError()
    }

    fun limpiarError() {
        _error.value = null
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        if (_ocupado.value || _inicializando.value) return
        _error.value = null
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa el correo y la contraseña"
            return
        }
        _ocupado.value = true
        viewModelScope.launch {
            try {
                inicializacion.await()
                val usuario = repo.login(correo.trim(), clave)
                when {
                    usuario == null ->
                        _error.value = "Correo o contraseña incorrectos"
                    usuario.rol != rolSeleccionado.name ->
                        _error.value = "La cuenta no corresponde al perfil seleccionado"
                    else -> {
                        preferencias.guardarSesion(rolSeleccionado, usuario.id)
                        _rolSeleccionado.value = rolSeleccionado
                        _usuarioActual.value = usuario
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo iniciar sesión. Inténtalo nuevamente."
            } finally {
                _ocupado.value = false
            }
        }
    }

    fun cerrarSesion() {
        if (_ocupado.value) return
        _ocupado.value = true
        viewModelScope.launch {
            try {
                preferencias.cerrarSesion()
                _usuarioActual.value = null
                _error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo cerrar la sesión. Inténtalo nuevamente."
            } finally {
                _ocupado.value = false
            }
        }
    }

    // Cambio entre las cuentas locales de demostración exigido por el laboratorio.
    fun cambiarPerfil() {
        if (_ocupado.value) return
        val actual = _usuarioActual.value ?: return
        val otro = if (actual.rol == Rol.ADMIN.name) Rol.CLIENTE else Rol.ADMIN
        _ocupado.value = true
        viewModelScope.launch {
            try {
                val destino = checkNotNull(repo.obtenerUsuarioPorRol(otro.name)) {
                    "No hay una cuenta disponible para ese perfil."
                }
                preferencias.guardarSesion(otro, destino.id)
                _rolSeleccionado.value = otro
                _usuarioActual.value = destino
                _error.value = null
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _error.value = "No se pudo cambiar de perfil." }
            finally { _ocupado.value = false }
        }
    }
}
