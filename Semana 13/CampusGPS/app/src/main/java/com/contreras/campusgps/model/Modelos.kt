package com.contreras.campusgps.model


// =====================================================
// 1. POSICIÓN GEOGRÁFICA
// =====================================================

/**
 * Representa una ubicación geográfica
 * mediante coordenadas de latitud y longitud.
 */
data class Posicion(

    val latitud: Double,

    val longitud: Double
)


// =====================================================
// 2. LUGAR DEL CAMPUS
// =====================================================

/**
 * Representa un lugar registrado en el campus.
 *
 * Cada lugar tiene:
 * - Identificador.
 * - Nombre.
 * - Categoría.
 * - Latitud.
 * - Longitud.
 */
data class Lugar(

    val id: Int,

    val nombre: String,

    val categoria: String,

    val latitud: Double,

    val longitud: Double
)


// =====================================================
// 3. LUGAR CON DISTANCIA
// =====================================================

/**
 * Relaciona un lugar con su distancia
 * respecto a la ubicación actual del usuario.
 *
 * metros será null mientras todavía
 * no conozcamos la ubicación.
 */
data class LugarConDistancia(

    val lugar: Lugar,

    val metros: Float?
)


// =====================================================
// 4. TIPOS DE ERRORES
// =====================================================

/**
 * Clasifica los problemas que pueden ocurrir
 * durante la obtención de la ubicación.
 *
 * Permite que la interfaz muestre una acción
 * diferente dependiendo del problema.
 */
enum class TipoErrorUbicacion {

    // El usuario rechazó el permiso de ubicación.
    PERMISO_DENEGADO,

    // Los servicios de ubicación están desactivados.
    GPS_APAGADO,

    // Google Play Services no está disponible.
    PLAY_SERVICES,

    // Ocurrió un problema diferente.
    GENERICO
}


// =====================================================
// 5. ESTADOS DE LA UBICACIÓN
// =====================================================

/**
 * Representa los diferentes estados
 * que puede tener la búsqueda de ubicación.
 *
 * La interfaz observa estos estados
 * mediante el ViewModel.
 */
sealed interface UbicacionUi {


    // -------------------------------------------------
    // ESTADO INICIAL
    // -------------------------------------------------

    /**
     * Todavía no se ha solicitado la ubicación.
     */
    data object Inicial : UbicacionUi


    // -------------------------------------------------
    // ESTADO BUSCANDO
    // -------------------------------------------------

    /**
     * Se está intentando obtener la ubicación.
     */
    data object Buscando : UbicacionUi


    // -------------------------------------------------
    // UBICACIÓN ENCONTRADA
    // -------------------------------------------------

    /**
     * La ubicación se obtuvo correctamente.
     *
     * Contiene las coordenadas actuales.
     */
    data class Lista(

        val posicion: Posicion

    ) : UbicacionUi


    // -------------------------------------------------
    // ERROR DE UBICACIÓN
    // -------------------------------------------------

    /**
     * Ocurrió un problema durante la búsqueda.
     *
     * mensaje: explicación para el usuario.
     *
     * tipo: identifica el problema y permite
     * mostrar las opciones correspondientes.
     */
    data class Error(

        val mensaje: String,

        val tipo: TipoErrorUbicacion =
            TipoErrorUbicacion.GENERICO

    ) : UbicacionUi
}