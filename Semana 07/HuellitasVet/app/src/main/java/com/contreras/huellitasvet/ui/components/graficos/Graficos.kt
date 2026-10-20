package com.contreras.huellitasvet.ui.components.graficos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.contreras.huellitasvet.model.DatoGrafico
@Composable
fun GraficoDona(datos:List<DatoGrafico>) {
    val total=datos.sumOf{it.valor}
    if(total==0){Text("Aún no hay citas para mostrar.");return}
    val colores=datos.map{when(it.etiqueta.uppercase()){
        "PENDIENTE"->Color(0xFFD97706);"CONFIRMADA"->Color(0xFF16A34A)
        "CANCELADA"->Color(0xFFEF4444);else->Color(0xFF64748B)}}
    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(18.dp)) {
        Box(Modifier.size(104.dp),contentAlignment=Alignment.Center) {
            Canvas(Modifier.fillMaxSize().semantics{contentDescription="Distribución de $total citas por estado"}) {
                val stroke=17.dp.toPx()
                val side=size.minDimension-stroke
                var angle=-90f
                datos.forEachIndexed{i,d->
                    val sweep=d.valor.toFloat()/total*360f
                    drawArc(colores[i],angle,sweep,false,Offset(stroke/2,stroke/2),Size(side,side),style=Stroke(stroke))
                    angle+=sweep
                }
            }
            Column(horizontalAlignment=Alignment.CenterHorizontally){
                Text(total.toString(),style=MaterialTheme.typography.titleLarge)
                Text("citas",style=MaterialTheme.typography.bodySmall)
            }
        }
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(7.dp)) {
            datos.forEachIndexed{i,d->Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
                Box(Modifier.size(8.dp).background(colores[i],RoundedCornerShape(2.dp)))
                Text(d.etiqueta.lowercase().replaceFirstChar{it.titlecase()},Modifier.weight(1f),style=MaterialTheme.typography.bodySmall)
                Text(d.valor.toString(),style=MaterialTheme.typography.labelMedium)
            }}
        }
    }
}
@Composable
fun GraficoBarras(datos:List<DatoGrafico>) {
    val maximo=datos.maxOfOrNull{it.valor}?:0
    if(maximo==0){Text("Aún no hay citas por servicio.");return}
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.SpaceEvenly,
        verticalAlignment=Alignment.Bottom) {
        datos.forEachIndexed{i,d->
            Column(Modifier.width(78.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                Box(Modifier.height(90.dp),contentAlignment=Alignment.BottomCenter){
                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                        Text(d.valor.toString(),style=MaterialTheme.typography.labelSmall)
                        Box(Modifier.width(40.dp).height((62f*d.valor/maximo).coerceAtLeast(2f).dp)
                            .background(if(i==0)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha=.45f),
                                RoundedCornerShape(topStart=5.dp,topEnd=5.dp)))
                    }
                }
                Text(d.etiqueta,Modifier.padding(top=6.dp),style=MaterialTheme.typography.labelSmall,maxLines=2)
            }
        }
    }
}
