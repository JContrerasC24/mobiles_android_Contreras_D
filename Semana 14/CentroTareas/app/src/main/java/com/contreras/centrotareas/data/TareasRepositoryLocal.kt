package com.contreras.centrotareas.data
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Implementación local del repositorio.
 *
 * Utiliza información ficticia para simular
 * operaciones que tardan varios segundos.
 */
class TareasRepositoryLocal : TareasRepository {

    /**
     * Simula la carga de un perfil.
     *
     * Tiempo aproximado: 2 segundos.
     */
    override suspend fun cargarPerfil(): String {

        delay(2000)

        return "Ana Torres (ficticia)"
    }

    /**
     * Simula la carga de pedidos.
     *
     * Tiempo aproximado: 3 segundos.
     */
    override suspend fun cargarPedidos(
        simularError: Boolean
    ): List<String> {

        delay(3000)

        if (simularError) {
            throw IOException("Sin conexión")
        }

        return listOf(
            "Pedido 101",
            "Pedido 102",
            "Pedido 103"
        )
    }

    /**
     * Pendiente del Paso 5 del laboratorio.
     *
     * Aquí implementaremos el cálculo
     * de números primos.
     */
    override suspend fun contarPrimos(
        hasta: Int
    ): Int {

        // TODO Paso 5: implementar cálculo de números primos.

        return 0
    }
}