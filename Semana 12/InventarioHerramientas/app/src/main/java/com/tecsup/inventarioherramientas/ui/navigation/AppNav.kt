package com.tecsup.inventarioherramientas.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
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
import com.tecsup.inventarioherramientas.domain.model.Sesion
import com.tecsup.inventarioherramientas.ui.detalle.DetalleScreen
import com.tecsup.inventarioherramientas.ui.detalle.DetalleViewModel
import com.tecsup.inventarioherramientas.ui.favoritos.FavoritosScreen
import com.tecsup.inventarioherramientas.ui.favoritos.FavoritosViewModel
import com.tecsup.inventarioherramientas.ui.lista.ListaScreen
import com.tecsup.inventarioherramientas.ui.lista.ListaViewModel

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
                    )
                },
                onVerFavoritos = {
                    navController.navigate("favoritos") {
                        launchSingleTop = true
                    }
                },
                onCerrarSesion = onCerrarSesion
            )
        }

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
                    )
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

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

            DetalleScreen(
                estado = estado,
                aviso = aviso,
                onReintentar = detalleViewModel::cargar,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}