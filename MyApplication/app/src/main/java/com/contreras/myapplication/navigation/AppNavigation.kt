package com.contreras.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.contreras.myapplication.model.StudentRepository
import com.contreras.myapplication.screens.DetailScreen
import com.contreras.myapplication.screens.HomeScreen
import com.contreras.myapplication.screens.ListScreen
import com.contreras.myapplication.screens.LoginScreen
import com.contreras.myapplication.screens.ProfileScreen

// Componente principal de navegación entre las pantallas de la aplicación
@Composable
fun AppNavigation() {
    // Control de navegación mediante Jetpack Navigation Compose
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // La pantalla inicial de la aplicación es el Login
        startDestination = Screen.Login.route
    ) {

        // Grafo de pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // Grafo de pantalla principal / Home
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // Grafo de pantalla de lista de alumnos
        composable(Screen.List.route) {
            ListScreen(navController)
        }

        // Grafo de pantalla de perfil académico del usuario logueado
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        // Grafo de pantalla de detalle con argumento tipado Int itemId
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            // Obtención del ID enviado desde el directorio
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1

            // Búsqueda del estudiante correspondiente en el repositorio
            val student = StudentRepository.students.find { it.id == itemId }
                ?: StudentRepository.students.first()

            DetailScreen(navController, student)
        }
    }
}
