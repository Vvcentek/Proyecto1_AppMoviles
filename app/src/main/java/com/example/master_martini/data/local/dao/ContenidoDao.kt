package com.example.master_martini.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface ContenidoDao {

    @Query("SELECT * FROM contenidos ORDER BY titulo")
    fun observarTodos(): Flow<List<Contenido>>

    @Query("SELECT * FROM contenidos WHERE categoria = :categoria ORDER BY titulo")
    fun observarPorCategoria(categoria: Categoria): Flow<List<Contenido>>

    @Query("SELECT * FROM contenidos WHERE id = :id")
    fun observarPorId(id: Long): Flow<Contenido?>

    @Query("SELECT COUNT(*) FROM contenidos")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(contenidos: List<Contenido>)
}