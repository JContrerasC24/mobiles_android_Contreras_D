package com.tecsup.inventarioherramientas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ItemEntity::class, FavoritoCuenta::class, Sincronizacion::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_herramientas.db"
                ).addMigrations(object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE favoritos_cuenta (uid TEXT NOT NULL, herramientaId TEXT NOT NULL, PRIMARY KEY(uid, herramientaId))")
                        db.execSQL("CREATE TABLE sincronizacion (id INTEGER NOT NULL PRIMARY KEY, fecha INTEGER NOT NULL)")
                        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            db.execSQL("INSERT INTO favoritos_cuenta SELECT ?, id FROM herramientas WHERE esFavorito = 1", arrayOf(uid))
                        }
                        db.execSQL("UPDATE herramientas SET esFavorito = 0")
                    }
                }).build().also {
                    instancia = it
                }
            }
        }
    }
}