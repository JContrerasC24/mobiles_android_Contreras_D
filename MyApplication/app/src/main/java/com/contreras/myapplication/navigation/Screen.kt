package com.contreras.myapplication.navigation

// Clase sellada para centralizar la definición de rutas de navegación
sealed class Screen(val route: String) {

    // Ruta para la pantalla de inicio de sesión
    object Login : Screen("login")

    // Ruta para la pantalla principal / Dashboard
    object Home : Screen("home")

    // Ruta para la pantalla del directorio de alumnos
    object List : Screen("list")

    // Ruta para la pantalla del perfil del usuario
    object Profile : Screen("profile")

    // Ruta para la pantalla de detalle de un alumno con argumento dinámico
    object Detail : Screen("detail/{itemId}") {
        // Función helper para construir la ruta con el ID del estudiante
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}
