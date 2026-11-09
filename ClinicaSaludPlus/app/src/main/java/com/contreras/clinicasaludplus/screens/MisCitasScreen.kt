package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.components.CitaCard
import com.contreras.clinicasaludplus.data.Cita
import com.contreras.clinicasaludplus.data.EstadoCita
import com.contreras.clinicasaludplus.navigation.Rutas

/**
 * Destino del drawer: lista de citas del paciente (LazyColumn).
 *
 * MEJORA (Fase 2, rama mejora-ia): se agrega la posibilidad de cancelar
 * una cita Confirmada. citaACancelar guarda temporalmente cual cita
 * esta pendiente de confirmar cancelacion (null = dialogo cerrado).
 * Esto sigue sin usar ViewModel: es el mismo patron remember/mutableStateOf
 * que en el resto del proyecto.
 */
@Composable
fun MisCitasScreen(citas: List<Cita>, navController: NavHostController) {
    var citaACancelar by remember { mutableStateOf<Cita?>(null) }

    DrawerScaffold(
        titulo = "Mis citas",
        rutaActual = Rutas.MIS_CITAS,
        navController = navController
    ) { padding ->
        if (citas.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aun no tienes citas agendadas")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(citas) { cita ->
                    CitaCard(
                        cita = cita,
                        onCancelar = { citaACancelar = cita }
                    )
                }
            }
        }
    }

    // Dialogo de confirmacion: solo se muestra si citaACancelar no es null
    citaACancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaACancelar = null },
            title = { Text("Cancelar cita") },
            text = {
                Text("Seguro que deseas cancelar la cita con ${cita.medico.nombre} el ${cita.fecha}, ${cita.hora}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    cita.estado = EstadoCita.CANCELADA
                    citaACancelar = null
                }) {
                    Text("Si, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { citaACancelar = null }) {
                    Text("No")
                }
            }
        )
    }
}