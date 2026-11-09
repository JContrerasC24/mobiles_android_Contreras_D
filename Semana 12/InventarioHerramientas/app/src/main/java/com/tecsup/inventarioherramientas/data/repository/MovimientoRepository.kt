package com.tecsup.inventarioherramientas.data.repository

interface MovimientoRepository {

    suspend fun prestar(
        herramientaId: String,
        responsable: String,
        observacion: String
    ): Result<String>

    suspend fun devolver(
        herramientaId: String,
        condicionRetorno: String,
        observacion: String
    ): Result<String>
}