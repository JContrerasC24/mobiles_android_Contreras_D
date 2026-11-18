package com.contreras.campusgps
import com.contreras.campusgps.model.Posicion
import com.contreras.campusgps.util.Trayectoria
import org.junit.Assert.*
import org.junit.Test

class TrayectoriaTest {
    @Test fun extremosYRumboEste() {
        val a = Posicion(0.0,0.0); val b = Posicion(0.0,1.0)
        val ruta = Trayectoria(listOf(a,b))
        assertEquals(a, ruta.punto(0f).posicion)
        assertEquals(b, ruta.punto(1f).posicion)
        assertEquals(0.5, ruta.punto(.5f).posicion.longitud, 0.00001)
        assertEquals(90f, ruta.punto(.5f).rumbo, .001f)
    }
    @Test fun giroAlCambiarSegmento() {
        val ruta = Trayectoria(listOf(Posicion(0.0,0.0), Posicion(0.0,1.0), Posicion(1.0,1.0)))
        assertEquals(90f, ruta.punto(.25f).rumbo, .01f)
        assertEquals(0f, ruta.punto(.75f).rumbo, .01f)
    }
    @Test fun puntosIgualesNoProducenNaN() {
        val p = Posicion(-12.0,-77.0)
        assertEquals(p, Trayectoria(listOf(p,p)).punto(.5f).posicion)
    }
    @Test fun cruzaAntimeridianoPorCaminoCorto() {
        val ruta = Trayectoria(listOf(Posicion(0.0,179.0),Posicion(0.0,-179.0)))
        assertEquals(180.0, kotlin.math.abs(ruta.punto(.5f).posicion.longitud), .001)
    }
}
