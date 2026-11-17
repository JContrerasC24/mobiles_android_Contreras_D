package com.contreras.campusgps.viewmodel

import android.content.Context

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.contreras.campusgps.data.LugaresRepositoryLocal
import com.contreras.campusgps.data.UbicacionRepositoryGoogle

/**
 * Factory encargada de construir MapaViewModel.
 *
 * Proporciona las implementaciones de los repositorios
 * que necesita el ViewModel.
 */
class CampusViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Comprueba que se está solicitando el ViewModel correcto.
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {

            // Crea el ViewModel con sus dependencias.
            return MapaViewModel(

                // Utiliza el contexto de la aplicación para evitar
                // conservar referencias innecesarias a una Activity.
                ubicacionRepo = UbicacionRepositoryGoogle(
                    context.applicationContext
                ),

                // Proporciona los cinco lugares ficticios.
                lugaresRepo = LugaresRepositoryLocal()

            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}