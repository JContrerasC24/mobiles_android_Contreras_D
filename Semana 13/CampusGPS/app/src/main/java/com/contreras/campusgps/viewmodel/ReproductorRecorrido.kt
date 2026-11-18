package com.contreras.campusgps.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EstadoReproduccion { LISTO, REPRODUCIENDO, PAUSADO, DETENIDO, FINALIZADO }
data class Reproduccion(val progreso: Float = 0f, val estado: EstadoReproduccion = EstadoReproduccion.LISTO)

/** Una sola tarea, reloj monotónico y ningún vínculo con la ubicación GPS. */
class ReproductorRecorrido(private val scope: CoroutineScope, private val duracionMs: Long = 12000L) {
    private val _estado = MutableStateFlow(Reproduccion())
    val estado = _estado.asStateFlow()
    private var tarea: Job? = null

    fun iniciar() {
        if (tarea?.isActive == true || _estado.value.estado == EstadoReproduccion.FINALIZADO) return
        val inicial = _estado.value.progreso
        _estado.value = Reproduccion(inicial, EstadoReproduccion.REPRODUCIENDO)
        tarea = scope.launch {
            val inicio = System.nanoTime()
            while (isActive) {
                val transcurrido = (System.nanoTime() - inicio) / 1_000_000.0
                val progreso = (inicial + transcurrido / duracionMs).toFloat().coerceAtMost(1f)
                _estado.value = Reproduccion(progreso, if (progreso >= 1f)
                    EstadoReproduccion.FINALIZADO else EstadoReproduccion.REPRODUCIENDO)
                if (progreso >= 1f) break
                delay(16)
            }
        }
    }

    fun cancelar() {
        tarea?.cancel()
        tarea = null
        _estado.value = Reproduccion()
    }
}
