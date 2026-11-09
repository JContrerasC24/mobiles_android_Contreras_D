package com.contreras.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.contreras.myapplication.navigation.Screen
import com.contreras.myapplication.ui.theme.BrandGradient
import com.contreras.myapplication.ui.theme.PurpleDark
import com.contreras.myapplication.ui.theme.PurpleLight
import com.contreras.myapplication.ui.theme.PurpleMid

// Pantalla principal / Dashboard para el usuario logueado Jose Contreras
@Composable
fun HomeScreen(navController: NavController) {
    // Contenedor exterior con degradado de 3 tonos cubriendo toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            // Botón inferior para Cerrar Sesión Segura
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            // Cierra sesión limpiando todo el backstack hasta la raíz
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cerrar Sesión Segura",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Column principal centrado vertical y horizontalmente entre topBar y bottomBar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center, // Centra verticalmente todo el bloque
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Texto de saludo regular blanco
                Text(
                    text = "Bienvenido,",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                // Nombre del usuario principal en negrita
                Text(
                    text = "Jose Contreras",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtítulo en tono lila claro
                Text(
                    text = "¿Qué deseas gestionar hoy?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PurpleLight
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card para acceder al Directorio de Alumnos
                HomeDashboardCard(
                    title = "Directorio de Alumnos",
                    icon = Icons.AutoMirrored.Outlined.List,
                    onClick = { navController.navigate(Screen.List.route) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card para acceder al Perfil Académico
                HomeDashboardCard(
                    title = "Mi Perfil Académico",
                    icon = Icons.Outlined.Person,
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
    }
}

// Tarjeta blanca reutilizable para las opciones del Dashboard
@Composable
private fun HomeDashboardCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleMid,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PurpleMid,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
