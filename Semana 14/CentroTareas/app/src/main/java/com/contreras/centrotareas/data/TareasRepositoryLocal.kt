package com.contreras.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

import java.io.IOException

/**
 * Implementación local del repositorio.
 *
 * Proporciona operaciones simuladas y realiza
 * cálculos matemáticos en segundo plano.
 *
 * Laboratorio 14: Hilos y corrutinas en Android.
 */
class TareasRepositoryLocal : TareasRepository {

    // ==========================================
    // CARGA SIMULADA DE PERFIL
    // ==========================================

    /**
     * Simula la carga de un perfil.
     *
     * Tiempo aproximado: 2 segundos.
     */
    override suspend fun cargarPerfil(): String {

        delay(2000)

        return "Ana Torres (ficticia)"
    }


    // ==========================================
    // CARGA SIMULADA DE PEDIDOS
    // ==========================================

    /**
     * Simula la carga de pedidos.
     *
     * Tiempo aproximado: 3 segundos.
     *
     * Permite generar un error cuando
     * simularError es verdadero.
     */
    override suspend fun cargarPedidos(
        simularError: Boolean
    ): List<String> {

        delay(3000)

        // Simula un problema de conexión.
        if (simularError) {

            throw IOException("Sin conexión")
        }

        // Devuelve pedidos ficticios.
        return listOf(
            "Pedido 101",
            "Pedido 102",
            "Pedido 103"
        )
    }


    // ==========================================
    // CÁLCULO DE NÚMEROS PRIMOS
    // ==========================================

    /**
     * Cuenta los números primos desde 2 hasta N.
     *
     * El cálculo se ejecuta utilizando
     * Dispatchers.Default porque consume CPU.
     *
     * ensureActive() permite cancelar
     * la operación durante su ejecución.
     */
    override suspend fun contarPrimos(
        hasta: Int
    ): Int = withContext(Dispatchers.Default) {

        var cantidad = 0

        // Recorre los números desde 2 hasta N.
        for (numero in 2..hasta) {

            // Comprueba si la corrutina sigue activa.
            ensureActive()

            // Verifica si el número es primo.
            if (esPrimo(numero)) {

                // Incrementa el contador.
                cantidad++
            }
        }

        // Devuelve la cantidad total.
        cantidad
    }


    // ==========================================
    // VERIFICACIÓN DE NÚMEROS PRIMOS
    // ==========================================

    /**
     * Determina si un número es primo.
     *
     * Un número primo es divisible
     * únicamente entre 1 y él mismo.
     */
    private fun esPrimo(n: Int): Boolean {

        // Los números menores que 2 no son primos.
        if (n < 2) {

            return false
        }

        // Primer divisor que vamos a comprobar.
        var divisor = 2

        /*
         * Solo necesitamos comprobar divisores
         * hasta la raíz cuadrada de n.
         *
         * Utilizamos Long para evitar desbordamientos
         * al multiplicar el divisor.
         */
        while (
            divisor.toLong() * divisor <= n.toLong()
        ) {

            // Si tiene un divisor exacto, no es primo.
            if (n % divisor == 0) {

                return false
            }

            divisor++
        }

        // Si no encontramos divisores, es primo.
        return true
    }
}