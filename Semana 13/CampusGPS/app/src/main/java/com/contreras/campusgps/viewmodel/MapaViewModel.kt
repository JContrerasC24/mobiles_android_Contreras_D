package com.contreras.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.campusgps.data.LugaresRepository
import com.contreras.campusgps.data.UbicacionRepository

import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel principal de CampusGPS.
 *
 * Se encarga de administrar los estados de la ubicación
 * y obtener los datos mediante los repositorios.
 *
 * No contiene componentes visuales ni utiliza Context.
 */
class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    // Estado de la búsqueda de ubicación.
    private val _estado = MutableStateFlow<UbicacionUi>(
        UbicacionUi.Inicial
    )

    // La interfaz observa este StateFlow para actualizarse.
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    // Obtiene inicialmente los lugares sin distancias calculadas.
    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map {
            LugarConDistancia(
                lugar = it,
                metros = null
            )
        }
    )

    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()

    // Identificador del lugar seleccionado.
    // Inicialmente no hay ningún lugar seleccionado.
    private val _seleccionado = MutableStateFlow<Int?>(null)

    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()

    /**
     * Solicita la ubicación actual del dispositivo.
     *
     * Debe ejecutarse después de comprobar
     * que el usuario concedió los permisos.
     */
    fun buscar() {

        // Informa a la interfaz que comenzó la búsqueda.
        _estado.value = UbicacionUi.Buscando

        // Ejecuta la solicitud dentro del ciclo de vida del ViewModel.
        viewModelScope.launch {

            ubicacionRepo.obtenerPosicion()

                .onSuccess { posicion ->

                    // La ubicación se obtuvo correctamente.
                    _estado.value = UbicacionUi.Lista(posicion)

                    // TODO Paso 5:
                    // Actualizar y ordenar las distancias.
                }

                .onFailure { error ->

                    // Comunica el problema a la interfaz.
                    _estado.value = UbicacionUi.Error(
                        mensajeDe(error)
                    )
                }
        }
    }

    /**
     * Informa que el usuario rechazó
     * los permisos de ubicación.
     */
    fun permisoDenegado() {

        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }

    // TODO Paso 6:
    // Implementar la selección de lugares.

    // TODO Paso 7:
    // Identificar el lugar más cercano.

    /**
     * Convierte las excepciones en mensajes
     * que pueda comprender el usuario.
     */
    private fun mensajeDe(error: Throwable): String {

        return when (error) {

            is SecurityException ->
                "Falta el permiso de ubicación"

            else ->
                error.message ?: "No se pudo obtener la ubicación"
        }
    }
}