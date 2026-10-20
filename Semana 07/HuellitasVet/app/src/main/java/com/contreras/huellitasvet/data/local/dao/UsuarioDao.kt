package com.contreras.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.contreras.huellitasvet.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtener(id: Int): Usuario?

    @Query(
        """
        SELECT * FROM usuarios
        WHERE correo = :correo AND clave = :clave
        LIMIT 1
        """
    )
    suspend fun login(
        correo: String,
        clave: String
    ): Usuario?

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY id LIMIT 1")
    suspend fun obtenerPorRol(rol: String): com.contreras.huellitasvet.data.local.entity.Usuario?

    // Actualización de datos; mantiene los IDs y sus relaciones.
    @Query("""
        UPDATE usuarios
        SET nombre = CASE correo
            WHEN 'ana@correo.test' THEN 'José'
            WHEN 'vet@huellitas.test' THEN 'Veterinario'
            ELSE nombre END,
            correo = CASE correo
                WHEN 'ana@correo.test' THEN 'josec@gmail.com'
                WHEN 'luis@correo.test' THEN 'luis@gmail.com'
                WHEN 'vet@huellitas.test' THEN 'vet@gmail.com'
                ELSE correo END
        WHERE correo IN ('ana@correo.test', 'luis@correo.test', 'vet@huellitas.test')
    """)
    suspend fun actualizarCuentasIniciales(): Int
}
