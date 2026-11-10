package com.tecsup.inventarioherramientas.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.domain.model.Item
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun FavoritosScreen(
    estado: UiState<List<Item>>,
    mensaje: String?,
    onQuitarFavorito: (String) -> Unit,
    onVerDetalle: (String) -> Unit,
    onVolver: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onVolver) {
                    Text("Volver al inventario")
                }

                com.tecsup.inventarioherramientas.ui.Cabecera("Favoritos", "Mi colección")

                if (mensaje != null) {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        when (estado) {
            UiState.Cargando -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                item {
                    Text(
                        text = estado.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    item {
                        Text(
                            "Todavía no tienes herramientas favoritas. " +
                                    "Puedes marcarlas desde el inventario."
                        )
                    }
                } else {
                    items(
                        items = estado.datos,
                        key = { it.id }
                    ) { herramienta ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = herramienta.nombre,
                                    style =
                                        MaterialTheme.typography.titleLarge
                                )

                                Text("Código: ${herramienta.codigo}")
                                Text("Ubicación: ${herramienta.ubicacion}")
                                Text(
                                    "Disponibilidad: " +
                                            herramienta.disponibilidad
                                )

                                Button(
                                    onClick = {
                                        onVerDetalle(herramienta.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver detalle")
                                }

                                OutlinedButton(
                                    onClick = {
                                        onQuitarFavorito(herramienta.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Quitar de favoritos")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}