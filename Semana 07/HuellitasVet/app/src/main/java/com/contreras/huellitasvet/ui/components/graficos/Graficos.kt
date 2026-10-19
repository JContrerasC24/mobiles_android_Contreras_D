package com.contreras.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.model.DatoGrafico

@Composable
fun GraficoDona(datos: List<DatoGrafico>) {
    val total = datos.sumOf { it.valor }
    val colores = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.error)
    if (total == 0) { Text("Aún no hay citas para mostrar."); return }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(Modifier.fillMaxWidth().height(180.dp).semantics { contentDescription = "Distribución de $total citas por estado" }) {
            val grosor = 28.dp.toPx()
            val lado = size.minDimension - grosor
            val origen = Offset((size.width - lado) / 2, (size.height - lado) / 2)
            var inicio = -90f
            datos.forEachIndexed { indice, dato ->
                val arco = dato.valor.toFloat() / total * 360f
                if (arco > 0f) drawArc(colores[indice % colores.size], inicio, arco,
                    false, origen, Size(lado, lado), style = Stroke(grosor))
                inicio += arco
            }
        }
        datos.forEachIndexed { indice, dato ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Canvas(Modifier.size(16.dp)) { drawCircle(colores[indice % colores.size]) }
                Text("${dato.etiqueta}: ${dato.valor}")
            }
        }
    }
}

@Composable
fun GraficoBarras(datos: List<DatoGrafico>) {
    val maximo = datos.maxOfOrNull { it.valor } ?: 0
    val color = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    if (maximo == 0) { Text("Aún no hay citas por servicio."); return }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        datos.forEach { dato ->
            Text("${dato.etiqueta}: ${dato.valor}")
            Canvas(Modifier.fillMaxWidth().height(18.dp)) {
                drawRect(fondo)
                drawRect(color, size = Size(size.width * dato.valor.toFloat() / maximo, size.height))
            }
        }
    }
}
