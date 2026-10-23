package com.example.master_martini.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.master_martini.data.local.dao.ContenidoDao
import com.example.master_martini.data.local.dao.FavoritoDao
import com.example.master_martini.data.local.dao.InteresDao
import com.example.master_martini.data.local.dao.UsuarioDao
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.entity.Favorito
import com.example.master_martini.data.local.entity.InteresUsuario
import com.example.master_martini.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, InteresUsuario::class, Contenido::class, Favorito::class],
    version = 1,
    exportSchema = false
)
abstract class MasterMartiniDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun interesDao(): InteresDao
    abstract fun contenidoDao(): ContenidoDao
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile
        private var instancia: MasterMartiniDatabase? = null

        fun obtener(context: Context): MasterMartiniDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    MasterMartiniDatabase::class.java,
                    "master_martini.db"
                ).build().also { instancia = it }
            }
    }
}