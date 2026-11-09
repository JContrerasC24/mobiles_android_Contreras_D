package com.tecsup.inventarioherramientas.ui.lista

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
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.domain.model.Sesion
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun ListaScreen(
    sesion: Sesion,
    estado: UiState<List<Item>>,
    actualizando: Boolean,
    aviso: String?,
    mensajeAccion: String?,
    onRefrescar: () -> Unit,
    onFavorito: (String) -> Unit,
    onVerDetalle: (String) -> Unit,
    onVerFavoritos: () -> Unit,
    onVerAdmin: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Inventario de herramientas",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(text = sesion.correo)

                Text(
                    text = if (sesion.rol == Rol.ADMIN) {
                        "Administrador"
                    } else {
                        "Usuario"
                    }
                )

                Button(
                    onClick = onRefrescar,
                    enabled = !actualizando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (actualizando) {
                            "Actualizando..."
                        } else {
                            "Actualizar inventario"
                        }
                    )
                }

                OutlinedButton(
                    onClick = onVerFavoritos,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver favoritos")
                }

                // Solo el administrador ve este acceso.
                if (sesion.rol == Rol.ADMIN) {
                    OutlinedButton(
                        onClick = onVerAdmin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Panel administrador")
                    }
                }

                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }

                if (aviso != null) {
                    Text(
                        text = aviso,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (mensajeAccion != null) {
                    Text(
                        text = mensajeAccion,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        when (estado) {
            UiState.Cargando -> {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Cargando herramientas...")
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = estado.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(
                            onClick = onRefrescar,
                            enabled = !actualizando
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    item {
                        Text("No hay herramientas registradas.")
                    }
                } else {
                    items(
                        items = estado.datos,
                        key = { herramienta -> herramienta.id }
                    ) { herramienta ->
                        HerramientaCard(
                            herramienta = herramienta,
                            onFavorito = {
                                onFavorito(herramienta.id)
                            },
                            onVerDetalle = {
                                onVerDetalle(herramienta.id)
                            }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Inventario del taller",
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun HerramientaCard(
    herramienta: Item,
    onFavorito: () -> Unit,
    onVerDetalle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = herramienta.nombre,
                style = MaterialTheme.typography.titleLarge
            )

            Text("Código: ${herramienta.codigo}")
            Text("Ubicación: ${herramienta.ubicacion}")
            Text("Condición: ${herramienta.condicion}")
            Text("Disponibilidad: ${herramienta.disponibilidad}")

            Button(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }

            OutlinedButton(
                onClick = onFavorito,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (herramienta.esFavorito) {
                        "Quitar de favoritos"
                    } else {
                        "Marcar favorito"
                    }
                )
            }
        }
    }
}