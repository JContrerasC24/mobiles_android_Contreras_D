package com.tecsup.inventarioherramientas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun cargarLoteTaller() {
        val db = FirebaseFirestore.getInstance()
        val uid = requireNotNull(FirebaseAuth.getInstance().currentUser).uid
        val nombres = listOf("Sierra circular", "Llave ajustable", "Amoladora", "Cinta métrica", "Caja de herramientas",
            "Taladro", "Martillo", "DESTORNILLADOR", "Alicate")
        db.runTransaction { tx ->
            require(tx.get(db.collection("usuarios").document(uid)).getString("rol") == "admin")
            val registros = nombres.flatMapIndexed { indice, nombre ->
                (1..(if (indice < 5) 3 else 2)).map { unidad ->
                    val codigo = "HER-" + (100 + indice * 10 + unidad)
                    val ref = db.collection("herramientas").document(codigo)
                    val anterior = tx.get(ref)
                    require(!anterior.exists() || anterior.getString("nombre") == nombre) { "Código ocupado: $codigo" }
                    Triple(ref, nombre, anterior.exists())
                }
            }
            for ((ref, nombre, existe) in registros) if (!existe) {
                tx.set(ref, mapOf("codigo" to ref.id, "nombre" to nombre, "ubicacion" to "Taller",
                    "descripcion" to "", "condicion" to "BUENA", "disponibilidad" to "DISPONIBLE"))
            }
            true
        }.await()
}
