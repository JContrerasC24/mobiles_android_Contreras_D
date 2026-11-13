package com.tecsup.inventarioherramientas.data.repository

interface RegistroHerramientaRepository {

    suspend fun registrar(
        codigo: String,
        nombre: String,
        descripcion: String,
        ubicacion: String,
        cantidad: Int,
        condicion: String
    ): Result<String>
}