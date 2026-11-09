package com.contreras.tecsupfit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(navController: NavController, nombreClase: String, horarioBase: String) {
    val opcionesHorario = listOf(horarioBase, "8:00 pm (Extra)")
    var horarioSeleccionado by remember { mutableStateOf(opcionesHorario[0]) }

    // MEJORA IA: Estados para el Snackbar y Corrutinas
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // MEJORA IA: Envolvemos en Scaffold para poder mostrar el Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle de clase") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(nombreClase, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Entrenamiento de alta intensidad. Cupos limitados.")
                }
            }

            Text("Elige tu horario / cupo:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            opcionesHorario.forEach { horario ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (horario == horarioSeleccionado),
                            onClick = { horarioSeleccionado = horario }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (horario == horarioSeleccionado),
                        onClick = { horarioSeleccionado = horario }
                    )
                    Text(text = horario, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // MEJORA IA: Mostrar Snackbar y simular carga antes de navegar
                    scope.launch {
                        snackbarHostState.showSnackbar("Procesando reserva...")
                        // Tiempo de carga ajustado para mejor visualizacion
                        delay(1200) // Simula un tiempo de carga
                        navController.navigate("confirmacion/$nombreClase/$horarioSeleccionado")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Reservar cupo", fontSize = 16.sp)
            }
        }
    }
}