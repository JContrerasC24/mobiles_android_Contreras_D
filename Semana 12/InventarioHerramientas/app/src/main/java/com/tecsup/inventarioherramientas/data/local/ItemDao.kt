package com.tecsup.inventarioherramientas.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM herramientas ORDER BY nombre")
    fun todos(): Flow<List<ItemEntity>>

    @Query(
        """
        SELECT * FROM herramientas
        WHERE esFavorito = 1
        ORDER BY nombre
        """
    )
    fun favoritos(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM herramientas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): ItemEntity?

    @Query("SELECT id FROM herramientas WHERE esFavorito = 1")
    suspend fun idsFavoritos(): List<String>

    @Query("SELECT COUNT(*) FROM herramientas")
    suspend fun contar(): Int

    @Query(
        "SELECT COUNT(*) FROM herramientas WHERE esFavorito = 1"
    )
    suspend fun contarFavoritos(): Int

    @Query(
        """
        UPDATE herramientas
        SET esFavorito = NOT esFavorito
        WHERE id = :id
        """
    )
    suspend fun alternarFavorito(id: String)

    @Upsert
    suspend fun guardarTodos(items: List<ItemEntity>)

    @Query("DELETE FROM herramientas")
    suspend fun borrarTodo()

    // Actualiza los datos sin perder las marcas locales.
    @Transaction
    suspend fun guardarSinPerderFavoritos(
        nuevos: List<ItemEntity>
    ) {
        val favoritosActuales = idsFavoritos().toSet()

        val actualizados = nuevos.map { herramienta ->
            herramienta.copy(
                esFavorito = herramienta.id in favoritosActuales
            )
        }

        guardarTodos(actualizados)
    }
    @Transaction
    suspend fun reemplazarInventario(
        nuevos: List<ItemEntity>
    ) {
        val favoritosActuales = idsFavoritos().toSet()

        val actualizados = nuevos.map { herramienta ->
            herramienta.copy(
                esFavorito = herramienta.id in favoritosActuales
            )
        }

        borrarTodo()
        guardarTodos(actualizados)
    }
}