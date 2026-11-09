package com.tecsup.inventarioherramientas

import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.data.auth.FirebaseAuthRepository

class AppContainer {

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }
}