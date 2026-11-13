package com.tecsup.inventarioherramientas.ui.registro

import com.tecsup.inventarioherramientas.ui.BotonVolver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.ui.UiState

@Composable
fun RegistroHerramientaScreen(
    estado: UiState<String?>,
    onRegistrar: (String, String, String, String, Int, String) -> Unit,
    onVolver: () -> Unit
) {
    var cantidad by rememberSaveable { mutableStateOf("1") }
    var codigo by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ubicacion by rememberSaveable { mutableStateOf("") }
    var condicion by rememberSaveable { mutableStateOf("BUENA") }

    val cargando = estado is UiState.Cargando
    val mensajeGuardado = (estado as? UiState.Exito)?.datos
    val editable = !cargando && mensajeGuardado == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BotonVolver("Volver", onVolver, enabled = !cargando)

        com.tecsup.inventarioherramientas.ui.Cabecera("Nueva herramienta", "Registro de unidades")

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código, por ejemplo HER-003") },
            enabled = editable,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            enabled = editable,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            enabled = editable,
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White),
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") },
            enabled = editable,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Condición física")

        com.tecsup.inventarioherramientas.ui.SelectorCondicion(condicion, editable) { condicion = it }
        OutlinedTextField(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedContainerColor = androidx.compose.ui.graphics.Color.White), value = cantidad, onValueChange = { cantidad = it.filter(Char::isDigit).take(3) },
            label = { Text("Cantidad de unidades") }, supportingText = { Text("Cada unidad recibe un código consecutivo desde el código inicial.") },
            singleLine = true, enabled = editable, modifier = Modifier.fillMaxWidth())

        if (estado is UiState.Error) {
            Text(
                text = estado.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (mensajeGuardado != null) {
            Text(mensajeGuardado)
        }

        Button(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
            onClick = {
                onRegistrar(
                    codigo,
                    nombre,
                    descripcion,
                    ubicacion,
                    cantidad.toIntOrNull() ?: 0,
                    condicion
                )
            },
            enabled = editable,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (cargando) "Guardando..." else "Guardar herramienta"
            )
        }

        if (cargando) {
            CircularProgressIndicator()
        }
    }
}