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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import com.tecsup.inventarioherramientas.ui.Cabecera
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
    var consulta by rememberSaveable { mutableStateOf("") }
    var disponibles by rememberSaveable { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
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
                Cabecera("Catálogo", sesion.correo)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onVerFavoritos) { Text("Favoritos") }
                    Box {
                        OutlinedButton(onClick = { menu = true }) { Text("Opciones") }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            if (sesion.rol == Rol.ADMIN) {
                                DropdownMenuItem(text = { Text("Administración") }, onClick = { menu = false; onVerAdmin() })
                            }
                            DropdownMenuItem(text = { Text("Actualizar inventario") }, enabled = !actualizando,
                                onClick = { menu = false; onRefrescar() })
                            DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = { menu = false; onCerrarSesion() })
                        }
                    }
                }
                OutlinedTextField(value = consulta, onValueChange = { consulta = it },
                    label = { Text("Buscar por nombre o código") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                FilterChip(selected = disponibles, onClick = { disponibles = !disponibles },
                    label = { Text("Solo disponibles") })
                if (actualizando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                val visibles = estado.datos.filter { item ->
                    (item.nombre.contains(consulta.trim(), ignoreCase = true) ||
                        item.codigo.contains(consulta.trim(), ignoreCase = true)) &&
                        (!disponibles || item.disponibilidad == "DISPONIBLE")
                }
                if (visibles.isEmpty()) {
                    item {
                        Text(if (estado.datos.isEmpty()) "No hay herramientas registradas." else "Ninguna herramienta coincide con la búsqueda.")
                    }
                } else {
                    items(
                        items = visibles,
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