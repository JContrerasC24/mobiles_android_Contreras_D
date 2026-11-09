package com.contreras.clinicasaludplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.contreras.clinicasaludplus.data.Cita
import com.contreras.clinicasaludplus.data.EstadoCita
import com.contreras.clinicasaludplus.ui.theme.GrisCompletada
import com.contreras.clinicasaludplus.ui.theme.GrisCompletadaBg
import com.contreras.clinicasaludplus.ui.theme.RojoCancelada
import com.contreras.clinicasaludplus.ui.theme.RojoCanceladaBg
import com.contreras.clinicasaludplus.ui.theme.VerdeConfirmada
import com.contreras.clinicasaludplus.ui.theme.VerdeConfirmadaBg

/**
 * Tarjeta de una cita en "Mis citas". El color del badge cambia
 * segun el estado (verde = Confirmada, gris = Completada, rojo =
 * Cancelada). El boton "Cancelar" solo se muestra si la cita todavia
 * esta Confirmada (no tiene sentido cancelar algo ya completado o
 * ya cancelado).
 *
 * onCancelar es un callback: quien use CitaCard decide que pasa al
 * presionar cancelar (en MisCitasScreen, abrira el AlertDialog).
 */
@Composable
fun CitaCard(cita: Cita, onCancelar: () -> Unit) {
    val (bgBadge, colorTexto, textoEstado) = when (cita.estado) {
        EstadoCita.CONFIRMADA -> Triple(VerdeConfirmadaBg, VerdeConfirmada, "Confirmada")
        EstadoCita.COMPLETADA -> Triple(GrisCompletadaBg, GrisCompletada, "Completada")
        EstadoCita.CANCELADA -> Triple(RojoCanceladaBg, RojoCancelada, "Cancelada")
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(cita.medico.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                "${cita.fecha}, ${cita.hora}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(bgBadge, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(textoEstado, color = colorTexto, style = MaterialTheme.typography.labelMedium)
                }

                if (cita.estado == EstadoCita.CONFIRMADA) {
                    TextButton(onClick = onCancelar) {
                        Text("Cancelar", color = RojoCancelada)
                    }
                }
            }
        }
    }
}