package com.tecsup.inventarioherramientas.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.notification.Notificador
import com.tecsup.inventarioherramientas.ui.UiState
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

data class ResumenAdmin(
    val totalGuardado: Int,
    val totalFavoritos: Int
)

class AdminViewModel(
    private val repositorio: ItemRepository,
    private val rol: Rol,
    private val notificador: Notificador
) : ViewModel() {

    // Ambos totales se calculan desde la misma lista de Room.
    val estado: StateFlow<UiState<ResumenAdmin>> =
        repositorio.items
            .map { herramientas ->
                val resultado: UiState<ResumenAdmin> =
                    UiState.Exito(
                        ResumenAdmin(
                            totalGuardado = herramientas.size,
                            totalFavoritos = herramientas.count {
                                it.esFavorito
                            }
                        )
                    )

                resultado
            }
            .catch {
                emit(
                    UiState.Error(
                        "No se pudieron consultar los totales."
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Cargando
            )

    private val _ocupado = MutableStateFlow(false)

    val ocupado: StateFlow<Boolean> =
        _ocupado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje.asStateFlow()

    fun sincronizar() {
        ejecutar {
            val resultado = withTimeoutOrNull(30_000L) {
                repositorio.refrescar()
            } ?: Result.failure<Int>(
                IOException(
                    "La actualización superó los 30 segundos."
                )
            )

            val cantidad = resultado.getOrThrow()

            notificador.mostrar(
                titulo = "Inventario actualizado",
                texto = "Se guardaron $cantidad elementos"
            )

            "Se guardaron $cantidad herramientas."
        }
    }

    fun limpiarCache() {
        ejecutar {
            repositorio.limpiarCache()

            "Caché local eliminada."
        }
    }

    private fun ejecutar(
        operacion: suspend () -> String
    ) {
        if (rol != Rol.ADMIN) {
            _mensaje.value =
                "Esta función es exclusiva del administrador."
            return
        }

        if (_ocupado.value) return

        _ocupado.value = true
        _mensaje.value = null

        viewModelScope.launch {
            try {
                _mensaje.value = operacion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = mensajeDeError(e)
            } finally {
                _ocupado.value = false
            }
        }
    }

    private fun mensajeDeError(error: Throwable): String {
        return when (error) {
            is IOException ->
                "No se pudo conectar. Los datos locales se conservan."

            is HttpException -> when (error.code()) {
                401 ->
                    "La sesión no está autorizada. Ingresa nuevamente."

                403 ->
                    "No tienes permiso. Revisa el perfil y las reglas."

                404 ->
                    "No se encontró el recurso solicitado."

                else ->
                    "El servidor respondió con error ${error.code()}."
            }

            is IllegalStateException ->
                error.message ?: "Los datos recibidos no son válidos."

            else ->
                "No se pudo completar la operación."
        }
    }

    class Factory(
        private val contenedor: AppContainer,
        private val rol: Rol
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (
                modelClass.isAssignableFrom(
                    AdminViewModel::class.java
                )
            ) {
                return AdminViewModel(
                    repositorio = contenedor.itemRepository,
                    rol = rol,
                    notificador = contenedor.notificador
                ) as T
            }

            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }
    }
}