package com.tecsup.inventarioherramientas.data.auth

import com.tecsup.inventarioherramientas.domain.model.Sesion

interface AuthRepository {

    suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion>

    suspend fun sesionActual(): Sesion?

    fun cerrarSesion()
}