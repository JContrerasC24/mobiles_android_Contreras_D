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
@Entity(tableName = "favoritos_cuenta", primaryKeys = ["uid", "herramientaId"])
data class FavoritoCuenta(val uid: String, val herramientaId: String)

@Entity(tableName = "sincronizacion")
data class Sincronizacion(@PrimaryKey val id: Int = 1, val fecha: Long)
