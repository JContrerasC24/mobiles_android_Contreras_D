package com.contreras.campusgps.util

import android.location.Location

import com.contreras.campusgps.model.Lugar
import com.contreras.campusgps.model.Posicion


/**
 * Utilidad para calcular distancias geográficas.
 *
 * Calcula la distancia aproximada en metros
 * entre la ubicación actual del usuario
 * y un lugar registrado en el campus.
 */
object Distancia {

    /**
     * Calcula la distancia entre dos posiciones.
     *
     * @param origen Ubicación actual del usuario.
     * @param destino Lugar del campus.
     *
     * @return Distancia aproximada expresada en metros.
     */
    fun calcularMetros(
        origen: Posicion,
        destino: Lugar
    ): Float {

        /*
         * Android almacenará la distancia
         * calculada en la primera posición
         * de este arreglo.
         */
        val resultados = FloatArray(1)

        /*
         * distanceBetween utiliza las coordenadas
         * geográficas para calcular la distancia
         * aproximada entre ambos puntos.
         */
        Location.distanceBetween(

            // Coordenadas del usuario.
            origen.latitud,
            origen.longitud,

            // Coordenadas del lugar.
            destino.latitud,
            destino.longitud,

            // Arreglo donde se guardará el resultado.
            resultados
        )

        /*
         * La distancia en metros
         * se encuentra en resultados[0].
         */
        return resultados[0]
    }
}