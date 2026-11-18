package com.contreras.centrotareas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contreras.centrotareas.ui.theme.NotionAccent
import com.contreras.centrotareas.ui.theme.NotionError
import com.contreras.centrotareas.ui.theme.NotionErrorContainer
import com.contreras.centrotareas.ui.theme.NotionSuccess
import com.contreras.centrotareas.ui.theme.NotionSuccessContainer
import com.contreras.centrotareas.ui.theme.NotionTextSecondary
import com.contreras.centrotareas.ui.theme.NotionWarning
import com.contreras.centrotareas.ui.theme.NotionWarningContainer

/**
 * Insignia / Tag de propiedad estilo Notion para representar estados.
 */
@Composable
fun NotionBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    dotColor: Color? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun NotionStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, txtColor, dotColor) = when (status.lowercase()) {
        "ejecutando", "calculando", "cargando", "trabajando..." -> Triple(
            NotionAccent.copy(alpha = 0.15f),
            NotionAccent,
            NotionAccent
        )
        "completado", "terminó", "listo", "éxito" -> Triple(
            NotionSuccessContainer,
            NotionSuccess,
            NotionSuccess
        )
        "cancelado" -> Triple(
            NotionWarningContainer,
            NotionWarning,
            NotionWarning
        )
        "error" -> Triple(
            NotionErrorContainer,
            NotionError,
            NotionError
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            NotionTextSecondary,
            NotionTextSecondary
        )
    }

    NotionBadge(
        text = status,
        modifier = modifier,
        backgroundColor = bgColor,
        textColor = txtColor,
        dotColor = dotColor
    )
}
