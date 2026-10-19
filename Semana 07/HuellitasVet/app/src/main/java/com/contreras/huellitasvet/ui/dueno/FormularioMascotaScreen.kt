package com.contreras.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contreras.huellitasvet.model.Especie
import com.contreras.huellitasvet.model.Sexo
import com.contreras.huellitasvet.ui.components.MascotaAvatar
import com.contreras.huellitasvet.ui.components.VetButton
import com.contreras.huellitasvet.viewmodel.MascotaViewModel

@Composable
fun FormularioMascotaScreen(
    vm: MascotaViewModel,
    id: Int? = null,
    onVolver: () -> Unit
) {
    val f by vm.formulario.collectAsStateWithLifecycle()
    val errores by vm.errores.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val ocupado by vm.ocupado.collectAsStateWithLifecycle()
    val finalizado by vm.finalizado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    LaunchedEffect(finalizado) { if (finalizado) onVolver() }
    val habilitado = !ocupado && !cargando && !finalizado && (id == null || mascota != null)

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onVolver, enabled = !ocupado) { Text("Volver") }
        Text(if (id == null) "Agregar mascota" else "Editar mascota",
            style = MaterialTheme.typography.headlineMedium)
        if (cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        MascotaAvatar(especie = f.especie)
        CampoMascota("Nombre", f.nombre, errores["nombre"], habilitado) { vm.actualizar(f.copy(nombre = it)) }
        Text("Especie", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Especie.entries.forEach { especie ->
                FilterChip(
                    selected = f.especie == especie.name, enabled = habilitado,
                    onClick = { vm.actualizar(f.copy(especie = especie.name)) },
                    label = { Text(especie.name.lowercase().replaceFirstChar { it.titlecase() }) }
                )
            }
        }
        errores["especie"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        CampoMascota("Raza", f.raza, null, habilitado) { vm.actualizar(f.copy(raza = it)) }
        CampoMascota("Año de nacimiento", f.anio, errores["anio"], habilitado, KeyboardType.Number) {
            vm.actualizar(f.copy(anio = it))
        }
        CampoMascota("Peso (kg)", f.peso, errores["peso"], habilitado, KeyboardType.Decimal) {
            vm.actualizar(f.copy(peso = it))
        }
        Text("Sexo", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Sexo.entries.forEach { sexo ->
                FilterChip(
                    selected = f.sexo == sexo.name, enabled = habilitado,
                    onClick = { vm.actualizar(f.copy(sexo = sexo.name)) },
                    label = { Text(sexo.name.lowercase().replaceFirstChar { it.titlecase() }) }
                )
            }
        }
        errores["sexo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        VetButton(
            text = if (ocupado) "Guardando…" else "Guardar mascota",
            onClick = vm::guardarFormulario, modifier = Modifier.fillMaxWidth(),
            enabled = habilitado
        )
    }
}

@Composable
private fun CampoMascota(
    etiqueta: String, valor: String, error: String?, habilitado: Boolean,
    teclado: KeyboardType = KeyboardType.Text, onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor, onValueChange = onCambio, label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = habilitado,
        isError = error != null, keyboardOptions = KeyboardOptions(keyboardType = teclado),
        supportingText = { if (error != null) Text(error) }
    )
}
