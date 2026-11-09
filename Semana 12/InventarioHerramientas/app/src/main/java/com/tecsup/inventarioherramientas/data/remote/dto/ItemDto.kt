package com.tecsup.inventarioherramientas.data.remote.dto

data class ValorFirestoreDto(
    val stringValue: String? = null
)

data class ItemDto(
    val name: String? = null,
    val fields: Map<String, ValorFirestoreDto>? = null
)

data class ListaItemsDto(
    val documents: List<ItemDto>? = null,
    val nextPageToken: String? = null
)