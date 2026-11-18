package com.contreras.campusgps
import com.contreras.campusgps.model.*
import com.contreras.campusgps.util.FiltroLugares
import org.junit.Assert.*
import org.junit.Test

class FiltroLugaresTest {
    private val lugares = listOf(
        LugarConDistancia(Lugar(1, "Cafetería Campus", "Comida", 0.0, 0.0), 10f),
        LugarConDistancia(Lugar(2, "Biblioteca Central", "Estudio", 0.0, 0.0), 20f))
    @Test fun buscaSinTildesYEspacios() {
        assertEquals(1, FiltroLugares.aplicar(lugares, " CAFETERIA ", null).single().lugar.id)
    }
    @Test fun combinaFiltrosYVacio() {
        assertTrue(FiltroLugares.aplicar(lugares, "Cafetería", "Estudio").isEmpty())
        assertEquals(2, FiltroLugares.aplicar(lugares, "", "Estudio").single().lugar.id)
    }
    @Test fun conservaOrdenYDistancias() {
        assertEquals(lugares, FiltroLugares.aplicar(lugares, "", null))
    }
}
