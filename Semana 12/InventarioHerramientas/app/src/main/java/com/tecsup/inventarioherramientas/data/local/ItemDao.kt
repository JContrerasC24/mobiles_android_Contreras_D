package com.tecsup.inventarioherramientas.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

private const val INVENTARIO = """SELECT h.id,h.codigo,h.nombre,h.descripcion,h.ubicacion,h.condicion,
    h.disponibilidad,h.prestamoActivoId,(f.uid IS NOT NULL) AS esFavorito FROM herramientas h
    LEFT JOIN favoritos_cuenta f ON f.herramientaId=h.id AND f.uid=:uid """

@Dao
interface ItemDao {
    @Query(INVENTARIO + "ORDER BY h.nombre")
    fun todos(uid: String): Flow<List<ItemEntity>>

    @Query(INVENTARIO + "WHERE f.uid IS NOT NULL ORDER BY h.nombre")
    fun favoritos(uid: String): Flow<List<ItemEntity>>

    @Query(INVENTARIO + "WHERE h.id=:id")
    suspend fun obtenerPorId(uid: String, id: String): ItemEntity?

    @Query("SELECT fecha FROM sincronizacion WHERE id=1")
    fun ultimaSincronizacion(): Flow<Long?>

    @Query("DELETE FROM favoritos_cuenta WHERE uid=:uid AND herramientaId=:id")
    suspend fun quitar(uid: String, id: String): Int

    @Query("INSERT INTO favoritos_cuenta SELECT :uid, id FROM herramientas WHERE id=:id")
    suspend fun marcar(uid: String, id: String)

    @Transaction
    suspend fun alternarFavorito(uid: String, id: String) {
        if (quitar(uid, id) == 0) marcar(uid, id)
    }

    @Upsert
    suspend fun guardarTodos(items: List<ItemEntity>)

    @Upsert
    suspend fun guardarFecha(valor: Sincronizacion)

    @Query("SELECT COUNT(*) FROM herramientas")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM favoritos_cuenta JOIN herramientas ON herramientas.id=herramientaId WHERE uid=:uid")
    suspend fun contarFavoritos(uid: String): Int

    @Query("DELETE FROM herramientas")
    suspend fun borrarTodo()

    @Query("DELETE FROM favoritos_cuenta WHERE uid=:uid")
    suspend fun borrarFavoritos(uid: String)

    @Query("DELETE FROM sincronizacion")
    suspend fun borrarFecha()

    @Transaction
    suspend fun limpiarCache(uid: String) {
        borrarTodo()
        borrarFavoritos(uid)
        borrarFecha()
    }

    @Transaction
    suspend fun reemplazarInventario(nuevos: List<ItemEntity>) {
        borrarTodo()
        guardarTodos(nuevos)
        guardarFecha(Sincronizacion(fecha = System.currentTimeMillis()))
    }
}
