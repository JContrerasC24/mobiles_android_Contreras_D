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

/** Cuarto destino del drawer: perfil del paciente (Juan Perez). */
@Composable
fun PerfilUsuarioScreen(navController: NavHostController) {
    DrawerScaffold(
        titulo = "Perfil",
        rutaActual = Rutas.PERFIL_USUARIO,
        navController = navController
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Juan Perez", style = MaterialTheme.typography.titleLarge)
            Text("Paciente", style = MaterialTheme.typography.bodyMedium)
        }
    }
}