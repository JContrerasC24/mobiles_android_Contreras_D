package com.contreras.huellitasvet

import android.app.Application
import com.contreras.huellitasvet.data.local.DatosIniciales
import com.contreras.huellitasvet.data.local.VetDatabase
import com.contreras.huellitasvet.data.prefs.PreferenciasStore
import com.contreras.huellitasvet.data.repository.VetRepository
import com.contreras.huellitasvet.data.repository.VetRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

class VetApp : Application() {
    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: VetDatabase by lazy { VetDatabase.obtener(this) }
    val repository: VetRepository by lazy { VetRepositoryRoom(database) }
    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    // async permite comunicar un fallo de inicio al ViewModel en lugar de cerrar la app.
    val inicializacion: Deferred<Unit> by lazy {
        applicationScope.async { DatosIniciales.sembrar(database) }
    }

    override fun onCreate() {
        super.onCreate()
        inicializacion.start()
    }
}
