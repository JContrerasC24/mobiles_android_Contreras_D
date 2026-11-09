package com.tecsup.inventarioherramientas.data.mapper

import com.tecsup.inventarioherramientas.data.local.ItemEntity
import com.tecsup.inventarioherramientas.domain.model.Item

fun ItemEntity.aModelo(): Item {
    return Item(
        id = id,
        codigo = codigo,
        nombre = nombre,
        descripcion = descripcion,
        ubicacion = ubicacion,
        condicion = condicion,
        disponibilidad = disponibilidad,
        prestamoActivoId = prestamoActivoId,
        esFavorito = esFavorito
    )
}

fun Item.aEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        codigo = codigo,
        nombre = nombre,
        descripcion = descripcion,
        ubicacion = ubicacion,
        condicion = condicion,
        disponibilidad = disponibilidad,
        prestamoActivoId = prestamoActivoId,
        esFavorito = esFavorito
    )
}