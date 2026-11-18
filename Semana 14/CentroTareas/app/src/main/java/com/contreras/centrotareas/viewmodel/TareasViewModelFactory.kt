package com.contreras.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.contreras.centrotareas.data.TareasRepository
import com.contreras.centrotareas.data.TareasRepositoryLocal

/**
 * Factory encargada de crear el ViewModel.
 *
 * Proporciona el Repository que necesita
 * TareasViewModel para ejecutar sus operaciones.
 */
class TareasViewModelFactory(
    private val repository: TareasRepository = TareasRepositoryLocal()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {

            return TareasViewModel(
                repo = repository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}