package com.tecsup.inventarioherramientas.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.MovimientoRepository
import com.tecsup.inventarioherramientas.domain.model.Movimiento
import com.tecsup.inventarioherramientas.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HistorialViewModel(
    private val repositorio: MovimientoRepository,
    private val herramientaId: String
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<List<Movimiento>>>(
            UiState.Cargando
        )

    val estado: StateFlow<UiState<List<Movimiento>>> =
        _estado.asStateFlow()

    private var trabajoCarga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        if (trabajoCarga?.isActive == true) return

        trabajoCarga = viewModelScope.launch {
            _estado.value = UiState.Cargando

            try {
                val resultado = withTimeoutOrNull(30_000L) {
                    repositorio.obtenerHistorial(herramientaId)
                }

                if (resultado == null) {
                    _estado.value = UiState.Error(
                        "La consulta superó los 30 segundos. " +
                                "Comprueba tu conexión."
                    )
                } else {
                    resultado
                        .onSuccess { movimientos ->
                            _estado.value =
                                UiState.Exito(movimientos)
                        }
                        .onFailure {
                            _estado.value = UiState.Error(
                                "No se pudo consultar el historial. " +
                                        "Comprueba la conexión y los permisos."
                            )
                        }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(
                    "Ocurrió un error al cargar el historial."
                )
            }
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
            if (modelClass.isAssignableFrom(HistorialViewModel::class.java)) {
                return HistorialViewModel(
                    repositorio = contenedor.movimientoRepository,
                    herramientaId = herramientaId
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}