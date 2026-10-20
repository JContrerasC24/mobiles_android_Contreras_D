package com.contreras.huellitasvet.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun MascotaAvatar(modifier:Modifier=Modifier,especie:String="",tamano:Dp=56.dp) {
    Surface(modifier.size(tamano),shape=CircleShape,color=MaterialTheme.colorScheme.primaryContainer) {
        Box(contentAlignment=Alignment.Center) {
            Text(when(especie){"PERRO"->"🐶";"GATO"->"🐱";else->"🐾"},fontSize=(tamano.value*0.53f).sp)
        }
    }
}
