package com.contreras.lab03registroproductoia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contreras.lab03registroproductoia.ui.theme.Lab03RegistroProductoIATheme

data class Producto(
    val nombre: String,
    val precio: Double,
    val cantidad: Int
) {
    val importe: Double get() = precio * cantidad
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab03RegistroProductoIATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaRegistro(modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }
    val productos = remember { mutableStateListOf<Producto>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Registro de Producto",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            "Completa los datos y presiona Agregar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
                mensajeExito = ""
            },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = precio,
                onValueChange = {
                    precio = it
                    mensajeError = ""
                    mensajeExito = ""
                },
                label = { Text("Precio (S/)") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                value = cantidad,
                onValueChange = {
                    cantidad = it
                    mensajeError = ""
                    mensajeExito = ""
                },
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f)
            )
        }

        if (mensajeError.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (mensajeExito.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                mensajeExito,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val precioNum = precio.toDoubleOrNull()
                val cantidadNum = cantidad.toIntOrNull()

                when {
                    nombre.isBlank() -> {
                        mensajeError = "El nombre del producto no puede estar vacío"
                        mensajeExito = ""
                    }
                    precio.isBlank() -> {
                        mensajeError = "Ingresa un precio válido"
                        mensajeExito = ""
                    }
                    precioNum == null || precioNum <= 0.0 -> {
                        mensajeError = "El precio debe ser un número mayor a 0"
                        mensajeExito = ""
                    }
                    cantidad.isBlank() -> {
                        mensajeError = "Ingresa una cantidad válida"
                        mensajeExito = ""
                    }
                    cantidadNum == null || cantidadNum <= 0 -> {
                        mensajeError = "La cantidad debe ser un número entero mayor a 0"
                        mensajeExito = ""
                    }
                    else -> {
                        productos.add(Producto(nombre, precioNum, cantidadNum))
                        mensajeExito = "✓ Producto registrado correctamente"
                        mensajeError = ""
                        nombre = ""
                        precio = ""
                        cantidad = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("AGREGAR PRODUCTO")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Productos registrados (${productos.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        if (productos.isEmpty()) {
            Text(
                "Aún no has registrado ningún producto",
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(productos) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("Precio: S/ " + String.format("%.2f", producto.precio))
                            Text("Cantidad: ${producto.cantidad}")
                            Text(
                                "Importe total: S/ " + String.format("%.2f", producto.importe),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Valor total del inventario: S/ " + String.format("%.2f", productos.sumOf { it.importe }),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Desarrollado por: Jose Abraham Contreras Cabrera",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.fillMaxWidth()
        )
    }
}