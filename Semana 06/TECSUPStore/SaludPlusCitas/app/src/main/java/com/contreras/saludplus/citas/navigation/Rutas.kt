package com.contreras.saludplus.citas.navigation

import android.net.Uri

object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val TERMINOS = "terminos"

    const val INICIO = "inicio"
    const val ESPECIALIDADES = "especialidades"
    const val MIS_CITAS = "mis_citas"
    const val PERFIL = "perfil"
    const val RESULTADOS = "resultados"
    const val NOTIFICACIONES = "notificaciones"

    const val MEDICOS = "medicos/{especialidadId}"
    const val FECHA_HORA = "fecha_hora/{medicoId}"
    const val CONFIRMAR_CITA = "confirmar_cita/{medicoId}/{fecha}/{hora}"
    const val CITA_EXITOSA = "cita_exitosa/{citaId}"
    const val DETALLE_CITA = "detalle_cita/{citaId}"

    fun medicos(especialidadId: Int): String {
        return "medicos/$especialidadId"
    }

    fun fechaHora(medicoId: Int): String {
        return "fecha_hora/$medicoId"
    }

    fun confirmarCita(
        medicoId: Int,
        fecha: String,
        hora: String
    ): String {
        return "confirmar_cita/$medicoId/${Uri.encode(fecha)}/${Uri.encode(hora)}"
    }

    fun citaExitosa(citaId: Int): String {
        return "cita_exitosa/$citaId"
    }

    fun detalleCita(citaId: Int): String {
        return "detalle_cita/$citaId"
    }
}

