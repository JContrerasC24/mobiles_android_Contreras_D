package com.tecsup.inventarioherramientas.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.domain.model.Sesion
import com.tecsup.inventarioherramientas.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repositorio: AuthRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)

    val estado: StateFlow<UiState<Sesion?>> =
        _estado.asStateFlow()

    init {
        recuperarSesion()
    }

    private fun recuperarSesion() {
        viewModelScope.launch {
            try {
                _estado.value =
                    UiState.Exito(repositorio.sesionActual())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(
                    "No se pudo recuperar la sesión. " +
                            "Comprueba tu conexión e inicia sesión nuevamente."
                )
            }
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        // Evita solicitudes repetidas.
        if (_estado.value is UiState.Cargando) return

        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = UiState.Error(
                "Completa el correo y la contraseña."
            )
            return
        }

        _estado.value = UiState.Cargando

        viewModelScope.launch {
            try {
                repositorio.iniciarSesion(correo, clave)
                    .onSuccess { sesion ->
                        _estado.value = UiState.Exito(sesion)
                    }
                    .onFailure { error ->
                        _estado.value = UiState.Error(
                            mensajeDeError(error)
                        )
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(
                    mensajeDeError(e)
                )
            }
        }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _estado.value = UiState.Exito(null)
    }

    private fun mensajeDeError(error: Throwable): String {
        return when (error) {
            is IllegalArgumentException,
            is IllegalStateException ->
                error.message ?: "No se pudo iniciar sesión."

            else ->
                "No se pudo iniciar sesión. Revisa el correo, " +
                        "la contraseña y la conexión a internet."
        }
    }

    class Factory(
        private val contenedor: AppContainer
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(
                    contenedor.authRepository
                ) as T
            }

            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }
    }
}