package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.contreras.huellitasvet.data.repository.VetRepository

class VetViewModelFactory(
    private val repository: VetRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass == ServiciosViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            return ServiciosViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel no registrado: ${modelClass.name}"
        )
    }
}