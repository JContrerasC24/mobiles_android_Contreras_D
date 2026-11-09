package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.components.AppDrawerContent
import com.contreras.clinicasaludplus.components.EspecialidadChip
import com.contreras.clinicasaludplus.components.MedicoListItem
import com.contreras.clinicasaludplus.data.especialidades
import com.contreras.clinicasaludplus.data.listaMedicos
import com.contreras.clinicasaludplus.navigation.Rutas
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

/**
 * Pantalla de Inicio.
 * - Requisito "Scaffold correcto": topBar + drawer + content con padding.
 * - Requisito "Listas": LazyRow de especialidades + LazyColumn de medicos.
 * - Es el punto de partida del flujo secuencial (Inicio -> Perfil -> Agendar -> Confirmacion).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Filtro de especialidad: seleccion unica (o "todas" si nada esta elegido)
    var especialidadSeleccionada by remember { mutableStateOf<String?>(null) }

    val medicosFiltrados = if (especialidadSeleccionada == null) {
        listaMedicos
    } else {
        listaMedicos.filter { it.especialidad == especialidadSeleccionada }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(rutaActual = Rutas.INICIO) { ruta ->
                scope.launch { drawerState.close() }
                if (ruta != Rutas.INICIO) navController.navigate(ruta)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Clinica Salud+", color = Color.White)
                            Text("Hola, Juan", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Especialidades", style = MaterialTheme.typography.titleMedium)
                LazyRow(modifier = Modifier.padding(vertical = 12.dp)) {
                    items(especialidades) { especialidad ->
                        EspecialidadChip(
                            nombre = especialidad,
                            seleccionado = especialidadSeleccionada == especialidad,
                            onClick = {
                                especialidadSeleccionada =
                                    if (especialidadSeleccionada == especialidad) null else especialidad
                            }
                        )
                    }
                }

                Text("Medicos disponibles", style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                    items(medicosFiltrados) { medico ->
                        MedicoListItem(medico = medico) {
                            navController.navigate(Rutas.perfilMedico(medico.id))
                        }
                    }
                }
            }
        }
    }
}