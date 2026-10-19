package com.contreras.huellitasvet.model

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class HorariosTest {
    private val ahora = LocalDateTime.parse("2026-10-18T10:15:00")
    private val hoy = ahora.toLocalDate()

    @Test fun generaDieciochoTurnosSinDuplicados() {
        assertEquals(18, Horarios.todos.size)
        assertEquals(18, Horarios.todos.distinct().size)
        assertEquals("09:00", Horarios.todos.first())
        assertEquals("17:30", Horarios.todos.last())
    }
    @Test fun descartaHorariosPasadosYOcupados() {
        assertFalse(Horarios.disponible(hoy, "10:00", emptyList(), ahora))
        assertFalse(Horarios.disponible(hoy, "10:30", listOf("10:30"), ahora))
        assertTrue(Horarios.disponible(hoy, "10:30", emptyList(), ahora))
    }
    @Test fun aceptaDeHoyHastaSieteDias() {
        assertFalse(Horarios.disponible(hoy.minusDays(1), "17:30", emptyList(), ahora))
        assertTrue(Horarios.disponible(hoy.plusDays(7), "09:00", emptyList(), ahora))
        assertFalse(Horarios.disponible(hoy.plusDays(8), "09:00", emptyList(), ahora))
    }
    @Test fun rechazaTurnosFueraDeLaGrillaYHoraExactaActual() {
        for (hora in listOf("08:30", "18:00", "10:20", "invalida")) {
            assertFalse(Horarios.disponible(hoy, hora, emptyList(), ahora))
        }
        assertFalse(Horarios.disponible(hoy, "10:30", emptyList(), ahora.withMinute(30)))
    }
}
