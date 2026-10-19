package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MascotaAvatar(modifier: Modifier = Modifier, especie: String = "") {
    val simbolo = when (especie) {
        "PERRO" -> "🐶"
        "GATO" -> "🐱"
        else -> "🐾"
    }
    Surface(
        modifier = modifier.size(56.dp), shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(simbolo, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
