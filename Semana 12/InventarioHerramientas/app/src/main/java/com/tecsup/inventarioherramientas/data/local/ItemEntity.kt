package com.tecsup.inventarioherramientas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "herramientas")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val condicion: String,
    val disponibilidad: String,
    val prestamoActivoId: String? = null,
    val esFavorito: Boolean = false
)