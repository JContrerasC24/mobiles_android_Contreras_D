package com.contreras.huellitasvet.ui.components
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun VetButton(text:String,onClick:()->Unit,modifier:Modifier=Modifier,enabled:Boolean=true) {
    Button(onClick,modifier.heightIn(min=48.dp),enabled=enabled,shape=RoundedCornerShape(12.dp)){Text(text)}
}
