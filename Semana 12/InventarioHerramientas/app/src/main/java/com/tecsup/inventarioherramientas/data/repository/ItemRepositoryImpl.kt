package com.tecsup.inventarioherramientas.data.repository

import com.tecsup.inventarioherramientas.data.local.ItemDao
import com.tecsup.inventarioherramientas.data.local.ItemEntity
import com.tecsup.inventarioherramientas.data.mapper.aEntity
import com.tecsup.inventarioherramientas.data.mapper.aModelo
import com.tecsup.inventarioherramientas.data.remote.ApiService
import com.tecsup.inventarioherramientas.domain.model.Item
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    private val proyectoId: String,
    private val obtenerAutorizacion: suspend () -> String
) : ItemRepository {

    private val mutex = Mutex()

    override val items: Flow<List<Item>> =
        dao.todos().map { entidades ->
            entidades.map { it.aModelo() }
        }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { entidades ->
            entidades.map { it.aModelo() }
        }

    override suspend fun refrescar(): Result<Int> {
        return mutex.withLock {
            intentar {
                val descargados = mutableListOf<ItemEntity>()
                var siguientePagina: String? = null

                do {
                    val respuesta = api.listar(
                        proyecto = proyectoId,
                        autorizacion = obtenerAutorizacion(),
                        pageToken = siguientePagina
                    )

                    descargados.addAll(
                        respuesta.documents
                            .orEmpty()
                            .map { it.aEntity() }
                    )

                    siguientePagina = respuesta.nextPageToken
                        ?.takeIf { it.isNotBlank() }

                } while (siguientePagina != null)

                dao.reemplazarInventario(descargados)

                descargados.size
            }
        }
    }

    override suspend fun obtenerDetalle(
        id: String
    ): Result<Item> {
        return mutex.withLock {
            intentar {
                val descargado = api.obtenerDetalle(
                    proyecto = proyectoId,
                    id = id,
                    autorizacion = obtenerAutorizacion()
                ).aEntity()

                dao.guardarSinPerderFavoritos(
                    listOf(descargado)
                )

                val guardado = dao.obtenerPorId(id)
                    ?: error("No se pudo recuperar el detalle guardado")

                guardado.aModelo()
            }
        }
    }

    override suspend fun alternarFavorito(id: String) {
        dao.alternarFavorito(id)
    }

    override suspend fun cantidadGuardada(): Int {
        return dao.contar()
    }

    override suspend fun cantidadFavoritos(): Int {
        return dao.contarFavoritos()
    }

    override suspend fun limpiarCache() {
        mutex.withLock {
            dao.borrarTodo()
        }
    }

    private suspend fun <T> intentar(
        operacion: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(operacion())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}