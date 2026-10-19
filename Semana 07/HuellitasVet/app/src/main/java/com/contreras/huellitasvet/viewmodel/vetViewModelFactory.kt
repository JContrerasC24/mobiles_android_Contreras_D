package com.contreras.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.contreras.huellitasvet.data.prefs.PreferenciasStore
import com.contreras.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.Deferred

class VetViewModelFactory(
    private val repository: VetRepository,
    private val preferencias: PreferenciasStore,
    private val inicializacion: Deferred<Unit>,
    private val dueno: com.contreras.huellitasvet.data.local.entity.Usuario? = null,
    private val mascotaId: Int? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when (modelClass) {
            MascotaViewModel::class.java -> MascotaViewModel(repository, requireNotNull(dueno), mascotaId)
            InicioViewModel::class.java -> InicioViewModel(repository, requireNotNull(dueno))
            ServiciosViewModel::class.java -> ServiciosViewModel(repository)
            SesionViewModel::class.java ->
                SesionViewModel(repository, preferencias, inicializacion)
            else -> throw IllegalArgumentException(
                "ViewModel no registrado: ${modelClass.name}"
            )
        }
        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}
