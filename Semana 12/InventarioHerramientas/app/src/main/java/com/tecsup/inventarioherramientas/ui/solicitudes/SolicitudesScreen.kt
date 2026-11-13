package com.tecsup.inventarioherramientas.ui.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.*
import com.tecsup.inventarioherramientas.AppContainer
import com.tecsup.inventarioherramientas.domain.model.*
import com.tecsup.inventarioherramientas.data.repository.FirebaseMovimientoRepository
import com.tecsup.inventarioherramientas.ui.*
import com.tecsup.inventarioherramientas.ui.movimiento.MovimientoScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun mensaje(e: Exception): String = if (e is FirebaseFirestoreException &&
    e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
    "Firestore rechazó la operación. El administrador debe publicar las reglas actualizadas de solicitudes."
else e.cause?.message ?: e.message ?: "No se pudo guardar. Comprueba tu conexión."

@Composable
fun SolicitarScreen(id: String, sesion: Sesion, onVolver: () -> Unit, onEnviado: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var dias by rememberSaveable { mutableStateOf("1") }
    var nota by rememberSaveable { mutableStateOf("") }
    var ocupado by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).imePadding(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BotonVolver("Volver al detalle", onVolver, enabled = !ocupado)
        Text("Solicitar alquiler", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("$id · Revisión → aprobación → pago", color = Petroleo)
        Text("Todavía no se realizará ningún cobro. El administrador revisará tu solicitud y confirmará la disponibilidad antes del pago.")
        OutlinedTextField(nombre, { if (it.length <= 100) nombre = it }, label = { Text("Nombre de quien recibe") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), enabled = !ocupado)
        OutlinedTextField(dias, { if (it.length <= 3 && it.all(Char::isDigit)) dias = it }, label = { Text("Días de alquiler (1–365)") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), enabled = !ocupado)
        OutlinedTextField(nota, { if (it.length <= 500) nota = it }, label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), enabled = !ocupado)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(enabled = !ocupado && nombre.isNotBlank() && (dias.toIntOrNull() ?: 0) in 1..365,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), onClick = {
                ocupado = true
                error = null
                scope.launch {
                    try {
                        val db = FirebaseFirestore.getInstance()
                        // Stable key prevents duplicate pending requests, including retries after a lost response.
                        val ref = db.collection("solicitudes").document("${sesion.uid}_$id")
                        db.runTransaction { tx ->
                            val anterior = tx.get(ref)
                            val herramienta = tx.get(db.collection("herramientas").document(id))
                            require(anterior.getString("estado") !in listOf("PENDIENTE", "APROBADA")) {
                                "Ya tienes una solicitud activa para esta unidad. Consulta Mis solicitudes."
                            }
                            require(herramienta.getString("disponibilidad") == "DISPONIBLE" &&
                                herramienta.getString("condicion") == "BUENA" && herramienta.getString("prestamoActivoId") == null) {
                                "La herramienta ya no está disponible. Elige otra unidad."
                            }
                            tx.set(ref, mapOf("uid" to sesion.uid, "herramientaId" to id,
                                "nombreHerramienta" to herramienta.getString("nombre").orEmpty(),
                                "responsable" to nombre.trim(), "dias" to dias.toInt(), "observacion" to nota.trim(),
                                "estado" to "PENDIENTE", "fecha" to FieldValue.serverTimestamp(),
                                "actualizado" to FieldValue.serverTimestamp()))
                        }.await()
                        onEnviado()
                    } catch (e: CancellationException) { throw e }
                    catch (e: Exception) { error = mensaje(e) }
                    finally { ocupado = false }
                }
            }) { Text(if (ocupado) "Enviando…" else "Enviar solicitud  →") }
    }
}

@Composable
fun SolicitudesScreen(sesion: Sesion, contenedor: AppContainer, onTerminar: () -> Unit, onVolver: () -> Unit) {
    val admin = sesion.rol == Rol.ADMIN
    val db = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()
    var documentos by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var ocupado by remember { mutableStateOf(false) }
    var pago by remember { mutableStateOf<DocumentSnapshot?>(null) }
    var estadoPago by remember { mutableStateOf<UiState<String?>>(UiState.Exito(null)) }
    DisposableEffect(sesion.uid, admin) {
        val query: Query = if (admin) db.collection("solicitudes")
            else db.collection("solicitudes").whereEqualTo("uid", sesion.uid)
        val listener = query.addSnapshotListener { datos, fallo ->
            cargando = false
            if (fallo != null) error = mensaje(fallo)
            else {
                error = null
                documentos = datos?.documents.orEmpty().sortedByDescending { it.getTimestamp("fecha")?.seconds ?: 0 }
            }
        }
        onDispose { listener.remove() }
    }
    val seleccion = pago
    if (seleccion != null) {
        BackHandler { if ((estadoPago as? UiState.Exito)?.datos != null) onTerminar() else if (estadoPago !is UiState.Cargando) pago = null }
        key(seleccion.id) {
            MovimientoScreen(solicitudAprobada = true, nombreInicial = seleccion.getString("responsable").orEmpty(),
                diasIniciales = (seleccion.getLong("dias") ?: 1).toInt(),
                tarifaInicial = tarifaSugerida(seleccion.getString("nombreHerramienta").orEmpty()),
                herramientaId = seleccion.getString("herramientaId").orEmpty(), tipo = "SALIDA", estado = estadoPago,
                onGuardar = { responsable, cobro, _, nota ->
                    if (estadoPago !is UiState.Cargando && (estadoPago as? UiState.Exito)?.datos == null) {
                        estadoPago = UiState.Cargando
                        scope.launch {
                            val resultado = FirebaseMovimientoRepository().prestarSolicitud(
                                seleccion.getString("herramientaId").orEmpty(), responsable, cobro, nota, seleccion.id)
                            estadoPago = resultado.fold({ UiState.Exito("Pago y préstamo registrados correctamente.") },
                                { UiState.Error(it.message ?: "No se pudo confirmar el préstamo.") })
                            if (resultado.isSuccess) contenedor.itemRepository.refrescar()
                        }
                    }
                }, onTerminar = { pago = null; onTerminar() }, onVolver = { if (estadoPago !is UiState.Cargando) pago = null })
        }
        return
    }
    fun resolver(doc: DocumentSnapshot, destino: String) {
        ocupado = true
        scope.launch {
            try {
                db.runTransaction { tx ->
                    val actual = tx.get(doc.reference)
                    require(actual.getString("estado") == "PENDIENTE" ||
                        (destino == "RECHAZADA" && actual.getString("estado") == "APROBADA")) { "La solicitud cambió de estado." }
                    if (destino == "APROBADA") {
                        val item = tx.get(db.collection("herramientas").document(actual.getString("herramientaId")!!))
                        require(item.getString("disponibilidad") == "DISPONIBLE" && item.getString("condicion") == "BUENA") {
                            "La unidad ya no está disponible."
                        }
                    }
                    tx.update(doc.reference, mapOf("estado" to destino, "actualizado" to FieldValue.serverTimestamp()))
                }.await()
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { error = mensaje(e) }
            finally { ocupado = false }
        }
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            BotonVolver("Volver al catálogo", onVolver)
            Text(if (admin) "Solicitudes de alquiler" else "Mis solicitudes", style = MaterialTheme.typography.headlineMedium)
            Text(if (admin) "Aprueba la solicitud y después verifica el pago para entregar la herramienta."
                else "Consulta aquí la respuesta del administrador. La aprobación está sujeta a disponibilidad al registrar el pago.")
        }
        error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        if (cargando) item { CircularProgressIndicator() }
        if (!cargando && documentos.isEmpty() && error == null) item { Text("Aún no hay solicitudes. Puedes solicitar una unidad disponible desde su ficha.") }
        items(documentos, key = { it.id }) { doc ->
            val estado = doc.getString("estado").orEmpty()
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(doc.getString("nombreHerramienta").orEmpty(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${doc.getString("herramientaId")} · ${doc.getLong("dias")} días")
                    Text(doc.getString("responsable").orEmpty())
                    Text(when (estado) {
                        "PENDIENTE" -> "Pendiente de aprobación"
                        "APROBADA" -> "Aprobada · pago pendiente"
                        "COMPLETADA" -> "Préstamo confirmado · pago registrado"
                        else -> "Solicitud rechazada"
                    }, fontWeight = FontWeight.Bold, color = Petroleo)
                    doc.getString("observacion")?.takeIf { it.isNotBlank() }?.let { Text(it) }
                    if (admin && estado == "PENDIENTE") Button(enabled = !ocupado, onClick = { resolver(doc, "APROBADA") },
                        modifier = Modifier.fillMaxWidth()) { Text("Aceptar solicitud  ✓") }
                    if (admin && estado == "APROBADA") Button(enabled = !ocupado, onClick = {
                        estadoPago = UiState.Exito(null); pago = doc
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Ambar, contentColor = Petroleo)) {
                        Text("Registrar pago y préstamo  →")
                    }
                    if (admin && estado in listOf("PENDIENTE", "APROBADA")) OutlinedButton(enabled = !ocupado,
                        onClick = { resolver(doc, "RECHAZADA") }, modifier = Modifier.fillMaxWidth()) { Text("Rechazar solicitud") }
                }
            }
        }
    }
}
