package com.contreras.clinicasaludplus.navigation

/**
 * Todas las rutas de navegacion de la app, en un solo lugar.
 * - Rutas "principales" (drawer): INICIO, MIS_CITAS, HISTORIAL, PERFIL_USUARIO
 * - Rutas "de flujo" (stack con boton atras): PERFIL_MEDICO, AGENDAR_CITA, CONFIRMACION
 */
object Rutas {
    const val INICIO = "inicio"
    const val PERFIL_MEDICO = "perfil_medico/{medicoId}"
    const val AGENDAR_CITA = "agendar_cita/{medicoId}"
    const val CONFIRMACION = "confirmacion"
    const val MIS_CITAS = "mis_citas"
    const val HISTORIAL = "historial"
    const val PERFIL_USUARIO = "perfil_usuario"

    fun perfilMedico(id: Int) = "perfil_medico/$id"
    fun agendarCita(id: Int) = "agendar_cita/$id"
}