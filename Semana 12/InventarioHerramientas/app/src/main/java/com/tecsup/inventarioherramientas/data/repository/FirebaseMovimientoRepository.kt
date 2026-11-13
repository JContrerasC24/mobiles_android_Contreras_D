package com.tecsup.inventarioherramientas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.Transaction
import com.tecsup.inventarioherramientas.domain.model.Movimiento
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseMovimientoRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : MovimientoRepository {

    override suspend fun finalizarMantenimiento(id: String, condicion: String, nota: String) = ejecutar {
        val uid = obtenerUid()
        require(condicion in setOf("BUENA", "DANADA") && nota.trim().length in 1..500) { "Revisa la condición y la observación." }
        val ref = db.collection("herramientas").document(id)
        val movimiento = db.collection("movimientos").document()
        db.runTransaction { tx ->
            comprobarAdmin(tx, uid)
            val item = tx.get(ref)
            require(item.getString("condicion") == "MANTENIMIENTO" && item.getString("disponibilidad") == "DISPONIBLE" &&
                item.getString("prestamoActivoId").isNullOrBlank()) { "La herramienta cambió de estado. Actualiza la ficha." }
            tx.set(movimiento, mapOf("herramientaId" to id, "codigoHerramienta" to item.getString("codigo"),
                "nombreHerramienta" to item.getString("nombre"), "tipo" to "FIN_MANTENIMIENTO",
                "responsable" to auth.currentUser?.email.orEmpty(), "registradoPorUid" to uid,
                "fecha" to FieldValue.serverTimestamp(), "condicion" to condicion, "observacion" to nota.trim(), "prestamoId" to movimiento.id))
            tx.update(ref, mapOf("condicion" to condicion, "ultimoMovimientoId" to movimiento.id))
            movimiento.id
        }.await()
    }

    override suspend fun prestar(
        herramientaId: String,
        responsable: String,
        cobro: com.tecsup.inventarioherramientas.domain.model.CobroAlquiler,
        observacion: String
    ): Result<String> = prestarSolicitud(herramientaId, responsable, cobro, observacion, null)

    suspend fun prestarSolicitud(herramientaId: String, responsable: String,
        cobro: com.tecsup.inventarioherramientas.domain.model.CobroAlquiler,
        observacion: String, solicitudId: String?): Result<String> {
        return ejecutar {
            val uid = obtenerUid()
            cobro.validar()
            val responsableLimpio = responsable.trim()
            val observacionLimpia = observacion.trim()

            require(herramientaId.isNotBlank()) {
                "Selecciona una herramienta."
            }

            require(responsableLimpio.isNotBlank()) {
                "Ingresa el nombre de quien recibe la herramienta."
            }

            require(responsableLimpio.length <= 100) {
                "El responsable admite hasta 100 caracteres."
            }

            require(observacionLimpia.length <= 500) {
                "La observación admite hasta 500 caracteres."
            }

            val herramientaRef = db
                .collection("herramientas")
                .document(herramientaId)

            // Se genera una sola vez, fuera de la transacción.
            val movimientoRef = db
                .collection("movimientos")
                .document()

            db.runTransaction { transaccion ->
                comprobarAdmin(transaccion, uid)

                val herramienta = transaccion.get(herramientaRef)

                require(herramienta.exists()) {
                    "La herramienta ya no existe."
                }

                require(
                    herramienta.getString("disponibilidad") ==
                            "DISPONIBLE"
                ) {
                    "La herramienta ya está prestada."
                }

                require(
                    herramienta.getString("condicion") == "BUENA"
                ) {
                    "Solo se pueden prestar herramientas en buena condición."
                }

                require(
                    herramienta.getString("prestamoActivoId")
                        .isNullOrBlank()
                ) {
                    "La herramienta tiene un préstamo pendiente."
                }

                val solicitudRef = solicitudId?.let { db.collection("solicitudes").document(it) }
                if (solicitudRef != null) {
                    val solicitud = transaccion.get(solicitudRef)
                    require(solicitud.getString("estado") == "APROBADA" && solicitud.getString("herramientaId") == herramientaId) {
                        "La solicitud ya no está aprobada. Actualiza la lista."
                    }
                    require(solicitud.getString("responsable") == responsableLimpio && solicitud.getLong("dias") == cobro.dias.toLong()) {
                        "Conserva el responsable y los días de la solicitud aprobada."
                    }
                }
                val movimiento = mutableMapOf<String, Any>(
                    "herramientaId" to herramientaId,
                    "codigoHerramienta" to
                            herramienta.getString("codigo").orEmpty(),
                    "nombreHerramienta" to
                            herramienta.getString("nombre").orEmpty(),
                    "tipo" to "SALIDA",
                    "alquiler" to mapOf("tarifaCentimos" to cobro.tarifaCentimos, "dias" to cobro.dias,
                        "totalCentimos" to cobro.totalCentimos, "metodo" to cobro.metodo,
                        "referencia" to cobro.referencia.trim(), "estado" to "PAGADO", "moneda" to "PEN"),
                    "responsable" to responsableLimpio,
                    "registradoPorUid" to uid,
                    "fecha" to FieldValue.serverTimestamp(),
                    "condicion" to "BUENA",
                    "observacion" to observacionLimpia,
                    "prestamoId" to movimientoRef.id
                )

                if (solicitudRef != null) {
                    movimiento["solicitudId"] = solicitudRef.id
                    transaccion.update(solicitudRef, mapOf("estado" to "COMPLETADA", "movimientoId" to movimientoRef.id,
                        "actualizado" to FieldValue.serverTimestamp()))
                }
                transaccion.set(
                    movimientoRef,
                    movimiento
                )

                transaccion.update(
                    herramientaRef,
                    mapOf(
                        "disponibilidad" to "PRESTADA",
                        "prestamoActivoId" to movimientoRef.id,
                        "ultimoMovimientoId" to movimientoRef.id
                    )
                )

                movimientoRef.id
            }.await()
        }
    }

    override suspend fun devolver(
        herramientaId: String,
        condicionRetorno: String,
        observacion: String
    ): Result<String> {
        return ejecutar {
            val uid = obtenerUid()
            val observacionLimpia = observacion.trim()

            require(herramientaId.isNotBlank()) {
                "Selecciona una herramienta."
            }

            require(
                condicionRetorno in setOf(
                    "BUENA",
                    "DANADA",
                    "MANTENIMIENTO"
                )
            ) {
                "Selecciona una condición de retorno válida."
            }

            require(observacionLimpia.length <= 500) {
                "La observación admite hasta 500 caracteres."
            }

            val herramientaRef = db
                .collection("herramientas")
                .document(herramientaId)

            val movimientoRef = db
                .collection("movimientos")
                .document()

            db.runTransaction { transaccion ->
                comprobarAdmin(transaccion, uid)

                val herramienta = transaccion.get(herramientaRef)

                require(herramienta.exists()) {
                    "La herramienta ya no existe."
                }

                require(
                    herramienta.getString("disponibilidad") ==
                            "PRESTADA"
                ) {
                    "La herramienta no está prestada."
                }

                val prestamoId = herramienta
                    .getString("prestamoActivoId")
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalArgumentException(
                        "No se encontró el préstamo activo."
                    )

                val salidaRef = db
                    .collection("movimientos")
                    .document(prestamoId)

                // Todas las lecturas se realizan antes de escribir.
                val salida = transaccion.get(salidaRef)

                require(
                    salida.exists() &&
                            salida.getString("tipo") == "SALIDA" &&
                            salida.getString("herramientaId") ==
                            herramientaId &&
                            salida.getString("prestamoId") == prestamoId
                ) {
                    "El préstamo activo no coincide con la herramienta."
                }

                val movimiento = mapOf(
                    "herramientaId" to herramientaId,
                    "codigoHerramienta" to
                            herramienta.getString("codigo").orEmpty(),
                    "nombreHerramienta" to
                            herramienta.getString("nombre").orEmpty(),
                    "tipo" to "ENTRADA",
                    "responsable" to
                            salida.getString("responsable").orEmpty(),
                    "registradoPorUid" to uid,
                    "fecha" to FieldValue.serverTimestamp(),
                    "condicion" to condicionRetorno,
                    "observacion" to observacionLimpia,
                    "prestamoId" to prestamoId
                )

                transaccion.set(
                    movimientoRef,
                    movimiento
                )

                transaccion.update(
                    herramientaRef,
                    mapOf(
                        "disponibilidad" to "DISPONIBLE",
                        "condicion" to condicionRetorno,
                        "prestamoActivoId" to null,
                        "ultimoMovimientoId" to movimientoRef.id
                    )
                )

                movimientoRef.id
            }.await()
        }
    }

    override suspend fun obtenerHistorial(
        herramientaId: String
    ): Result<List<Movimiento>> {
        return try {
            obtenerUid()

            require(herramientaId.isNotBlank()) {
                "Selecciona una herramienta."
            }

            val respuesta = db
                .collection("movimientos")
                .whereEqualTo("herramientaId", herramientaId)
                .get(Source.SERVER)
                .await()

            val movimientos = respuesta.documents
                .map { documento ->
                    val fecha = requireNotNull(
                        documento.getTimestamp("fecha")
                    ) {
                        "Un movimiento no tiene una fecha válida."
                    }

                    Movimiento(
                        id = documento.id,
                        herramientaId = documento
                            .getString("herramientaId").orEmpty(),
                        codigoHerramienta = documento
                            .getString("codigoHerramienta").orEmpty(),
                        nombreHerramienta = documento
                            .getString("nombreHerramienta").orEmpty(),
                        tipo = documento
                            .getString("tipo").orEmpty(),
                        responsable = documento
                            .getString("responsable").orEmpty(),
                        registradoPorUid = documento
                            .getString("registradoPorUid").orEmpty(),
                        fechaMillis = fecha.toDate().time,
                        condicion = documento
                            .getString("condicion").orEmpty(),
                        observacion = documento
                            .getString("observacion").orEmpty(),
                        prestamoId = documento
                            .getString("prestamoId").orEmpty(),
                        cobro = (documento.get("alquiler") as? Map<*, *>)?.let { pago ->
                            com.tecsup.inventarioherramientas.domain.model.CobroAlquiler(
                                (pago["tarifaCentimos"] as Number).toLong(), (pago["dias"] as Number).toInt(),
                                pago["metodo"] as String, pago["referencia"] as String)
                        }
                    )
                }
                .sortedByDescending { movimiento ->
                    movimiento.fechaMillis
                }

            Result.success(movimientos)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun obtenerUid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )
    }

    private fun comprobarAdmin(
        transaccion: Transaction,
        uid: String
    ) {
        val perfilRef = db
            .collection("usuarios")
            .document(uid)

        val perfil = transaccion.get(perfilRef)

        require(perfil.getString("rol") == "admin") {
            "Solo el administrador puede registrar movimientos."
        }
    }

    private suspend fun ejecutar(
        operacion: suspend () -> String
    ): Result<String> {
        return try {
            Result.success(operacion())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val validacion = generateSequence<Throwable>(e) {
                it.cause
            }
                .filterIsInstance<IllegalArgumentException>()
                .firstOrNull()

            val mensaje = when {
                validacion != null ->
                    validacion.message ?: "La operación no es válida."

                e is FirebaseFirestoreException &&
                        e.code ==
                        FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "No tienes permiso para registrar este movimiento."

                e is IllegalStateException ->
                    e.message ?: "No se pudo realizar la operación."

                else ->
                    "No se pudo confirmar el movimiento. " +
                            "Comprueba la conexión y actualiza el inventario " +
                            "antes de volver a intentarlo."
            }

            Result.failure(
                IllegalStateException(mensaje, e)
            )
        }
    }
}