package com.tecsup.inventarioherramientas.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.tecsup.inventarioherramientas.R
import com.tecsup.inventarioherramientas.domain.model.Item

val Petroleo = Color(0xFF06454B)
val Turquesa = Color(0xFF007B79)
val Ambar = Color(0xFFFFC349)
val Marfil = Color(0xFFFAF7F2)
val TallerColors = lightColorScheme(primary = Turquesa, onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F1ED), onPrimaryContainer = Petroleo,
    secondaryContainer = Ambar, onSecondaryContainer = Color(0xFF382700),
    background = Color(0xFFF3F6F6), surface = Color.White, onSurface = Color(0xFF122D31))

private fun foto(nombre: String): Int {
    val n = nombre.lowercase()
    return when {
        "destornillador" in n || "desarmador" in n -> R.drawable.foto_destornillador
        "alicate" in n || "pinza" in n -> R.drawable.foto_alicate
        "sierra" in n -> R.drawable.foto_sierra
        "rotomartillo" in n || "taladro" in n -> R.drawable.foto_taladro
        "martillo" in n -> R.drawable.foto_martillo
        "llave" in n -> R.drawable.foto_llave
        "amoladora" in n || "esmeril" in n -> R.drawable.foto_amoladora
        "flexómetro" in n || "cinta" in n -> R.drawable.foto_cinta
        else -> R.drawable.foto_caja
    }
}

@Composable
fun Foto(nombre: String, modifier: Modifier) {
    Box(modifier.background(Marfil), contentAlignment = Alignment.Center) {
        Image(painterResource(foto(nombre)), null, Modifier.fillMaxSize().padding(6.dp))
    }
}

@Composable
fun Cabecera(titulo: String, contexto: String) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
        .background(Brush.linearGradient(listOf(Petroleo, Turquesa))).padding(22.dp)) {
        Text(contexto.uppercase(), color = Color(0xFFC8EBE7), fontSize = 11.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun Indicador(valor: String, etiqueta: String, modifier: Modifier = Modifier, destacado: Boolean = false) {
    Surface(modifier, shape = RoundedCornerShape(18.dp), color = if (destacado) Ambar else Petroleo,
        contentColor = if (destacado) Petroleo else Color.White) {
        Column(Modifier.padding(18.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EstadoItem(item: Item) {
    val disponible = item.disponibilidad.equals("DISPONIBLE", true)
    Surface(shape = CircleShape, color = if (disponible) Color(0xFFD9F1E4) else Color(0xFFFFE8BD)) {
        Text(item.disponibilidad.lowercase().replaceFirstChar { it.uppercase() },
            Modifier.padding(horizontal = 9.dp, vertical = 3.dp), color = Petroleo, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun Corazon(item: Item, accion: () -> Unit) {
    IconToggleButton(item.esFavorito, { accion() }, Modifier.semantics {
        contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Guardar en favoritos"
    }) {
        Surface(shape = CircleShape, color = Color.White) {
            Text(if (item.esFavorito) "♥" else "♡", Modifier.padding(horizontal = 7.dp),
                fontSize = 22.sp, color = if (item.esFavorito) Color(0xFFAB3346) else Petroleo)
        }
    }
}

@Composable
fun HerramientaCard(herramienta: Item, onFavorito: () -> Unit, onVerDetalle: () -> Unit, compacta: Boolean = false) {
    ElevatedCard(onClick = onVerDetalle, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
        if (compacta) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Foto(herramienta.nombre, Modifier.size(74.dp).clip(RoundedCornerShape(12.dp)))
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(herramienta.nombre, fontWeight = FontWeight.Bold)
                    Text(herramienta.codigo, style = MaterialTheme.typography.labelSmall)
                    EstadoItem(herramienta)
                }
                Corazon(herramienta, onFavorito)
            }
        } else {
            Box {
                Foto(herramienta.nombre, Modifier.fillMaxWidth().aspectRatio(1.42f))
                Box(Modifier.align(Alignment.TopEnd)) { Corazon(herramienta, onFavorito) }
            }
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(herramienta.codigo, color = Turquesa, letterSpacing = 1.sp, style = MaterialTheme.typography.labelSmall)
                Text(herramienta.nombre, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(herramienta.ubicacion, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                EstadoItem(herramienta)
            }
        }
    }
}
