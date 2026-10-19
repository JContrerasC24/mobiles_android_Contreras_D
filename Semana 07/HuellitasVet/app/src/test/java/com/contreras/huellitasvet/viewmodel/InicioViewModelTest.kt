package com.contreras.huellitasvet.viewmodel

import com.contreras.huellitasvet.data.local.entity.*
import com.contreras.huellitasvet.data.local.relation.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class InicioViewModelTest {
    private val ahora = LocalDateTime.parse("2026-10-18T10:00:00")

    private fun cita(id: Int, fecha: String, hora: String, estado: String = "PENDIENTE"): CitaConDetalle {
        val dueno = Usuario(1, "Ana", "ana@correo.test", "1234", "CLIENTE")
        val mascota = Mascota(1, 1, "Luna", "PERRO", "Mestiza", 2023, 10.0, "HEMBRA")
        val servicio = Servicio(1, "Consulta", "Consulta general", 30, 50.0, true, "Pets")
        return CitaConDetalle(
            Cita(id = id, mascotaId = 1, servicioId = 1, fecha = fecha,
                hora = hora, estado = estado, precio = 50.0),
            MascotaConDueno(mascota, dueno),
            servicio
        )
    }

    @Test fun ordenaPorFechaYHoraSinDependerDelOrdenDelDao() {
        val citas = listOf(
            cita(1, "2026-10-19", "08:00"),
            cita(2, "2026-10-18", "14:00"),
            cita(3, "2026-10-18", "11:00", "CONFIRMADA")
        )
        assertEquals(3, elegirProximaCita(citas, ahora)?.cita?.id)
    }

    @Test fun descartaPasadasCanceladasYAtendidas() {
        val citas = listOf(
            cita(1, "2026-10-17", "12:00"),
            cita(2, "2026-10-18", "09:59"),
            cita(3, "2026-10-18", "10:30", "CANCELADA"),
            cita(4, "2026-10-18", "10:45", "ATENDIDA"),
            cita(5, "2026-10-18", "11:00")
        )
        assertEquals(5, elegirProximaCita(citas, ahora)?.cita?.id)
    }

    @Test fun incluyeLaHoraExactaYLaDescartaAlPasar() {
        val citas = listOf(cita(1, "2026-10-18", "10:00"))
        assertEquals(1, elegirProximaCita(citas, ahora)?.cita?.id)
        assertNull(elegirProximaCita(citas, ahora.plusSeconds(1)))
    }

    @Test fun sinCitasElegiblesDevuelveNull() {
        assertNull(elegirProximaCita(emptyList(), ahora))
        assertNull(elegirProximaCita(listOf(cita(1, "2026-10-18", "12:00", "ATENDIDA")), ahora))
    }
}
