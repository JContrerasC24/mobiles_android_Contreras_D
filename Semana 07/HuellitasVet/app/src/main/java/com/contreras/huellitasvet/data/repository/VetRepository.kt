package com.contreras.huellitasvet.data.repository

import com.contreras.huellitasvet.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

interface VetRepository {

    fun observarServicios(): Flow<List<Servicio>>

    fun observarServiciosActivos(): Flow<List<Servicio>>

    suspend fun guardarServicio(s: Servicio)

    suspend fun eliminarServicio(s: Servicio)

    suspend fun cambiarActivo(id: Int, activo: Boolean)
}