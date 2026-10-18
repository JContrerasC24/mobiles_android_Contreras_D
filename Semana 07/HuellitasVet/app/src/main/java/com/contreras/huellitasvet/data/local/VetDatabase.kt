package com.contreras.huellitasvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.contreras.huellitasvet.data.local.dao.ServicioDao
import com.contreras.huellitasvet.data.local.entity.Servicio

@Database(
    entities = [Servicio::class],
    version = 1,
    exportSchema = false
)
abstract class VetDatabase : RoomDatabase() {

    abstract fun servicioDao(): ServicioDao

    companion object {
        @Volatile
        private var INSTANCE: VetDatabase? = null

        fun obtener(context: Context): VetDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VetDatabase::class.java,
                    "huellitasvet.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
