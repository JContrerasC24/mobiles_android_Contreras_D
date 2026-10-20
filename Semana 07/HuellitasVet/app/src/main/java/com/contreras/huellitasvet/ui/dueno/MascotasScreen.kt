package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.ui.components.*
import com.contreras.huellitasvet.viewmodel.InicioViewModel
import java.time.LocalDate

@Composable
fun MascotasScreen(
    vm: InicioViewModel,
    onMascota: (Int) -> Unit,
    onAgregar: () -> Unit,
    onVolver: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val anioActual = LocalDate.now().year

    Scaffold(
        contentWindowInsets=WindowInsets(0,0,0,0),
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { VetHeader("Mis mascotas") }
            error?.let { mensaje -> item { Text(mensaje, color = MaterialTheme.colorScheme.error) } }
            if (mascotas.isEmpty()) {
                item { Text("Todavía no tienes mascotas registradas. Usa + para agregar una.") }
            }
            items(mascotas, key = { it.id }) { mascota ->
                Card(onClick = { onMascota(mascota.id) }, modifier = Modifier.fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),border=androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant)) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MascotaAvatar(especie = mascota.especie)
                        Column(Modifier.weight(1f)) {
                            Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${mascota.especie.lowercase().replaceFirstChar { it.titlecase() }} · ${mascota.raza}")
                            val edad = anioActual - mascota.anioNacimiento
                            Text("$edad ${if (edad == 1) "año" else "años"}")
                        }
                    }
                }
            }
        }
    }
}
