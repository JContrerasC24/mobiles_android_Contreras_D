package com.tecsup.inventarioherramientas.ui.movimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.data.repository.MovimientoRepository
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MovimientoViewModel(
    private val movimientoRepository: MovimientoRepository,
    private val itemRepository: ItemRepository,
    private val herramientaId: String,
    private val tipo: String,
    private val rol: Rol
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<String?>>(UiState.Exito(null))

    val estado: StateFlow<UiState<String?>> =
        _estado.asStateFlow()

    fun guardar(
        responsable: String,
        cobro: com.tecsup.inventarioherramientas.domain.model.CobroAlquiler,
        condicionRetorno: String,
        observacion: String
    ) {
        if (_estado.value is UiState.Cargando) return

        val confirmado = (_estado.value as? UiState.Exito)?.datos
        if (confirmado != null) return

        if (rol != Rol.ADMIN) {
            _estado.value = UiState.Error(
                "Solo el administrador puede registrar movimientos."
            )
            return
        }

        if (tipo !in setOf("SALIDA", "ENTRADA")) {
            _estado.value = UiState.Error(
                "El tipo de movimiento no es válido."
            )
            return
        }

        _estado.value = UiState.Cargando

        viewModelScope.launch {
            try {
                val resultado = if (tipo == "SALIDA") {
                    movimientoRepository.prestar(
                        herramientaId = herramientaId,
                        responsable = responsable,
                        cobro = cobro,
                        observacion = observacion
                    )
                } else {
                    movimientoRepository.devolver(
                        herramientaId = herramientaId,
                        condicionRetorno = condicionRetorno,
                        observacion = observacion
                    )
                }

                resultado.getOrThrow()

                // El movimiento ya está confirmado en Firestore.
                val actualizado = try {
                    withTimeoutOrNull(30_000L) {
                        itemRepository.refrescar().isSuccess
                    } == true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    false
                }

                val mensaje = if (tipo == "SALIDA") {
                    "Préstamo registrado correctamente."
                } else {
                    "Devolución registrada correctamente."
                }

                _estado.value = UiState.Exito(
                    if (actualizado) {
                        mensaje
                    } else {
                        "$mensaje Actualiza el inventario " +
                                "cuando tengas conexión."
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(
                    e.message ?: "No se pudo confirmar el movimiento."
                )
            }
        }
    }

    class Factory(
        private val contenedor: AppContainer,
        private val herramientaId: String,
        private val tipo: String,
        private val rol: Rol
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(MovimientoViewModel::class.java)) {
                return MovimientoViewModel(
                    movimientoRepository = contenedor.movimientoRepository,
                    itemRepository = contenedor.itemRepository,
                    herramientaId = herramientaId,
                    tipo = tipo,
                    rol = rol
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}