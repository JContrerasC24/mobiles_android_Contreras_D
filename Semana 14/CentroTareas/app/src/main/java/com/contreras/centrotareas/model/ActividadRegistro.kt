package com.contreras.centrotareas.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Registro de actividad para el historial de operaciones realizadas.
 */
data class ActividadRegistro(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titulo: String,
    val descripcion: String,
    val estado: String, // "Completado", "Cancelado", "Error", "Ejecutando"
    val duracionMs: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val horaFormateada: String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
