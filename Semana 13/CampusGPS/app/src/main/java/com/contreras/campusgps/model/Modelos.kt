package com.contreras.campusgps.model

// Representa la ubicación geográfica mediante latitud y longitud.
data class Posicion(
    val latitud: Double,
    val longitud: Double
)

// Representa un lugar dentro del campus.
data class Lugar(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)

// Relaciona un lugar con la distancia respecto al usuario.
// metros será null mientras no conozcamos la ubicación.
data class LugarConDistancia(
    val lugar: Lugar,
    val metros: Float?
)

// Representa los diferentes estados de la ubicación.
sealed interface UbicacionUi {

    // Estado inicial: todavía no se solicita la ubicación.
    data object Inicial : UbicacionUi

    // Indica que se está buscando la ubicación.
    data object Buscando : UbicacionUi

    // La ubicación se obtuvo correctamente.
    data class Lista(
        val posicion: Posicion
    ) : UbicacionUi

    // Ocurrió un problema al obtener la ubicación.
    data class Error(
        val mensaje: String
    ) : UbicacionUi
}