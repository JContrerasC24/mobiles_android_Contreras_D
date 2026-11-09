package com.contreras.clinicasaludplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.contreras.clinicasaludplus.data.Medico
import com.contreras.clinicasaludplus.ui.theme.PurpleLight
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary

/**
 * Tarjeta de un medico dentro del LazyColumn de Inicio.
 * Avatar circular morado con icono de cruz medica + nombre + especialidad + rating.
 */
@Composable
fun MedicoListItem(medico: Medico, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circular con icono, como en el mockup
            Row(
                modifier = Modifier
                    .size(44.dp)
                    .background(PurpleLight, CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalHospital,
                    contentDescription = null,
                    tint = PurplePrimary
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(medico.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    medico.especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " ${medico.calificacion}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}