package com.contreras.saludplus.citas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Muestra una pantalla temporal con acciones de navegación.
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    descripcion: String,
    acciones: List<Pair<String, () -> Unit>> = emptyList()
) {
    // Permite desplazamiento cuando el contenido supera la pantalla.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Identifica la pantalla actual.
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium
        )

        // Explica qué falta implementar.
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge
        )

        // Crea un botón para cada acción recibida.
        acciones.forEach { (texto, accion) ->
            Button(
                onClick = accion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = texto)
            }
        }
    }
}