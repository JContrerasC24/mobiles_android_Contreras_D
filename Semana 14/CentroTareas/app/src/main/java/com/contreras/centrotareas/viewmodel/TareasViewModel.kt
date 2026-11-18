
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
     * Estado interno del progreso.
     *
     * 0f representa el 0 %.
     * 1f representa el 100 %.
     */
    private val _progreso = MutableStateFlow(0f)

    /**
     * Estado público que observará la interfaz.
     */
    val progreso: StateFlow<Float> = _progreso.asStateFlow()
    /**
     * Guarda la tarea que se encuentra ejecutándose.
     */
    private var trabajo: Job? = null

    /**
     * Estado interno que indica si hay una tarea activa.
     */
    private val _corriendo = MutableStateFlow(false)

    /**
     * Estado público observado por la interfaz.
     */
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

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

    /**
     * Ejecuta una tarea con progreso.
     *
     * La operación dura aproximadamente cuatro segundos
     * y actualiza el avance desde 0 % hasta 100 %.
     */
    /**
     * Ejecuta una tarea con progreso y cancelación.
     *
     * Utiliza Job para controlar la corrutina.
     */
    fun tareaLarga() {

        // Evita iniciar dos tareas simultáneamente.
        if (_corriendo.value) {
            return
        }

        // Indica que comenzó la ejecución.
        _corriendo.value = true

        // Reinicia el progreso.
        _progreso.value = 0f

        // Actualiza el mensaje.
        _mensaje.value = "Trabajando..."

        // Guarda la referencia de la corrutina.
        trabajo = viewModelScope.launch {

            try {

                Log.d(
                    "HILO",
                    "Antes: ${Thread.currentThread().name}"
                )

                // Ejecuta el trabajo en segundo plano.
                withContext(Dispatchers.Default) {

                    Log.d(
                        "HILO",
                        "Dentro: ${Thread.currentThread().name}"
                    )

                    // Simula una tarea de cuatro segundos.
                    for (i in 1..100) {

                        delay(40)

                        // Actualiza el porcentaje.
                        _progreso.value = i / 100f
                    }
                }

                Log.d(
                    "HILO",
                    "Despues: ${Thread.currentThread().name}"
                )

                // Solo se ejecuta si la tarea termina normalmente.
                _mensaje.value = "Terminó"

            } finally {

                // Se ejecuta si la tarea termina o es cancelada.
                _corriendo.value = false

                // Libera la referencia al trabajo.
                trabajo = null
            }
        }
    }
    /**
     * Cancela la tarea que se encuentra ejecutándose.
     *
     * Conserva el porcentaje alcanzado.
     */
    fun cancelar() {

        // Verifica que exista una tarea activa.
        if (!_corriendo.value) {
            return
        }

        // Solicita la cancelación de la corrutina.
        trabajo?.cancel()

        // Actualiza el mensaje.
        _mensaje.value = "Cancelado"
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
     * Ejecuta dos cargas de manera secuencial.
     *
     * Primero carga el perfil.
     * Después carga los pedidos.
     *
     * Tiempo aproximado: 5 segundos.
     */
    fun cargarUnaPorUna() {

        // Evita iniciar otra carga mientras existe una activa.
        if (_carga.value == CargaUi.Cargando) {
            return
        }

        // Informa que comenzó la carga.
        _carga.value = CargaUi.Cargando

        // Ejecuta las operaciones dentro del ViewModel.
        viewModelScope.launch {

            // Registra el tiempo inicial.
            val inicio = System.currentTimeMillis()

            // Primera carga: aproximadamente 2 segundos.
            val perfil = repo.cargarPerfil()

            // Segunda carga: aproximadamente 3 segundos.
            val pedidos = repo.cargarPedidos(false)

            // Calcula el tiempo transcurrido.
            val tiempo = System.currentTimeMillis() - inicio

            // Comunica el resultado a la interfaz.
            _carga.value = CargaUi.Exito(
                modo = "Una por una",
                perfil = perfil,
                pedidos = pedidos.size,
                milisegundos = tiempo
            )
        }
    }
    /**
     * Ejecuta dos cargas de manera paralela.
     *
     * Utiliza async para iniciar ambas operaciones
     * y await para esperar sus resultados.
     *
     * Tiempo aproximado: 3 segundos.
     */
    fun cargarALaVez() {

        // Evita ejecutar varias cargas simultáneamente.
        if (_carga.value == CargaUi.Cargando) {
            return
        }

        // Actualiza el estado.
        _carga.value = CargaUi.Cargando

        // Inicia una corrutina asociada al ViewModel.
        viewModelScope.launch {

            // Registra el tiempo inicial.
            val inicio = System.currentTimeMillis()

            // Ejecuta ambas operaciones concurrentemente.
            val (perfil, pedidos) = coroutineScope {

                // Primera operación.
                val perfilAsync = async {

                    repo.cargarPerfil()
                }

                // Segunda operación.
                val pedidosAsync = async {

                    repo.cargarPedidos(false)
                }

                // Espera los resultados de ambas operaciones.
                Pair(
                    perfilAsync.await(),
                    pedidosAsync.await()
                )
            }

            // Calcula el tiempo transcurrido.
            val tiempo = System.currentTimeMillis() - inicio

            // Comunica el resultado a la interfaz.
            _carga.value = CargaUi.Exito(
                modo = "A la vez",
                perfil = perfil,
                pedidos = pedidos.size,
                milisegundos = tiempo
            )
        }
    }

    /**
     * Estado público del cálculo.
     */
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    /**
     * Guarda la corrutina que realiza
     * el cálculo de números primos.
     */
    private var trabajoPrimos: Job? = null

    /**
     * Inicia el cálculo de números primos.
     *
     * Recibe el número máximo hasta el cual
     * se realizará la búsqueda.
     */
    fun contar(hasta: Int) {

        // Ignora valores menores que 2.
        if (hasta < 2) {
            return
        }

        // Evita iniciar otro cálculo si ya existe uno activo.
        if (trabajoPrimos?.isActive == true) {
            return
        }

        // Actualiza el estado.
        _primos.value = PrimosUi.Calculando

        // Inicia el cálculo.
        trabajoPrimos = viewModelScope.launch {

            val inicio = System.currentTimeMillis()

            try {

                // Solicita el cálculo al Repository.
                val cantidad = repo.contarPrimos(hasta)

                // Calcula el tiempo transcurrido.
                val tiempo = System.currentTimeMillis() - inicio

                // Informa que el cálculo terminó.
                _primos.value = PrimosUi.Listo(
                    cantidad = cantidad,
                    milisegundos = tiempo
                )

            } catch (e: CancellationException) {

                // Informa que el usuario canceló el cálculo.
                _primos.value = PrimosUi.Cancelado

                // Conserva el comportamiento de cancelación.
                throw e

            } finally {

                // Libera la referencia al trabajo.
                trabajoPrimos = null
            }
        }
    }

    /**
     * Solicita la cancelación del cálculo
     * de números primos.
     */
    fun cancelarPrimos() {

        trabajoPrimos?.cancel()
    }

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