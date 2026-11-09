package com.tecsup.inventarioherramientas.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val repositorio: ItemRepository
) : ViewModel() {

    val estado: StateFlow<UiState<List<Item>>> =
        repositorio.favoritos
            .map<List<Item>, UiState<List<Item>>> { favoritos ->
                UiState.Exito(favoritos)
            }
            .catch {
                emit(
                    UiState.Error(
                        "No se pudieron consultar los favoritos."
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Cargando
            )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun quitarFavorito(id: String) {
        viewModelScope.launch {
            _mensaje.value = null

            try {
                repositorio.alternarFavorito(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo quitar la herramienta de favoritos."
            }
        }
    }

    class Factory(
        private val contenedor: AppContainer
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
                return FavoritosViewModel(
                    contenedor.itemRepository
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}