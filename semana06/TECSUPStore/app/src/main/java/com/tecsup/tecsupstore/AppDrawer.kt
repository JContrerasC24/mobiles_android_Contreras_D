package com.tecsup.tecsupstore

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DestinoDrawer(val ruta: String, val titulo: String, val icono: ImageVector)

val destinosDrawer = listOf(
    DestinoDrawer("inicio", "Inicio", Icons.Default.Home),
    DestinoDrawer("pedidos", "Mis pedidos", Icons.Default.ShoppingCart),
    DestinoDrawer("favoritos", "Favoritos", Icons.Default.Favorite),
    DestinoDrawer("perfil", "Perfil", Icons.Default.Person)
)

@Composable
fun AppDrawer(
    rutaActual: String,
    cantidadFavoritos: Int,
    onNavegar: (String) -> Unit
) {
    val context = LocalContext.current

    ModalDrawerSheet {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MoradoClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("JC", color = Morado, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text("Jose Contreras", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("jose.contreras.ca@tecsup.edu.pe", color = Color.Gray, fontSize = 12.sp)
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(12.dp))

        destinosDrawer.forEach { destino ->
            NavigationDrawerItem(
                label = { Text(destino.titulo) },
                icon = { Icon(destino.icono, contentDescription = null) },
                badge = {
                    if (destino.ruta == "favoritos" && cantidadFavoritos > 0) {
                        Badge(containerColor = Morado, contentColor = Color.White) {
                            Text(cantidadFavoritos.toString())
                        }
                    }
                },
                selected = destino.ruta == rutaActual,
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MoradoClaro,
                    selectedTextColor = Morado,
                    selectedIconColor = Morado
                ),
                onClick = { onNavegar(destino.ruta) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        NavigationDrawerItem(
            label = { Text("Cerrar sesion") },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            selected = false,
            onClick = {
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}