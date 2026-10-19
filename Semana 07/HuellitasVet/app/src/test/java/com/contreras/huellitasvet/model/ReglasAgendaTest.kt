package com.contreras.huellitasvet.model

import org.junit.Assert.*
import org.junit.Test

class ReglasAgendaTest {
    @Test fun soloPendientesPuedenConfirmarse() {
        assertTrue(ReglasAgenda.permite("PENDIENTE", EstadoCita.CONFIRMADA))
        for (estado in listOf("CONFIRMADA", "ATENDIDA", "CANCELADA")) {
            assertFalse(ReglasAgenda.permite(estado, EstadoCita.CONFIRMADA))
        }
    }
    @Test fun soloCitasActivasPuedenCancelarse() {
        assertTrue(ReglasAgenda.permite("PENDIENTE", EstadoCita.CANCELADA))
        assertTrue(ReglasAgenda.permite("CONFIRMADA", EstadoCita.CANCELADA))
        assertFalse(ReglasAgenda.permite("ATENDIDA", EstadoCita.CANCELADA))
        assertFalse(ReglasAgenda.permite("CANCELADA", EstadoCita.CANCELADA))
    }
    @Test fun noPermiteReabrirNiAtenderSinRegistrarAtencion() {
        for (estado in EstadoCita.entries) {
            assertFalse(ReglasAgenda.permite(estado.name, EstadoCita.PENDIENTE))
            assertFalse(ReglasAgenda.permite(estado.name, EstadoCita.ATENDIDA))
        }
    }
}
