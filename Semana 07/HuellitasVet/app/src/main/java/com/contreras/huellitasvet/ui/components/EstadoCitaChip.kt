package com.contreras.huellitasvet.ui.components
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
@Composable
fun EstadoCitaChip(estado:String) {
    val dark=MaterialTheme.colorScheme.background.luminance()<0.4f
    val (bg,fg)=when(estado){
        "PENDIENTE"->Color(0xFFFFF3C4) to Color(0xFFB56A00)
        "CONFIRMADA"->Color(0xFFDDFBE9) to Color(0xFF138544)
        "CANCELADA"->Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        else->Color(0xFFE2E8F0) to Color(0xFF475569)
    }
    Surface(shape=RoundedCornerShape(30.dp),color=if(dark)fg.copy(alpha=.25f) else bg,
        contentColor=if(dark)bg else fg) {
        Text(tituloValor(estado),Modifier.padding(horizontal=9.dp,vertical=3.dp),style=MaterialTheme.typography.labelSmall)
    }
}
