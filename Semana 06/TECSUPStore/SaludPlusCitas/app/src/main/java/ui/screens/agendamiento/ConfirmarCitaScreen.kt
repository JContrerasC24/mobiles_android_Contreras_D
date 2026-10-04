package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.util.CalendarioCitas
import java.util.Locale

// Presenta la fecha en español y confirma la reserva.
@Composable
fun ConfirmarCitaScreen(
    navController: NavHostController,
    medicoId: Int,
    fecha: String,
    hora: String
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val usuario = Repositorio.usuarioActual

    val especialidad = medico?.let {
        Repositorio.obtenerEspecialidad(it.especialidadId)
    }

    var motivo by rememberSaveable(medicoId, fecha, hora) {
        mutableStateOf("")
    }

    var error by remember(medicoId, fecha, hora) {
        mutableStateOf<String?>(null)
    }

    var citaGuardadaId by rememberSaveable(medicoId, fecha, hora) {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Confirmar cita",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Revisa los datos antes de reservar.")

        if (usuario == null) {
            Text(
                text = "Necesitas iniciar sesión para reservar.",
                color = MaterialTheme.colorScheme.error
            )
        } else if (medico == null) {
            Text(
                text = "No se encontró el médico seleccionado.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = medico.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = especialidad?.nombre ?: medico.descripcion
                    )

                    Text("Paciente: ${usuario.nombres}")

                    // Cambia la presentación sin modificar la fecha guardada.
                    Text(
                        text = "Fecha: ${CalendarioCitas.fechaLarga(fecha)}"
                    )

                    Text("Hora: $hora")

                    Text(
                        text = String.format(
                            Locale.forLanguageTag("es-PE"),
                            "Consulta: S/ %.2f",
                            medico.precioConsulta
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            OutlinedTextField(
                value = motivo,
                onValueChange = {
                    motivo = it
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Motivo de consulta") },
                supportingText = { Text("Opcional") },
                minLines = 3,
                maxLines = 5
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = citaGuardadaId == null,
                onClick = {
                    if (citaGuardadaId == null) {

                        // Conserva la fecha ISO para verificar y guardar horarios.
                        val cita = Repositorio.agendarCita(
                            medicoId = medicoId,
                            fecha = fecha,
                            hora = hora,
                            motivo = motivo
                        )

                        if (cita == null) {
                            error = "No se pudo reservar. Verifica tu sesión y el horario."
                        } else {
                            citaGuardadaId = cita.id

                            // Elimina el flujo de reserva y conserva Inicio.
                            navController.navigate(
                                Rutas.citaExitosa(cita.id)
                            ) {
                                popUpTo(Rutas.INICIO) {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }
                    }
                }
            ) {
                Text("Confirmar cita")
            }
        }
    }
}