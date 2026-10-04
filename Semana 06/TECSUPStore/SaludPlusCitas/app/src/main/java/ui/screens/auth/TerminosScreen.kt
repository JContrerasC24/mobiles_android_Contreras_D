package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Explica las condiciones del prototipo mediante contenido desplazable.
@Composable
fun TerminosScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver")
        }

        Text(
            text = "Términos y condiciones",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "1. Finalidad del proyecto",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "SaludPlus es un prototipo académico que permite practicar " +
                    "el registro de pacientes y la reserva de citas."
        )

        Text(
            text = "2. Datos de prueba",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "Utiliza información ficticia para probar el registro. " +
                    "No ingreses contraseñas utilizadas en otros servicios."
        )

        Text(
            text = "3. Almacenamiento temporal",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "Los usuarios y las citas se almacenan en memoria. " +
                    "Los datos se pierden cuando termina el proceso de la aplicación."
        )

        Text(
            text = "4. Reservas y cancelaciones",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "Puedes reservar horarios disponibles y cancelar tus citas " +
                    "desde la pantalla de detalle."
        )

        Text(
            text = "5. Resultados y avisos",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            "Los resultados son ejemplos de demostración. " +
                    "Los avisos se muestran dentro de la aplicación."
        )

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Volver al registro")
        }
    }
}