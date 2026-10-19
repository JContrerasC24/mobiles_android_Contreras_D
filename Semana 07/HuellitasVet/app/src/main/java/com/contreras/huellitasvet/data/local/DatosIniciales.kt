package com.contreras.huellitasvet.data.local

import androidx.room.withTransaction
import com.contreras.huellitasvet.data.local.entity.Cita
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Servicio
import com.contreras.huellitasvet.data.local.entity.Usuario
import com.contreras.huellitasvet.model.Especie
import com.contreras.huellitasvet.model.EstadoCita
import com.contreras.huellitasvet.model.Rol
import com.contreras.huellitasvet.model.Sexo
import kotlinx.coroutines.flow.first
import java.time.LocalDate

object DatosIniciales {

    suspend fun sembrar(db: VetDatabase) {
        db.withTransaction {
            // Si ya existen usuarios, la siembra ya se realizó.
            if (db.usuarioDao().contar() > 0) {
                return@withTransaction
            }

            // Conserva los servicios si ya estaban cargados.
            if (db.servicioDao().contar() == 0) {
                db.servicioDao().insertarTodos(
                    listOf(
                        Servicio(
                            nombre = "Consulta general",
                            descripcion = "Evaluación general de la mascota",
                            duracionMin = 30,
                            precio = 60.0,
                            activo = true,
                            icono = "heart"
                        ),
                        Servicio(
                            nombre = "Vacunación",
                            descripcion = "Aplicación de vacunas",
                            duracionMin = 30,
                            precio = 45.0,
                            activo = true,
                            icono = "syringe"
                        ),
                        Servicio(
                            nombre = "Baño y corte",
                            descripcion = "Aseo y cuidado del pelaje",
                            duracionMin = 60,
                            precio = 50.0,
                            activo = true,
                            icono = "scissors"
                        ),
                        Servicio(
                            nombre = "Desparasitación",
                            descripcion = "Control de parásitos",
                            duracionMin = 15,
                            precio = 25.0,
                            activo = false,
                            icono = "heart"
                        )
                    )
                )
            }

            db.usuarioDao().insertarTodos(
                listOf(
                    Usuario(
                        nombre = "Ana Torres",
                        correo = "ana@correo.test",
                        clave = "1234",
                        rol = Rol.CLIENTE.name
                    ),
                    Usuario(
                        nombre = "Luis Pérez",
                        correo = "luis@correo.test",
                        clave = "1234",
                        rol = Rol.CLIENTE.name
                    ),
                    Usuario(
                        nombre = "Veterinario Demo",
                        correo = "vet@huellitas.test",
                        clave = "admin",
                        rol = Rol.ADMIN.name
                    )
                )
            )

            // Recuperamos los IDs generados, sin asumir su valor.
            val ana = checkNotNull(
                db.usuarioDao().login("ana@correo.test", "1234")
            )
            val luis = checkNotNull(
                db.usuarioDao().login("luis@correo.test", "1234")
            )

            val servicios = db.servicioDao().observarTodos().first()

            val consulta = servicios.first {
                it.nombre == "Consulta general"
            }
            val vacunacion = servicios.first {
                it.nombre == "Vacunación"
            }
            val bano = servicios.first {
                it.nombre == "Baño y corte"
            }

            val hoy = LocalDate.now()

            val lunaId = db.mascotaDao().insertar(
                Mascota(
                    duenoId = ana.id,
                    nombre = "Luna",
                    especie = Especie.PERRO.name,
                    raza = "Mestiza",
                    anioNacimiento = hoy.year - 3,
                    peso = 12.5,
                    sexo = Sexo.HEMBRA.name
                )
            ).toInt()

            val maxId = db.mascotaDao().insertar(
                Mascota(
                    duenoId = ana.id,
                    nombre = "Max",
                    especie = Especie.PERRO.name,
                    raza = "Labrador",
                    anioNacimiento = hoy.year - 5,
                    peso = 25.0,
                    sexo = Sexo.MACHO.name
                )
            ).toInt()

            val rockyId = db.mascotaDao().insertar(
                Mascota(
                    duenoId = luis.id,
                    nombre = "Rocky",
                    especie = Especie.PERRO.name,
                    raza = "Mestizo",
                    anioNacimiento = hoy.year - 2,
                    peso = 18.0,
                    sexo = Sexo.MACHO.name
                )
            ).toInt()

            val michiId = db.mascotaDao().insertar(
                Mascota(
                    duenoId = luis.id,
                    nombre = "Michi",
                    especie = Especie.GATO.name,
                    raza = "Común",
                    anioNacimiento = hoy.year - 1,
                    peso = 4.2,
                    sexo = Sexo.HEMBRA.name
                )
            ).toInt()

            fun cita(
                mascotaId: Int,
                servicio: Servicio,
                dias: Long,
                hora: String,
                estado: EstadoCita,
                diagnostico: String? = null,
                tratamiento: String? = null,
                peso: Double? = null
            ): Cita {
                return Cita(
                    mascotaId = mascotaId,
                    servicioId = servicio.id,
                    fecha = hoy.plusDays(dias).toString(),
                    hora = hora,
                    nota = "Cita ficticia del laboratorio",
                    estado = estado.name,
                    precio = servicio.precio,
                    diagnostico = diagnostico,
                    tratamiento = tratamiento,
                    pesoRegistrado = peso
                )
            }

            val citas = listOf(
                cita(
                    mascotaId = lunaId,
                    servicio = consulta,
                    dias = -20,
                    hora = "09:00",
                    estado = EstadoCita.ATENDIDA,
                    diagnostico = "Control general satisfactorio",
                    tratamiento = "Mantener alimentación balanceada",
                    peso = 11.8
                ),
                cita(
                    mascotaId = lunaId,
                    servicio = vacunacion,
                    dias = -10,
                    hora = "10:00",
                    estado = EstadoCita.ATENDIDA,
                    diagnostico = "Mascota apta para vacunación",
                    tratamiento = "Vacuna aplicada y observación",
                    peso = 12.1
                ),
                cita(
                    mascotaId = lunaId,
                    servicio = consulta,
                    dias = -2,
                    hora = "11:00",
                    estado = EstadoCita.ATENDIDA,
                    diagnostico = "Evolución favorable en control",
                    tratamiento = "Continuar cuidados habituales",
                    peso = 12.5
                ),
                cita(
                    mascotaId = michiId,
                    servicio = consulta,
                    dias = -1,
                    hora = "15:00",
                    estado = EstadoCita.ATENDIDA,
                    diagnostico = "Buen estado general",
                    tratamiento = "Mantener hidratación y alimentación",
                    peso = 4.2
                ),
                cita(
                    mascotaId = maxId,
                    servicio = consulta,
                    dias = 0,
                    hora = "09:00",
                    estado = EstadoCita.CONFIRMADA
                ),
                cita(
                    mascotaId = rockyId,
                    servicio = vacunacion,
                    dias = 0,
                    hora = "16:00",
                    estado = EstadoCita.PENDIENTE
                ),
                cita(
                    mascotaId = lunaId,
                    servicio = bano,
                    dias = 1,
                    hora = "10:00",
                    estado = EstadoCita.PENDIENTE
                ),
                cita(
                    mascotaId = michiId,
                    servicio = vacunacion,
                    dias = 2,
                    hora = "11:00",
                    estado = EstadoCita.CONFIRMADA
                ),
                cita(
                    mascotaId = maxId,
                    servicio = bano,
                    dias = 3,
                    hora = "14:00",
                    estado = EstadoCita.CANCELADA
                ),
                cita(
                    mascotaId = rockyId,
                    servicio = consulta,
                    dias = 5,
                    hora = "17:00",
                    estado = EstadoCita.PENDIENTE
                )
            )

            citas.forEach { cita ->
                db.citaDao().insertar(cita)
            }
        }
    }
}