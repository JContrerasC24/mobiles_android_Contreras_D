package com.contreras.huellitasvet.viewmodel

import org.junit.Assert.*
import org.junit.Test

class MascotaValidacionTest {
    private val valido = FormularioMascota(
        nombre = "Luna", raza = "Mestiza", especie = "PERRO",
        sexo = "HEMBRA", anio = "2023", peso = "8.5"
    )

    @Test fun camposVaciosMuestranLosErroresCorrespondientes() {
        assertEquals(setOf("nombre", "especie", "sexo", "anio", "peso"),
            validarMascota(FormularioMascota(), 2026).keys)
    }

    @Test fun nombreSeValidaSinContarEspaciosExteriores() {
        assertTrue("nombre" in validarMascota(valido.copy(nombre = " A "), 2026))
        assertFalse("nombre" in validarMascota(valido.copy(nombre = " Bo "), 2026))
    }

    @Test fun anioAceptaAmbosLimitesYRechazaFueraDelRango() {
        for (anio in listOf("1990", "2026")) {
            assertFalse("anio" in validarMascota(valido.copy(anio = anio), 2026))
        }
        for (anio in listOf("1989", "2027", "dos mil", "2026.5")) {
            assertTrue("anio" in validarMascota(valido.copy(anio = anio), 2026))
        }
    }

    @Test fun pesoRechazaCeroNegativosNoNumericosEInfinitos() {
        for (peso in listOf("0", "-2", "abc", "NaN", "Infinity", "1e999")) {
            assertTrue("peso inválido: $peso", "peso" in validarMascota(valido.copy(peso = peso), 2026))
        }
    }

    @Test fun pesoAceptaDecimalesConPuntoOComaYRazaEsOpcional() {
        for (peso in listOf("0.1", "8,5", "8.5")) {
            assertTrue(validarMascota(valido.copy(peso = peso, raza = ""), 2026).isEmpty())
        }
    }

    @Test fun especieYSexoDebenSerOpcionesReconocidas() {
        val errores = validarMascota(valido.copy(especie = "INVALIDO", sexo = "INVALIDO"), 2026)
        assertEquals(setOf("especie", "sexo"), errores.keys)
    }
}
