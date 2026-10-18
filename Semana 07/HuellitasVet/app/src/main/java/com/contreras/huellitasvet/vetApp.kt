package com.contreras.huellitasvet

import android.app.Application
import com.contreras.huellitasvet.data.local.DatosIniciales
import com.contreras.huellitasvet.data.local.VetDatabase
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.data.repository.VetRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VetApp : Application() {

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: VetDatabase by lazy {
        VetDatabase.obtener(this)
    }

    val repository: VetRepository by lazy {
        VetRepositoryRoom(database)
    }

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            DatosIniciales.sembrar(database)
        }
    }
}
