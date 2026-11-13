package com.tecsup.inventarioherramientas.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.tecsup.inventarioherramientas.domain.model.soles
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CancellationException

@Composable
fun RegistroCobros() {
    var abierto by remember { mutableStateOf(false) }
    var registros by remember { mutableStateOf<List<String>>(emptyList()) }
    var estado by remember { mutableStateOf("") }
    var intento by remember { mutableIntStateOf(0) }
    OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) { Text("Consultar cobros · Efectivo / Yape / Tarjeta") }
    if (abierto) {
        LaunchedEffect(intento) {
            estado = "Consultando cobros…"
            try {
                val documentos = FirebaseFirestore.getInstance().collection("movimientos")
                    .whereEqualTo("tipo", "SALIDA").get(Source.SERVER).await().documents
                registros = documentos.sortedByDescending { it.getTimestamp("fecha") }.map { doc ->
                    val pago = doc.get("alquiler") as? Map<*, *>
                    val cabecera = "${doc.getString("codigoHerramienta")} · ${doc.getString("responsable")}"
                    if (pago == null) "$cabecera\nSin datos de pago registrados"
                    else "$cabecera\n${soles((pago["totalCentimos"] as Number).toLong())} · ${pago["metodo"]} · Pagado\n${pago["dias"]} días · ${pago["referencia"]}"
                }
                estado = if (registros.isEmpty()) "Todavía no hay alquileres registrados." else ""
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { estado = "No se pudieron consultar los cobros. Comprueba tu conexión y permisos." }
        }
        AlertDialog(onDismissRequest = { abierto = false }, title = { Text("Cobros de alquiler") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (estado.isNotEmpty()) Text(estado)
                    registros.forEach { texto -> ElevatedCard { Text(texto, Modifier.padding(14.dp)) } }
                }
            },
            confirmButton = { TextButton(onClick = { abierto = false }) { Text("Cerrar") } },
            dismissButton = { TextButton(onClick = { intento++ }) { Text("Actualizar") } })
    }
}
