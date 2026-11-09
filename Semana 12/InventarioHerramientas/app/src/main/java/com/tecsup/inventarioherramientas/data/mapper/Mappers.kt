package com.tecsup.inventarioherramientas.data.mapper

import com.tecsup.inventarioherramientas.data.local.ItemEntity
import com.tecsup.inventarioherramientas.data.remote.dto.ItemDto
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

fun ItemDto.aEntity(): ItemEntity {
    val idDocumento = name
        ?.substringAfterLast('/')
        ?.takeIf { it.isNotBlank() }
        ?: error("La herramienta no tiene un identificador válido")

    fun textoObligatorio(campo: String): String {
        return fields
            ?.get(campo)
            ?.stringValue
            ?.takeIf { it.isNotBlank() }
            ?: error(
                "Falta el campo '$campo' en la herramienta $idDocumento"
            )
    }

    return ItemEntity(
        id = idDocumento,
        codigo = textoObligatorio("codigo"),
        nombre = textoObligatorio("nombre"),
        descripcion = fields
            ?.get("descripcion")
            ?.stringValue
            .orEmpty(),
        ubicacion = textoObligatorio("ubicacion"),
        condicion = textoObligatorio("condicion"),
        disponibilidad = textoObligatorio("disponibilidad"),
        prestamoActivoId = fields
            ?.get("prestamoActivoId")
            ?.stringValue
            ?.takeIf { it.isNotBlank() },
        esFavorito = false
    )
}