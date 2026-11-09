package com.contreras.tecsupfit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// Pantalla final del flujo secuencial mostrando el resumen

@Composable
fun ConfirmacionScreen(navController: NavController, nombreClase: String, horarioElegido: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Éxito",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        Text("¡Cupo reservado!", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Text(nombreClase, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text("Hoy, $horarioElegido", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("reservas") {
                    popUpTo("inicio") { inclusive = false }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mis reservas")
        }
    }
}