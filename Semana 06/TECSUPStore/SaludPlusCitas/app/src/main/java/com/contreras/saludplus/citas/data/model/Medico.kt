package com.contreras.saludplus.citas.data.model

// Representa al médico disponible para atender citas.
data class Medico(
    // Identificador único del médico.
    val id: Int,

    // Relaciona al médico con una especialidad existente.
    val especialidadId: Int,

    // Nombre mostrado al seleccionar y confirmar citas.
    val nombre: String,

    // Información breve sobre el médico.
    val descripcion: String,

    // Valoración utilizada para ordenar médicos de mayor a menor.
    val calificacion: Double,

    // Precio de la consulta expresado en soles.
    val precioConsulta: Double
)