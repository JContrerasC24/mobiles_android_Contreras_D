package com.contreras.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.campusgps.data.LugaresRepository
import com.contreras.campusgps.data.UbicacionRepository

import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi

// Utilidad encargada de calcular las distancias.
import com.contreras.campusgps.util.Distancia

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel principal de CampusGPS.
 *
 * Responsabilidades:
 *
 * 1. Solicitar la ubicación actual.
 * 2. Obtener los lugares del campus.
 * 3. Calcular las distancias.
 * 4. Ordenar los lugares por cercanía.
 * 5. Administrar los estados de la interfaz.
 *
 * Sigue la arquitectura MVVM.
 */
class MapaViewModel(

    private val ubicacionRepo: UbicacionRepository,

    private val lugaresRepo: LugaresRepository

) : ViewModel() {


    // =====================================================
    // ESTADO DE UBICACIÓN
    // =====================================================

    private val _estado = MutableStateFlow<UbicacionUi>(
        UbicacionUi.Inicial
    )

    val estado: StateFlow<UbicacionUi> =
        _estado.asStateFlow()


    // =====================================================
    // LUGARES DEL CAMPUS
    // =====================================================

    /*
     * Inicialmente obtenemos todos los lugares
     * registrados en LugaresRepositoryLocal.
     *
     * Todavía no conocemos la ubicación del usuario.
     * Por eso cada distancia comienza con null.
     */
    private val _lugares = MutableStateFlow(

        lugaresRepo.obtenerLugares().map { lugar ->

            LugarConDistancia(

                lugar = lugar,

                metros = null
            )
        }
    )

    /*
     * PantallaMapa observa esta lista.
     *
     * Cuando se calculen las distancias,
     * la interfaz recibirá los nuevos valores.
     */
    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()


    // =====================================================
    // LUGAR SELECCIONADO
    // =====================================================

    /*
     * Este estado se utilizará posteriormente
     * para seleccionar lugares desde la interfaz.
     */
    private val _seleccionado = MutableStateFlow<Int?>(null)

    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()


    // =====================================================
    // OBTENER UBICACIÓN
    // =====================================================

    /**
     * Solicita la ubicación actual del dispositivo.
     *
     * Debe ejecutarse después de verificar
     * que Android concedió los permisos.
     */
    fun buscar() {

        // Informa a la interfaz que comenzó la búsqueda.
        _estado.value = UbicacionUi.Buscando

        /*
         * viewModelScope permite ejecutar operaciones
         * utilizando corrutinas asociadas al ViewModel.
         */
        viewModelScope.launch {

            ubicacionRepo.obtenerPosicion()

                .onSuccess { posicion ->

                    // =====================================
                    // UBICACIÓN OBTENIDA
                    // =====================================

                    /*
                     * Ahora que conocemos la ubicación,
                     * podemos calcular la distancia
                     * hasta cada lugar del campus.
                     */

                    val lugaresConDistancia =
                        lugaresRepo.obtenerLugares().map { lugar ->

                            // Calcula la distancia en metros.
                            val metros = Distancia.calcularMetros(

                                origen = posicion,

                                destino = lugar
                            )

                            /*
                             * Asocia cada lugar
                             * con su distancia calculada.
                             */
                            LugarConDistancia(

                                lugar = lugar,

                                metros = metros
                            )
                        }


                    // =====================================
                    // ORDENAR LUGARES
                    // =====================================

                    /*
                     * Ordena los lugares de menor a mayor
                     * distancia respecto al usuario.
                     *
                     * Float.MAX_VALUE funciona como
                     * respaldo si alguna distancia es null.
                     */
                    val lugaresOrdenados =
                        lugaresConDistancia.sortedBy { elemento ->

                            elemento.metros ?: Float.MAX_VALUE
                        }


                    // =====================================
                    // ACTUALIZAR STATEFLOW
                    // =====================================

                    /*
                     * Guarda la lista ordenada.
                     *
                     * Los observadores de lugares recibirán
                     * automáticamente los nuevos datos.
                     */
                    _lugares.value = lugaresOrdenados


                    // =====================================
                    // ACTUALIZAR UBICACIÓN
                    // =====================================

                    /*
                     * Finalmente, informamos que
                     * la ubicación se obtuvo correctamente.
                     *
                     * Para este momento, las distancias
                     * ya se encuentran calculadas.
                     */
                    _estado.value = UbicacionUi.Lista(
                        posicion
                    )
                }

                .onFailure { error ->

                    // =====================================
                    // ERROR DE UBICACIÓN
                    // =====================================

                    _estado.value = UbicacionUi.Error(

                        mensajeDe(error)
                    )
                }
        }
    }


    // =====================================================
    // PERMISO DENEGADO
    // =====================================================

    /**
     * Informa que el usuario rechazó
     * el acceso a la ubicación.
     */
    fun permisoDenegado() {

        _estado.value = UbicacionUi.Error(

            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }


    // =====================================================
    // FUNCIONALIDADES POSTERIORES
    // =====================================================

    // TODO: Implementar la selección de lugares.
    // TODO: Identificar y resaltar el lugar más cercano.


    // =====================================================
    // MANEJO DE ERRORES
    // =====================================================

    /**
     * Convierte las excepciones en mensajes
     * comprensibles para el usuario.
     */
    private fun mensajeDe(
        error: Throwable
    ): String {

        return when (error) {

            is SecurityException ->

                "Falta el permiso de ubicación"

            else ->

                error.message
                    ?: "No se pudo obtener la ubicación"
        }
    }
}