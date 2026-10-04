package com.contreras.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.contreras.saludplus.citas.R
import com.contreras.saludplus.citas.navigation.Rutas
import com.contreras.saludplus.citas.ui.components.BotonSalud

// Presenta la marca y la ilustración médica de bienvenida.
@Composable
fun SplashScreen(navController: NavHostController) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFFF0F7FF))) {
        val altoImagen = (maxHeight * 0.44f).coerceIn(190.dp, 340.dp)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))
            Image(painterResource(R.drawable.sp_logo), "Logo de SaludPlus", Modifier.size(82.dp))
            Text("Clínica", fontSize = 22.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF21488D))
            Text("SaludPlus", fontSize = 32.sp, lineHeight = 35.sp, fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF21488D))
            Text("Tu salud, nuestra prioridad", Modifier.padding(top = 8.dp, bottom = 14.dp),
                style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Image(
                painterResource(R.drawable.sp_bienvenida),
                contentDescription = "Ilustración de un médico con portapapeles",
                modifier = Modifier.fillMaxWidth().height(altoImagen),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                BotonSalud("Comenzar") {
                    navController.navigate(Rutas.REGISTRO) { launchSingleTop = true }
                }
                TextButton(onClick = {
                    navController.navigate(Rutas.LOGIN) { launchSingleTop = true }
                }) { Text("Ya tengo una cuenta", style = MaterialTheme.typography.labelMedium) }
            }
        }
    }
}
