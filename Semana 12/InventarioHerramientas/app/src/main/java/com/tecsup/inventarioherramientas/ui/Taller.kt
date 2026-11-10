package com.tecsup.inventarioherramientas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val TallerColors = lightColorScheme(
    primary = Color(0xFF006B66), onPrimary = Color.White,
    primaryContainer = Color(0xFFC9EFEB), onPrimaryContainer = Color(0xFF003D39),
    secondaryContainer = Color(0xFFFFE3A0), onSecondaryContainer = Color(0xFF463300),
    background = Color(0xFFF4F7F6), surface = Color.White,
    surfaceVariant = Color(0xFFE3EBE8), onSurface = Color(0xFF192F2D)
)

@Composable
fun Cabecera(titulo: String, contexto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(contexto, style = MaterialTheme.typography.labelLarge)
            Text(titulo, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun Indicador(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Column(Modifier.padding(16.dp)) {
            Text(valor, style = MaterialTheme.typography.headlineMedium)
            Text(etiqueta, style = MaterialTheme.typography.labelLarge)
        }
    }
}
