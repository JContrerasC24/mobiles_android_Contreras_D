package com.tecsup.inventarioherramientas

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.data.auth.FirebaseAuthRepository
import com.tecsup.inventarioherramientas.data.local.AppDatabase
import com.tecsup.inventarioherramientas.data.remote.RetrofitClient
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.data.repository.ItemRepositoryImpl
import kotlinx.coroutines.tasks.await

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val database: AppDatabase by lazy {
        AppDatabase.obtener(appContext)
    }

    val itemRepository: ItemRepository by lazy {
        val proyectoId = requireNotNull(
            FirebaseApp.getInstance().options.projectId
        ) {
            "No se encontró el ID del proyecto Firebase"
        }

        ItemRepositoryImpl(
            api = RetrofitClient.api,
            dao = database.itemDao(),
            proyectoId = proyectoId,
            obtenerAutorizacion = {
                val usuario = FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?: error("Debes iniciar sesión")

                val token = usuario
                    .getIdToken(false)
                    .await()
                    .token
                    ?: error("No se pudo obtener la autorización")

                "Bearer $token"
            }
        )
    }
}