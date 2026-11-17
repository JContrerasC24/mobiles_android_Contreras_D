package com.contreras.campusgps.data

import android.annotation.SuppressLint
import android.content.Context

import com.contreras.campusgps.model.Posicion

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

import kotlinx.coroutines.tasks.await

/**
 * Implementación del repositorio de ubicación.
 *
 * Utiliza Google Play Services para obtener
 * las coordenadas actuales del dispositivo.
 */
class UbicacionRepositoryGoogle(
    context: Context
) : UbicacionRepository {

    // Cliente que permite acceder a los servicios de ubicación.
    private val cliente = LocationServices
        .getFusedLocationProviderClient(context.applicationContext)

    /**
     * Solicita la ubicación actual.
     *
     * Esta función debe llamarse únicamente después
     * de comprobar que el usuario concedió los permisos.
     */
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {

        return runCatching {

            // Solicita una ubicación con alta precisión.
            val ubicacion = cliente.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            // Si Google no devuelve coordenadas, informa del problema.
            if (ubicacion == null) {
                throw IllegalStateException(
                    "No se pudo obtener la ubicación. Verifica el GPS."
                )
            }

            // Convierte la ubicación de Android a nuestro modelo Posicion.
            Posicion(
                latitud = ubicacion.latitude,
                longitud = ubicacion.longitude
            )
        }
    }
}