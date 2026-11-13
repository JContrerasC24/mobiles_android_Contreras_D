package com.tecsup.inventarioherramientas.data.repository
import com.tecsup.inventarioherramientas.domain.model.Movimiento
interface MovimientoRepository {

    suspend fun finalizarMantenimiento(id: String, condicion: String, nota: String): Result<String>

    suspend fun prestar(
        herramientaId: String,
        responsable: String,
        cobro: com.tecsup.inventarioherramientas.domain.model.CobroAlquiler,
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