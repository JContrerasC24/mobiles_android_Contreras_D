package com.contreras.huellitasvet.viewmodel

import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.MascotaConDueno
import com.contreras.huellitasvet.model.validarAtencion
import org.junit.Assert.*
import org.junit.Test

class AtencionPacientesTest {
    @Test fun diagnosticoRequiereCincoCaracteresSinEspaciosExteriores() {
        assertTrue("diagnostico" in validarAtencion(" abcd ", 5.0))
        assertTrue(validarAtencion(" abcde ", 5.0).isEmpty())
    }
    @Test fun pesoDebeSerPositivoYFinito() {
        for (peso in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertTrue("peso" in validarAtencion("Control general", peso))
        }
        assertTrue(validarAtencion("Control general", 0.1).isEmpty())
    }
    private fun paciente(id: Int, nombre: String, especie: String, dueno: String) =
        MascotaConDueno(
            Mascota(id, id, nombre, especie, "Mestiza", 2023, 5.0, "HEMBRA"),
            Usuario(id, dueno, "correo$id@test", "1234", "CLIENTE")
        )
    private val lista = listOf(paciente(1, "Luna", "GATO", "Ana"),
        paciente(2, "Max", "PERRO", "Luis"), paciente(3, "Nina", "PERRO", "Ana"))

    @Test fun buscaPorMascotaODuenoIgnorandoMayusculasYEspacios() {
        assertEquals(listOf(1), filtrarPacientes(lista, " LUNA ", null).map { it.mascota.id })
        assertEquals(listOf(1, 3), filtrarPacientes(lista, "ana", null).map { it.mascota.id })
    }
    @Test fun combinaEspecieConTextoYTodosRestauraLaLista() {
        assertEquals(listOf(3), filtrarPacientes(lista, "Ana", "PERRO").map { it.mascota.id })
        assertTrue(filtrarPacientes(lista, "Luis", "GATO").isEmpty())
        assertEquals(3, filtrarPacientes(lista, "", null).size)
    }
}
