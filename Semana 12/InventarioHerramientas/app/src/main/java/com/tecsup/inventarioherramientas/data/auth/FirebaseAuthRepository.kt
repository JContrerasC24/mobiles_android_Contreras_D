package com.tecsup.inventarioherramientas.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.domain.model.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion> {
        val correoLimpio = correo.trim()

        // Evita llamar a Firebase con campos vacíos.
        if (correoLimpio.isBlank() || clave.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Completa el correo y la contraseña")
            )
        }

        return try {
            val resultado = auth
                .signInWithEmailAndPassword(correoLimpio, clave)
                .await()

            val usuario = resultado.user
                ?: error("No se pudo obtener el usuario")

            val sesion = leerSesion(
                uid = usuario.uid,
                correo = usuario.email ?: correoLimpio
            )

            Result.success(sesion)
        } catch (e: CancellationException) {
            auth.signOut()
            throw e
        } catch (e: Exception) {
            // Si falla la lectura del rol, no deja un ingreso incompleto.
            auth.signOut()
            Result.failure(e)
        }
    }

    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null

        return leerSesion(
            uid = usuario.uid,
            correo = usuario.email.orEmpty()
        )
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    private suspend fun leerSesion(
        uid: String,
        correo: String
    ): Sesion {
        val documento = db
            .collection("usuarios")
            .document(uid)
            .get()
            .await()

        if (!documento.exists()) {
            error("El usuario no tiene un perfil configurado en Firestore")
        }

        val rol = when (documento.getString("rol")) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> error("El usuario tiene un rol no válido")
        }

        return Sesion(
            uid = uid,
            correo = correo,
            rol = rol
        )
    }
}