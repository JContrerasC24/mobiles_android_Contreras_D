package com.contreras.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Ilustración vectorial local: se adapta a cualquier densidad sin recursos remotos. */
@Composable
fun MascotaAvatar(modifier:Modifier=Modifier,especie:String="",tamano:Dp=56.dp) {
    val background=MaterialTheme.colorScheme.primaryContainer
    val ink=Color(0xFF30395E)
    Canvas(modifier.size(tamano)) {
        val u=size.minDimension
        fun point(x:Float,y:Float)=Offset(x*u,y*u)
        drawCircle(background)
        val cat=especie=="GATO"
        val other=especie!="PERRO" && !cat
        val fur=if(cat)Color(0xFFABBCE9) else Color(0xFFE8C398)
        if(other) {
            drawOval(ink,point(.29f,.49f),Size(u*.42f,u*.29f))
            listOf(.24f to .39f,.41f to .26f,.60f to .26f,.77f to .39f).forEach {(x,y)->drawCircle(ink,u*.085f,point(x,y))}
        } else {
            if(cat) {
                val ears=Path().apply {moveTo(u*.23f,u*.46f);lineTo(u*.22f,u*.18f);lineTo(u*.47f,u*.33f);lineTo(u*.65f,u*.31f);lineTo(u*.80f,u*.18f);lineTo(u*.79f,u*.52f);close()}
                drawPath(ears,fur)
            } else {
                drawOval(Color(0xFFAF825E),point(.15f,.24f),Size(u*.23f,u*.44f))
                drawOval(Color(0xFFAF825E),point(.64f,.24f),Size(u*.23f,u*.44f))
            }
            drawOval(fur,point(.25f,.28f),Size(u*.50f,u*.52f))
            drawOval(Color(0xFFFFF0DF),point(.35f,.52f),Size(u*.30f,u*.22f))
            drawCircle(ink,u*.024f,point(.39f,.48f));drawCircle(ink,u*.024f,point(.61f,.48f))
            drawOval(ink,point(.455f,.55f),Size(u*.09f,u*.06f))
            drawLine(ink,point(.50f,.60f),point(.50f,.66f),u*.018f)
            drawArc(ink,0f,180f,false,point(.43f,.61f),Size(u*.14f,u*.07f),style=Stroke(u*.015f))
            drawLine(Color(0xFF536DEA),point(.35f,.79f),point(.65f,.79f),u*.045f)
            drawCircle(Color(0xFFFFCB72),u*.043f,point(.5f,.83f))
        }
    }
}
