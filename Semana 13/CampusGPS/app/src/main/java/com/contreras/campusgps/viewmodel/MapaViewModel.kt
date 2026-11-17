package com.contreras.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.campusgps.data.LugaresRepository
import com.contreras.campusgps.data.UbicacionRepository

import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi

// Utilidad para calcular las distancias geográficas.
import com.contreras.campusgps.util.Distancia

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel principal de CampusGPS.
 *
 * Implementa la arquitectura MVVM.
 *
 * Responsabilidades:
 *
 * 1. Obtener la ubicación actual del dispositivo.
 * 2. Consultar los lugares registrados en el campus.
 * 3. Calcular la distancia hacia cada lugar.
 * 4. Ordenar los lugares del más cercano al más lejano.
 * 5. Permitir la selección de lugares.
 * 6. Administrar los estados que observa la interfaz.
 *
 * El ViewModel no contiene componentes visuales.
 */
class MapaViewModel(

    // Repositorio encargado de obtener la ubicación.
    private val ubicacionRepo: UbicacionRepository,

    // Repositorio encargado de proporcionar los lugares.
    private val lugaresRepo: LugaresRepository

) : ViewModel() {


    // =====================================================
    // 1. ESTADO DE LA UBICACIÓN
    // =====================================================

    /*
     * MutableStateFlow permite modificar el estado
     * de ubicación desde el ViewModel.
     *
     * Inicialmente no se ha solicitado ninguna ubicación.
     */
    private val _estado = MutableStateFlow<UbicacionUi>(
        UbicacionUi.Inicial
    )

    /*
     * Expone el estado para que PantallaMapa
     * pueda observar sus cambios.
     *
     * La interfaz no puede modificarlo directamente.
     */
    val estado: StateFlow<UbicacionUi> =
        _estado.asStateFlow()


    // =====================================================
    // 2. LUGARES DEL CAMPUS
    // =====================================================

    /*
     * Obtiene inicialmente los lugares registrados
     * en LugaresRepositoryLocal.
     *
     * Como todavía no conocemos la ubicación
     * del usuario, las distancias empiezan en null.
     */
    private val _lugares = MutableStateFlow<List<LugarConDistancia>>(

        lugaresRepo.obtenerLugares().map { lugar ->

            LugarConDistancia(

                lugar = lugar,

                metros = null
            )
        }
    )

    /*
     * Permite que la interfaz observe la lista
     * de lugares y sus distancias.
     *
     * Cuando los datos cambian, Compose puede
     * actualizar automáticamente la pantalla.
     */
    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()


    // =====================================================
    // 3. LUGAR SELECCIONADO
    // =====================================================

    /*
     * Guarda el identificador del lugar
     * que seleccionó el usuario.
     *
     * null significa que todavía
     * no se ha seleccionado ningún lugar.
     */
    private val _seleccionado = MutableStateFlow<Int?>(null)

    /*
     * PantallaMapa observa esta propiedad
     * para saber qué lugar debe resaltar
     * y hacia dónde mover la cámara.
     */
    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()


    // =====================================================
    // 4. OBTENER UBICACIÓN DEL DISPOSITIVO
    // =====================================================

    /**
     * Solicita la ubicación actual.
     *
     * Debe ejecutarse después de comprobar
     * que Android concedió los permisos necesarios.
     */
    fun buscar() {

        // Informa que comenzó una nueva búsqueda.
        _estado.value = UbicacionUi.Buscando

        /*
         * Reinicia la selección anterior.
         *
         * Así, cuando se solicite nuevamente
         * la ubicación, el mapa podrá comenzar
         * mostrando la posición actual del usuario.
         */
        _seleccionado.value = null

        /*
         * Ejecuta la búsqueda utilizando corrutinas.
         *
         * viewModelScope cancela automáticamente
         * las operaciones cuando el ViewModel
         * deja de utilizarse.
         */
        viewModelScope.launch {

            ubicacionRepo.obtenerPosicion()

                .onSuccess { posicion ->

                    // =========================================
                    // 4.1 UBICACIÓN OBTENIDA
                    // =========================================

                    /*
                     * Ya conocemos las coordenadas
                     * actuales del dispositivo.
                     *
                     * Ahora calcularemos la distancia
                     * hasta cada lugar del campus.
                     */


                    // =========================================
                    // 4.2 CALCULAR DISTANCIAS
                    // =========================================

                    val lugaresConDistancia =

                        lugaresRepo.obtenerLugares().map { lugar ->

                            /*
                             * Calcula la distancia en metros
                             * entre el usuario y el lugar.
                             */
                            val metros = Distancia.calcularMetros(

                                origen = posicion,

                                destino = lugar
                            )

                            /*
                             * Relaciona el lugar
                             * con la distancia calculada.
                             */
                            LugarConDistancia(

                                lugar = lugar,

                                metros = metros
                            )
                        }


                    // =========================================
                    // 4.3 ORDENAR POR DISTANCIA
                    // =========================================

                    /*
                     * sortedBy ordena los resultados
                     * de menor a mayor.
                     *
                     * El primer elemento será
                     * el lugar más cercano.
                     */
                    val lugaresOrdenados =

                        lugaresConDistancia.sortedBy { elemento ->

                            elemento.metros ?: Float.MAX_VALUE
                        }


                    // =========================================
                    // 4.4 ACTUALIZAR LISTA
                    // =========================================

                    /*
                     * Guarda los lugares con sus distancias
                     * y en el orden correspondiente.
                     *
                     * PantallaMapa y ListaLugares
                     * recibirán estos nuevos datos.
                     */
                    _lugares.value = lugaresOrdenados


                    // =========================================
                    // 4.5 ACTUALIZAR ESTADO
                    // =========================================

                    /*
                     * Informa a la interfaz que
                     * la ubicación se obtuvo correctamente.
                     *
                     * Las distancias ya se encuentran
                     * calculadas antes de publicar este estado.
                     */
                    _estado.value = UbicacionUi.Lista(
                        posicion
                    )
                }

                .onFailure { error ->

                    // =========================================
                    // 4.6 ERROR DE UBICACIÓN
                    // =========================================

                    /*
                     * Si ocurre un problema,
                     * comunica un mensaje comprensible
                     * a la interfaz.
                     */
                    _estado.value = UbicacionUi.Error(

                        mensajeDe(error)
                    )
                }
        }
    }


    // =====================================================
    // 5. SELECCIONAR UN LUGAR DEL CAMPUS
    // =====================================================

    /**
     * Permite seleccionar un lugar registrado.
     *
     * Se ejecuta cuando el usuario:
     *
     * - Presiona una tarjeta de ListaLugares.
     * - Selecciona un marcador de Google Maps.
     *
     * @param id Identificador del lugar seleccionado.
     */
    fun seleccionarLugar(id: Int) {

        // =============================================
        // 5.1 COMPROBAR SI EL LUGAR EXISTE
        // =============================================

        /*
         * any devuelve true si encuentra
         * un lugar que coincida con el identificador.
         */
        val existeLugar = _lugares.value.any { elemento ->

            elemento.lugar.id == id
        }


        // =============================================
        // 5.2 ACTUALIZAR SELECCIÓN
        // =============================================

        if (existeLugar) {

            /*
             * Guarda el identificador seleccionado.
             *
             * PantallaMapa observará este cambio
             * y podrá desplazar la cámara hacia
             * las coordenadas del lugar.
             */
            _seleccionado.value = id
        }
    }


    // =====================================================
    // 6. PERMISO DE UBICACIÓN DENEGADO
    // =====================================================

    /**
     * Comunica que el usuario rechazó
     * los permisos de ubicación.
     */
    fun permisoDenegado() {

        _estado.value = UbicacionUi.Error(

            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }


    // =====================================================
    // 7. FUNCIONALIDADES DEL SIGUIENTE COMMIT
    // =====================================================

    /*
     * TODO - Octavo commit:
     *
     * Identificar automáticamente el lugar
     * más cercano a la ubicación del usuario.
     *
     * También permitiremos resaltarlo
     * y desplazar la cámara hasta él.
     */


    // =====================================================
    // 8. MANEJO DE ERRORES
    // =====================================================

    /**
     * Convierte las excepciones en mensajes
     * comprensibles para el usuario.
     *
     * @param error Excepción recibida.
     *
     * @return Mensaje que se mostrará en la interfaz.
     */
    private fun mensajeDe(
        error: Throwable
    ): String {

        return when (error) {

            // Error relacionado con permisos.
            is SecurityException -> {

                "Falta el permiso de ubicación"
            }

            // Cualquier otro error.
            else -> {

                error.message
                    ?: "No se pudo obtener la ubicación"
            }
        }
    }
}