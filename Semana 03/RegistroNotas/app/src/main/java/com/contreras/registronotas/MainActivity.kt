package com.contreras.registronotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contreras.registronotas.ui.theme.RegistroNotasTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroNotasTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Registro de Notas", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { innerPadding ->
                    PantallaNotas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/** Un curso con su peso fijo y su propia variable de estado para la nota. */
private class CursoUI(
    val nombre: String,
    val peso: Double,
    val nota: MutableState<Float>
)

@Composable
fun PantallaNotas(modifier: Modifier = Modifier) {
    // Cada curso tiene SU PROPIA variable de estado (4 remember independientes)
    val notaFundamentos = remember { mutableStateOf(0f) }
    val notaPOO = remember { mutableStateOf(0f) }
    val notaMoviles = remember { mutableStateOf(0f) }
    val notaBD = remember { mutableStateOf(0f) }

    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }

    val cursos = listOf(
        CursoUI("Fundamentos de Programación", 0.20, notaFundamentos),
        CursoUI("Programación Orientada a Objetos", 0.25, notaPOO),
        CursoUI("Programación en Móviles", 0.30, notaMoviles),
        CursoUI("Base de Datos", 0.25, notaBD)
    )

    val promedioPonderado = cursos.sumOf { it.nota.value * it.peso }
    val promedioFinal = if (redondear) promedioPonderado.roundToInt().toDouble() else promedioPonderado

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 56.dp) // deja espacio para el pie fijo
        ) {
            Text("Notas del ciclo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Desliza para asignar cada nota (0 a 20)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(20.dp))

            cursos.forEach { curso ->
                FilaCurso(curso)
                Spacer(Modifier.height(16.dp))
            }

            // Switch: redondear promedio final
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Redondear promedio final")
                Switch(checked = redondear, onCheckedChange = { redondear = it })
            }
            Spacer(Modifier.height(8.dp))

            // Checkbox: confirmación
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
                Text("Confirmo que las notas son correctas")
            }
            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { mostrarResultado = true },
                    enabled = confirmado,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CALCULAR PROMEDIO")
                }
                Spacer(Modifier.width(12.dp))
                // Reto opcional: boton LIMPIAR
                OutlinedButton(
                    onClick = {
                        notaFundamentos.value = 0f
                        notaPOO.value = 0f
                        notaMoviles.value = 0f
                        notaBD.value = 0f
                        redondear = false
                        confirmado = false
                        mostrarResultado = false
                    }
                ) {
                    Text("LIMPIAR")
                }
            }

            Spacer(Modifier.height(20.dp))

            if (!confirmado || !mostrarResultado) {
                Text(
                    "Asigna las notas y confirma para calcular",
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                TarjetaResultado(
                    cursos = cursos,
                    promedioPonderado = promedioPonderado,
                    promedioFinal = promedioFinal,
                    redondear = redondear
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "✓ Promedio calculado correctamente",
                    color = Color(0xFF2E7D32)
                )
            }
        }

        // Pie fijo abajo
        Text(
            "Desarrollado por: Jose Contreras",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun FilaCurso(curso: CursoUI) {
    val notaInt = curso.nota.value.toInt()
    // Reto opcional: semaforo -> rojo si < 13, verde si >= 13
    val colorBadge = if (notaInt < 13) Color(0xFFD32F2F) else Color(0xFF2E7D32)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(curso.nombre, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text(
                    "(${(curso.peso * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorBadge.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    "$notaInt",
                    color = colorBadge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Slider(
            value = curso.nota.value,
            onValueChange = { curso.nota.value = it },
            valueRange = 0f..20f,
            steps = 19 // 20 posiciones enteras (0..20)
        )
    }
}

/** Devuelve el texto de observacion y el color asociado, segun el promedio final. */
private fun obtenerObservacion(promedioFinal: Double): Pair<String, Color> {
    return when {
        promedioFinal >= 17 -> "EXCELENTE" to Color(0xFF1B5E20)      // verde oscuro
        promedioFinal >= 13 -> "APROBADO" to Color(0xFF43A047)       // verde
        promedioFinal >= 10 -> "EN RECUPERACIÓN" to Color(0xFFFFA000) // ambar
        else -> "DESAPROBADO" to Color(0xFFD32F2F)                    // rojo
    }
}

@Composable
private fun TarjetaResultado(
    cursos: List<CursoUI>,
    promedioPonderado: Double,
    promedioFinal: Double,
    redondear: Boolean
) {
    val (observacion, colorChip) = obtenerObservacion(promedioFinal)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Promedio ponderado: " + String.format("%.2f", promedioPonderado))

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Promedio final: " + if (redondear) promedioFinal.toInt().toString()
                    else String.format("%.2f", promedioFinal),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (redondear) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "(redondeado)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            // Reto opcional: aporte por curso
            cursos.forEach { curso ->
                val aporte = curso.nota.value * curso.peso
                Text(
                    "${curso.nombre}: ${curso.nota.value.toInt()} × ${(curso.peso * 100).toInt()}% = " +
                            String.format("%.2f", aporte),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorChip.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(observacion, color = colorChip, fontWeight = FontWeight.Bold)
            }
        }
    }
}