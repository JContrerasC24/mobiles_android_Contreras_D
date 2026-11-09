package com.tecsup.inventarioherramientas.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.inventarioherramientas.MainActivity

class NotificadorAndroid(
    context: Context
) : Notificador {

    private val appContext = context.applicationContext

    companion object {
        private const val CANAL_ID = "sincronizacion_inventario"
        private const val NOTIFICACION_ID = 1001
    }

    override fun mostrar(
        titulo: String,
        texto: String
    ) {
        crearCanal()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val gestor = NotificationManagerCompat.from(appContext)

        if (!gestor.areNotificationsEnabled()) return

        val intent = Intent(
            appContext,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val abrirApp = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(
            appContext,
            CANAL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(texto)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        try {
            gestor.notify(
                NOTIFICACION_ID,
                notificacion
            )
        } catch (e: SecurityException) {
            // El permiso pudo cambiar después de comprobarlo.
            Log.w(
                "NotificadorAndroid",
                "Android no permitió mostrar la notificación.",
                e
            )
        }
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Sincronización del inventario",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    "Avisos al terminar de actualizar las herramientas."
            }

            val gestor = appContext.getSystemService(
                NotificationManager::class.java
            )

            gestor.createNotificationChannel(canal)
        }
    }
}