package com.example.master_martini.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.entity.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    // Une favoritos con contenidos para mostrar la lista completa en la pantalla Favoritos.
    @Query(
        """
        SELECT c.* FROM contenidos c
        INNER JOIN favoritos f ON f.contenidoId = c.id
        WHERE f.usuarioId = :usuarioId
        ORDER BY f.fechaGuardado DESC
        """
    )
    fun observarContenidosFavoritos(usuarioId: Long): Flow<List<Contenido>>

    @Query("SELECT contenidoId FROM favoritos WHERE usuarioId = :usuarioId")
    fun observarIdsFavoritos(usuarioId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(favorito: Favorito)

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId")
    suspend fun quitar(usuarioId: Long, contenidoId: Long)
}