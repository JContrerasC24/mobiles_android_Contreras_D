package com.contreras.saludplus.citas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.contreras.saludplus.citas.R
import com.contreras.saludplus.citas.data.model.Medico
import com.contreras.saludplus.citas.data.repository.Repositorio
import com.contreras.saludplus.citas.util.CalendarioCitas
import java.time.LocalDate
import java.util.Locale

// Comparte una cabecera compacta con regreso accesible.
@Composable
fun CabeceraSalud(
    titulo: String,
    volver: () -> Unit,
    accion: (@Composable () -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = volver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Text(
            titulo,
            Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            accion?.invoke()
        }
    }
}

// Uniforma los botones principales del flujo.
@Composable
fun BotonSalud(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    accion: () -> Unit
) {
    Button(
        onClick = accion,
        modifier = modifier.fillMaxWidth().heightIn(min = 50.dp),
        enabled = habilitado,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

// Asocia colores e iconos a cada especialidad.
data class AparienciaEspecialidad(
    val icono: ImageVector,
    val color: Color,
    val fondo: Color
)

fun aparienciaEspecialidad(id: Int): AparienciaEspecialidad = when (id) {
    2 -> AparienciaEspecialidad(Icons.Outlined.ChildCare, Color(0xFFDA8A21), Color(0xFFFFF3DF))
    3 -> AparienciaEspecialidad(Icons.Outlined.Female, Color(0xFFD95A9C), Color(0xFFFDECF5))
    4 -> AparienciaEspecialidad(Icons.Filled.Favorite, Color(0xFFE15B69), Color(0xFFFFECEE))
    5 -> AparienciaEspecialidad(Icons.Outlined.WbSunny, Color(0xFFDE9A34), Color(0xFFFFF4DF))
    6 -> AparienciaEspecialidad(Icons.Outlined.Healing, Color(0xFF42A3C0), Color(0xFFE8F7FC))
    7 -> AparienciaEspecialidad(Icons.Outlined.Visibility, Color(0xFF7370DE), Color(0xFFEFEEFF))
    else -> AparienciaEspecialidad(Icons.Filled.Person, Color(0xFF318CDC), Color(0xFFE9F5FF))
}

// Presenta el símbolo de una especialidad sobre un fondo suave.
@Composable
fun IconoEspecialidad(id: Int, modifier: Modifier = Modifier) {
    val aspecto = aparienciaEspecialidad(id)
    Box(
        modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(aspecto.fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(aspecto.icono, null, Modifier.size(28.dp), tint = aspecto.color)
    }
}

// Utiliza retratos ilustrativos locales para los médicos ficticios.
@Composable
fun FotoMedico(medico: Medico, modifier: Modifier = Modifier) {
    val imagen = when (medico.id) {
        2, 5, 7 -> R.drawable.sp_doctor_luis
        3, 4, 8 -> R.drawable.sp_doctora_carla
        else -> R.drawable.sp_doctora_ana
    }
    Image(
        painterResource(imagen),
        contentDescription = null,
        modifier = modifier.size(56.dp).clip(CircleShape),
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter
    )
}

// Calcula la disponibilidad sin inventar horarios libres.
private fun disponibilidadMedico(medicoId: Int): String {
    val hoy = LocalDate.now()
    val fecha = CalendarioCitas.proximosDiasHabiles(hoy).firstOrNull {
        Repositorio.horariosDisponibles(medicoId, it.toString()).isNotEmpty()
    }
    return when (fecha) {
        hoy -> "Disponible hoy"
        hoy.plusDays(1) -> "Disponible mañana"
        null -> "Consultar otras semanas"
        else -> "Disponible ${CalendarioCitas.diaCorto(fecha)} ${fecha.dayOfMonth}"
    }
}

// Comparte la ficha entre médicos, calendario y confirmación.
@Composable
fun FichaMedico(
    medico: Medico,
    modifier: Modifier = Modifier,
    mostrarValoracion: Boolean = false,
    mostrarDisponibilidad: Boolean = false,
    seleccionar: (() -> Unit)? = null
) {
    val interaccion = if (seleccionar != null) Modifier.clickable(onClick = seleccionar) else Modifier
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F7FB)).then(interaccion).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FotoMedico(medico)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(medico.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(medico.descripcion, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (mostrarValoracion) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, "Calificación", Modifier.size(15.dp), Color(0xFFF5AC34))
                    Text(String.format(Locale.forLanguageTag("es-PE"), " %.1f", medico.calificacion),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            if (mostrarDisponibilidad) {
                Text(
                    disponibilidadMedico(medico.id),
                    Modifier.align(Alignment.End).clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE1F7EB)).padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF21734C)
                )
            }
        }
    }
}

// Presenta los datos de confirmación con iconos uniformes.
@Composable
fun DatoReserva(icono: ImageVector, titulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFEEF4FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, Modifier.size(21.dp), MaterialTheme.colorScheme.primary)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
