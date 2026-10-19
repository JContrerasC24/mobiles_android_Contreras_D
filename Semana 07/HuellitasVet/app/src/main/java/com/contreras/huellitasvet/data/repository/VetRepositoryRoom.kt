package com.contreras.huellitasvet.data.repository

import androidx.room.withTransaction
import com.contreras.huellitasvet.data.local.VetDatabase
import com.contreras.huellitasvet.data.local.entity.Cita
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.model.EstadoCita

class VetRepositoryRoom(
    private val db: VetDatabase
) : VetRepository {

    override suspend fun obtenerUsuario(id: Int) = db.usuarioDao().obtener(id)

    override suspend fun login(correo: String, clave: String) =
        db.usuarioDao().login(correo, clave)

    override fun observarServicios() = db.servicioDao().observarTodos()

    override fun observarServiciosActivos() = db.servicioDao().observarActivos()

    override suspend fun guardarServicio(s: Servicio) {
        if (s.id == 0) db.servicioDao().insertar(s)
        else db.servicioDao().actualizar(s)
    }

    override suspend fun eliminarServicio(s: Servicio) {
        db.servicioDao().eliminar(s)
    }

    override suspend fun cambiarActivo(id: Int, activo: Boolean) {
        db.servicioDao().cambiarActivo(id, activo)
    }

    override suspend fun servicioTieneCitas(id: Int) =
        db.citaDao().contarPorServicioId(id) > 0

    override fun observarMascotasDeDueno(duenoId: Int) =
        db.mascotaDao().observarPorDueno(duenoId)

    override fun observarMascota(id: Int) = db.mascotaDao().observarUna(id)

    override fun observarTodasLasMascotas() = db.mascotaDao().observarTodas()

    override suspend fun guardarMascota(m: Mascota) {
        if (m.id == 0) db.mascotaDao().insertar(m)
        else db.mascotaDao().actualizar(m)
    }

    override suspend fun eliminarMascota(m: Mascota) {
        db.mascotaDao().eliminar(m)
    }

    override suspend fun mascotaTieneCitas(id: Int) =
        db.mascotaDao().contarCitas(id) > 0

    override fun observarHorasOcupadas(fecha: String) =
        db.citaDao().observarHorasOcupadas(fecha)

    override suspend fun crearCita(
        mascotaId: Int,
        servicioId: Int,
        fecha: String,
        hora: String,
        nota: String
    ): Long = db.withTransaction {
        val servicio = checkNotNull(db.servicioDao().obtener(servicioId)) {
            "El servicio no existe"
        }
        // La comprobacion y la insercion comparten transaccion para evitar duplicados.
        check(db.citaDao().contarOcupadas(fecha, hora) == 0) {
            "El horario ya esta ocupado"
        }
        db.citaDao().insertar(
            Cita(
                mascotaId = mascotaId,
                servicioId = servicioId,
                fecha = fecha,
                hora = hora,
                nota = nota,
                estado = EstadoCita.PENDIENTE.name,
                precio = servicio.precio,
                fechaCreacion = System.currentTimeMillis()
            )
        )
    }

    override fun observarCitasDeDueno(duenoId: Int) =
        db.citaDao().observarPorDueno(duenoId)

    override fun observarCitasPorFecha(fecha: String) =
        db.citaDao().observarPorFecha(fecha)

    override fun observarCita(id: Int) = db.citaDao().observarUna(id)

    override fun observarHistorial(mascotaId: Int) =
        db.citaDao().observarHistorial(mascotaId)

    override suspend fun cambiarEstadoCita(id: Int, estado: EstadoCita) {
        db.citaDao().actualizarEstado(id, estado.name)
    }

    override suspend fun registrarAtencion(
        citaId: Int,
        diagnostico: String,
        tratamiento: String,
        peso: Double
    ) {
        db.withTransaction {
            val cita = checkNotNull(db.citaDao().obtener(citaId)) {
                "La cita no existe"
            }
            db.citaDao().registrarAtencion(citaId, diagnostico, tratamiento, peso)
            db.mascotaDao().actualizarPeso(cita.mascotaId, peso)
        }
    }

    override fun observarCitasDeFecha(fecha: String) =
        db.citaDao().contarPorFecha(fecha)

    override fun observarCantidadPorEstado(estado: EstadoCita) =
        db.citaDao().contarPorEstado(estado.name)

    override fun observarCitasPorServicio() = db.citaDao().contarPorServicio()

    override fun observarIngresosDesde(fechaInicio: String) =
        db.citaDao().ingresosDesde(fechaInicio)
}
