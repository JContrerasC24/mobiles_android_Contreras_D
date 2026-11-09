package com.contreras.saludplus.citas.data.model

// Representa una reserva médica realizada por un paciente.
data class Cita(
    // Identificador único de la cita.
    val id: Int,

    // Identifica al paciente que realizó la reserva.
    val usuarioId: Int,

    // Identifica al médico seleccionado.
    val medicoId: Int,

    // Guarda la fecha con formato yyyy-MM-dd.
    val fecha: String,

    // Guarda la hora con formato HH:mm.
    val hora: String,

    // Motivo opcional indicado por el paciente.
    val motivo: String = ""
)