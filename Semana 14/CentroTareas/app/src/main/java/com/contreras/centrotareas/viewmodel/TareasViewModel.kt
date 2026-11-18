package com.contreras.centrotareas.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.contreras.centrotareas.data.TareasRepository
import com.contreras.centrotareas.model.ActividadRegistro
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
 * el progreso, las cancelaciones, los errores y el historial de actividad.
 *
 * Arquitectura: MVVM + Repository.
 */
class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // ==========================================
    // HISTORIAL DE ACTIVIDAD
    // ==========================================
    private val _historial = MutableStateFlow<List<ActividadRegistro>>(emptyList())
    val historial: StateFlow<List<ActividadRegistro>> = _historial.asStateFlow()

    private fun registrarActividad(
        titulo: String,
        descripcion: String,
        estado: String,
        duracionMs: Long? = null
    ) {
        val nuevoRegistro = ActividadRegistro(
            titulo = titulo,
            descripcion = descripcion,
            estado = estado,
            duracionMs = duracionMs
        )
        _historial.value = listOf(nuevoRegistro) + _historial.value
    }

    fun limpiarHistorial() {
        _historial.value = emptyList()
    }


    // ==========================================
    // PARTE A: TAREA CON PROGRESO
    // ==========================================

    // Mensaje de la tarea.
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Progreso desde 0f hasta 1f.
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    // Indica si la tarea está ejecutándose.
    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

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

        val inicio = System.currentTimeMillis()

        // Ejecuta la tarea dentro del ViewModel.
        trabajo = viewModelScope.launch {

            try {
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")

                // Ejecuta la tarea en Dispatchers.Default.
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")

                    // Actualiza el progreso.
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }

                Log.d("HILO", "Despues: ${Thread.currentThread().name}")

                // La tarea terminó correctamente.
                _mensaje.value = "Terminó"
                val duracion = System.currentTimeMillis() - inicio
                registrarActividad(
                    titulo = "Tarea Larga",
                    descripcion = "Procesamiento completado al 100%",
                    estado = "Completado",
                    duracionMs = duracion
                )

            } catch (e: CancellationException) {
                _mensaje.value = "Cancelado"
                val duracion = System.currentTimeMillis() - inicio
                registrarActividad(
                    titulo = "Tarea Larga",
                    descripcion = "Cancelada por el usuario en el ${(_progreso.value * 100).toInt()}%",
                    estado = "Cancelado",
                    duracionMs = duracion
                )
                throw e
            } finally {
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
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

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
                val tiempo = System.currentTimeMillis() - inicio

                // Comunica el resultado.
                _primos.value = PrimosUi.Listo(
                    cantidad = cantidad,
                    milisegundos = tiempo
                )

                registrarActividad(
                    titulo = "Cálculo de Primos",
                    descripcion = "Encontrados $cantidad primos hasta el número $hasta",
                    estado = "Completado",
                    duracionMs = tiempo
                )

            } catch (e: CancellationException) {
                val tiempo = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Cancelado
                registrarActividad(
                    titulo = "Cálculo de Primos",
                    descripcion = "Cálculo cancelado antes de finalizar (hasta $hasta)",
                    estado = "Cancelado",
                    duracionMs = tiempo
                )
                throw e
            } finally {
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
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()


    // ==========================================
    // PARTE D: SIMULACIÓN DE ERRORES
    // ==========================================

    // Indica si debemos simular un error.
    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()


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

        if (_carga.value == CargaUi.Cargando) {
            return
        }

        val errorActivo = _simularError.value
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {
            val inicio = System.currentTimeMillis()

            try {
                val perfil = repo.cargarPerfil()
                val pedidos = repo.cargarPedidos(simularError = errorActivo)
                val tiempo = System.currentTimeMillis() - inicio

                _carga.value = CargaUi.Exito(
                    modo = "Una por una",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = tiempo
                )

                registrarActividad(
                    titulo = "Carga Secuencial",
                    descripcion = "Carga sucesiva completada ($perfil, ${pedidos.size} pedidos)",
                    estado = "Completado",
                    duracionMs = tiempo
                )

            } catch (e: CancellationException) {
                val tiempo = System.currentTimeMillis() - inicio
                registrarActividad(
                    titulo = "Carga Secuencial",
                    descripcion = "Operación cancelada",
                    estado = "Cancelado",
                    duracionMs = tiempo
                )
                throw e

            } catch (e: Exception) {
                val mensajeError = e.message ?: "Error desconocido"
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: $mensajeError"
                )

                registrarActividad(
                    titulo = "Carga Secuencial",
                    descripcion = "Falló: $mensajeError",
                    estado = "Error"
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

        if (_carga.value == CargaUi.Cargando) {
            return
        }

        val errorActivo = _simularError.value
        _carga.value = CargaUi.Cargando

        viewModelScope.launch {
            val inicio = System.currentTimeMillis()

            try {
                val (perfil, pedidos) = coroutineScope {
                    val perfilAsync = async { repo.cargarPerfil() }
                    val pedidosAsync = async { repo.cargarPedidos(simularError = errorActivo) }

                    Pair(perfilAsync.await(), pedidosAsync.await())
                }

                val tiempo = System.currentTimeMillis() - inicio

                _carga.value = CargaUi.Exito(
                    modo = "A la vez",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = tiempo
                )

                registrarActividad(
                    titulo = "Carga Paralela",
                    descripcion = "Carga concurrente completada ($perfil, ${pedidos.size} pedidos)",
                    estado = "Completado",
                    duracionMs = tiempo
                )

            } catch (e: CancellationException) {
                val tiempo = System.currentTimeMillis() - inicio
                registrarActividad(
                    titulo = "Carga Paralela",
                    descripcion = "Operación cancelada",
                    estado = "Cancelado",
                    duracionMs = tiempo
                )
                throw e

            } catch (e: Exception) {
                val mensajeError = e.message ?: "Error desconocido"
                _carga.value = CargaUi.Error(
                    mensaje = "No se pudo cargar: $mensajeError"
                )

                registrarActividad(
                    titulo = "Carga Paralela",
                    descripcion = "Falló: $mensajeError",
                    estado = "Error"
                )
            }
        }
    }
}
