package com.contreras.campusgps.data

import com.contreras.campusgps.model.*
import com.contreras.campusgps.util.Distancia

interface RecorridosRepository {
    fun crear(origen: Posicion, destino: Lugar): Recorrido
}

/** Segmento ilustrativo: no consulta caminos ni ofrece navegación vial. */
class RecorridosRepositoryDemostracion : RecorridosRepository {
    override fun crear(origen: Posicion, destino: Lugar) = Recorrido(destino,
        listOf(origen, Posicion(destino.latitud, destino.longitud)),
        Distancia.calcularMetros(origen, destino))
}
