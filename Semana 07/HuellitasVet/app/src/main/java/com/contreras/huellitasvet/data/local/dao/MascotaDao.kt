package com.contreras.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.contreras.huellitasvet.data.local.entity.Mascota
import com.contreras.huellitasvet.data.local.relation.MascotaConDueno
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    @Insert
    suspend fun insertar(mascota: Mascota): Long

    @Update
    suspend fun actualizar(mascota: Mascota)

    @Delete
    suspend fun eliminar(mascota: Mascota)

    @Query(
        "UPDATE mascotas SET peso = :peso WHERE id = :id"
    )
    suspend fun actualizarPeso(
        id: Int,
        peso: Double
    )

    @Query(
        """
        SELECT * FROM mascotas
        WHERE duenoId = :duenoId
        ORDER BY nombre
        """
    )
    fun observarPorDueno(
        duenoId: Int
    ): Flow<List<Mascota>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarUna(id: Int): Flow<Mascota?>

    @Transaction
    @Query("SELECT * FROM mascotas ORDER BY nombre")
    fun observarTodas(): Flow<List<MascotaConDueno>>

    @Query(
        "SELECT COUNT(*) FROM citas WHERE mascotaId = :mascotaId"
    )
    suspend fun contarCitas(mascotaId: Int): Int
}