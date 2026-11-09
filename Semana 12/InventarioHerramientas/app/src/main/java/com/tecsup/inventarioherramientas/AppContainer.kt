package com.tecsup.inventarioherramientas

import android.content.Context
import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.data.auth.FirebaseAuthRepository
import com.tecsup.inventarioherramientas.data.local.AppDatabase

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val database: AppDatabase by lazy {
        AppDatabase.obtener(appContext)
    }
}