package com.tecsup.inventarioherramientas.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.data.repository.RegistroHerramientaRepository
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class RegistroHerramientaViewModel(
    private val registroRepository: RegistroHerramientaRepository,
    private val itemRepository: ItemRepository,
    private val rol: Rol
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UiState<String?>>(UiState.Exito(null))

    val estado: StateFlow<UiState<String?>> =
        _estado.asStateFlow()

    fun registrar(
        codigo: String,
        nombre: String,
        descripcion: String,
        ubicacion: String,
        condicion: String
    ) {
        if (_estado.value is UiState.Cargando) return

        val mensajeGuardado =
            (_estado.value as? UiState.Exito)?.datos

        if (mensajeGuardado != null) return

        if (rol != Rol.ADMIN) {
            _estado.value = UiState.Error(
                "Solo el administrador puede registrar herramientas."
            )
            return
        }

        _estado.value = UiState.Cargando

        viewModelScope.launch {
            try {
                val codigoRegistrado = registroRepository.registrar(
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    condicion = condicion
                ).getOrThrow()

                // El registro ya fue confirmado en Firestore.
                // Ahora intentamos actualizar Room.
                val actualizada = try {
                    withTimeoutOrNull(30_000L) {
                        itemRepository.refrescar().isSuccess
                    } == true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    false
                }

                val mensaje = if (actualizada) {
                    "Herramienta $codigoRegistrado registrada correctamente."
                } else {
                    "Herramienta $codigoRegistrado registrada en Firestore. " +
                            "Actualiza el inventario cuando tengas conexión."
                }

                _estado.value = UiState.Exito(mensaje)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(
                    e.message ?: "No se pudo registrar la herramienta."
                )
            }
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
                    RegistroHerramientaViewModel::class.java
                )
            ) {
                return RegistroHerramientaViewModel(
                    registroRepository =
                        contenedor.registroHerramientaRepository,
                    itemRepository = contenedor.itemRepository,
                    rol = rol
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}