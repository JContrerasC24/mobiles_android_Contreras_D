package com.contreras.campusgps.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BuscadorLugares(consulta: String, categoria: String?, categorias: List<String>,
    onConsulta: (String) -> Unit, onCategoria: (String?) -> Unit) {
    OutlinedTextField(value = consulta, onValueChange = onConsulta,
        label = { Text("Buscar por nombre") }, singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = { if (consulta.isNotEmpty()) TextButton(onClick = { onConsulta("") }) { Text("Limpiar") } })
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = categoria == null, onClick = { onCategoria(null) }, label = { Text("Todas") })
        categorias.forEach { item ->
            FilterChip(selected = categoria == item, onClick = { onCategoria(item) }, label = { Text(item) })
        }
    }
}
