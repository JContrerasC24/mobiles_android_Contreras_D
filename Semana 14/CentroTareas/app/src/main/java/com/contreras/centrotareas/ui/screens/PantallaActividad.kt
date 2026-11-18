package com.contreras.centrotareas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contreras.centrotareas.model.ActividadRegistro
import com.contreras.centrotareas.ui.components.NotionCard
import com.contreras.centrotareas.ui.components.NotionPrimaryButton
import com.contreras.centrotareas.ui.components.NotionSecondaryButton
import com.contreras.centrotareas.ui.components.NotionStatusBadge
import com.contreras.centrotareas.viewmodel.TareasViewModel

/**
 * Pantalla que muestra el historial dinámico de operaciones en tiempo real con filtros por estado.
 */
@Composable
fun PantallaActividad(
    vm: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val historial by vm.historial.collectAsState()
    var filtroEstado by remember { mutableStateOf("Todas") }

    val historialFiltrado = when (filtroEstado) {
        "Completadas" -> historial.filter { it.estado.equals("Completado", ignoreCase = true) }
        "Canceladas" -> historial.filter { it.estado.equals("Cancelado", ignoreCase = true) }
        "Errores" -> historial.filter { it.estado.equals("Error", ignoreCase = true) }
        else -> historial
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ENCABEZADO Y BOTÓN DE LIMPIEZA
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial de Actividad",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Registro dinámico de operaciones ejecutadas en la sesión.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (historial.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    NotionSecondaryButton(
                        text = "Limpiar",
                        icon = Icons.Default.Delete,
                        onClick = { vm.limpiarHistorial() }
                    )
                }
            }
        }

        // CHIPS DE FILTRADO FUNCIONALES
        if (historial.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todas", "Completadas", "Canceladas", "Errores").forEach { filtro ->
                        val isSelected = filtroEstado == filtro
                        if (isSelected) {
                            NotionPrimaryButton(
                                text = filtro,
                                onClick = { filtroEstado = filtro },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            NotionSecondaryButton(
                                text = filtro,
                                onClick = { filtroEstado = filtro },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ESTADO VACÍO ELEGANTE O LISTA DE REGISTROS
        if (historialFiltrado.isEmpty()) {
            item {
                NotionCard(
                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (historial.isEmpty()) "Aún no se han registrado operaciones." else "No hay registros bajo el filtro '$filtroEstado'.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ejecuta procesos, cálculos o comparaciones para generar actividad real.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        } else {
            items(
                items = historialFiltrado,
                key = { it.id }
            ) { registro ->
                ItemHistorialCard(registro = registro)
            }
        }
    }
}

@Composable
private fun ItemHistorialCard(
    registro: ActividadRegistro,
    modifier: Modifier = Modifier
) {
    NotionCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = registro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                NotionStatusBadge(status = registro.estado)
            }

            Text(
                text = registro.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hora: ${registro.horaFormateada}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (registro.duracionMs != null) {
                    Text(
                        text = "Duración: ${registro.duracionMs} ms",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
