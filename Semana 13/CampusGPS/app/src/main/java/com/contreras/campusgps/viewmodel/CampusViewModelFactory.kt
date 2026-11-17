package com.contreras.campusgps.viewmodel

import android.content.Context

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Repositorios utilizados por CampusGPS.
import com.contreras.campusgps.data.LugaresRepositoryLocal
import com.contreras.campusgps.data.UbicacionRepositoryGoogle


/**
 * Factory encargada de crear MapaViewModel.
 *
 * Implementa ViewModelProvider.Factory.
 *
 * Su responsabilidad consiste en proporcionar
 * las dependencias necesarias al ViewModel.
 *
 * De esta manera mantenemos la arquitectura
 * MVVM + Repository.
 */
class CampusViewModelFactory(

    private val context: Context

) : ViewModelProvider.Factory {


    /**
     * Crea una instancia del ViewModel solicitado.
     *
     * @param modelClass Clase del ViewModel.
     *
     * @return Instancia del ViewModel con sus repositorios.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(

        modelClass: Class<T>

    ): T {

        // =================================================
        // 1. COMPROBAR EL VIEWMODEL SOLICITADO
        // =================================================

        /*
         * Verifica que la clase solicitada
         * corresponda a MapaViewModel.
         */
        if (
            modelClass.isAssignableFrom(
                MapaViewModel::class.java
            )
        ) {

            // =============================================
            // 2. CREAR EL VIEWMODEL
            // =============================================

            /*
             * Proporciona al ViewModel
             * los dos repositorios que necesita.
             */
            return MapaViewModel(

                // =========================================
                // REPOSITORIO DE UBICACIÓN
                // =========================================

                /*
                 * Utiliza Google Play Services para obtener
                 * las coordenadas del dispositivo.
                 *
                 * También contiene las verificaciones
                 * de permisos, GPS y Play Services.
                 */
                ubicacionRepo = UbicacionRepositoryGoogle(

                    context.applicationContext
                ),


                // =========================================
                // REPOSITORIO DE LUGARES
                // =========================================

                /*
                 * Proporciona los cinco lugares ficticios
                 * que tenemos registrados en el campus.
                 */
                lugaresRepo = LugaresRepositoryLocal()

            ) as T
        }


        // =================================================
        // 3. VIEWMODEL DESCONOCIDO
        // =================================================

        /*
         * Si se solicita una clase diferente,
         * informa que no puede construirla.
         */
        throw IllegalArgumentException(

            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}