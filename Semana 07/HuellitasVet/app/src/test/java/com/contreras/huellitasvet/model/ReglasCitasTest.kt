package com.contreras.huellitasvet.model

import com.contreras.huellitasvet.data.local.entity.Cita
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class ReglasCitasTest {
    private val ahora = LocalDateTime.parse("2026-10-19T10:00:00")
    private fun cita(estado: String = "PENDIENTE", hora: String = "12:00", fecha: String = "2026-10-19") =
        Cita(id = 1, mascotaId = 1, servicioId = 1, fecha = fecha, hora = hora,
            estado = estado, precio = 50.0)

    @Test fun limiteDeDosHorasEsEstricto() {
        assertTrue(ReglasCitas.puedeCancelar(cita(), ahora.minusSeconds(1)))
        assertFalse(ReglasCitas.puedeCancelar(cita(), ahora))
        assertFalse(ReglasCitas.puedeCancelar(cita(), ahora.plusSeconds(1)))
    }

    @Test fun unaHoraOPasadaNoPermiteCancelar() {
        assertFalse(ReglasCitas.puedeCancelar(cita(hora = "11:00"), ahora))
        assertFalse(ReglasCitas.puedeCancelar(cita(fecha = "2026-10-18"), ahora))
    }

    @Test fun soloPendientesYConfirmadasSePuedenCancelar() {
        for (estado in listOf("PENDIENTE", "CONFIRMADA")) {
            assertTrue(ReglasCitas.puedeCancelar(cita(estado, "13:00"), ahora))
        }
        for (estado in listOf("ATENDIDA", "CANCELADA")) {
            assertFalse(ReglasCitas.puedeCancelar(cita(estado, "13:00"), ahora))
            assertFalse(ReglasCitas.proxima(cita(estado, "13:00"), ahora))
        }
    }

    @Test fun activasPasanAHistorialAlPasarSuHora() {
        val cita = cita(hora = "10:00")
        assertTrue(ReglasCitas.proxima(cita, ahora))
        assertFalse(ReglasCitas.proxima(cita, ahora.plusSeconds(1)))
    }

    @Test fun cambiarEstadoOAvanzarTiempoRevocaPermiso() {
        val cita = cita(hora = "13:00")
        assertTrue(ReglasCitas.puedeCancelar(cita, ahora))
        assertFalse(ReglasCitas.puedeCancelar(cita.copy(estado = "ATENDIDA"), ahora))
        assertFalse(ReglasCitas.puedeCancelar(cita, ahora.plusHours(1)))
    }
}
