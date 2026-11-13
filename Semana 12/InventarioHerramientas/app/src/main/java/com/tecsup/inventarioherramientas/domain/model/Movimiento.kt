package com.tecsup.inventarioherramientas.domain.model

data class Movimiento(
    val id: String,
    val herramientaId: String,
    val codigoHerramienta: String,
    val nombreHerramienta: String,
    val tipo: String,
    val responsable: String,
    val registradoPorUid: String,
    val fechaMillis: Long,
    val condicion: String,
    val observacion: String,
    val cobro: CobroAlquiler? = null,
    val prestamoId: String
)