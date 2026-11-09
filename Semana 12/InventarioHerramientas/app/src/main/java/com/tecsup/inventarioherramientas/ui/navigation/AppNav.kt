package com.tecsup.inventarioherramientas.ui.navigation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.domain.model.Sesion
import com.tecsup.inventarioherramientas.ui.admin.AdminScreen
import com.tecsup.inventarioherramientas.ui.admin.AdminViewModel
import com.tecsup.inventarioherramientas.ui.detalle.DetalleScreen
import com.tecsup.inventarioherramientas.ui.detalle.DetalleViewModel
import com.tecsup.inventarioherramientas.ui.favoritos.FavoritosScreen
import com.tecsup.inventarioherramientas.ui.favoritos.FavoritosViewModel
import com.tecsup.inventarioherramientas.ui.historial.HistorialScreen
import com.tecsup.inventarioherramientas.ui.historial.HistorialViewModel
import com.tecsup.inventarioherramientas.ui.lista.ListaScreen
import com.tecsup.inventarioherramientas.ui.lista.ListaViewModel
import com.tecsup.inventarioherramientas.ui.movimiento.MovimientoScreen
import com.tecsup.inventarioherramientas.ui.movimiento.MovimientoViewModel
import com.tecsup.inventarioherramientas.ui.registro.RegistroHerramientaScreen
import com.tecsup.inventarioherramientas.ui.registro.RegistroHerramientaViewModel

@Composable
fun AppNav(
    contenedor: AppContainer,
    sesion: Sesion,
    onCerrarSesion: () -> Unit
) {
    val navController = rememberNavController()

    val listaFactory = remember(contenedor) {
        ListaViewModel.Factory(contenedor)
    }

    val favoritosFactory = remember(contenedor) {
        FavoritosViewModel.Factory(contenedor)
    }

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        // Inventario principal.
        composable("lista") {
            val listaViewModel: ListaViewModel = viewModel(
                factory = listaFactory
            )

            val estado by listaViewModel.estado
                .collectAsStateWithLifecycle()

            val actualizando by listaViewModel.actualizando
                .collectAsStateWithLifecycle()

            val aviso by listaViewModel.aviso
                .collectAsStateWithLifecycle()

            val mensajeAccion by listaViewModel.mensajeAccion
                .collectAsStateWithLifecycle()

            ListaScreen(
                sesion = sesion,
                estado = estado,
                actualizando = actualizando,
                aviso = aviso,
                mensajeAccion = mensajeAccion,
                onRefrescar = listaViewModel::refrescar,
                onFavorito = listaViewModel::alternarFavorito,
                onVerDetalle = { id ->
                    navController.navigate(
                        "detalle/${Uri.encode(id)}"
                    ) {
                        launchSingleTop = true
                    }
                },
                onVerFavoritos = {
                    navController.navigate("favoritos") {
                        launchSingleTop = true
                    }
                },
                onVerAdmin = {
                    if (sesion.rol == Rol.ADMIN) {
                        navController.navigate("admin") {
                            launchSingleTop = true
                        }
                    }
                },
                onCerrarSesion = onCerrarSesion
            )
        }

        // Favoritos locales.
        composable("favoritos") {
            val favoritosViewModel: FavoritosViewModel = viewModel(
                factory = favoritosFactory
            )

            val estado by favoritosViewModel.estado
                .collectAsStateWithLifecycle()

            val mensaje by favoritosViewModel.mensaje
                .collectAsStateWithLifecycle()

            FavoritosScreen(
                estado = estado,
                mensaje = mensaje,
                onQuitarFavorito =
                    favoritosViewModel::quitarFavorito,
                onVerDetalle = { id ->
                    navController.navigate(
                        "detalle/${Uri.encode(id)}"
                    ) {
                        launchSingleTop = true
                    }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Detalle de una herramienta.
        composable(
            route = "detalle/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val herramientaId = requireNotNull(
                entrada.arguments?.getString("id")
            )

            val detalleFactory = remember(
                contenedor,
                herramientaId
            ) {
                DetalleViewModel.Factory(
                    contenedor = contenedor,
                    herramientaId = herramientaId
                )
            }

            val detalleViewModel: DetalleViewModel = viewModel(
                factory = detalleFactory
            )

            val estado by detalleViewModel.estado
                .collectAsStateWithLifecycle()

            val aviso by detalleViewModel.aviso
                .collectAsStateWithLifecycle()

            // Recarga al regresar de un préstamo o devolución.
            val recargarDetalle by entrada.savedStateHandle
                .getStateFlow("recargarDetalle", false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(recargarDetalle) {
                if (recargarDetalle) {
                    detalleViewModel.cargar()
                    entrada.savedStateHandle["recargarDetalle"] = false
                }
            }

            DetalleScreen(
                estado = estado,
                aviso = aviso,
                onReintentar = detalleViewModel::cargar,
                esAdmin = sesion.rol == Rol.ADMIN,
                onMovimiento = { id, tipo ->
                    if (
                        sesion.rol == Rol.ADMIN &&
                        tipo in setOf("SALIDA", "ENTRADA")
                    ) {
                        navController.navigate(
                            "movimiento/${Uri.encode(id)}/$tipo"
                        ) {
                            launchSingleTop = true
                        }
                    }
                },
                onVerHistorial = { id ->
                    navController.navigate(
                        "historial/${Uri.encode(id)}"
                    ) {
                        launchSingleTop = true
                    }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Panel del administrador.
        composable("admin") {
            if (sesion.rol == Rol.ADMIN) {
                val adminFactory = remember(
                    contenedor,
                    sesion.rol
                ) {
                    AdminViewModel.Factory(
                        contenedor = contenedor,
                        rol = sesion.rol
                    )
                }

                val adminViewModel: AdminViewModel = viewModel(
                    factory = adminFactory
                )

                val estado by adminViewModel.estado
                    .collectAsStateWithLifecycle()

                val ocupado by adminViewModel.ocupado
                    .collectAsStateWithLifecycle()

                val mensaje by adminViewModel.mensaje
                    .collectAsStateWithLifecycle()

                AdminScreen(
                    estado = estado,
                    ocupado = ocupado,
                    mensaje = mensaje,
                    onSincronizar = adminViewModel::sincronizar,
                    onLimpiarCache = adminViewModel::limpiarCache,
                    onRegistrarHerramienta = {
                        navController.navigate("registro") {
                            launchSingleTop = true
                        }
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            } else {
                Text("Acceso exclusivo para administradores.")
            }
        }

        // Registro de nuevas herramientas.
        composable("registro") {
            if (sesion.rol == Rol.ADMIN) {
                val registroFactory = remember(
                    contenedor,
                    sesion.rol
                ) {
                    RegistroHerramientaViewModel.Factory(
                        contenedor = contenedor,
                        rol = sesion.rol
                    )
                }

                val registroViewModel: RegistroHerramientaViewModel =
                    viewModel(
                        factory = registroFactory
                    )

                val estado by registroViewModel.estado
                    .collectAsStateWithLifecycle()

                RegistroHerramientaScreen(
                    estado = estado,
                    onRegistrar = registroViewModel::registrar,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            } else {
                Text("Acceso exclusivo para administradores.")
            }
        }

        // Préstamo o devolución.
        composable(
            route = "movimiento/{id}/{tipo}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                },
                navArgument("tipo") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val herramientaId = requireNotNull(
                entrada.arguments?.getString("id")
            )

            val tipo = requireNotNull(
                entrada.arguments?.getString("tipo")
            )

            if (
                sesion.rol == Rol.ADMIN &&
                tipo in setOf("SALIDA", "ENTRADA")
            ) {
                val movimientoFactory = remember(
                    contenedor,
                    herramientaId,
                    tipo,
                    sesion.rol
                ) {
                    MovimientoViewModel.Factory(
                        contenedor = contenedor,
                        herramientaId = herramientaId,
                        tipo = tipo,
                        rol = sesion.rol
                    )
                }

                val movimientoViewModel: MovimientoViewModel =
                    viewModel(
                        factory = movimientoFactory
                    )

                val estado by movimientoViewModel.estado
                    .collectAsStateWithLifecycle()

                val volverAlDetalle: () -> Unit = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("recargarDetalle", true)

                    navController.popBackStack()
                    Unit
                }

                // También actualiza el detalle si se usa
                // el botón o gesto Atrás de Android.
                BackHandler {
                    volverAlDetalle()
                }

                MovimientoScreen(
                    herramientaId = herramientaId,
                    tipo = tipo,
                    estado = estado,
                    onGuardar = movimientoViewModel::guardar,
                    onVolver = volverAlDetalle
                )
            } else {
                Text("No tienes acceso a esta operación.")
            }
        }

        // Historial de una herramienta.
        composable(
            route = "historial/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val herramientaId = requireNotNull(
                entrada.arguments?.getString("id")
            )

            val historialFactory = remember(
                contenedor,
                herramientaId
            ) {
                HistorialViewModel.Factory(
                    contenedor = contenedor,
                    herramientaId = herramientaId
                )
            }

            val historialViewModel: HistorialViewModel = viewModel(
                factory = historialFactory
            )

            val estado by historialViewModel.estado
                .collectAsStateWithLifecycle()

            HistorialScreen(
                herramientaId = herramientaId,
                estado = estado,
                onActualizar = historialViewModel::cargar,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}