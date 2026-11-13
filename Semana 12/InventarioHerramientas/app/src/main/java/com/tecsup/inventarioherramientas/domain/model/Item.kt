package com.tecsup.inventarioherramientas.domain.model

data class Item(
    val id: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val condicion: String,
    val disponibilidad: String,
    val prestamoActivoId: String? = null,
    val esFavorito: Boolean = false
) {
    val listaParaPrestamo: Boolean
        get() = disponibilidad == "DISPONIBLE" && condicion == "BUENA" && prestamoActivoId.isNullOrBlank()
}