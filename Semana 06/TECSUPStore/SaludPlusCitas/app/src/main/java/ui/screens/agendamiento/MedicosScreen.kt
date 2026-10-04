package com.contreras.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.*

// Presenta médicos filtrados, retratos y disponibilidad calculada.
@Composable
fun MedicosScreen(navController: NavHostController, especialidadId: Int) {
    var consulta by rememberSaveable(especialidadId) { mutableStateOf("") }
    var buscar by rememberSaveable(especialidadId) { mutableStateOf(false) }
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val medicos = Repositorio.buscarMedicos(especialidadId, consulta)
    Column(Modifier.fillMaxSize().imePadding()) {
        CabeceraSalud("Médicos de ${especialidad?.nombre ?: "la especialidad"}",
            volver = { navController.popBackStack() }, accion = {
                IconButton(onClick = { buscar = !buscar; if (!buscar) consulta = "" }) {
                    Icon(Icons.Filled.Search, "Buscar médico")
                }
            })
        if (buscar) {
            OutlinedTextField(consulta, { consulta = it },
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
                placeholder = { Text("Buscar médico...") }, singleLine = true,
                shape = RoundedCornerShape(10.dp))
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (medicos.isEmpty()) item { Text("No encontramos médicos para esta búsqueda.") }
            items(medicos, key = { it.id }) { medico ->
                FichaMedico(medico, mostrarValoracion = true, mostrarDisponibilidad = true,
                    seleccionar = {
                        navController.navigate(Rutas.fechaHora(medico.id)) { launchSingleTop = true }
                    })
            }
        }
    }
}
