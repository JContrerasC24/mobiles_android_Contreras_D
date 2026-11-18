package com.contreras.campusgps.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/** Recursos originales dibujados en memoria; no requieren descargas. */
object IconosMapa {
    fun automovil(): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(64, 96, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.rgb(20, 35, 43)
        canvas.drawRoundRect(7f, 18f, 57f, 38f, 4f, 4f, p)
        canvas.drawRoundRect(7f, 60f, 57f, 80f, 4f, 4f, p)
        p.color = Color.rgb(245, 176, 50)
        canvas.drawRoundRect(13f, 6f, 51f, 90f, 12f, 12f, p)
        p.color = Color.rgb(26, 70, 85)
        canvas.drawRoundRect(18f, 25f, 46f, 41f, 4f, 4f, p)
        canvas.drawRoundRect(18f, 64f, 46f, 76f, 3f, 3f, p)
        p.color = Color.WHITE
        canvas.drawRect(17f, 10f, 25f, 16f, p)
        canvas.drawRect(39f, 10f, 47f, 16f, p)
        p.color = Color.RED
        canvas.drawRect(17f, 82f, 24f, 87f, p)
        canvas.drawRect(40f, 82f, 47f, 87f, p)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

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
