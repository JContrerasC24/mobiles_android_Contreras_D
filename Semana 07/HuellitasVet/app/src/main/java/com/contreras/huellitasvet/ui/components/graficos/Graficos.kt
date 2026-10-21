package com.contreras.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.model.DatoGrafico
import com.contreras.huellitasvet.model.RegistroPeso
import com.contreras.huellitasvet.ui.components.fechaCorta

@Composable
fun GraficoDona(datos: List<DatoGrafico>) {
    val total = datos.sumOf { it.valor }
    if (total == 0) {
        Text("Aún no hay citas para mostrar.")
        return
    }
    val colores = datos.map {
        when (it.etiqueta.uppercase()) {
            "PENDIENTE" -> Color(0xFFD97706)
            "CONFIRMADA" -> Color(0xFF16A34A)
            "CANCELADA" -> Color(0xFFEF4444)
            else -> Color(0xFF64748B)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(Modifier.size(104.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().semantics {
                contentDescription = "Distribución de $total citas por estado"
            }) {
                val stroke = 17.dp.toPx()
                val side = size.minDimension - stroke
                var angle = -90f
                datos.forEachIndexed { i, d ->
                    val sweep = d.valor.toFloat() / total * 360f
                    drawArc(
                        colores[i],
                        angle,
                        sweep,
                        false,
                        Offset(stroke / 2, stroke / 2),
                        Size(side, side),
                        style = Stroke(stroke)
                    )
                    angle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toString(), style = MaterialTheme.typography.titleLarge)
                Text("citas", style = MaterialTheme.typography.bodySmall)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            datos.forEachIndexed { i, d ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(Modifier.size(8.dp).background(colores[i], RoundedCornerShape(2.dp)))
                    Text(
                        d.etiqueta.lowercase().replaceFirstChar { it.titlecase() },
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(d.valor.toString(), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun GraficoBarras(datos: List<DatoGrafico>) {
    val maximo = datos.maxOfOrNull { it.valor } ?: 0
    if (maximo == 0) {
        Text("Aún no hay citas por servicio.")
        return
    }
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEachIndexed { i, d ->
            Column(Modifier.width(78.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(90.dp), contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(d.valor.toString(), style = MaterialTheme.typography.labelSmall)
                        Box(
                            Modifier.width(40.dp)
                                .height((62f * d.valor / maximo).coerceAtLeast(2f).dp)
                                .background(
                                    if (i == 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = .45f),
                                    RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                                )
                        )
                    }
                }
                Text(
                    d.etiqueta,
                    Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun GraficoLineas(
    registros: List<RegistroPeso>,
    modifier: Modifier = Modifier
) {
    if (registros.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no hay registros de peso en citas atendidas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val textoAccesible = remember(registros) {
        "Evolución del peso (${registros.size} registros): " + registros.joinToString(", ") {
            "${fechaCorta(it.fecha)}: ${it.peso} kg"
        }
    }

    val minPeso = remember(registros) { registros.minOf { it.peso } }
    val maxPeso = remember(registros) { registros.maxOf { it.peso } }

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val pointColor = MaterialTheme.colorScheme.primary
    val pointInnerColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = textoAccesible }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            val width = size.width
            val height = size.height

            val topPadding = 18.dp.toPx()
            val bottomPadding = 18.dp.toPx()
            val usableHeight = (height - topPadding - bottomPadding).coerceAtLeast(1f)

            val gridLines = 3
            for (i in 0 until gridLines) {
                val y = topPadding + usableHeight * (i.toFloat() / (gridLines - 1))
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = if (registros.size == 1) {
                listOf(Offset(width / 2f, topPadding + usableHeight / 2f))
            } else {
                registros.mapIndexed { i, reg ->
                    val x = width * (i.toFloat() / (registros.size - 1))
                    val yNorm = if (maxPeso == minPeso) 0.5f else ((reg.peso - minPeso) / (maxPeso - minPeso)).toFloat()
                    val y = topPadding + (usableHeight * (1f - yNorm))
                    Offset(x, y)
                }
            }

            if (points.size > 1) {
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            points.forEach { pt ->
                drawCircle(color = pointColor, radius = 6.dp.toPx(), center = pt)
                drawCircle(color = pointInnerColor, radius = 3.dp.toPx(), center = pt)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = if (registros.size <= 4) Arrangement.SpaceEvenly else Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            registros.forEach { reg ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${reg.peso} kg",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = fechaCorta(reg.fecha),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = textoAccesible,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
