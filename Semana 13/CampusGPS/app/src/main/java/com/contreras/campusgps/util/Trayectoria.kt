package com.contreras.campusgps.util

import com.contreras.campusgps.model.Posicion
import kotlin.math.*

data class PuntoAnimado(val posicion: Posicion, val rumbo: Float)

/** Interpolación esférica por distancia; también admite rutas con varios segmentos. */
class Trayectoria(puntos: List<Posicion>) {
    private val puntos = puntos.filterIndexed { i, p -> i == 0 || p != puntos[i - 1] }
    private val longitudes = this.puntos.zipWithNext { a, b -> angulo(a, b) }
    private val total = longitudes.sum()
    init { require(this.puntos.isNotEmpty()) }

    fun punto(progreso: Float): PuntoAnimado {
        if (puntos.size == 1 || total < 1e-12) return PuntoAnimado(puntos.first(), 0f)
        var restante = progreso.coerceIn(0f, 1f) * total
        var segmento = 0
        while (segmento < longitudes.lastIndex && restante > longitudes[segmento]) {
            restante -= longitudes[segmento++]
        }
        val a = puntos[segmento]
        val b = puntos[segmento + 1]
        val fraccion = (restante / longitudes[segmento]).coerceIn(0.0, 1.0)
        val posicion = interpolar(a, b, fraccion)
        val rumbo = if (fraccion >= 0.999999) rumbo(interpolar(a, b, 0.999), b) else rumbo(posicion, b)
        return PuntoAnimado(posicion, rumbo)
    }

    private fun angulo(a: Posicion, b: Posicion): Double {
        val lat1 = Math.toRadians(a.latitud)
        val lat2 = Math.toRadians(b.latitud)
        val dlon = Math.toRadians(b.longitud - a.longitud)
        val h = sin((lat2-lat1)/2).pow(2) + cos(lat1)*cos(lat2)*sin(dlon/2).pow(2)
        return 2 * asin(sqrt(h.coerceIn(0.0, 1.0)))
    }

    private fun interpolar(a: Posicion, b: Posicion, t: Double): Posicion {
        if (t <= 0) return a
        if (t >= 1) return b
        val distancia = angulo(a,b)
        if (distancia < 1e-12) return a
        val lat1 = Math.toRadians(a.latitud); val lon1 = Math.toRadians(a.longitud)
        val lat2 = Math.toRadians(b.latitud); val lon2 = Math.toRadians(b.longitud)
        val u = sin((1-t)*distancia) / sin(distancia)
        val v = sin(t*distancia) / sin(distancia)
        val x = u*cos(lat1)*cos(lon1) + v*cos(lat2)*cos(lon2)
        val y = u*cos(lat1)*sin(lon1) + v*cos(lat2)*sin(lon2)
        val z = u*sin(lat1) + v*sin(lat2)
        return Posicion(Math.toDegrees(atan2(z, sqrt(x*x+y*y))), Math.toDegrees(atan2(y,x)))
    }

    private fun rumbo(a: Posicion, b: Posicion): Float {
        val lat1 = Math.toRadians(a.latitud); val lat2 = Math.toRadians(b.latitud)
        val dlon = Math.toRadians(b.longitud-a.longitud)
        return ((Math.toDegrees(atan2(sin(dlon)*cos(lat2),
            cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(dlon)))+360)%360).toFloat()
    }
}
