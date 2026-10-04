package com.tecsup.tecsupstore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaInicio(
    favoritos: List<Int>,
    onToggleFavorito: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(productosDemo) { producto ->
            TarjetaProducto(
                producto = producto,
                esFavorito = producto.id in favoritos,
                onToggleFavorito = { onToggleFavorito(producto.id) }
            )
        }
    }
}

@Composable
fun PantallaFavoritos(
    favoritos: List<Int>,
    onToggleFavorito: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lista = productosDemo.filter { it.id in favoritos }

    if (lista.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes favoritos")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lista) { producto ->
                TarjetaProducto(
                    producto = producto,
                    esFavorito = true,
                    onToggleFavorito = { onToggleFavorito(producto.id) }
                )
            }
        }
    }
}