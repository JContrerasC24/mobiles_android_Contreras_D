package com.tecsup.inventarioherramientas.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}