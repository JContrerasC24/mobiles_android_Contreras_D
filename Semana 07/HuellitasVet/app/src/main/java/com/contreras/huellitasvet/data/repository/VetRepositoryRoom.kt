package com.contreras.huellitasvet.data.repository

import com.contreras.huellitasvet.data.local.VetDatabase
import com.contreras.huellitasvet.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

class VetRepositoryRoom(
    private val db: VetDatabase
) : VetRepository {

    override fun observarServicios(): Flow<List<Servicio>> {
        return db.servicioDao().observarTodos()
    }

    override fun observarServiciosActivos(): Flow<List<Servicio>> {
        return db.servicioDao().observarActivos()
    }

    override suspend fun guardarServicio(s: Servicio) {
        if (s.id == 0) {
            db.servicioDao().insertar(s)
        } else {
            db.servicioDao().actualizar(s)
        }
    }

    override suspend fun eliminarServicio(s: Servicio) {
        db.servicioDao().eliminar(s)
    }

    override suspend fun cambiarActivo(
        id: Int,
        activo: Boolean
    ) {
        db.servicioDao().cambiarActivo(id, activo)
    }
}
