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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.data.Medico
import com.contreras.clinicasaludplus.navigation.Rutas
import com.contreras.clinicasaludplus.ui.theme.PurpleLight
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary

/**
 * Segundo paso del flujo secuencial: Perfil del medico.
 * Recibe "medico" por parametro de navegacion (via NavGraph, segun el id de la ruta).
 * Boton principal "Agendar cita" -> avanza al tercer paso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilMedicoScreen(medico: Medico, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del medico") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .size(90.dp)
                    .background(PurpleLight, CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalHospital,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                medico.nombre,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                "${medico.especialidad} - ${medico.aniosExperiencia} anios exp.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107))
                Text(" ${medico.calificacion} (${medico.numeroResenas} resenias)")
            }

            Text(
                medico.descripcion,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp),
                color = Color.DarkGray
            )

            Column(modifier = Modifier.weight(1f)) {}

            Button(
                onClick = { navController.navigate(Rutas.agendarCita(medico.id)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar cita")
            }
        }
    }
}