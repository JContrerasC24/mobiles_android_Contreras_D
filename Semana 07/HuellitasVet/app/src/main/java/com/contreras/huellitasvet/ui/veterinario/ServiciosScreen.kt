package com.contreras.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import java.util.Locale

@Composable
fun ServiciosScreen(
    vm: ServiciosViewModel,
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Servicios",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        error?.let { mensaje ->
            item {
                Column {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = vm::limpiarError) {
                        Text("Cerrar")
                    }
                }
            }
        }

        if (servicios.isEmpty()) {
            item {
                Text("Aún no hay servicios disponibles")
            }
        }

        items(
            items = servicios,
            key = { it.id }
        ) { servicio ->
            ServicioItem(
                servicio = servicio,
                onToggle = { vm.alternar(servicio) },
                onEditar = { onEditar(servicio.id) }
            )
        }
    }
}

@Composable
private fun ServicioItem(
    servicio: Servicio,
    onToggle: () -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = servicio.activo,
                    onCheckedChange = { onToggle() }
                )
            }

            Text(servicio.descripcion)

            Text(
                text = "${servicio.duracionMin} min · ${
                    String.format(
                        Locale("es", "PE"),
                        "S/ %.2f",
                        servicio.precio
                    )
                }"
            )

            Text(
                text = if (servicio.activo) "Activo" else "Inactivo",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(onClick = onEditar) {
                Text("Editar")
            }
        }
    }
}