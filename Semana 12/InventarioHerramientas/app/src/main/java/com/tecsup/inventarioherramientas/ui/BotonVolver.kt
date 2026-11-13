package com.tecsup.inventarioherramientas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.inventarioherramientas.R

@Composable
fun BotonVolver(etiqueta: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Petroleo, contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(start = 7.dp, end = 18.dp, top = 7.dp, bottom = 7.dp)
    ) {
        Surface(shape = CircleShape, color = if (enabled) Ambar else Color.Transparent) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_volver), null, Modifier.size(20.dp),
                    tint = if (enabled) Petroleo else LocalContentColor.current)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(etiqueta, fontWeight = FontWeight.SemiBold)
    }
}
