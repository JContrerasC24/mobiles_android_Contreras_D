package com.contreras.saludplus.citas.ui.screens.resultados

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.model.Resultado
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.PantallaPrincipal

// Presenta una colección fija de resultados de demostración.
@Composable
fun ResultadosScreen(navController: NavHostController) {
    val resultados = remember {
        listOf(
            Resultado(
                id = 1,
                nombre = "Hemograma de ejemplo",
                fecha = "2026-09-20",
                estado = "Disponible"
            ),
            Resultado(
                id = 2,
                nombre = "Análisis de orina de ejemplo",
                fecha = "2026-09-25",
                estado = "En proceso"
            )
        )
    }

    PantallaPrincipal(
        navController = navController,
        destinoActual = Rutas.RESULTADOS
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Resultados",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text("Datos de demostración para el proyecto académico.")
            }

            items(
                items = resultados,
                key = { it.id }
            ) { resultado ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = resultado.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Fecha: ${resultado.fecha}")

                        Text(
                            text = "Estado: ${resultado.estado}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}