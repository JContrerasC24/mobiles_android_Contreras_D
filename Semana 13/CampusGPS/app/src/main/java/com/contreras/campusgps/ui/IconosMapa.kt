package com.contreras.campusgps.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/** Recursos originales dibujados en memoria; no requieren descargas. */
object IconosMapa {
    fun categoria(categoria: String, seleccionado: Boolean): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(96, 112, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = if (seleccionado) Color.rgb(19, 61, 76) else Color.WHITE
        canvas.drawCircle(48f, 46f, 44f, paint)
        paint.color = when (categoria) {
            "Estudio" -> Color.rgb(24, 100, 169)
            "Comida" -> Color.rgb(173, 79, 25)
            "Eventos" -> Color.rgb(126, 64, 157)
            else -> Color.rgb(0, 116, 102)
        }
        canvas.drawCircle(48f, 46f, 36f, paint)
        canvas.drawRoundRect(40f, 72f, 56f, 106f, 8f, 8f, paint)
        paint.color = Color.WHITE
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        when (categoria) {
            "Estudio" -> { // Libro abierto
                canvas.drawRect(25f, 28f, 71f, 63f, paint)
                canvas.drawLine(48f, 28f, 48f, 63f, paint)
            }
            "Comida" -> { // Tenedor y cuchillo
                canvas.drawLine(34f, 27f, 34f, 65f, paint)
                canvas.drawLine(25f, 27f, 25f, 42f, paint)
                canvas.drawLine(43f, 27f, 43f, 42f, paint)
                canvas.drawLine(25f, 42f, 43f, 42f, paint)
                canvas.drawLine(62f, 27f, 62f, 65f, paint)
                canvas.drawLine(68f, 27f, 68f, 46f, paint)
            }
            "Eventos" -> { // Calendario
                canvas.drawRect(26f, 30f, 70f, 64f, paint)
                canvas.drawLine(26f, 42f, 70f, 42f, paint)
                canvas.drawLine(36f, 24f, 36f, 35f, paint)
                canvas.drawLine(60f, 24f, 60f, 35f, paint)
            }
            else -> {
                paint.style = Paint.Style.FILL
                paint.textSize = 46f
                paint.textAlign = Paint.Align.CENTER
                paint.isFakeBoldText = true
                canvas.drawText("P", 48f, 63f, paint)
            }
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
