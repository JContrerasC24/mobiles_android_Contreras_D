package com.contreras.clinicasaludplus.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.contreras.clinicasaludplus.components.AppDrawerContent
import com.contreras.clinicasaludplus.navigation.Rutas
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

/**
 * Scaffold reutilizable para los destinos del drawer (Mis citas,
 * Historial medico, Perfil). Evita repetir topBar + ModalNavigationDrawer
 * + padding en cada pantalla (requisito "Scaffold correcto" en todas
 * las pantallas).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(
    titulo: String,
    rutaActual: String,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(rutaActual = rutaActual) { ruta ->
                scope.launch { drawerState.close() }
                if (ruta != rutaActual) {
                    navController.navigate(ruta) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
                )
            }
        ) { padding ->
            content(padding)
        }
    }
}