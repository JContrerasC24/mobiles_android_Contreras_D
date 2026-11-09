package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.navigation.Rutas

/** Destino del drawer: historial medico (pantalla independiente, aun sin datos reales). */
@Composable
fun HistorialMedicoScreen(navController: NavHostController) {
    DrawerScaffold(
        titulo = "Historial medico",
        rutaActual = Rutas.HISTORIAL,
        navController = navController
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Aqui se mostrara el historial medico del paciente.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}