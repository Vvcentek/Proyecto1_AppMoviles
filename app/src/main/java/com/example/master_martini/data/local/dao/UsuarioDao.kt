package com.example.master_martini.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.master_martini.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    // La app maneja un solo perfil local: el último registrado.
    @Query("SELECT * FROM usuarios ORDER BY id DESC LIMIT 1")
    fun observarUsuarioActual(): Flow<Usuario?>

    @Query("SELECT * FROM usuarios ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUsuarioActual(): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun contarPorCorreo(correo: String): Int
}