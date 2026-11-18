package com.contreras.campusgps.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EstadoReproduccion { LISTO, REPRODUCIENDO, PAUSADO, DETENIDO, FINALIZADO }
data class Reproduccion(val progreso: Float = 0f, val estado: EstadoReproduccion = EstadoReproduccion.LISTO)

/** Una sola tarea, reloj monotónico y ningún vínculo con la ubicación GPS. */
class ReproductorRecorrido(
    private val scope: CoroutineScope,
    private val duracionMs: Long = 12000L,
    private val reloj: () -> Long = System::nanoTime,
    private val esperarFotograma: suspend () -> Unit = { delay(16) }
) {
    init { require(duracionMs > 0) }
    private val _estado = MutableStateFlow(Reproduccion())
    val estado = _estado.asStateFlow()
    private var tarea: Job? = null

    fun iniciar() {
        if (tarea?.isActive == true || _estado.value.estado == EstadoReproduccion.FINALIZADO) return
        val inicial = _estado.value.progreso
        _estado.value = Reproduccion(inicial, EstadoReproduccion.REPRODUCIENDO)
        tarea = scope.launch {
            val inicio = reloj()
            while (isActive) {
                val transcurrido = (reloj() - inicio) / 1_000_000.0
                val progreso = (inicial + transcurrido / duracionMs).toFloat().coerceAtMost(1f)
                _estado.value = Reproduccion(progreso, if (progreso >= 1f)
                    EstadoReproduccion.FINALIZADO else EstadoReproduccion.REPRODUCIENDO)
                if (progreso >= 1f) break
                esperarFotograma()
            }
        }
    }

    fun pausar() {
        if (_estado.value.estado != EstadoReproduccion.REPRODUCIENDO) return
        tarea?.cancel()
        tarea = null
        _estado.value = _estado.value.copy(estado = EstadoReproduccion.PAUSADO)
    }

    fun detener() {
        cancelar()
        _estado.value = Reproduccion(estado = EstadoReproduccion.DETENIDO)
    }

    fun reiniciar() { cancelar(); iniciar() }

    fun cancelar() {
        tarea?.cancel()
        tarea = null
        _estado.value = Reproduccion()
    }
}
