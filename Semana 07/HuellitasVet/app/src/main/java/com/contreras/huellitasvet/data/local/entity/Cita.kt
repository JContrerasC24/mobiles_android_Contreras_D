package com.contreras.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.contreras.huellitasvet.model.EstadoCita

@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(
            entity = Mascota::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Servicio::class,
            parentColumns = ["id"],
            childColumns = ["servicioId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["mascotaId"]),
        Index(value = ["servicioId"]),
        Index(value = ["fecha", "hora"])
    ]
)
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mascotaId: Int,
    val servicioId: Int,
    val fecha: String,
    val hora: String,
    val nota: String = "",
    val estado: String = EstadoCita.PENDIENTE.name,
    val precio: Double,
    val diagnostico: String? = null,
    val tratamiento: String? = null,
    val pesoRegistrado: Double? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)