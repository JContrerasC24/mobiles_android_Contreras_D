package com.contreras.clinicasaludplus.data

/** Estado visual de una cita en "Mis citas". */
enum class EstadoCita { CONFIRMADA, COMPLETADA, CANCELADA }

/**
 * Opcion de fecha para agendar. chipLabel es lo que se ve en el chip
 * ("Vie 27") y nombreCompleto es lo que se muestra en la confirmacion
 * ("Viernes 27").
 */
data class FechaOpcion(val chipLabel: String, val nombreCompleto: String)

/**
 * Opcion de hora para agendar. chipLabel es compacto ("10:30") y
 * horaCompleta incluye am/pm ("10:30 am") para la confirmacion.
 */
data class HoraOpcion(val chipLabel: String, val horaCompleta: String)

val fechasDisponibles = listOf(
    FechaOpcion("Jue 26", "Jueves 26"),
    FechaOpcion("Vie 27", "Viernes 27"),
    FechaOpcion("Sab 28", "Sabado 28")
)

val horasDisponibles = listOf(
    HoraOpcion("9:00", "9:00 am"),
    HoraOpcion("10:30", "10:30 am"),
    HoraOpcion("3:00", "3:00 pm")
)

/** Una cita agendada por el paciente. */
data class Cita(
    val medico: Medico,
    val fecha: String,
    val hora: String,
    var estado: EstadoCita = EstadoCita.CONFIRMADA
)

/** Citas de ejemplo que ya existen al abrir la app (como en el mockup de "Mis citas"). */
fun citasIniciales(): MutableList<Cita> = mutableListOf(
    Cita(listaMedicos[0], "Viernes 27", "10:30 am", EstadoCita.CONFIRMADA),
    Cita(listaMedicos[1], "Miercoles 15", "3:00 pm", EstadoCita.COMPLETADA)
)