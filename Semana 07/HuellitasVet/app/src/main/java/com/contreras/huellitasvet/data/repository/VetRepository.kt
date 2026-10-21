package com.contreras.huellitasvet.data.repository

import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.data.local.relation.CitaConDetalle
import com.contreras.huellitasvet.data.local.relation.MascotaConDueno
import com.contreras.huellitasvet.model.ConteoServicio
import com.contreras.huellitasvet.model.EstadoCita
import com.contreras.huellitasvet.model.RegistroPeso
import kotlinx.coroutines.flow.Flow

interface VetRepository {
    suspend fun obtenerUsuarioPorRol(rol: String): Usuario?
    suspend fun obtenerUsuario(id: Int): Usuario?
    suspend fun login(correo: String, clave: String): Usuario?

    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun guardarServicio(s: Servicio)
    suspend fun eliminarServicio(s: Servicio)
    suspend fun cambiarActivo(id: Int, activo: Boolean)
    suspend fun servicioTieneCitas(id: Int): Boolean

    fun observarMascotasDeDueno(duenoId: Int): Flow<List<Mascota>>
    fun observarMascota(id: Int): Flow<Mascota?>
    fun observarTodasLasMascotas(): Flow<List<MascotaConDueno>>
    suspend fun guardarMascota(m: Mascota)
    suspend fun eliminarMascota(m: Mascota)
    suspend fun mascotaTieneCitas(id: Int): Boolean

    fun observarHorasOcupadas(fecha: String): Flow<List<String>>
    fun observarHorasOcupadasExcluyendo(fecha: String, citaId: Int): Flow<List<String>>
    suspend fun crearCita(
        mascotaId: Int, servicioId: Int, fecha: String,
        hora: String, nota: String
    ): Long
    fun observarCitasDeDueno(duenoId: Int): Flow<List<CitaConDetalle>>
    fun observarCitasPorFecha(fecha: String): Flow<List<CitaConDetalle>>
    fun observarCita(id: Int): Flow<CitaConDetalle?>
    fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>>
    fun observarHistorialPeso(mascotaId: Int): Flow<List<RegistroPeso>>
    suspend fun cancelarCitaDeDueno(id: Int, duenoId: Int)
    suspend fun reprogramarCitaDeDueno(
        citaId: Int, duenoId: Int, nuevaFecha: String, nuevaHora: String
    )
    suspend fun cambiarEstadoCita(id: Int, estado: EstadoCita)
    suspend fun registrarAtencion(
        citaId: Int, diagnostico: String, tratamiento: String, peso: Double
    )

    fun observarCitasDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: EstadoCita): Flow<Int>
    fun observarCitasPorServicio(): Flow<List<ConteoServicio>>
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
}
