package com.tecsup.inventarioherramientas.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.notification.Notificador
import com.tecsup.inventarioherramientas.ui.UiState
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException

class ListaViewModel(
    private val repositorio: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    val ultimaSincronizacion = repositorio.ultimaSincronizacion

    private data class ResultadoRefresco(
        val terminado: Boolean = false,
        val error: String? = null,
        val sinConexion: Boolean = false
    )

    private val resultadoRefresco =
        MutableStateFlow(ResultadoRefresco())

    private val _actualizando = MutableStateFlow(false)

    val actualizando: StateFlow<Boolean> =
        _actualizando.asStateFlow()

    private val _mensajeAccion = MutableStateFlow<String?>(null)

    val mensajeAccion: StateFlow<String?> =
        _mensajeAccion.asStateFlow()

    // La lista visible siempre procede de Room.
    val estado: StateFlow<UiState<List<Item>>> =
        combine(
            repositorio.items,
            resultadoRefresco
        ) { lista, resultado ->

            val nuevoEstado: UiState<List<Item>> = when {
                lista.isNotEmpty() ->
                    UiState.Exito(lista)

                !resultado.terminado ->
                    UiState.Cargando

                resultado.error != null ->
                    UiState.Error(resultado.error)

                else ->
                    UiState.Exito(emptyList())
            }

            nuevoEstado
        }.catch { error ->
            emit(
                UiState.Error(
                    mensajeDeError(error)
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Cargando
        )

    val aviso: StateFlow<String?> =
        combine(
            repositorio.items,
            resultadoRefresco
        ) { lista, resultado ->
            when {
                lista.isEmpty() || resultado.error == null ->
                    null

                resultado.sinConexion ->
                    "Sin conexión: mostrando datos guardados."

                else ->
                    "No se pudo actualizar: mostrando datos guardados. " +
                            resultado.error
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        refrescar()
    }

    fun refrescar() {
        if (_actualizando.value) return

        _actualizando.value = true
        _mensajeAccion.value = null
        resultadoRefresco.value = ResultadoRefresco()

        viewModelScope.launch {
            try {
                val resultado = withTimeoutOrNull(30_000L) {
                    repositorio.refrescar()
                } ?: Result.failure<Int>(
                    IOException(
                        "La actualización superó los 30 segundos."
                    )
                )

                resultado
                    .onSuccess { cantidad ->
                        resultadoRefresco.value =
                            ResultadoRefresco(
                                terminado = true
                            )

                        notificador.mostrar(
                            titulo = "Inventario actualizado",
                            texto = "Se guardaron $cantidad elementos"
                        )
                    }
                    .onFailure { error ->
                        resultadoRefresco.value =
                            ResultadoRefresco(
                                terminado = true,
                                error = mensajeDeError(error),
                                sinConexion = error is IOException
                            )
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                resultadoRefresco.value =
                    ResultadoRefresco(
                        terminado = true,
                        error = mensajeDeError(e),
                        sinConexion = e is IOException
                    )
            } finally {
                _actualizando.value = false
            }
        }
    }

    fun alternarFavorito(id: String) {
        viewModelScope.launch {
            _mensajeAccion.value = null

            try {
                repositorio.alternarFavorito(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensajeAccion.value =
                    "No se pudo actualizar el favorito."
            }
        }
    }

    private fun mensajeDeError(error: Throwable): String {
        return when (error) {
            is IOException ->
                "No se pudo conectar con el servidor."

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
                "Ocurrió un error al consultar el inventario."
        }
    }

    class Factory(
        private val contenedor: AppContainer
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (
                modelClass.isAssignableFrom(
                    ListaViewModel::class.java
                )
            ) {
                return ListaViewModel(
                    repositorio = contenedor.itemRepository,
                    notificador = contenedor.notificador
                ) as T
            }

            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }
    }
}