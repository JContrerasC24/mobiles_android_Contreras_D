package com.contreras.myapplication.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.contreras.myapplication.model.Student
import com.contreras.myapplication.ui.theme.BackgroundLilac
import com.contreras.myapplication.ui.theme.BrandGradient
import com.contreras.myapplication.ui.theme.DetailCardGray
import com.contreras.myapplication.ui.theme.PurpleMid

// Pantalla de expediente académico detallado del alumno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, student: Student) {
    // Scaffold principal con la TopAppBar
    Scaffold(
        containerColor = BackgroundLilac,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Académico",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleMid
                )
            )
        }
    ) { innerPadding ->
        // Column deslizable para el expediente del estudiante
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header superior con degradado de 3 tonos y esquinas redondeadas abajo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(BrandGradient)
            )

            // Contenedor del avatar superpuesto sobre el borde del header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-48).dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular grande (96dp) con borde blanco de 4dp
                AsyncImage(
                    model = student.avatarUrl,
                    contentDescription = student.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(4.dp, Color.White), CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre del estudiante centrado
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Carrera en tono PurpleMid
                Text(
                    text = student.career,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = PurpleMid
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // UNA SOLA Card gris con la información del expediente y biografía
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DetailCardGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Fila para ID Estudiante
                        DetailInfoRow(
                            icon = Icons.Outlined.Badge,
                            label = "ID Estudiante",
                            value = student.studentCode
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fila para Correo Electrónico
                        DetailInfoRow(
                            icon = Icons.Outlined.Email,
                            label = "Correo Electrónico",
                            value = student.email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fila para Facultad
                        DetailInfoRow(
                            icon = Icons.Outlined.School,
                            label = "Facultad",
                            value = student.faculty
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divisor horizontal
                        HorizontalDivider(color = Color.LightGray)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Título de Biografía
                        Text(
                            text = "Biografía",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Texto de la biografía del estudiante
                        Text(
                            text = student.bio,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

// Fila reutilizable para las propiedades del expediente en DetailScreen
@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PurpleMid,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
