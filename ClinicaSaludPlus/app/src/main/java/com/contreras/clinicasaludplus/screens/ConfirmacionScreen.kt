package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.data.Cita
import com.contreras.clinicasaludplus.navigation.Rutas

/**
 * Ultimo paso del flujo secuencial: Confirmacion.
 * Muestra el resumen de la cita recien agendada y permite ir
 * directo a "Mis citas" (igual que el mockup: boton "Ver mis citas").
 */
@Composable
fun ConfirmacionScreen(cita: Cita, navController: NavHostController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFDFF5E1), CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                "Cita agendada!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(cita.medico.nombre, modifier = Modifier.padding(top = 8.dp))
            Text("${cita.fecha}, ${cita.hora}", color = Color.Gray)

            Button(
                onClick = {
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.INICIO)
                    }
                },
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Text("Ver mis citas")
            }
        }
    }
}