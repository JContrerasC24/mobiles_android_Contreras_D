package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*

// Filtra especialidades y presenta filas compactas con iconos.
@Composable
fun EspecialidadesScreen(navController: NavHostController) {
    var consulta by rememberSaveable { mutableStateOf("") }
    val especialidades = Repositorio.buscarEspecialidades(consulta)
    Column(Modifier.fillMaxSize().imePadding()) {
        CabeceraSalud("Especialidades", { navController.popBackStack() })
        OutlinedTextField(
            value = consulta, onValueChange = { consulta = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            placeholder = { Text("Buscar especialidad...") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true, shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF6F7FA),
                unfocusedBorderColor = Color.Transparent
            )
        )
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(18.dp)) {
            if (especialidades.isEmpty()) item { Text("No encontramos especialidades con ese nombre.") }
            items(especialidades, key = { it.id }) { especialidad ->
                Row(
                    Modifier.fillMaxWidth().clickable {
                        navController.navigate(Rutas.medicos(especialidad.id)) { launchSingleTop = true }
                    }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconoEspecialidad(especialidad.id)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(especialidad.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(especialidad.descripcion, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider(color = Color(0xFFF0F2F6))
            }
        }
    }
}
