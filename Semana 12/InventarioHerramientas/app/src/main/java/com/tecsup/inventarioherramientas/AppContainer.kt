package com.tecsup.inventarioherramientas

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.inventarioherramientas.data.auth.AuthRepository
import com.tecsup.inventarioherramientas.data.auth.FirebaseAuthRepository
import com.tecsup.inventarioherramientas.data.local.AppDatabase
import com.tecsup.inventarioherramientas.data.remote.RetrofitClient
import com.tecsup.inventarioherramientas.data.repository.FirebaseRegistroHerramientaRepository
import com.tecsup.inventarioherramientas.data.repository.ItemRepository
import com.tecsup.inventarioherramientas.data.repository.ItemRepositoryImpl
import com.tecsup.inventarioherramientas.data.repository.RegistroHerramientaRepository
import kotlinx.coroutines.tasks.await
import com.tecsup.inventarioherramientas.data.repository.MovimientoRepository
import com.tecsup.inventarioherramientas.data.repository.FirebaseMovimientoRepository
import com.tecsup.inventarioherramientas.notification.Notificador
import com.tecsup.inventarioherramientas.notification.NotificadorAndroid

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    // Inicio, recuperación y cierre de sesión.
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val notificador: Notificador by lazy {
        NotificadorAndroid(appContext)
    }

    // Base de datos local Room.
    val database: AppDatabase by lazy {
        AppDatabase.obtener(appContext)
    }

    // Consulta del inventario, sincronización y favoritos.
    val itemRepository: ItemRepository
        get() {
        val usuarioSesion = requireNotNull(FirebaseAuth.getInstance().currentUser) { "Debes iniciar sesión." }
        val proyectoId = requireNotNull(
            FirebaseApp.getInstance().options.projectId
        ) {
            "No se encontró el ID del proyecto Firebase."
        }

        return ItemRepositoryImpl(
            uid = usuarioSesion.uid,
            api = RetrofitClient.api,
            dao = database.itemDao(),
            proyectoId = proyectoId,
            obtenerAutorizacion = {
                val usuario = FirebaseAuth
                    .getInstance()
                    .currentUser
                    ?: error("Debes iniciar sesión.")

                require(usuario.uid == usuarioSesion.uid) { "La sesión ha cambiado." }
                val token = usuario
                    .getIdToken(false)
                    .await()
                    .token
                    ?: error("No se pudo obtener la autorización.")

                "Bearer $token"
            }
        )
    }

    // Registro de nuevas herramientas en Firestore.
    val registroHerramientaRepository: RegistroHerramientaRepository by lazy {
        FirebaseRegistroHerramientaRepository()
    }
    val movimientoRepository: MovimientoRepository by lazy {
        FirebaseMovimientoRepository()
    }
}