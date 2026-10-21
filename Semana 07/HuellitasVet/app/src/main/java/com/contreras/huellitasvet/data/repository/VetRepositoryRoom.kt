package com.contreras.huellitasvet.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import com.contreras.huellitasvet.model.ReglasCitas
import java.time.LocalDateTime
import com.contreras.huellitasvet.data.local.VetDatabase
import com.contreras.huellitasvet.data.local.entity.Cita
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.model.EstadoCita

class VetRepositoryRoom(
    private val db: VetDatabase
) : VetRepository {

    override suspend fun obtenerUsuarioPorRol(rol: String) = db.usuarioDao().obtenerPorRol(rol)

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
        db.withTransaction {
            check(db.citaDao().contarPorServicioId(s.id) == 0) {
                "El servicio tiene citas y no se puede eliminar. Puedes desactivarlo."
            }
            db.servicioDao().eliminar(s)
        }
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
        db.withTransaction {
            check(db.mascotaDao().contarCitas(m.id) == 0) {
                "No se puede eliminar: la mascota tiene citas registradas."
            }
            db.mascotaDao().eliminar(m)
        }
    }

    override suspend fun mascotaTieneCitas(id: Int) =
        db.mascotaDao().contarCitas(id) > 0

    override fun observarHorasOcupadas(fecha: String) =
        db.citaDao().observarHorasOcupadas(fecha)

    override fun observarHorasOcupadasExcluyendo(fecha: String, citaId: Int) =
        db.citaDao().observarHorasOcupadasExcluyendo(fecha, citaId)

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
        check(servicio.activo) { "El servicio ya no está disponible." }
        check(com.contreras.huellitasvet.model.Horarios.disponible(
            java.time.LocalDate.parse(fecha), hora, emptyList(), java.time.LocalDateTime.now()
        )) { "El horario ya no está disponible. Elige otro." }
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

    override fun observarHistorialPeso(mascotaId: Int) =
        db.citaDao().observarHistorialPeso(mascotaId)

    override suspend fun cancelarCitaDeDueno(id: Int, duenoId: Int) {
        db.withTransaction {
            val cita = checkNotNull(db.citaDao().obtener(id)) { "La cita ya no existe." }
            val mascota = db.mascotaDao().observarUna(cita.mascotaId).first()
            check(mascota?.duenoId == duenoId) { "No puedes cancelar esta cita." }
            check(ReglasCitas.puedeCancelar(cita, LocalDateTime.now())) {
                "Ya no puedes cancelar esta cita"
            }
            db.citaDao().actualizarEstado(id, EstadoCita.CANCELADA.name)
        }
    }

    override suspend fun reprogramarCitaDeDueno(
        citaId: Int,
        duenoId: Int,
        nuevaFecha: String,
        nuevaHora: String
    ) {
        db.withTransaction {
            val cita = checkNotNull(db.citaDao().obtener(citaId)) { "La cita ya no existe." }
            val mascota = db.mascotaDao().observarUna(cita.mascotaId).first()
            check(mascota?.duenoId == duenoId) { "No puedes reprogramar esta cita." }
            check(ReglasCitas.puedeReprogramar(cita, LocalDateTime.now())) {
                "Ya no puedes reprogramar esta cita"
            }
            val fechaParsed = java.time.LocalDate.parse(nuevaFecha)
            check(com.contreras.huellitasvet.model.Horarios.disponible(
                fechaParsed, nuevaHora, emptyList(), LocalDateTime.now()
            )) { "El horario ya no está disponible. Elige otro." }
            check(db.citaDao().contarOcupadasExcluyendo(nuevaFecha, nuevaHora, citaId) == 0) {
                "El horario ya está ocupado"
            }
            db.citaDao().reprogramarCita(citaId, nuevaFecha, nuevaHora)
        }
    }

    override suspend fun cambiarEstadoCita(id: Int, estado: EstadoCita) {
        db.withTransaction {
            val cita = checkNotNull(db.citaDao().obtener(id)) { "La cita ya no existe." }
            check(com.contreras.huellitasvet.model.ReglasAgenda.permite(cita.estado, estado)) {
                "La cita ya no permite esta acción."
            }
            db.citaDao().actualizarEstado(id, estado.name)
        }
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
            check(ReglasCitas.activa(cita)) { "La cita ya no permite registrar atención." }
            check(com.contreras.huellitasvet.model.validarAtencion(diagnostico, peso).isEmpty()) {
                "Revisa el diagnóstico y el peso."
            }
            db.citaDao().registrarAtencion(citaId, diagnostico.trim(), tratamiento.trim(), peso)
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
