package com.tecsup.inventarioherramientas.data.remote

import com.tecsup.inventarioherramientas.data.remote.dto.ItemDto
import com.tecsup.inventarioherramientas.data.remote.dto.ListaItemsDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("projects/{proyecto}/databases/(default)/documents/herramientas")
    suspend fun listar(
        @Path("proyecto") proyecto: String,
        @Header("Authorization") autorizacion: String,
        @Query("pageSize") pageSize: Int = 100,
        @Query("pageToken") pageToken: String? = null
    ): ListaItemsDto

    @GET("projects/{proyecto}/databases/(default)/documents/herramientas/{id}")
    suspend fun obtenerDetalle(
        @Path("proyecto") proyecto: String,
        @Path("id") id: String,
        @Header("Authorization") autorizacion: String
    ): ItemDto
}