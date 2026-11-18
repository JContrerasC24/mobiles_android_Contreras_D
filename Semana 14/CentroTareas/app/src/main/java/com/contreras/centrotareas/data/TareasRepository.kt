package com.contreras.centrotareas.data

/**
 * Contrato que define las operaciones de datos
 * utilizadas por CentroTareas.
 */
interface TareasRepository {

    // Cuenta números primos hasta un número determinado.
    suspend fun contarPrimos(hasta: Int): Int

    // Simula la carga de un perfil.
    suspend fun cargarPerfil(): String

    // Simula la carga de pedidos.
    suspend fun cargarPedidos(
        simularError: Boolean
    ): List<String>
}