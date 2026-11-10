package com.tecsup.inventarioherramientas.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.inventarioherramientas.domain.model.*
import com.tecsup.inventarioherramientas.ui.*

@Composable
fun ListaScreen(sesion: Sesion, estado: UiState<List<Item>>, actualizando: Boolean, aviso: String?,
    mensajeAccion: String?, onRefrescar: () -> Unit, onFavorito: (String) -> Unit,
    onVerDetalle: (String) -> Unit, onVerFavoritos: () -> Unit, onVerAdmin: () -> Unit, onCerrarSesion: () -> Unit) {
    var consulta by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf("Todas") }
    var menu by remember { mutableStateOf(false) }
    val inventario = (estado as? UiState.Exito)?.datos.orEmpty()
    val visibles = inventario.filter {
        (it.nombre.contains(consulta.trim(), true) || it.codigo.contains(consulta.trim(), true)) &&
            when (filtro) {
                "Disponibles" -> it.disponibilidad == "DISPONIBLE"
                "Prestadas" -> it.disponibilidad == "PRESTADA"
                else -> true
            }
    }
    Column(Modifier.fillMaxSize()) {
        LazyVerticalGrid(GridCells.Adaptive(150.dp), modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(22.dp), color = Petroleo) {
                        Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Petroleo, Turquesa))).padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("INVENTARIO DEL TALLER", color = Color(0xFFBCE8E1), fontSize = 10.sp, letterSpacing = 2.sp)
                                    Text("Catálogo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                                }
                                Box {
                                    TextButton(onClick = { menu = true }) { Text("Cuenta", color = Color.White) }
                                    DropdownMenu(menu, { menu = false }) {
                                        DropdownMenuItem(text = { Text(sesion.correo) }, onClick = {}, enabled = false)
                                        DropdownMenuItem(text = { Text("Actualizar inventario") }, enabled = !actualizando,
                                            onClick = { menu = false; onRefrescar() })
                                        DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = { menu = false; onCerrarSesion() })
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(consulta, { consulta = it }, Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar herramienta o código") }, singleLine = true,
                                shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (opcion in listOf("Todas", "Disponibles", "Prestadas")) {
                            FilterChip(filtro == opcion, { filtro = opcion }, label = { Text(opcion) })
                        }
                    }
                    if (actualizando) LinearProgressIndicator(Modifier.fillMaxWidth())
                    listOfNotNull(aviso, mensajeAccion).forEach { Text(it, color = MaterialTheme.colorScheme.error) }
                    if (estado is UiState.Exito) Text("${visibles.size} herramientas", style = MaterialTheme.typography.labelLarge)
                }
            }
            when (estado) {
                UiState.Cargando -> item { CircularProgressIndicator() }
                is UiState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Column { Text(estado.mensaje); TextButton(onClick = onRefrescar) { Text("Reintentar") } }
                }
                is UiState.Exito -> if (visibles.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { Text(if (inventario.isEmpty()) "El inventario está vacío." else "Sin coincidencias con estos filtros.") }
                } else items(visibles, key = { it.id }) { item ->
                    HerramientaCard(item, { onFavorito(item.id) }, { onVerDetalle(item.id) })
                }
            }
        }
        NavigationBar(containerColor = Color.White, windowInsets = WindowInsets(0,0,0,0)) {
            NavigationBarItem(true, {}, icon = { Text("▦", fontSize = 22.sp) }, label = { Text("Catálogo") })
            NavigationBarItem(false, onVerFavoritos, icon = { Text("♡", fontSize = 22.sp) }, label = { Text("Favoritos") })
            if (sesion.rol == Rol.ADMIN) NavigationBarItem(false, onVerAdmin,
                icon = { Text("▤", fontSize = 22.sp) }, label = { Text("Administración") })
        }
    }
}
