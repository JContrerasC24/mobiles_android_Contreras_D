package com.tecsup.inventarioherramientas.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.ui.UiState
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

class DetalleViewModel(
    private val repositorio: ItemRepository,
    private val herramientaId: String
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<Item>>(UiState.Cargando)

    val estado: StateFlow<UiState<Item>> =
        _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private var trabajoCarga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        if (trabajoCarga?.isActive == true) return

        trabajoCarga = viewModelScope.launch {
            _estado.value = UiState.Cargando
            _aviso.value = null

            try {
                val resultado = withTimeoutOrNull(30_000L) {
                    repositorio.obtenerDetalle(herramientaId)
                } ?: Result.failure<Item>(
                    IOException("La consulta superó los 30 segundos.")
                )

                val herramienta = resultado.getOrNull()

                if (herramienta != null) {
                    _estado.value = UiState.Exito(herramienta)
                } else {
                    val error = resultado.exceptionOrNull()
                        ?: IllegalStateException(
                            "No se pudo obtener el detalle."
                        )

                    mostrarErrorOContenidoLocal(error)
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

    private suspend fun mostrarErrorOContenidoLocal(
        error: Throwable
    ) {
        // Usamos la copia local ante un fallo de conexión.
        if (error is IOException) {
            val guardada = repositorio.items
                .first()
                .firstOrNull { it.id == herramientaId }

            if (guardada != null) {
                _estado.value = UiState.Exito(guardada)
                _aviso.value =
                    "No se pudo conectar: mostrando el detalle guardado."
                return
            }
        }

        _estado.value = UiState.Error(
            mensajeDeError(error)
        )
    }

    private fun mensajeDeError(error: Throwable): String {
        return when (error) {
            is IOException ->
                "No hay conexión o el servidor no respondió. " +
                        "No se pudo recuperar un detalle local."

            is HttpException -> when (error.code()) {
                401 -> "La sesión no está autorizada."
                403 -> "No tienes permiso para consultar esta herramienta."
                404 -> "La herramienta ya no existe en el servidor."
                else -> "Error del servidor: ${error.code()}."
            }

            is IllegalStateException ->
                error.message ?: "Los datos no son válidos."

            else ->
                "No se pudo cargar el detalle."
        }
    }

    class Factory(
        private val contenedor: AppContainer,
        private val herramientaId: String
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
                return DetalleViewModel(
                    repositorio = contenedor.itemRepository,
                    herramientaId = herramientaId
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}