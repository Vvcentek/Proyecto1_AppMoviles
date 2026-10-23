package com.example.master_martini.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.master_martini.data.local.entity.InteresUsuario
import kotlinx.coroutines.flow.Flow

@Dao
abstract class InteresDao {

    @Query("SELECT * FROM intereses_usuario WHERE usuarioId = :usuarioId")
    abstract fun observarPorUsuario(usuarioId: Long): Flow<List<InteresUsuario>>

    @Query("DELETE FROM intereses_usuario WHERE usuarioId = :usuarioId")
    abstract suspend fun borrarPorUsuario(usuarioId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertarTodos(intereses: List<InteresUsuario>)

    // Al editar la encuesta se reemplazan todos los intereses de una vez.
    @Transaction
    open suspend fun reemplazar(usuarioId: Long, intereses: List<InteresUsuario>) {
        borrarPorUsuario(usuarioId)
        insertarTodos(intereses)
    }
}