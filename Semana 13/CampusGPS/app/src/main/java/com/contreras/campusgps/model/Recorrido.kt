package com.contreras.campusgps.model

data class Recorrido(val destino: Lugar, val puntos: List<Posicion>, val metros: Float)
