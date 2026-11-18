package com.contreras.centrotareas.model

// Estados utilizados durante el cálculo de números primos.
sealed interface PrimosUi {

    // Todavía no se ha iniciado el cálculo.
    data object Inicial : PrimosUi

    // El cálculo está ejecutándose.
    data object Calculando : PrimosUi

    // El cálculo terminó correctamente.
    data class Listo(
        val cantidad: Int,
        val milisegundos: Long
    ) : PrimosUi

    // El usuario canceló el cálculo.
    data object Cancelado : PrimosUi
}

// Estados utilizados para las cargas de datos.
sealed interface CargaUi {

    // Estado inicial.
    data object Inicial : CargaUi

    // Los datos están cargándose.
    data object Cargando : CargaUi

    // Los datos se cargaron correctamente.
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi

    // Ocurrió un error durante la carga.
    data class Error(
        val mensaje: String
    ) : CargaUi
}