package com.tecsup.inventarioherramientas.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.tecsup.inventarioherramientas.domain.model.Rol
import com.tecsup.inventarioherramientas.domain.model.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion> {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank() || clave.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Completa el correo y la contraseña."
                )
            )
        }

        return try {
            val resultado = auth
                .signInWithEmailAndPassword(correoLimpio, clave)
                .await()

            val usuario = resultado.user
                ?: error("No se pudo obtener el usuario.")

            // Un ingreso con credenciales consulta el perfil
            // directamente en el servidor.
            val documento = db
                .collection("usuarios")
                .document(usuario.uid)
                .get(Source.SERVER)
                .await()

            val sesion = crearSesion(
                uid = usuario.uid,
                correo = usuario.email ?: correoLimpio,
                documento = documento
            )

            Result.success(sesion)
        } catch (e: CancellationException) {
            auth.signOut()
            throw e
        } catch (e: Exception) {
            auth.signOut()
            Result.failure(e)
        }
    }

    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null

        val documento = recuperarPerfil(usuario.uid)

        return crearSesion(
            uid = usuario.uid,
            correo = usuario.email.orEmpty(),
            documento = documento
        )
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    private suspend fun recuperarPerfil(
        uid: String
    ): DocumentSnapshot {
        val referencia = db
            .collection("usuarios")
            .document(uid)

        try {
            // Primero intentamos obtener el perfil actualizado.
            val documentoServidor = withTimeoutOrNull(8_000L) {
                referencia.get(Source.SERVER).await()
            }

            if (documentoServidor != null) {
                return documentoServidor
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseFirestoreException) {
            val permiteCopiaLocal = e.code in setOf(
                FirebaseFirestoreException.Code.UNAVAILABLE,
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
            )

            // No sustituimos una denegación de permisos
            // por un perfil antiguo.
            if (!permiteCopiaLocal) {
                throw e
            }
        }

        // Sin respuesta del servidor, buscamos únicamente
        // el perfil previamente guardado para este UID.
        val documentoLocal = try {
            referencia.get(Source.CACHE).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(
                "No hay un perfil local disponible. " +
                        "Conecta internet para recuperar la sesión.",
                e
            )
        }

        check(documentoLocal.exists()) {
            "No hay un perfil local disponible. " +
                    "Conecta internet para recuperar la sesión."
        }

        return documentoLocal
    }

    private fun crearSesion(
        uid: String,
        correo: String,
        documento: DocumentSnapshot
    ): Sesion {
        check(documento.exists()) {
            "El usuario no tiene un perfil configurado en Firestore."
        }

        val rol = when (documento.getString("rol")) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> error("El usuario tiene un rol no válido.")
        }

        return Sesion(
            uid = uid,
            correo = correo,
            rol = rol
        )
    }
}