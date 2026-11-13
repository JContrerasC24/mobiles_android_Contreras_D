package com.tecsup.inventarioherramientas
import com.tecsup.inventarioherramientas.domain.model.*
import org.junit.Test
import org.junit.Assert.*

class CobroAlquilerTest {
    @Test fun calculaCentimosSinRedondeo() {
        assertEquals(1250L, leerCentimos("12,50"))
        assertNull(leerCentimos("12.501"))
        assertEquals(3750L, CobroAlquiler(1250, 3, "EFECTIVO", "").totalCentimos)
    }
    @Test fun exigeComprobanteParaPagoElectronico() {
        assertTrue(runCatching { CobroAlquiler(2500, 2, "YAPE", "").validar() }.isFailure)
        CobroAlquiler(2500, 2, "TARJETA", "OPER-001").validar()
    }
    @Test fun rechazaValoresFueraDeRango() {
        assertTrue(runCatching { CobroAlquiler(0, 2, "EFECTIVO", "").validar() }.isFailure)
        assertTrue(runCatching { CobroAlquiler(2500, 0, "EFECTIVO", "").validar() }.isFailure)
        assertTrue(runCatching { CobroAlquiler(2500, 366, "EFECTIVO", "").validar() }.isFailure)
    }
}
