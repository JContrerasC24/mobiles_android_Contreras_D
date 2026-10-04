package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import java.util.Locale

// Muestra médicos pertenecientes a la especialidad recibida.
@Composable
fun MedicosScreen(
    navController: NavHostController,
    especialidadId: Int
) {
    // Reinicia la búsqueda cuando cambia la especialidad.
    var consulta by rememberSaveable(especialidadId) {
        mutableStateOf("")
    }

    // Consulta la especialidad y sus médicos ordenados.
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val medicos = Repositorio.buscarMedicos(
        especialidadId = especialidadId,
        consulta = consulta
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Regresa a la selección de especialidades.
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Volver")
        }

        Text(
            text = "Médicos",
            style = MaterialTheme.typography.headlineMedium
        )

        if (especialidad == null) {
            // Evita mostrar información para una especialidad inexistente.
            Text(
                text = "La especialidad seleccionada no existe.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(
                text = especialidad.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Ordenados por mayor calificación.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Filtra médicos dentro de esta especialidad.
            OutlinedTextField(
                value = consulta,
                onValueChange = {
                    consulta = it
                },
                label = {
                    Text("Buscar médico")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (medicos.isEmpty()) {
                // Informa cuando no existen médicos para la búsqueda.
                Text(
                    text = "No encontramos médicos con esa búsqueda.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Presenta los médicos mediante una lista desplazable.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(
                        items = medicos,
                        key = { it.id }
                    ) { medico ->
                        // Envía el médico seleccionado a Fecha y hora.
                        Card(
                            onClick = {
                                navController.navigate(
                                    Rutas.fechaHora(medico.id)
                                ) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = medico.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = medico.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Calificación: ${medico.calificacion}/5",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // Presenta el precio con dos decimales.
                                Text(
                                    text = String.format(
                                        Locale("es", "PE"),
                                        "Consulta: S/ %.2f",
                                        medico.precioConsulta
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Seleccionar médico",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}