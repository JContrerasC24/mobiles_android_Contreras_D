package com.contreras.campusgps.data

import com.contreras.campusgps.model.Lugar

/**
 * Define el contrato para obtener los lugares del campus.
 *
 * Los datos podrán provenir de una lista local
 * sin que el ViewModel necesite conocer su origen.
 */
interface LugaresRepository {

    // Devuelve una lista de lugares registrados en el campus.
    fun obtenerLugares(): List<Lugar>
}