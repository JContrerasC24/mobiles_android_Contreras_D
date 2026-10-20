package com.contreras.huellitasvet.model

import com.contreras.huellitasvet.data.local.entity.Cita
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object ReglasCitas {
    fun momento(cita: Cita): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(cita.fecha), LocalTime.parse(cita.hora))
    fun activa(cita: Cita): Boolean =
        cita.estado == EstadoCita.PENDIENTE.name || cita.estado == EstadoCita.CONFIRMADA.name
    fun proxima(cita: Cita, ahora: LocalDateTime): Boolean =
        activa(cita) && !momento(cita).isBefore(ahora)
    fun puedeCancelar(cita: Cita, ahora: LocalDateTime): Boolean =
        activa(cita) && Duration.between(ahora, momento(cita)) > Duration.ofHours(2)
    fun puedeReprogramar(cita: Cita, ahora: LocalDateTime): Boolean =
        puedeCancelar(cita, ahora)
}
