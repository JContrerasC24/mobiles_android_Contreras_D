package com.tecsup.inventarioherramientas.data.repository
import com.tecsup.inventarioherramientas.domain.model.Movimiento
interface MovimientoRepository {

    suspend fun prestar(
        herramientaId: String,
        responsable: String,
        observacion: String
    ): Result<String>

    suspend fun obtenerHistorial(
        herramientaId: String
    ): Result<List<Movimiento>>

    suspend fun devolver(
        herramientaId: String,
        condicionRetorno: String,
        observacion: String
    ): Result<String>
}