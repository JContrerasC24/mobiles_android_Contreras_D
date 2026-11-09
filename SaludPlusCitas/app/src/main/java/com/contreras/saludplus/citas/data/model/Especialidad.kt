package com.contreras.saludplus.citas.data.model

// Representa una especialidad disponible en la clínica.
data class Especialidad(
    // Identificador único de la especialidad.
    val id: Int,

    // Nombre mostrado en las listas y búsquedas.
    val nombre: String,

    // Descripción breve de la atención ofrecida.
    val descripcion: String
)