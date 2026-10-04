package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas

// Muestra especialidades y permite buscar antes de seleccionar.
@Composable
fun EspecialidadesScreen(navController: NavHostController) {
    // Conserva la búsqueda cuando la pantalla se recrea.
    var consulta by rememberSaveable {
        mutableStateOf("")
    }

    // Actualiza los resultados cuando cambia el texto.
    val especialidades = Repositorio.buscarEspecialidades(consulta)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Regresa a la pantalla anterior.
        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Volver")
        }

        Text(
            text = "Especialidades",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Selecciona la especialidad para tu consulta.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Filtra por nombre o descripción mientras escribes.
        OutlinedTextField(
            value = consulta,
            onValueChange = {
                consulta = it
            },
            label = {
                Text("Buscar especialidad")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (especialidades.isEmpty()) {
            // Informa cuando la búsqueda no devuelve resultados.
            Text(
                text = "No encontramos especialidades con esa búsqueda.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // Genera únicamente los elementos necesarios para la lista.
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(
                    items = especialidades,
                    key = { it.id }
                ) { especialidad ->
                    // Envía el identificador de la especialidad seleccionada.
                    Card(
                        onClick = {
                            navController.navigate(
                                Rutas.medicos(especialidad.id)
                            ) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = especialidad.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = especialidad.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Ver médicos",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}