package com.contreras.centrotareas.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.centrotareas.data.TareasRepository
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel principal de CentroTareas.
 *
 * Gestiona las tareas en segundo plano, sus estados,
 * el progreso, las cancelaciones y los errores.
 *
 * Arquitectura: MVVM + Repository.
 */
class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // ==========================================
    // PARTE A: TAREA CON PROGRESO
    // ==========================================

    // Mensaje de la tarea.
    private val _mensaje = MutableStateFlow("Listo")

    val mensaje: StateFlow<String> =
        _mensaje.asStateFlow()

    // Progreso desde 0f hasta 1f.
    private val _progreso = MutableStateFlow(0f)

    val progreso: StateFlow<Float> =
        _progreso.asStateFlow()

    // Indica si la tarea está ejecutándose.
    private val _corriendo = MutableStateFlow(false)

    val corriendo: StateFlow<Boolean> =
        _corriendo.asStateFlow()

    // Referencia a la corrutina de progreso.
    private var trabajo: Job? = null


    /**
     * Inicia una tarea con progreso.
     *
     * Ejecuta 100 iteraciones de 40 milisegundos.
     * Duración aproximada: 4 segundos.
     */
    fun tareaLarga() {

        // Evita iniciar dos tareas simultáneamente.
        if (_corriendo.value) {
            return
        }

        // Actualiza los estados iniciales.
        _corriendo.value = true
        _progreso.value = 0f
        _mensaje.value = "Trabajando..."

        // Ejecuta la tarea dentro del ViewModel.
        trabajo = viewModelScope.launch {

            try {

                Log.d(
                    "HILO",
                    "Antes: ${Thread.currentThread().name}"
                )

                // Ejecuta la tarea en Dispatchers.Default.
                withContext(Dispatchers.Default) {

                    Log.d(
                        "HILO",
                        "Dentro: ${Thread.currentThread().name}"
                    )

                    // Actualiza el progreso.
                    for (i in 1..100) {

                        delay(40)

                        _progreso.value = i / 100f
                    }
                }

                Log.d(
                    "HILO",
                    "Despues: ${Thread.currentThread().name}"
                )

                // La tarea terminó correctamente.
                _mensaje.value = "Terminó"

            } finally {

                // Se ejecuta al terminar o cancelar.
                _corriendo.value = false

                trabajo = null
            }
        }
    }


    /**
     * Cancela la tarea con progreso.
     *
     * Conserva el porcentaje alcanzado.
     */
    fun cancelar() {

        if (!_corriendo.value) {
            return
        }

        trabajo?.cancel()

        _mensaje.value = "Cancelado"
    }


    // ==========================================
    // PARTE B: CONTAR NÚMEROS PRIMOS
    // ==========================================

    // Estado del cálculo.
    private val _primos = MutableStateFlow<PrimosUi>(
        PrimosUi.Inicial
    )

    val primos: StateFlow<PrimosUi> =
        _primos.asStateFlow()

    // Referencia a la corrutina de cálculo.
    private var trabajoPrimos: Job? = null


    /**
     * Inicia el cálculo de números primos.
     *
     * El Repository realiza el trabajo pesado
     * utilizando Dispatchers.Default.
     */
    fun contar(hasta: Int) {

        // Ignora números menores que 2.
        if (hasta < 2) {
            return
        }

        // Evita ejecutar dos cálculos simultáneamente.
        if (trabajoPrimos?.isActive == true) {
            return
        }

        // Informa que comenzó el cálculo.
        _primos.value = PrimosUi.Calculando

        // Inicia la corrutina.
        trabajoPrimos = viewModelScope.launch {

            val inicio = System.currentTimeMillis()

            try {

                // Solicita el cálculo al Repository.
                val cantidad = repo.contarPrimos(hasta)

                // Calcula el tiempo transcurrido.
                val tiempo =
                    System.currentTimeMillis() - inicio

                // Comunica el resultado.
                _primos.value = PrimosUi.Listo(
                    cantidad = cantidad,
                    milisegundos = tiempo
                )

            } catch (e: CancellationException) {

                // Informa que el usuario canceló el cálculo.
                _primos.value = PrimosUi.Cancelado

                // Mantiene el comportamiento de cancelación.
                throw e

            } finally {

                // Libera la referencia.
                trabajoPrimos = null
            }
        }
    }


    /**
     * Cancela el cálculo de números primos.
     */
    fun cancelarPrimos() {

        trabajoPrimos?.cancel()
    }


    // ==========================================
    // PARTE C: CARGAS DE DATOS
    // ==========================================

    // Estado de las cargas.
    private val _carga = MutableStateFlow<CargaUi>(
        CargaUi.Inicial
    )

    val carga: StateFlow<CargaUi> =
        _carga.asStateFlow()


    // ==========================================
    // PARTE D: SIMULACIÓN DE ERRORES
    // ==========================================

    // Indica si debemos simular un error.
    private val _simularError = MutableStateFlow(false)

    val simularError: StateFlow<Boolean> =
        _simularError.asStateFlow()


    /**
     * Activa o desactiva la simulación de errores.
     */
    fun cambiarSimularError(valor: Boolean) {

        _simularError.value = valor
    }


    // ==========================================
    // CARGA SECUENCIAL
    // ==========================================

    /**
     * Ejecuta las cargas una después de otra.
     *
     * Perfil: aproximadamente 2 segundos.
     * Pedidos: aproximadamente 3 segundos.
     *
     * Tiempo total aproximado: 5 segundos.
     */
    fun cargarUnaPorUna() {

        // Evita iniciar una segunda carga.
        if (_carga.value == CargaUi.Cargando) {
            return
        }

        // Guarda el valor del interruptor.
        val errorActivo = _simularError.value

        // Actualiza el estado.
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {

            val inicio = System.currentTimeMillis()

            try {

                // Primera carga.
                val perfil = repo.cargarPerfil()

                // Segunda carga.
                val pedidos = repo.cargarPedidos(
                    simularError = errorActivo
                )

                // Tiempo transcurrido.
                val tiempo =
                    System.currentTimeMillis() - inicio

                // Resultado correcto.
                _carga.value = CargaUi.Exito(
                    modo = "Una por una",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = tiempo
                )

            } catch (e: CancellationException) {

                // La cancelación debe propagarse.
                throw e

            } catch (e: Exception) {

                // Comunica el error a la pantalla.
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: " +
                            (e.message ?: "Error desconocido")
                )
            }
        }
    }


    // ==========================================
    // CARGA PARALELA
    // ==========================================

    /**
     * Ejecuta las dos cargas concurrentemente.
     *
     * Utiliza async y await.
     *
     * Tiempo total aproximado: 3 segundos.
     */
    fun cargarALaVez() {

        // Evita iniciar varias cargas simultáneamente.
        if (_carga.value == CargaUi.Cargando) {
            return
        }

        // Guarda el valor del interruptor.
        val errorActivo = _simularError.value

        // Actualiza el estado.
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {

            val inicio = System.currentTimeMillis()

            try {

                // Ejecuta ambas operaciones concurrentemente.
                val (perfil, pedidos) = coroutineScope {

                    // Primera operación.
                    val perfilAsync = async {

                        repo.cargarPerfil()
                    }

                    // Segunda operación.
                    val pedidosAsync = async {

                        repo.cargarPedidos(
                            simularError = errorActivo
                        )
                    }

                    // Espera los dos resultados.
                    Pair(
                        perfilAsync.await(),
                        pedidosAsync.await()
                    )
                }

                // Calcula el tiempo transcurrido.
                val tiempo =
                    System.currentTimeMillis() - inicio

                // Comunica el resultado.
                _carga.value = CargaUi.Exito(
                    modo = "A la vez",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = tiempo
                )

            } catch (e: CancellationException) {

                // Mantiene la cancelación estructurada.
                throw e

            } catch (e: Exception) {

                // Muestra el error sin cerrar la aplicación.
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: " +
                            (e.message ?: "Error desconocido")
                )
            }
        }
    }
}