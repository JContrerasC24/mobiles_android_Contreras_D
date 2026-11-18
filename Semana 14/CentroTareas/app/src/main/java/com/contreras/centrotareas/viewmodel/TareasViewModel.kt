
package com.contreras.centrotareas.viewmodel

import androidx.lifecycle.ViewModel

import com.contreras.centrotareas.data.TareasRepository
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel principal de CentroTareas.
 *
 * Administra los estados de las tareas y se comunica
 * con el Repository.
 *
 * Laboratorio 14: Hilos y corrutinas en Android.
 */
class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // ==========================================
    // PARTE A: TAREA CON PROGRESO
    // ==========================================

    /**
     * Estado interno del mensaje.
     */
    private val _mensaje = MutableStateFlow("Listo")

    /**
     * Estado público observado por la interfaz.
     */
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    /**
     * PASO 1: PROVOCAR EL CONGELAMIENTO
     *
     * Esta función demuestra qué ocurre cuando
     * una tarea pesada se ejecuta directamente
     * en el hilo principal de Android.
     *
     * IMPORTANTE:
     * Esta implementación es intencionalmente
     * incorrecta y será reemplazada en el Paso 2.
     */
    /**
     * Paso 2: ejecutar una tarea larga mediante corrutinas.
     *
     * La operación se realiza sin bloquear la interfaz
     * y permite seguir interactuando con la aplicación.
     */
    fun tareaLarga() {

        // Inicia una corrutina asociada al ciclo de vida del ViewModel.
        viewModelScope.launch {

            // Informa que comenzó la tarea.
            _mensaje.value = "Trabajando..."

            // Comprueba el hilo antes de la operación.
            Log.d(
                "HILO",
                "Antes: ${Thread.currentThread().name}"
            )

            // Ejecuta la espera utilizando Dispatchers.Default.
            withContext(Dispatchers.Default) {

                Log.d(
                    "HILO",
                    "Dentro: ${Thread.currentThread().name}"
                )

                // Suspende la corrutina durante cuatro segundos.
                delay(4000)
            }

            // Comprueba el hilo después de la operación.
            Log.d(
                "HILO",
                "Despues: ${Thread.currentThread().name}"
            )

            // Actualiza el mensaje al finalizar.
            _mensaje.value = "Terminó"
        }
    }

    /**
     * PASO 2:
     *
     * Reemplazar tareaLargaMala() por tareaLarga()
     * utilizando viewModelScope.launch,
     * withContext y Dispatchers.Default.
     */

    /**
     * PASO 3:
     *
     * Implementar el progreso mediante
     * StateFlow<Float>.
     *
     * El progreso deberá avanzar desde
     * 0f hasta 1f.
     */

    /**
     * PASO 4:
     *
     * Implementar:
     *
     * - Job para controlar la tarea.
     * - StateFlow<Boolean> para indicar si está corriendo.
     * - Función cancelar().
     * - Protección contra ejecuciones duplicadas.
     */


    // ==========================================
    // PARTE B: CONTAR NÚMEROS PRIMOS
    // ==========================================

    /**
     * Estado interno del cálculo de primos.
     */
    private val _primos = MutableStateFlow<PrimosUi>(
        PrimosUi.Inicial
    )

    /**
     * Estado público del cálculo.
     */
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    /**
     * PASO 5:
     *
     * Implementar contar(hasta: Int).
     *
     * El cálculo deberá ejecutarse utilizando
     * Dispatchers.Default.
     */

    /**
     * PASO 5:
     *
     * Implementar cancelarPrimos().
     */


    // ==========================================
    // PARTE C: DOS CARGAS DE DATOS
    // ==========================================

    /**
     * Estado interno de las cargas.
     */
    private val _carga = MutableStateFlow<CargaUi>(
        CargaUi.Inicial
    )

    /**
     * Estado público de las cargas.
     */
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    /**
     * PASO 6:
     *
     * Implementar cargarUnaPorUna().
     *
     * Primero cargará el perfil y después
     * los pedidos.
     */

    /**
     * PASO 6:
     *
     * Implementar cargarALaVez().
     *
     * Utilizará async y await para ejecutar
     * las dos cargas en paralelo.
     */


    // ==========================================
    // PARTE D: MANEJO DE ERRORES
    // ==========================================

    /**
     * PASO 7:
     *
     * Implementar StateFlow<Boolean>
     * para simular errores.
     */

    /**
     * PASO 7:
     *
     * Implementar cambiarSimularError(valor).
     */

    /**
     * PASO 7:
     *
     * Implementar:
     *
     * - Manejo de excepciones.
     * - Estado de error.
     * - Reintento de la carga.
     */
}