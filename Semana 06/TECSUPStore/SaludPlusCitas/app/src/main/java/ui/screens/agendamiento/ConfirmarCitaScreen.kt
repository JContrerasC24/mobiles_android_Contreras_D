package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*
import com.contreras.saludplus.citas.util.CalendarioCitas
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.Locale

// Presenta el resumen con iconos y conserva el guardado de reservas.
@Composable
fun ConfirmarCitaScreen(navController: NavHostController, medicoId: Int, fecha: String, hora: String) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val usuario = Repositorio.usuarioActual
    var motivo by rememberSaveable(medicoId, fecha, hora) { mutableStateOf("") }
    var error by remember(medicoId, fecha, hora) { mutableStateOf<String?>(null) }
    var citaGuardadaId by rememberSaveable(medicoId, fecha, hora) { mutableStateOf<Int?>(null) }
    val fin = try { LocalTime.parse(hora).plusMinutes(30).toString() } catch (_: DateTimeParseException) { "" }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CabeceraSalud("Confirmar cita", { navController.popBackStack() }) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall) }
                    BotonSalud("Agendar cita", habilitado = medico != null && usuario != null && citaGuardadaId == null) {
                        if (citaGuardadaId == null) {
                            // Mantiene la fecha ISO para bloquear horarios duplicados.
                            val cita = Repositorio.agendarCita(medicoId, fecha, hora, motivo)
                            if (cita == null) {
                                error = "El horario ya no está disponible o la sesión terminó."
                            } else {
                                citaGuardadaId = cita.id
                                navController.navigate(Rutas.citaExitosa(cita.id)) {
                                    popUpTo(Rutas.INICIO) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { margen ->
        Column(Modifier.fillMaxSize().padding(margen).verticalScroll(rememberScrollState()).padding(18.dp)) {
            if (medico == null || usuario == null) {
                Text("No se encontró la reserva o la sesión terminó.")
            } else {
                FichaMedico(medico)
                Spacer(Modifier.height(10.dp))
                DatoReserva(Icons.Outlined.CalendarMonth, "Fecha", CalendarioCitas.fechaLarga(fecha))
                DatoReserva(Icons.Outlined.Schedule, "Hora", if (fin.isBlank()) hora else "$hora a $fin")
                DatoReserva(Icons.Outlined.MedicalServices, "Tipo de atención", "Consulta presencial")
                DatoReserva(Icons.Outlined.LocationOn, "Dirección de ejemplo", "Av. Los Olivos 123\nLima")
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F2F6))
                Text("Paciente: ${usuario.nombres}", style = MaterialTheme.typography.bodySmall)
                Text(String.format(Locale.forLanguageTag("es-PE"), "Consulta: S/ %.2f", medico.precioConsulta),
                    Modifier.padding(top = 6.dp, bottom = 16.dp), style = MaterialTheme.typography.labelLarge)
                Text("Motivo de consulta (opcional)", style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = motivo, onValueChange = { motivo = it; error = null },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    placeholder = { Text("Consulta de rutina") },
                    minLines = 2, maxLines = 4, shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}
