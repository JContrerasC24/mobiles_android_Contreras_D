package com.contreras.saludplus.citas.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

// Centraliza la generación y presentación de fechas.
object CalendarioCitas {

    private val idioma = Locale.forLanguageTag("es-PE")

    // Genera días hábiles desde la fecha indicada.
    fun proximosDiasHabiles(
        inicio: LocalDate,
        cantidad: Int = 5
    ): List<LocalDate> {
        require(cantidad >= 0)

        return generateSequence(inicio) {
            it.plusDays(1)
        }
            .filter {
                it.dayOfWeek != DayOfWeek.SATURDAY &&
                        it.dayOfWeek != DayOfWeek.SUNDAY
            }
            .take(cantidad)
            .toList()
    }

    // Presenta el nombre abreviado del día.
    fun diaCorto(fecha: LocalDate): String {
        return fecha.dayOfWeek
            .getDisplayName(TextStyle.SHORT, idioma)
            .replace(".", "")
            .replaceFirstChar {
                it.titlecase(idioma)
            }
    }

    // Presenta el mes y año del calendario.
    fun mesYAnio(fecha: LocalDate): String {
        val mes = nombreMes(fecha).replaceFirstChar {
            it.titlecase(idioma)
        }

        return "$mes ${fecha.year}"
    }

    // Convierte una fecha ISO en texto español.
    fun fechaLarga(fechaIso: String): String {
        val fecha = try {
            LocalDate.parse(fechaIso)
        } catch (_: DateTimeParseException) {
            return "Fecha no válida"
        }

        val dia = fecha.dayOfWeek
            .getDisplayName(TextStyle.FULL, idioma)
            .replaceFirstChar {
                it.titlecase(idioma)
            }

        val mes = nombreMes(fecha)

        return "$dia ${fecha.dayOfMonth} de $mes ${fecha.year}"
    }

    // Utiliza la variante setiembre solicitada en el ejercicio.
    private fun nombreMes(fecha: LocalDate): String {
        return fecha.month
            .getDisplayName(TextStyle.FULL, idioma)
            .replace("septiembre", "setiembre")
    }
}