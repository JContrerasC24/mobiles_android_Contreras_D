package com.contreras.huellitasvet.model

import com.contreras.huellitasvet.data.local.entity.Cita
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class ReprogramarCitaTest {
    private val ahora = LocalDateTime.parse("2026-10-19T10:00:00")
    private fun cita(id: Int = 1, estado: String = "PENDIENTE", fecha: String = "2026-10-19", hora: String = "13:00") =
        Cita(id = id, mascotaId = 10, servicioId = 5, fecha = fecha, hora = hora,
            nota = "Nota original", estado = estado, precio = 60.0, fechaCreacion = 1000L)

    @Test
    fun permiteReprogramarSiFaltanMasDeDosHoras() {
        val cita = cita(hora = "13:00") // 3 horas de margen respecto a las 10:00
        assertTrue(ReglasCitas.puedeReprogramar(cita, ahora))
    }

    @Test
    fun rechazaReprogramarSiFaltanDosHorasOMenos() {
        val citaExacta2h = cita(hora = "12:00") // Exactamente 2 horas
        val cita1h = cita(hora = "11:00")
        assertFalse(ReglasCitas.puedeReprogramar(citaExacta2h, ahora))
        assertFalse(ReglasCitas.puedeReprogramar(cita1h, ahora))
    }

    @Test
    fun rechazaEstadosNoPermitidos() {
        for (estado in listOf("ATENDIDA", "CANCELADA")) {
            val citaInactiva = cita(estado = estado, hora = "15:00")
            assertFalse(ReglasCitas.puedeReprogramar(citaInactiva, ahora))
        }
    }

    @Test
    fun excluyePropiaCitaDeHorariosOcupadosAlReprogramar() {
        val ocupadas = listOf("09:00", "10:00", "11:00")
        val propiaHora = "10:00"
        // Al excluir la propia cita del listado de ocupados, el horario de la propia cita queda disponible para ella misma
        val ocupadasSinPropia = ocupadas.filter { it != propiaHora }
        
        val fecha = ahora.toLocalDate().plusDays(1)
        assertTrue(Horarios.disponible(fecha, propiaHora, ocupadasSinPropia, ahora))
        assertFalse(Horarios.disponible(fecha, "09:00", ocupadasSinPropia, ahora))
    }

    @Test
    fun reprogramacionConservaIdentificadorYCamposNoRelacionados() {
        val original = cita(id = 42, fecha = "2026-10-20", hora = "10:00")
        val reprogramada = original.copy(fecha = "2026-10-22", hora = "15:00")

        assertEquals(42, reprogramada.id)
        assertEquals(10, reprogramada.mascotaId)
        assertEquals(5, reprogramada.servicioId)
        assertEquals("Nota original", reprogramada.nota)
        assertEquals("PENDIENTE", reprogramada.estado)
        assertEquals(60.0, reprogramada.precio, 0.001)
        assertEquals(1000L, reprogramada.fechaCreacion)
        assertEquals("2026-10-22", reprogramada.fecha)
        assertEquals("15:00", reprogramada.hora)
    }
}
