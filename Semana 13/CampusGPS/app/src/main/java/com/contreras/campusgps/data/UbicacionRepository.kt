package com.contreras.campusgps.data

import com.contreras.campusgps.model.Posicion

/**
 * Define el contrato para obtener la ubicación del dispositivo.
 *
 * La interfaz no necesita conocer cómo se obtiene la ubicación.
 * De esa responsabilidad se encargará su implementación.
 */
interface UbicacionRepository {

    // suspend permite utilizar esta función dentro de una corrutina.
    // Result permite representar un resultado correcto o un error.
    suspend fun obtenerPosicion(): Result<Posicion>
}