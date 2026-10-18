package com.contreras.huellitasvet.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.entity.Usuario

data class MascotaConDueno(
    @Embedded
    val mascota: Mascota,

    @Relation(
        parentColumn = "duenoId",
        entityColumn = "id"
    )
    val dueno: Usuario
)