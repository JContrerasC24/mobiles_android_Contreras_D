package com.contreras.campusgps

import com.contreras.campusgps.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert.*
import org.junit.Test

class ReproductorTest {
    @Test fun pausaReanudaYFinalizaSinContarElTiempoPausado() = runBlocking {
        var ahora = 0L
        val frames = Channel<Unit>(Channel.UNLIMITED)
        val motor = ReproductorRecorrido(this, 1000, { ahora }, { frames.receive() })
        motor.iniciar(); yield()
        ahora = 400_000_000L; frames.send(Unit); yield()
        assertEquals(.4f, motor.estado.value.progreso, .001f)
        motor.pausar(); ahora = 9_000_000_000L; yield()
        assertEquals(EstadoReproduccion.PAUSADO, motor.estado.value.estado)
        assertEquals(.4f, motor.estado.value.progreso, .001f)
        motor.iniciar(); yield()
        ahora += 600_000_000L; frames.send(Unit); yield()
        assertEquals(EstadoReproduccion.FINALIZADO, motor.estado.value.estado)
        assertEquals(1f, motor.estado.value.progreso, 0f)
        motor.cancelar()
    }
    @Test fun iniciarDosVecesNoDuplicaAnimacionesYDetenerCancela() = runBlocking {
        var ahora = 0L
        val frames = Channel<Unit>(Channel.UNLIMITED)
        var esperas = 0
        val motor = ReproductorRecorrido(this, 1000, { ahora }, { esperas++; frames.receive() })
        motor.iniciar(); motor.iniciar(); yield()
        assertEquals(1, esperas)
        motor.detener(); ahora = 5_000_000_000L; frames.send(Unit); yield()
        assertEquals(EstadoReproduccion.DETENIDO, motor.estado.value.estado)
        assertEquals(0f, motor.estado.value.progreso, 0f)
        motor.cancelar()
    }
    @Test fun reiniciarYCerrarRestablecenElProgreso() = runBlocking {
        var ahora = 0L
        val frames = Channel<Unit>(Channel.UNLIMITED)
        val motor = ReproductorRecorrido(this, 1000, { ahora }, { frames.receive() })
        motor.iniciar(); yield()
        ahora = 600_000_000L; frames.send(Unit); yield()
        motor.reiniciar(); yield()
        assertEquals(0f, motor.estado.value.progreso, 0f)
        assertEquals(EstadoReproduccion.REPRODUCIENDO, motor.estado.value.estado)
        motor.cancelar(); ahora += 5_000_000_000L; frames.send(Unit); yield()
        assertEquals(Reproduccion(), motor.estado.value)
    }
}
