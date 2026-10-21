package com.contreras.saludplus.citas.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

// Verifica fines de semana y cambios de calendario.
class CalendarioCitasTest {

    @Test
    fun sabadoComienzaEnLunes() {
        val inicio = LocalDate.of(2026, 10, 3)
        val dias = CalendarioCitas.proximosDiasHabiles(inicio)

        assertEquals(5, dias.size)
        assertEquals(LocalDate.of(2026, 10, 5), dias.first())

        assertTrue(
            dias.all {
                it.dayOfWeek != DayOfWeek.SATURDAY &&
                        it.dayOfWeek != DayOfWeek.SUNDAY &&
                        !it.isBefore(inicio)
            }
        )
    }

    @Test
    fun viernesContinuaElLunes() {
        val dias = CalendarioCitas.proximosDiasHabiles(
            LocalDate.of(2026, 10, 2)
        )

        assertEquals(LocalDate.of(2026, 10, 2), dias[0])
        assertEquals(LocalDate.of(2026, 10, 5), dias[1])
    }

    @Test
    fun calendarioCruzaDeAnio() {
        val dias = CalendarioCitas.proximosDiasHabiles(
            LocalDate.of(2026, 12, 31)
        )

        val esperados = listOf(
            LocalDate.of(2026, 12, 31),
            LocalDate.of(2027, 1, 1),
            LocalDate.of(2027, 1, 4),
            LocalDate.of(2027, 1, 5),
            LocalDate.of(2027, 1, 6)
        )

        assertEquals(esperados, dias)
    }

    @Test
    fun avanzarSemanaDesplazaSieteDias() {
        val inicio = LocalDate.of(2026, 10, 1)

        val actuales = CalendarioCitas.proximosDiasHabiles(inicio)
        val siguientes = CalendarioCitas.proximosDiasHabiles(
            inicio.plusWeeks(1)
        )

        assertEquals(
            actuales.map { it.plusWeeks(1) },
            siguientes
        )
    }

    @Test
    fun fechaSePresentaEnEspanol() {
        assertEquals(
            "Miércoles 16 de setiembre 2026",
            CalendarioCitas.fechaLarga("2026-09-16")
        )
    }

    @Test
    fun encabezadoCambiaDeMesYAnio() {
        assertEquals(
            "Diciembre 2026",
            CalendarioCitas.mesYAnio(LocalDate.of(2026, 12, 31))
        )

        assertEquals(
            "Enero 2027",
            CalendarioCitas.mesYAnio(LocalDate.of(2027, 1, 1))
        )
    }
}