package com.contreras.centrotareas.viewmodel

import androidx.lifecycle.ViewModel

import com.contreras.centrotareas.data.TareasRepository
import com.contreras.centrotareas.model.CargaUi
import com.contreras.centrotareas.model.PrimosUi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel principal de CentroTareas.
 *
 * Administra los estados de las tareas y se comunica
 * con el Repository.
 *
 * No debe contener componentes visuales de Compose.
 */
class TareasViewModel(
    private val repo: TareasRepository
) : ViewModel() {

    // ==========================================
    // PARTE A: TAREA CON PROGRESO
    // ==========================================

    // Estado interno del mensaje.
    private val _mensaje = MutableStateFlow("Listo")

    // Estado público que observará la interfaz.
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // TODO Paso 1:
    // Implementar tareaLargaMala().

    // TODO Paso 2:
    // Reemplazarla por tareaLarga() utilizando corrutinas.

    // TODO Paso 3:
    // Implementar StateFlow<Float> para el progreso.

    // TODO Paso 4:
    // Implementar Job, estado corriendo y cancelar().


    // ==========================================
    // PARTE B: CONTAR NÚMEROS PRIMOS
    // ==========================================

    // Estado interno del cálculo.
    private val _primos = MutableStateFlow<PrimosUi>(
        PrimosUi.Inicial
    )

    // Estado público del cálculo.
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    // TODO Paso 5:
    // Implementar contar(hasta: Int).

    // TODO Paso 5:
    // Implementar cancelarPrimos().


    // ==========================================
    // PARTE C: DOS CARGAS DE DATOS
    // ==========================================

    // Estado interno de las cargas.
    private val _carga = MutableStateFlow<CargaUi>(
        CargaUi.Inicial
    )

    // Estado público de las cargas.
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    // TODO Paso 6:
    // Implementar cargarUnaPorUna().

    // TODO Paso 6:
    // Implementar cargarALaVez().


    // ==========================================
    // PARTE D: MANEJO DE ERRORES
    // ==========================================

    // TODO Paso 7:
    // Implementar simularError.

    // TODO Paso 7:
    // Implementar cambiarSimularError(valor).

    // TODO Paso 7:
    // Implementar manejo de errores y reintentos.
}