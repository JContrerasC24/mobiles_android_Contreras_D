package com.contreras.tecsupfit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Importaciones obligatorias para usar 'by remember'
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class ClaseGym(val nombre: String, val horario: String, val sala: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navController: NavController) {

    // los columns sirven para poder apilar uno debajo del otro
    val clases = listOf(
        ClaseGym("Yoga funcional", "7:00 am", "Sala 2"),
        ClaseGym("Cross Training", "6:00 pm", "Sala 1"),
        ClaseGym("Spinning", "7:30 pm", "Sala 3")
    )

    var filtroSeleccionado by remember { mutableStateOf("Hoy") }
    val filtros = listOf("Hoy", "Esta semana")

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TECSUP Fit", color = MaterialTheme.colorScheme.onPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Hola, Jose Abraham", color = MaterialTheme.colorScheme.onPrimary)
            }

        }

        LazyRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtros) { filtro ->
                FilterChip(
                    selected = filtroSeleccionado == filtro,
                    onClick = { filtroSeleccionado = filtro },
                    label = { Text(filtro) }
                )
            }
        }

        Text("Clases disponibles", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clases) { clase ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("detalle/${clase.nombre}/${clase.horario}")
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    // Implementación de LazyRow para filtros por día
                    Row(modifier = Modifier.padding(16.dp)) {
                        Column {
                            Text(clase.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("${clase.horario} - ${clase.sala}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}