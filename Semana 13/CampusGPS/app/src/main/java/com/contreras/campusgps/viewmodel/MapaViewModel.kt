package com.contreras.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.campusgps.data.LugaresRepository
import com.contreras.campusgps.data.UbicacionRepository

import com.contreras.campusgps.model.LugarConDistancia
import com.contreras.campusgps.model.UbicacionUi

// Utilidad para calcular distancias geográficas.
import com.contreras.campusgps.util.Distancia

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import com.contreras.campusgps.model.TipoErrorUbicacion
import com.contreras.campusgps.data.GpsDesactivadoException
import com.contreras.campusgps.data.PlayServicesNoDisponiblesException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.contreras.campusgps.util.FiltroLugares


/**
 * ViewModel principal de CampusGPS.
 *
 * Implementa la arquitectura MVVM.
 *
 * Responsabilidades:
 *
 * 1. Administrar los estados de ubicación.
 * 2. Obtener la ubicación actual del usuario.
 * 3. Obtener los lugares registrados en el campus.
 * 4. Calcular las distancias geográficas.
 * 5. Ordenar los lugares por distancia.
 * 6. Administrar la selección de lugares.
 * 7. Identificar el lugar más cercano.
 * 8. Gestionar errores.
 *
 * No contiene componentes visuales de Jetpack Compose.
 */
class MapaViewModel(

    // Repositorio para obtener la ubicación del dispositivo.
    private val ubicacionRepo: UbicacionRepository,

    // Repositorio para obtener los lugares del campus.
    private val lugaresRepo: LugaresRepository

) : ViewModel() {


    // =====================================================
    // 1. ESTADO DE UBICACIÓN
    // =====================================================

    /*
     * Almacena el estado actual de la ubicación.
     *
     * Inicialmente no hemos solicitado la ubicación.
     */
    private val _estado = MutableStateFlow<UbicacionUi>(
        UbicacionUi.Inicial
    )

    /*
     * Expone el estado para que PantallaMapa
     * pueda observar sus cambios.
     */
    val estado: StateFlow<UbicacionUi> =
        _estado.asStateFlow()


    // =====================================================
    // 2. LUGARES DEL CAMPUS
    // =====================================================

    /*
     * Obtiene los cinco lugares registrados
     * en LugaresRepositoryLocal.
     *
     * Inicialmente las distancias son null
     * porque todavía no conocemos nuestra ubicación.
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
     * Permite que la interfaz observe los lugares.
     *
     * Cuando las distancias sean calculadas,
     * Compose recibirá los nuevos resultados.
     */
    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()


    // =====================================================
    // 3. LUGAR SELECCIONADO
    // =====================================================

    /*
     * Almacena el identificador del lugar
     * seleccionado por el usuario.
     *
     * null indica que no existe una selección.
     */
    private val _seleccionado = MutableStateFlow<Int?>(null)

    /*
     * PantallaMapa observa este StateFlow
     * para resaltar una tarjeta y mover la cámara.
     */
    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()


    // =====================================================
    // 4. OBTENER UBICACIÓN ACTUAL
    // =====================================================

    /**
     * Solicita la ubicación actual del dispositivo.
     *
     * Debe ejecutarse después de comprobar
     * que el usuario concedió los permisos.
     */
    private val reproductor = ReproductorRecorrido(viewModelScope)
    val reproduccion = reproductor.estado
    fun iniciarRecorrido() { if (_recorrido.value != null) reproductor.iniciar() }
    fun cancelarAnimacion() { reproductor.cancelar() }
    fun pausarRecorrido() { reproductor.pausar() }
    fun detenerRecorrido() { reproductor.detener() }
    fun reiniciarRecorrido() { if (_recorrido.value != null) reproductor.reiniciar() }

    private val recorridosRepo = com.contreras.campusgps.data.RecorridosRepositoryDemostracion()
    private val _recorrido = MutableStateFlow<com.contreras.campusgps.model.Recorrido?>(null)
    val recorrido = _recorrido.asStateFlow()

    fun verRecorrido() {
        val origen = (_estado.value as? UbicacionUi.Lista)?.posicion ?: return
        val destino = _lugares.value.firstOrNull { it.lugar.id == _seleccionado.value }?.lugar ?: return
        reproductor.cancelar()
        _recorrido.value = recorridosRepo.crear(origen, destino)
    }

    fun cerrarRecorrido() { reproductor.cancelar(); _recorrido.value = null }

    private val _consulta = MutableStateFlow("")
    val consulta = _consulta.asStateFlow()
    private val _categoria = MutableStateFlow<String?>(null)
    val categoria = _categoria.asStateFlow()
    val categorias = lugaresRepo.obtenerLugares().map { it.categoria }.distinct()
    val resultados = combine(_lugares, _consulta, _categoria) { lugares, texto, categoria ->
        FiltroLugares.aplicar(lugares, texto, categoria)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    fun cambiarConsulta(texto: String) {
        cerrarRecorrido()
        _consulta.value = texto
        validarSeleccionVisible()
    }

    fun cambiarCategoria(categoria: String?) {
        cerrarRecorrido()
        _categoria.value = categoria?.takeIf { it in categorias }
        validarSeleccionVisible()
    }

    private fun validarSeleccionVisible() {
        if (FiltroLugares.aplicar(_lugares.value, _consulta.value, _categoria.value)
                .none { it.lugar.id == _seleccionado.value }) _seleccionado.value = null
    }

    private var busqueda: Job? = null

    fun buscar() {
        busqueda?.cancel()
        cerrarRecorrido()

        // Cambia el estado a Buscando.
        _estado.value = UbicacionUi.Buscando

        /*
         * Reinicia la selección anterior.
         *
         * Esto permite comenzar una nueva búsqueda
         * mostrando inicialmente la ubicación actual.
         */
        _seleccionado.value = null

        /*
         * Ejecuta la operación mediante corrutinas.
         *
         * viewModelScope administra el ciclo de vida
         * de las operaciones del ViewModel.
         */
        busqueda = viewModelScope.launch {

            ubicacionRepo.obtenerPosicion()

                .onSuccess { posicion ->

                    // =========================================
                    // 4.1 UBICACIÓN OBTENIDA
                    // =========================================

                    /*
                     * El repositorio devolvió correctamente
                     * las coordenadas del dispositivo.
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
                             * Calcula la distancia aproximada
                             * en metros entre el usuario
                             * y el lugar del campus.
                             */
                            val metros = Distancia.calcularMetros(

                                origen = posicion,

                                destino = lugar
                            )

                            /*
                             * Relaciona el lugar
                             * con su distancia calculada.
                             */
                            LugarConDistancia(

                                lugar = lugar,

                                metros = metros
                            )
                        }


                    // =========================================
                    // 4.3 ORDENAR LUGARES
                    // =========================================

                    /*
                     * Ordena los lugares por distancia.
                     *
                     * El primer elemento será el más cercano.
                     *
                     * El último elemento será el más lejano.
                     */
                    val lugaresOrdenados =

                        lugaresConDistancia.sortedBy { elemento ->

                            elemento.metros ?: Float.MAX_VALUE
                        }


                    // =========================================
                    // 4.4 ACTUALIZAR LISTA DE LUGARES
                    // =========================================

                    /*
                     * Guarda la lista con las distancias
                     * calculadas y ordenadas.
                     *
                     * PantallaMapa y ListaLugares
                     * observarán los cambios.
                     */
                    _lugares.value = lugaresOrdenados


                    // =========================================
                    // 4.5 ACTUALIZAR UBICACIÓN
                    // =========================================

                    /*
                     * Informa que la ubicación
                     * se obtuvo correctamente.
                     *
                     * Las distancias ya se encuentran
                     * calculadas en este momento.
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
                     * Convierte la excepción recibida
                     * en un mensaje para el usuario.
                     */
                    _estado.value = UbicacionUi.Error(

                        mensajeDe(error),
                        when (error) {
                            is SecurityException -> TipoErrorUbicacion.PERMISO_DENEGADO
                            is GpsDesactivadoException -> TipoErrorUbicacion.GPS_APAGADO
                            is PlayServicesNoDisponiblesException -> TipoErrorUbicacion.PLAY_SERVICES
                            else -> TipoErrorUbicacion.GENERICO
                        }
                    )
                }
        }
    }


    // =====================================================
    // 5. SELECCIONAR UN LUGAR DEL CAMPUS
    // =====================================================

    /**
     * Permite seleccionar un lugar del campus.
     *
     * Esta función es utilizada cuando el usuario:
     *
     * - Selecciona una tarjeta de ListaLugares.
     * - Presiona un marcador de Google Maps.
     *
     * @param id Identificador del lugar.
     */
    fun seleccionarLugar(id: Int) {

        // =============================================
        // 5.1 VERIFICAR QUE EL LUGAR EXISTA
        // =============================================

        /*
         * Busca si existe algún lugar
         * con el identificador recibido.
         */
        val existeLugar = _lugares.value.any { elemento ->

            elemento.lugar.id == id
        }


        // =============================================
        // 5.2 ACTUALIZAR SELECCIÓN
        // =============================================

        if (existeLugar) {
            if (_seleccionado.value != id) cerrarRecorrido()

            /*
             * Guarda el identificador seleccionado.
             *
             * La interfaz detectará este cambio
             * y podrá desplazar la cámara
             * hacia el lugar correspondiente.
             */
            _seleccionado.value = id
        }
    }


    // =====================================================
    // 6. SELECCIONAR EL LUGAR MÁS CERCANO
    // =====================================================

    /**
     * Identifica automáticamente el lugar
     * más cercano a la ubicación del usuario.
     *
     * Esta función pertenece al octavo commit.
     *
     * Como los lugares ya están ordenados
     * de menor a mayor distancia, utilizamos
     * el primer lugar que tenga una distancia válida.
     */
    fun seleccionarMasCercano() {

        // =============================================
        // 6.1 BUSCAR EL LUGAR MÁS CERCANO
        // =============================================

        /*
         * Obtiene el primer lugar cuya distancia
         * ya haya sido calculada.
         *
         * Si todavía no conocemos la ubicación,
         * el resultado será null.
         */
        val cercano = FiltroLugares.aplicar(_lugares.value, _consulta.value, _categoria.value).firstOrNull { elemento ->

            elemento.metros != null
        }


        // =============================================
        // 6.2 SELECCIONAR EL LUGAR
        // =============================================

        if (cercano != null) {

            /*
             * Reutiliza seleccionarLugar().
             *
             * Esto actualiza el identificador seleccionado
             * y permite que Google Maps navegue
             * hacia las coordenadas correspondientes.
             */
            seleccionarLugar(

                cercano.lugar.id
            )
        }
    }


    // =====================================================
    // 7. PERMISO DE UBICACIÓN DENEGADO
    // =====================================================

    /**
     * Comunica que el usuario rechazó
     * los permisos de ubicación.
     */
    fun permisoDenegado() {
        busqueda?.cancel()
        cerrarRecorrido()

        _estado.value = UbicacionUi.Error(

            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoErrorUbicacion.PERMISO_DENEGADO
        )
    }


    // =====================================================
    // 8. MANEJO DE ERRORES
    // =====================================================

    /**
     * Convierte las excepciones en mensajes
     * comprensibles para el usuario.
     *
     * @param error Excepción recibida.
     *
     * @return Mensaje que se mostrará en PantallaMapa.
     */
    private fun mensajeDe(
        error: Throwable
    ): String {

        return when (error) {

            // Error relacionado con los permisos.
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
