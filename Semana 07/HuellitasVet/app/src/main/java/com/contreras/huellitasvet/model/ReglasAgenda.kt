package com.contreras.huellitasvet.model

object ReglasAgenda {
    fun permite(actual: String, destino: EstadoCita): Boolean = when (destino) {
        EstadoCita.CONFIRMADA -> actual == EstadoCita.PENDIENTE.name
        EstadoCita.CANCELADA -> actual == EstadoCita.PENDIENTE.name || actual == EstadoCita.CONFIRMADA.name
        else -> false // La atención se registra mediante su transacción específica.
    }
}
