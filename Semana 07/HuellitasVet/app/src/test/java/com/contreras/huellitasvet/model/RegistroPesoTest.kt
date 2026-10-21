package com.contreras.huellitasvet.model

import org.junit.Assert.*
import org.junit.Test

class RegistroPesoTest {

    @Test
    fun registroPesoConservaPropiedades() {
        val registro = RegistroPeso(fecha = "2026-10-20", hora = "10:00", peso = 12.5)
        assertEquals("2026-10-20", registro.fecha)
        assertEquals("10:00", registro.hora)
        assertEquals(12.5, registro.peso, 0.001)
    }

    @Test
    fun ordenamientoCronologicoMantieneRegistrosDelMismoDia() {
        val r1 = RegistroPeso("2026-10-10", "09:00", 11.5)
        val r2 = RegistroPeso("2026-10-10", "15:00", 11.8)
        val r3 = RegistroPeso("2026-10-15", "10:00", 12.0)

        val desordenados = listOf(r3, r2, r1)
        val ordenados = desordenados.sortedWith(compareBy({ it.fecha }, { it.hora }))

        assertEquals(listOf(r1, r2, r3), ordenados)
        assertEquals(3, ordenados.size)
    }
}
