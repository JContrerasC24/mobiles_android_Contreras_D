package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.components.SeleccionChip
import com.contreras.clinicasaludplus.data.FechaOpcion
import com.contreras.clinicasaludplus.data.HoraOpcion
import com.contreras.clinicasaludplus.data.Medico
import com.contreras.clinicasaludplus.data.fechasDisponibles
import com.contreras.clinicasaludplus.data.horasDisponibles

/**
 * Tercer paso del flujo: seleccion de fecha y hora.
 * Ambas listas son de SELECCION UNICA: cada una se controla con un solo
 * mutableStateOf que guarda la opcion marcada (no una lista de marcados).
 * El boton "Confirmar cita" solo se habilita si ambas fueron elegidas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    medico: Medico,
    navController: NavHostController,
    onCitaConfirmada: (fecha: String, hora: String) -> Unit
) {
    var fechaElegida by remember { mutableStateOf<FechaOpcion?>(null) }
    var horaElegida by remember { mutableStateOf<HoraOpcion?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Cita con ${medico.nombre}", style = MaterialTheme.typography.titleMedium)

            Text("Selecciona fecha", modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
            Row {
                fechasDisponibles.forEach { opcion ->
                    SeleccionChip(
                        texto = opcion.chipLabel,
                        seleccionado = fechaElegida == opcion,
                        onClick = { fechaElegida = opcion }
                    )
                }
            }

            Text("Selecciona hora", modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            Row {
                horasDisponibles.forEach { opcion ->
                    SeleccionChip(
                        texto = opcion.chipLabel,
                        seleccionado = horaElegida == opcion,
                        onClick = { horaElegida = opcion }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val f = fechaElegida ?: return@Button
                    val h = horaElegida ?: return@Button
                    onCitaConfirmada(f.nombreCompleto, h.horaCompleta)
                },
                enabled = fechaElegida != null && horaElegida != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar cita")
            }
        }
    }
}