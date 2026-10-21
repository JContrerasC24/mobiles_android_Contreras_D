package com.contreras.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.contreras.huellitasvet.ui.dueno.PedirCitaScreen
import com.contreras.huellitasvet.ui.dueno.MisCitasScreen
import com.contreras.huellitasvet.ui.veterinario.PanelScreen
import com.contreras.huellitasvet.ui.veterinario.AgendaScreen
import com.contreras.huellitasvet.ui.veterinario.AtenderCitaScreen
import com.contreras.huellitasvet.ui.veterinario.PacientesScreen
import com.contreras.huellitasvet.ui.veterinario.FichaPacienteScreen
import com.contreras.huellitasvet.viewmodel.AtenderCitaViewModel
import com.contreras.huellitasvet.viewmodel.PacientesViewModel
import com.contreras.huellitasvet.viewmodel.PreferenciasViewModel
import com.contreras.huellitasvet.ui.dueno.PerfilScreen
import com.contreras.huellitasvet.ui.veterinario.AjustesScreen
import com.contreras.huellitasvet.ui.veterinario.FormularioServicioScreen
import com.contreras.huellitasvet.viewmodel.PanelViewModel
import com.contreras.huellitasvet.viewmodel.AgendaViewModel
import com.contreras.huellitasvet.viewmodel.MisCitasViewModel
import com.contreras.huellitasvet.viewmodel.PedirCitaViewModel
import com.contreras.huellitasvet.viewmodel.MascotaViewModel
import com.contreras.huellitasvet.viewmodel.InicioViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.contreras.huellitasvet.model.Rol
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
    atencionFactory: (Int) -> ViewModelProvider.Factory,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val inicializando by sesion.inicializando.collectAsStateWithLifecycle()
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val ocupado by sesion.ocupado.collectAsStateWithLifecycle()
    val error by sesion.error.collectAsStateWithLifecycle()


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

    val entradaActual by navController.currentBackStackEntryAsState()
    val ruta=entradaActual?.destination?.route
    val vet=usuario?.rol==Rol.ADMIN.name
    val tabs=if(vet) listOf(
        Triple(Rutas.PANEL,"Panel",Icons.Default.BarChart),Triple(Rutas.AGENDA,"Agenda",Icons.Default.CalendarMonth),
        Triple(Rutas.PACIENTES,"Pacientes",Icons.Default.Pets),Triple(Rutas.SERVICIOS,"Servicios",Icons.Default.Sell),
        Triple(Rutas.AJUSTES,"Ajustes",Icons.Default.Settings)
    ) else listOf(Triple(Rutas.INICIO,"Inicio",Icons.Default.Home),Triple(Rutas.MASCOTAS,"Mascotas",Icons.Default.Pets),
        Triple(Rutas.MIS_CITAS,"Citas",Icons.Default.CalendarMonth),Triple(Rutas.PERFIL,"Perfil",Icons.Default.PersonOutline))
    val mostrarBarra=usuario!=null && (tabs.any{it.first==ruta} || ruta==Rutas.FICHA_MASCOTA || ruta==Rutas.FICHA_PACIENTE)
    Scaffold(modifier=modifier,bottomBar={
        if(mostrarBarra) {
            HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
            NavigationBar(containerColor=MaterialTheme.colorScheme.surface,tonalElevation=0.dp,
                windowInsets=NavigationBarDefaults.windowInsets) {
                tabs.forEach { (destino,label,icono) ->
                    val selected=ruta==destino || (destino==Rutas.MASCOTAS && ruta==Rutas.FICHA_MASCOTA) || (destino==Rutas.PACIENTES && ruta==Rutas.FICHA_PACIENTE)
                    NavigationBarItem(selected=selected,onClick={
                        if(ruta!=destino)navController.navigate(destino) {
                            popUpTo(if(vet)Rutas.PANEL else Rutas.INICIO){inclusive=false}
                            launchSingleTop=true
                        }
                    },icon={Icon(icono,label,Modifier.size(22.dp))},label={Text(label,style=MaterialTheme.typography.labelSmall)},
                        colors=NavigationBarItemDefaults.colors(selectedIconColor=MaterialTheme.colorScheme.primary,
                            selectedTextColor=MaterialTheme.colorScheme.primary,indicatorColor=MaterialTheme.colorScheme.primaryContainer))
                }
            }
        }
    }) { padding ->
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = Modifier.fillMaxSize().padding(padding)
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
                    onPedirCita = { navController.navigate("cita/nueva") },
                    onVerMascotas = { navController.navigate(Rutas.MASCOTAS) },
                    onMisCitas = { navController.navigate(Rutas.MIS_CITAS) },
                    onPerfil = { navController.navigate(Rutas.PERFIL) },
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
                    onPedirCita = { navController.navigate("cita/nueva?mascotaId=$id") }
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
        composable(
            Rutas.PEDIR_CITA,
            arguments = listOf(navArgument("mascotaId") { type = NavType.IntType; defaultValue = -1 })
        ) { entrada ->
            val dueno = usuario
            if (dueno?.rol == Rol.CLIENTE.name) {
                val id = entrada.arguments?.getInt("mascotaId")?.takeIf { it > 0 }
                val vm: PedirCitaViewModel = viewModel(factory = mascotaFactory(dueno, id))
                PedirCitaScreen(vm, onVolver = { navController.popBackStack() }, onCreada = {
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.INICIO)
                        launchSingleTop = true
                    }
                })
            }
        }
        composable(Rutas.MIS_CITAS) {
            val dueno = usuario
            if (dueno?.rol == Rol.CLIENTE.name) {
                val vm: MisCitasViewModel = viewModel(factory = mascotaFactory(dueno, null))
                MisCitasScreen(vm,
                    onInicio = { navController.popBackStack(Rutas.INICIO, false) },
                    onPedir = { navController.navigate("cita/nueva") })
            }
        }
        composable(Rutas.PANEL) {
            val veterinario = usuario
            if (veterinario?.rol == Rol.ADMIN.name) {
                val vm: PanelViewModel = viewModel(factory = inicioFactory(veterinario))
                PanelScreen(vm = vm, nombre = veterinario.nombre,
                    onAgenda = { navController.navigate(Rutas.AGENDA) },
                    onAjustes = { navController.navigate(Rutas.AJUSTES) },
                    onPacientes = { navController.navigate(Rutas.PACIENTES) },
                    onServicios = { navController.navigate(Rutas.SERVICIOS) },
                    onCerrarSesion = sesion::cerrarSesion,
                    ocupado = ocupado, errorSesion = error)
            }
        }
        composable(Rutas.AGENDA) {
            val veterinario = usuario
            if (veterinario?.rol == Rol.ADMIN.name) {
                val vm: AgendaViewModel = viewModel(factory = inicioFactory(veterinario))
                AgendaScreen(vm, onVolver = { navController.popBackStack() },
                    onAtender = { navController.navigate("atender/$it") })
            }
        }
        composable(Rutas.ATENDER_CITA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })) { entrada ->
            if (usuario?.rol == Rol.ADMIN.name) {
                val id = entrada.arguments?.getInt("citaId") ?: return@composable
                val vm: AtenderCitaViewModel = viewModel(factory = atencionFactory(id))
                AtenderCitaScreen(vm, onVolver = { navController.popBackStack() })
            }
        }
        composable(Rutas.PACIENTES) {
            val veterinario = usuario
            if (veterinario?.rol == Rol.ADMIN.name) {
                val vm: PacientesViewModel = viewModel(factory = mascotaFactory(veterinario, null))
                PacientesScreen(vm, onVolver = { navController.popBackStack() },
                    onFicha = { navController.navigate("paciente/$it") })
            }
        }
        composable(Rutas.FICHA_PACIENTE,
            arguments = listOf(navArgument("mascotaId") { type = NavType.IntType })) { entrada ->
            val veterinario = usuario
            if (veterinario?.rol == Rol.ADMIN.name) {
                val id = entrada.arguments?.getInt("mascotaId") ?: return@composable
                val vm: PacientesViewModel = viewModel(factory = mascotaFactory(veterinario, id))
                FichaPacienteScreen(vm, onVolver = { navController.popBackStack() })
            }
        }
        composable(Rutas.PERFIL) {
            val actual = usuario
            if (actual?.rol == Rol.CLIENTE.name) {
                val vm: PreferenciasViewModel = viewModel(factory = inicioFactory(actual))
                PerfilScreen(vm, { navController.popBackStack() }, sesion::cambiarPerfil,
                    sesion::cerrarSesion, ocupado, error)
            }
        }
        composable(Rutas.AJUSTES) {
            val actual = usuario
            if (actual?.rol == Rol.ADMIN.name) {
                val vm: PreferenciasViewModel = viewModel(factory = inicioFactory(actual))
                AjustesScreen(vm, { navController.popBackStack() }, sesion::cambiarPerfil,
                    sesion::cerrarSesion, ocupado, error)
            }
        }
        composable(Rutas.FORMULARIO_SERVICIO,
            arguments = listOf(navArgument("servicioId") { type = NavType.IntType })) { entrada ->
            if (usuario?.rol == Rol.ADMIN.name) {
                val id = entrada.arguments?.getInt("servicioId")?.takeIf { it > 0 }
                FormularioServicioScreen(servicios, id) { navController.popBackStack() }
            }
        }
        composable(Rutas.SERVICIOS) {
            if (usuario?.rol == Rol.ADMIN.name) {
                Column(Modifier.fillMaxSize()) {
                    ServiciosScreen(
                        vm = servicios,
                        onEditar = { id -> servicios.prepararFormulario(); navController.navigate("servicio/$id") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
}
