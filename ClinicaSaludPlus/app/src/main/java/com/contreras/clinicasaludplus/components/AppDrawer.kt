package com.contreras.clinicasaludplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contreras.clinicasaludplus.navigation.Rutas
import com.contreras.clinicasaludplus.ui.theme.PurpleLight
import com.contreras.clinicasaludplus.ui.theme.PurplePrimary

/**
 * Contenido del menu lateral (drawer): encabezado con el paciente
 * (avatar "JP" + nombre + rol) y 4 destinos: Inicio, Mis citas,
 * Historial medico y Perfil (minimo pedido: 3).
 *
 * rutaActual se usa para resaltar en morado el destino activo,
 * igual que se ve "Mis citas" resaltado en el mockup.
 */
@Composable
fun AppDrawerContent(
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PurplePrimary, CircleShape),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("JP", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("Juan Perez", fontWeight = FontWeight.Bold)
                    Text("Paciente", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Divider()

        DrawerItem("Inicio", Rutas.INICIO, rutaActual, onNavegar)
        DrawerItem("Mis citas", Rutas.MIS_CITAS, rutaActual, onNavegar)
        DrawerItem("Historial medico", Rutas.HISTORIAL, rutaActual, onNavegar)
        DrawerItem("Perfil", Rutas.PERFIL_USUARIO, rutaActual, onNavegar)
    }
}

@Composable
private fun DrawerItem(
    etiqueta: String,
    ruta: String,
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    val seleccionado = ruta == rutaActual
    val fondo = if (seleccionado) PurpleLight else Color.Transparent
    val colorTexto = if (seleccionado) PurplePrimary else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(fondo, RoundedCornerShape(24.dp))
            .clickable { onNavegar(ruta) }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = null,
            tint = if (seleccionado) PurplePrimary else Color.LightGray,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = etiqueta,
            modifier = Modifier.padding(start = 16.dp),
            color = colorTexto,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )
    }
}