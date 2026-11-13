package com.tecsup.inventarioherramientas.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import com.tecsup.inventarioherramientas.R
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.inventarioherramientas.domain.model.*
import com.tecsup.inventarioherramientas.ui.*

@Composable
fun ListaScreen(ultimaSync: Long?, sesion: Sesion, estado: UiState<List<Item>>, actualizando: Boolean, aviso: String?,
    onSolicitudes: () -> Unit, mensajeAccion: String?, onRefrescar: () -> Unit, onFavorito: (String) -> Unit,
    onVerDetalle: (String) -> Unit, onVerFavoritos: () -> Unit, onVerAdmin: () -> Unit, onCerrarSesion: () -> Unit) {
    var consulta by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf("Todas") }
    var menu by remember { mutableStateOf(false) }
    val inventario = (estado as? UiState.Exito)?.datos.orEmpty()
    val visibles = inventario.filter {
        (it.nombre.contains(consulta.trim(), true) || it.codigo.contains(consulta.trim(), true)) &&
            when (filtro) {
                "Listas para préstamo" -> it.listaParaPrestamo
                "Prestadas" -> it.disponibilidad == "PRESTADA"
                else -> true
            }
    }
    val familias = inventario.groupBy { it.nombre.trim().lowercase() }
    val grupos = visibles.groupBy { it.nombre.trim().lowercase() }
    var grupoAbierto by rememberSaveable { mutableStateOf<String?>(null) }
    val unidades = grupos[grupoAbierto].orEmpty().sortedBy { it.codigo }
    if (unidades.isNotEmpty()) {
        SelectorUnidades(unidades, cerrar = { grupoAbierto = null }, abrir = { id ->
            grupoAbierto = null
            onVerDetalle(id)
        })
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
                                FilledTonalButton(onClick = { menu = true },
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White, contentColor = Petroleo)) {
                                    Icon(painterResource(R.drawable.ic_cuenta), null, Modifier.size(20.dp))
                                    Spacer(Modifier.width(7.dp))
                                    Text("Cuenta", fontWeight = FontWeight.Bold)
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
                        for (opcion in listOf("Todas", "Listas para préstamo", "Prestadas")) {
                            FilterChip(filtro == opcion, { filtro = opcion }, label = { Text(opcion) })
                        }
                    }
                    FilledTonalButton(onClick = onSolicitudes, modifier = Modifier.fillMaxWidth()) {
                        Text(if (sesion.rol == Rol.ADMIN) "Gestionar solicitudes  →" else "Mis solicitudes  →")
                    }
                    if (actualizando) LinearProgressIndicator(Modifier.fillMaxWidth())
                    ultimaSync?.let { fecha ->
                        val formato = java.text.SimpleDateFormat("dd MMM · HH:mm", java.util.Locale.forLanguageTag("es-PE"))
                        Text("Última actualización: " + formato.format(java.util.Date(fecha)), style = MaterialTheme.typography.bodySmall)
                    }
                    if (aviso != null) {
                        Text(aviso, style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = onRefrescar, enabled = !actualizando) { Text("Reintentar") }
                    }
                    listOfNotNull(mensajeAccion).forEach { Text(it, color = MaterialTheme.colorScheme.error) }
                    if (estado is UiState.Exito) Text("${grupos.size} tipos · ${visibles.size} unidades", style = MaterialTheme.typography.labelLarge)
                }
            }
            when (estado) {
                UiState.Cargando -> item { CircularProgressIndicator() }
                is UiState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Column { Text(estado.mensaje); TextButton(onClick = onRefrescar) { Text("Reintentar") } }
                }
                is UiState.Exito -> if (visibles.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { Text(if (inventario.isEmpty()) "El inventario está vacío." else "Sin coincidencias con estos filtros.") }
                } else items(grupos.keys.toList(), key = { it }) { grupo ->
                    val coincidencias = grupos.getValue(grupo)
                    val item = coincidencias.firstOrNull { it.listaParaPrestamo } ?: coincidencias.first()
                    val todas = familias.getValue(grupo)
                    HerramientaCard(item, { onFavorito(item.id) }, {
                        if (coincidencias.size == 1) onVerDetalle(item.id) else grupoAbierto = grupo
                    }, stock = "${todas.count { it.listaParaPrestamo }} de ${todas.size} listas")
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
    if (menu) PanelCuenta(sesion, actualizando, onRefrescar, onCerrarSesion, { menu = false })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PanelCuenta(sesion: Sesion, actualizando: Boolean, actualizar: () -> Unit,
    salir: () -> Unit, cerrar: () -> Unit) {
    ModalBottomSheet(onDismissRequest = cerrar, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(16.dp), color = Ambar) {
                    Icon(painterResource(R.drawable.ic_cuenta), null, Modifier.padding(13.dp).size(26.dp), tint = Petroleo)
                }
                Column(Modifier.padding(start = 14.dp)) {
                    Text(if (sesion.rol == Rol.ADMIN) "Cuenta de administrador" else "Mi cuenta",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(sesion.correo, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Button(onClick = actualizar, enabled = !actualizando,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Petroleo)) {
                if (actualizando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Icon(painterResource(R.drawable.ic_sincronizar), null, Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(if (actualizando) "Actualizando inventario…" else "Actualizar inventario")
            }
            FilledTonalButton(onClick = { cerrar(); salir() }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFFFEFEB), contentColor = Color(0xFF82392F))) {
                Icon(painterResource(R.drawable.ic_salir), null, Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text("Cerrar sesión")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorUnidades(unidades: List<Item>, cerrar: () -> Unit, abrir: (String) -> Unit) {
    val ordenadas = unidades.sortedWith(compareByDescending<Item> { it.listaParaPrestamo }.thenBy { it.codigo })
    val listas = unidades.count { it.listaParaPrestamo }
    ModalBottomSheet(onDismissRequest = cerrar, containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = Petroleo) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(shape = RoundedCornerShape(12.dp)) {
                        Foto(unidades.first().nombre, Modifier.size(58.dp))
                    }
                    Column {
                        Text(unidades.first().nombre, color = Color.White, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall)
                        Text("$listas de ${unidades.size} listas para préstamo", color = Color.White,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            androidx.compose.foundation.lazy.LazyColumn(Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ordenadas.size, key = { ordenadas[it].id }) { indice ->
                    val unidad = ordenadas[indice]
                    OutlinedCard(shape = RoundedCornerShape(16.dp), colors = CardDefaults.outlinedCardColors(containerColor = Color.White)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(unidad.codigo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(unidad.ubicacion, style = MaterialTheme.typography.bodySmall)
                                EstadoItem(unidad)
                            }
                            Button(onClick = { abrir(unidad.id) }, shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp), modifier = Modifier.heightIn(min = 48.dp)) {
                                Text("Ver ficha")
                            }
                        }
                    }
                }
            }
            OutlinedButton(onClick = cerrar, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text("Volver al catálogo")
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}