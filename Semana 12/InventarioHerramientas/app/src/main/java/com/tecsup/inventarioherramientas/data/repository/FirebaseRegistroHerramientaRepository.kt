package com.tecsup.inventarioherramientas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseRegistroHerramientaRepository(
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) : RegistroHerramientaRepository {

    override suspend fun registrar(
        codigo: String,
        nombre: String,
        descripcion: String,
        ubicacion: String,
        condicion: String
    ): Result<String> {
        return try {
            val codigoLimpio = codigo.trim().uppercase(Locale.ROOT)
            val nombreLimpio = nombre.trim()
            val descripcionLimpia = descripcion.trim()
            val ubicacionLimpia = ubicacion.trim()

            require(
                Regex("HER-[0-9]{3,6}").matches(codigoLimpio)
            ) {
                "El código debe tener el formato HER-003, " +
                        "con entre 3 y 6 dígitos."
            }

            require(nombreLimpio.isNotBlank()) {
                "Ingresa el nombre de la herramienta."
            }

            require(nombreLimpio.length <= 100) {
                "El nombre admite hasta 100 caracteres."
            }

            require(ubicacionLimpia.isNotBlank()) {
                "Ingresa la ubicación."
            }

            require(ubicacionLimpia.length <= 100) {
                "La ubicación admite hasta 100 caracteres."
            }

            require(descripcionLimpia.length <= 500) {
                "La descripción admite hasta 500 caracteres."
            }

            require(
                condicion in setOf(
                    "BUENA",
                    "DANADA",
                    "MANTENIMIENTO"
                )
            ) {
                "Selecciona una condición válida."
            }

            val documento = db
                .collection("herramientas")
                .document(codigoLimpio)

            val datos = mapOf(
                "codigo" to codigoLimpio,
                "nombre" to nombreLimpio,
                "descripcion" to descripcionLimpia,
                "ubicacion" to ubicacionLimpia,
                "condicion" to condicion,
                "disponibilidad" to "DISPONIBLE"
            )

            db.runTransaction { transaccion ->
                val existente = transaccion.get(documento)

                if (existente.exists()) {
                    throw IllegalArgumentException(
                        "Ya existe una herramienta con código $codigoLimpio."
                    )
                }

                transaccion.set(documento, datos)

                codigoLimpio
            }.await()

            Result.success(codigoLimpio)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val validacion = generateSequence<Throwable>(e) {
                it.cause
            }.filterIsInstance<IllegalArgumentException>()
                .firstOrNull()

            val mensaje = when {
                validacion != null ->
                    validacion.message ?: "Revisa los datos ingresados."

                e is FirebaseFirestoreException &&
                        e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "No tienes permiso. Revisa el rol y las reglas de Firestore."

                else ->
                    "No se pudo confirmar el registro. Comprueba la conexión " +
                            "y revisa el inventario antes de volver a intentarlo."
            }

            Result.failure(
                IllegalStateException(mensaje, e)
            )
        }
    }
}