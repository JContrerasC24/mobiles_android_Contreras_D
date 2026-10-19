package com.contreras.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.ui.dueno.InicioScreen
import com.contreras.huellitasvet.ui.dueno.MascotasScreen
import com.contreras.huellitasvet.ui.dueno.FormularioMascotaScreen
import com.contreras.huellitasvet.ui.dueno.FichaMascotaScreen
import com.contreras.huellitasvet.viewmodel.MascotaViewModel
import com.contreras.huellitasvet.viewmodel.InicioViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.contreras.huellitasvet.model.Rol
import com.contreras.huellitasvet.ui.components.VetButton
import com.contreras.huellitasvet.ui.dueno.LoginScreen
import com.contreras.huellitasvet.ui.dueno.SplashScreen
import com.contreras.huellitasvet.ui.veterinario.ServiciosScreen
import com.contreras.huellitasvet.viewmodel.ServiciosViewModel
import com.contreras.huellitasvet.viewmodel.SesionViewModel

@Composable
fun NavVet(
    sesion: SesionViewModel,
    servicios: ServiciosViewModel,
    inicioFactory: (Usuario) -> ViewModelProvider.Factory,
    mascotaFactory: (Usuario, Int?) -> ViewModelProvider.Factory,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val inicializando by sesion.inicializando.collectAsStateWithLifecycle()
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val ocupado by sesion.ocupado.collectAsStateWithLifecycle()
    val error by sesion.error.collectAsStateWithLifecycle()
    var avisoEdicion by remember { mutableStateOf(false) }

    LaunchedEffect(inicializando, usuario?.id) {
        if (!inicializando) {
            val destino = when (usuario?.rol) {
                Rol.CLIENTE.name -> Rutas.INICIO
                Rol.ADMIN.name -> Rutas.PANEL
                else -> Rutas.LOGIN
            }
            navController.navigate(destino) {
                popUpTo(navController.graph.id) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        composable(Rutas.SPLASH) { SplashScreen() }
        composable(Rutas.LOGIN) { LoginScreen(sesion) }

        composable(Rutas.INICIO) {
            val dueno = usuario
            if (dueno?.rol == Rol.CLIENTE.name) {
                val inicio: InicioViewModel = viewModel(
                    key = "inicio-${dueno.id}", factory = inicioFactory(dueno)
                )
                InicioScreen(
                    vm = inicio,
                    onMascota = { navController.navigate("mascota/$it") },
                    onAgregar = { navController.navigate(Rutas.AGREGAR_MASCOTA) },
                    onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) },
                    onVerMascotas = { navController.navigate(Rutas.MASCOTAS) },
                    onCerrarSesion = sesion::cerrarSesion,
                    cerrandoSesion = ocupado,
                    errorSesion = error
                )
            }
        }
        composable(Rutas.MASCOTAS) {
            val dueno = usuario
            if (dueno?.rol == Rol.CLIENTE.name) {
                val inicio: InicioViewModel = viewModel(
                    key = "mascotas-${dueno.id}", factory = inicioFactory(dueno)
                )
                MascotasScreen(
                    vm = inicio,
                    onMascota = { navController.navigate("mascota/$it") },
                    onAgregar = { navController.navigate(Rutas.AGREGAR_MASCOTA) },
                    onVolver = { navController.popBackStack() }
                )
            }
        }
        composable(Rutas.AGREGAR_MASCOTA) {
            val dueno = usuario
            if (dueno?.rol == Rol.CLIENTE.name) {
                val vm: MascotaViewModel = viewModel(factory = mascotaFactory(dueno, null))
                FormularioMascotaScreen(vm = vm, onVolver = { navController.popBackStack() })
            }
        }
        composable(
            Rutas.FICHA_MASCOTA,
            arguments = listOf(navArgument("mascotaId") { type = NavType.IntType })
        ) { entrada ->
            val dueno = usuario
            val id = entrada.arguments?.getInt("mascotaId") ?: return@composable
            if (dueno?.rol == Rol.CLIENTE.name) {
                val vm: MascotaViewModel = viewModel(factory = mascotaFactory(dueno, id))
                FichaMascotaScreen(
                    vm = vm, mascotaId = id,
                    onVolver = { navController.popBackStack() },
                    onEditar = { navController.navigate("mascota/$id/editar") },
                    onPedirCita = { navController.navigate(Rutas.PEDIR_CITA) }
                )
            }
        }
        composable(
            Rutas.EDITAR_MASCOTA,
            arguments = listOf(navArgument("mascotaId") { type = NavType.IntType })
        ) { entrada ->
            val dueno = usuario
            val id = entrada.arguments?.getInt("mascotaId") ?: return@composable
            if (dueno?.rol == Rol.CLIENTE.name) {
                val vm: MascotaViewModel = viewModel(factory = mascotaFactory(dueno, id))
                FormularioMascotaScreen(vm = vm, id = id, onVolver = { navController.popBackStack() })
            }
        }
        composable(Rutas.PEDIR_CITA) {
            if (usuario?.rol == Rol.CLIENTE.name) {
                DestinoPendiente("Pedir una cita", "La reserva de citas estará disponible próximamente.") {
                    navController.popBackStack()
                }
            }
        }
        // El paso 11 incorporará los contadores, ingresos y gráficos.
        composable(Rutas.PANEL) {
            if (usuario?.rol == Rol.ADMIN.name) {
                DestinoInicial(
                    titulo = "Panel de la clínica",
                    nombre = usuario?.nombre.orEmpty(),
                    descripcion = "Administra la atención de tus pacientes.",
                    ocupado = ocupado,
                    error = error,
                    onCerrarSesion = sesion::cerrarSesion,
                    onServicios = { navController.navigate(Rutas.SERVICIOS) }
                )
            }
        }
        composable(Rutas.SERVICIOS) {
            if (usuario?.rol == Rol.ADMIN.name) {
                Column(Modifier.fillMaxSize()) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Volver al panel")
                    }
                    ServiciosScreen(
                        vm = servicios,
                        onEditar = { avisoEdicion = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (avisoEdicion) {
        AlertDialog(
            onDismissRequest = { avisoEdicion = false },
            title = { Text("Editar servicio") },
            text = { Text("El formulario estará disponible en una próxima etapa.") },
            confirmButton = {
                TextButton(onClick = { avisoEdicion = false }) { Text("Aceptar") }
            }
        )
    }
}

@Composable
private fun DestinoInicial(
    titulo: String,
    nombre: String,
    descripcion: String,
    ocupado: Boolean,
    error: String?,
    onCerrarSesion: () -> Unit,
    onServicios: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Text("Hola, $nombre", style = MaterialTheme.typography.titleLarge)
        Text(descripcion)
        onServicios?.let {
            VetButton(text = "Ver servicios", onClick = it)
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        VetButton(
            text = "Cerrar sesión",
            onClick = onCerrarSesion,
            enabled = !ocupado
        )
    }
}

@Composable
private fun DestinoPendiente(titulo: String, mensaje: String, onVolver: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Text(mensaje)
        TextButton(onClick = onVolver) { Text("Volver") }
    }
}
