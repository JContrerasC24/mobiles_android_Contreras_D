package com.contreras.saludplus.citas.ui.screens.agendamiento

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Permite elegir una fecha y un horario disponible.
@Composable
fun FechaHoraScreen(
    navController: NavHostController,
    medicoId: Int
) {
    val contexto = LocalContext.current

    // Mantiene el formato interno utilizado por las rutas.
    val formatoFecha = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    // Inicia la selección con la fecha actual del dispositivo.
    var fechaSeleccionada by rememberSaveable(medicoId) {
        mutableStateOf(
            formatoFecha.format(Calendar.getInstance().time)
        )
    }

    // Conserva la hora seleccionada durante recreaciones.
    var horaSeleccionada by rememberSaveable(medicoId) {
        mutableStateOf<String?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    // Consulta el médico y los horarios de la fecha seleccionada.
    val medico = Repositorio.obtenerMedico(medicoId)
    val horarios = Repositorio.horariosDisponibles(
        medicoId = medicoId,
        fecha = fechaSeleccionada
    )

    // Invalida visualmente una selección que dejó de estar disponible.
    val horaValida = horaSeleccionada?.takeIf {
        it in horarios
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Regresa a la selección del médico.
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Volver")
        }

        Text(
            text = "Fecha y hora",
            style = MaterialTheme.typography.headlineMedium
        )

        if (medico == null) {
            // Evita continuar con un identificador inexistente.
            Text(
                text = "El médico seleccionado no existe.",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(
                text = medico.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = medico.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Fecha seleccionada: $fechaSeleccionada",
                style = MaterialTheme.typography.bodyLarge
            )

            // Abre el calendario nativo de Android.
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val fechaInicial = Calendar.getInstance().apply {
                        time = requireNotNull(
                            formatoFecha.parse(fechaSeleccionada)
                        )
                    }

                    val calendario = DatePickerDialog(
                        contexto,
                        { _, anio, mes, dia ->
                            // Convierte la selección al formato interno.
                            val nuevaFecha = Calendar.getInstance().apply {
                                set(anio, mes, dia)
                            }

                            fechaSeleccionada = formatoFecha.format(
                                nuevaFecha.time
                            )

                            // Reinicia la hora al cambiar la fecha.
                            horaSeleccionada = null
                            error = null
                        },
                        fechaInicial.get(Calendar.YEAR),
                        fechaInicial.get(Calendar.MONTH),
                        fechaInicial.get(Calendar.DAY_OF_MONTH)
                    )

                    // Impide seleccionar días anteriores a hoy.
                    val inicioHoy = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    calendario.datePicker.minDate = inicioHoy.timeInMillis
                    calendario.show()
                }
            ) {
                Text("Elegir fecha")
            }

            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.titleMedium
            )

            if (horarios.isEmpty()) {
                // Explica por qué la cuadrícula está vacía.
                Text(
                    text = "No quedan horarios disponibles. Selecciona otra fecha.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Distribuye los horarios disponibles en tres columnas.
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = horarios,
                    key = { it }
                ) { hora ->
                    val seleccionada = hora == horaValida

                    // Destaca el horario seleccionado mediante sus colores.
                    Card(
                        onClick = {
                            horaSeleccionada = hora
                            error = null
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (seleccionada) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = hora,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Text(
                text = horaValida?.let {
                    "Hora seleccionada: $it"
                } ?: "Selecciona un horario para continuar."
            )

            error?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                enabled = horaValida != null,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val hora = horaValida

                    // Revisa nuevamente la disponibilidad antes de avanzar.
                    val disponibles = Repositorio.horariosDisponibles(
                        medicoId = medicoId,
                        fecha = fechaSeleccionada
                    )

                    if (hora != null && hora in disponibles) {
                        // Envía médico, fecha y hora a Confirmar cita.
                        navController.navigate(
                            Rutas.confirmarCita(
                                medicoId = medicoId,
                                fecha = fechaSeleccionada,
                                hora = hora
                            )
                        ) {
                            launchSingleTop = true
                        }
                    } else {
                        horaSeleccionada = null
                        error = "El horario ya no está disponible."
                    }
                }
            ) {
                Text("Continuar")
            }
        }
    }
}