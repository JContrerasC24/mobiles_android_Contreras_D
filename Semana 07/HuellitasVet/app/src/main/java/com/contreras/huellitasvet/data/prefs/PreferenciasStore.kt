package com.contreras.huellitasvet.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.contreras.huellitasvet.model.Rol
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// Una unica declaracion por archivo de preferencias para toda la aplicacion.
private val Context.dataStore by preferencesDataStore(name = "ajustes")

class PreferenciasStore(context: Context) {

    private val store = context.applicationContext.dataStore

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val ACEPTAR_CITAS = booleanPreferencesKey("aceptarCitas")
    }

    private val preferencias: Flow<Preferences> = store.data.catch { error ->
        if (error is IOException) {
            emit(emptyPreferences())
        } else {
            throw error
        }
    }

    val perfilActivo: Flow<String> = preferencias
        .map { it[Claves.PERFIL_ACTIVO] ?: "" }
        .distinctUntilChanged()

    // Cero significa que no hay un usuario con sesion iniciada.
    val usuarioId: Flow<Int> = preferencias
        .map { it[Claves.USUARIO_ID] ?: 0 }
        .distinctUntilChanged()

    val modoOscuro: Flow<Boolean> = preferencias
        .map { it[Claves.MODO_OSCURO] ?: false }
        .distinctUntilChanged()

    val notificaciones: Flow<Boolean> = preferencias
        .map { it[Claves.NOTIFICACIONES] ?: true }
        .distinctUntilChanged()

    val aceptarCitas: Flow<Boolean> = preferencias
        .map { it[Claves.ACEPTAR_CITAS] ?: true }
        .distinctUntilChanged()

    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        require(usuarioId > 0) { "El identificador del usuario debe ser positivo" }
        store.edit { preferencias ->
            preferencias[Claves.PERFIL_ACTIVO] = rol.name
            preferencias[Claves.USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        store.edit { it[Claves.MODO_OSCURO] = b }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        store.edit { it[Claves.NOTIFICACIONES] = b }
    }

    suspend fun guardarAceptarCitas(b: Boolean) {
        store.edit { it[Claves.ACEPTAR_CITAS] = b }
    }

    suspend fun cerrarSesion() {
        // Cerrar sesion conserva el tema y las preferencias de la clinica.
        store.edit {
            it.remove(Claves.PERFIL_ACTIVO)
            it.remove(Claves.USUARIO_ID)
        }
    }
}
