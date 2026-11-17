package com.contreras.campusgps.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

import com.contreras.campusgps.model.Posicion

// Google Play Services.
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


/**
 * Excepción utilizada cuando los servicios
 * de ubicación del dispositivo están desactivados.
 */
class GpsDesactivadoException : IllegalStateException(

    "La ubicación del dispositivo está desactivada. " +
            "Actívala desde la configuración de Android."
)


/**
 * Excepción utilizada cuando Google Play Services
 * no está disponible o necesita una actualización.
 */
class PlayServicesNoDisponiblesException : IllegalStateException(

    "Google Play Services no está disponible. " +
            "Comprueba que el dispositivo tenga los servicios de Google."
)


/**
 * Implementación del repositorio de ubicación.
 *
 * Utiliza Google Play Services para obtener
 * las coordenadas actuales del dispositivo.
 *
 * Antes de solicitar la ubicación verifica:
 *
 * 1. Disponibilidad de Google Play Services.
 * 2. Permisos de ubicación.
 * 3. Servicios de ubicación activados.
 */
class UbicacionRepositoryGoogle(

    context: Context

) : UbicacionRepository {


    // =====================================================
    // 1. CONTEXTO DE LA APLICACIÓN
    // =====================================================

    /*
     * Utilizamos applicationContext para evitar
     * conservar referencias innecesarias
     * a una Activity.
     */
    private val contexto = context.applicationContext


    // =====================================================
    // 2. CLIENTE DE UBICACIÓN
    // =====================================================

    /*
     * FusedLocationProviderClient obtiene la ubicación
     * utilizando las fuentes disponibles del dispositivo.
     *
     * Se inicializa cuando se necesita por primera vez.
     */
    private val cliente by lazy {

        LocationServices.getFusedLocationProviderClient(
            contexto
        )
    }


    // =====================================================
    // 3. OBTENER UBICACIÓN
    // =====================================================

    /**
     * Solicita las coordenadas actuales del dispositivo.
     *
     * @return Result con la posición obtenida
     * o con la excepción correspondiente.
     */
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {

        return try {

            // =============================================
            // 3.1 VERIFICAR GOOGLE PLAY SERVICES
            // =============================================

            /*
             * Comprueba si Google Play Services
             * está instalado y disponible.
             */
            val disponibilidad =
                GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(contexto)


            if (disponibilidad != ConnectionResult.SUCCESS) {

                throw PlayServicesNoDisponiblesException()
            }


            // =============================================
            // 3.2 COMPROBAR PERMISOS
            // =============================================

            /*
             * Comprueba si el usuario autorizó
             * la ubicación precisa.
             */
            val permisoPreciso =
                ContextCompat.checkSelfPermission(

                    contexto,

                    Manifest.permission.ACCESS_FINE_LOCATION

                ) == PackageManager.PERMISSION_GRANTED


            /*
             * Comprueba si el usuario autorizó
             * la ubicación aproximada.
             */
            val permisoAproximado =
                ContextCompat.checkSelfPermission(

                    contexto,

                    Manifest.permission.ACCESS_COARSE_LOCATION

                ) == PackageManager.PERMISSION_GRANTED


            /*
             * Si ninguno de los permisos fue concedido,
             * no podemos solicitar la ubicación.
             */
            if (!permisoPreciso && !permisoAproximado) {

                throw SecurityException(

                    "No se concedieron los permisos de ubicación."
                )
            }


            // =============================================
            // 3.3 COMPROBAR SERVICIOS DE UBICACIÓN
            // =============================================

            /*
             * Obtiene el administrador de ubicación
             * proporcionado por Android.
             */
            val administradorUbicacion =
                contexto.getSystemService(
                    Context.LOCATION_SERVICE
                ) as? LocationManager


            if (administradorUbicacion == null) {

                throw IllegalStateException(

                    "El dispositivo no dispone de un servicio de ubicación."
                )
            }


            /*
             * Comprueba si los servicios de ubicación
             * del dispositivo están activados.
             *
             * LocationManagerCompat permite realizar
             * esta comprobación en diferentes versiones
             * de Android.
             */
            val ubicacionActivada =
                LocationManagerCompat.isLocationEnabled(
                    administradorUbicacion
                )


            if (!ubicacionActivada) {

                throw GpsDesactivadoException()
            }


            // =============================================
            // 3.4 SELECCIONAR PRECISIÓN
            // =============================================

            /*
             * Si el usuario autorizó la ubicación precisa,
             * utilizamos alta precisión.
             *
             * Si solamente autorizó la aproximada,
             * utilizamos precisión equilibrada.
             */
            val prioridad = if (permisoPreciso) {

                Priority.PRIORITY_HIGH_ACCURACY

            } else {

                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }


            // =============================================
            // 3.5 SOLICITAR UBICACIÓN
            // =============================================

            /*
             * Permite cancelar la solicitud
             * cuando ya no sea necesaria.
             */
            val cancelacion = CancellationTokenSource()


            val ubicacion = try {

                cliente.getCurrentLocation(

                    prioridad,

                    cancelacion.token

                ).await()

            } finally {

                // Libera la solicitud de ubicación.
                cancelacion.cancel()
            }


            // =============================================
            // 3.6 COMPROBAR RESULTADO
            // =============================================

            /*
             * Aunque los permisos y servicios estén activos,
             * Android podría no conseguir una ubicación.
             */
            if (ubicacion == null) {

                throw IllegalStateException(

                    "No se pudo obtener la ubicación actual. " +
                            "Inténtalo nuevamente."
                )
            }


            // =============================================
            // 3.7 CONVERTIR A NUESTRO MODELO
            // =============================================

            /*
             * Convierte las coordenadas de Android
             * al modelo Posicion de CampusGPS.
             */
            val posicion = Posicion(

                latitud = ubicacion.latitude,

                longitud = ubicacion.longitude
            )


            // Devuelve el resultado correctamente.
            Result.success(posicion)


        } catch (error: CancellationException) {

            /*
             * Las cancelaciones de corrutinas
             * deben propagarse correctamente.
             */
            throw error


        } catch (error: Exception) {

            /*
             * Convierte cualquier excepción
             * en un resultado fallido.
             *
             * El ViewModel podrá recibirla
             * mediante onFailure().
             */
            Result.failure(error)
        }
    }
}