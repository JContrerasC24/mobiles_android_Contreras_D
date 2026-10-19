package com.contreras.huellitasvet.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object Horarios {
    val todos: List<String> = (0 until 18).map {
        LocalTime.of(9, 0).plusMinutes(it * 30L).toString()
    }
    fun disponible(fecha: LocalDate, hora: String, ocupadas: List<String>, ahora: LocalDateTime): Boolean =
        hora in todos && hora !in ocupadas &&
            fecha >= ahora.toLocalDate() && fecha <= ahora.toLocalDate().plusDays(7) &&
            LocalDateTime.of(fecha, LocalTime.parse(hora)).isAfter(ahora)
}
