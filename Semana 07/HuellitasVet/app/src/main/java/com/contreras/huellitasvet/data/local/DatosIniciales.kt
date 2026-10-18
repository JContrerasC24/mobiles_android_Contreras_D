package com.contreras.huellitasvet.data.local

import androidx.room.withTransaction
import com.contreras.huellitasvet.data.local.entity.Servicio

object DatosIniciales {

    suspend fun sembrar(db: VetDatabase) {
        db.withTransaction {
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
        }
    }
}