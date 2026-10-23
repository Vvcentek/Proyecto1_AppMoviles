package com.example.master_martini.data.repository

import com.example.master_martini.data.local.dao.ContenidoDao
import com.example.master_martini.data.local.dao.FavoritoDao
import com.example.master_martini.data.local.dao.InteresDao
import com.example.master_martini.data.local.dao.UsuarioDao
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.entity.Favorito
import com.example.master_martini.data.local.entity.InteresUsuario
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.seed.DatosSemilla
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MasterMartiniRepository(
    private val usuarioDao: UsuarioDao,
    private val interesDao: InteresDao,
    private val contenidoDao: ContenidoDao,
    private val favoritoDao: FavoritoDao
) {

    // --- Usuario ---
    val usuarioActual: Flow<Usuario?> = usuarioDao.observarUsuarioActual()

    suspend fun obtenerUsuarioActual(): Usuario? = usuarioDao.obtenerUsuarioActual()

    suspend fun correoYaRegistrado(correo: String): Boolean =
        usuarioDao.contarPorCorreo(correo.trim().lowercase()) > 0

    suspend fun registrarUsuario(usuario: Usuario): Long = usuarioDao.insertar(usuario)

    suspend fun actualizarUsuario(usuario: Usuario) = usuarioDao.actualizar(usuario)

    // --- Intereses ---
    fun interesesDe(usuarioId: Long): Flow<Set<Categoria>> =
        interesDao.observarPorUsuario(usuarioId).map { lista -> lista.map { it.categoria }.toSet() }

    suspend fun guardarIntereses(usuarioId: Long, categorias: Set<Categoria>) =
        interesDao.reemplazar(usuarioId, categorias.map { InteresUsuario(usuarioId, it) })

    // --- Contenidos ---
    fun contenidos(categoria: Categoria? = null): Flow<List<Contenido>> =
        if (categoria == null) contenidoDao.observarTodos()
        else contenidoDao.observarPorCategoria(categoria)

    fun contenido(id: Long): Flow<Contenido?> = contenidoDao.observarPorId(id)

    // --- Favoritos ---
    fun favoritosDe(usuarioId: Long): Flow<List<Contenido>> =
        favoritoDao.observarContenidosFavoritos(usuarioId)

    fun idsFavoritosDe(usuarioId: Long): Flow<Set<Long>> =
        favoritoDao.observarIdsFavoritos(usuarioId).map { it.toSet() }

    suspend fun alternarFavorito(usuarioId: Long, contenidoId: Long, esFavoritoAhora: Boolean) {
        if (esFavoritoAhora) favoritoDao.quitar(usuarioId, contenidoId)
        else favoritoDao.agregar(Favorito(usuarioId = usuarioId, contenidoId = contenidoId))
    }

    // --- Datos semilla ---
    suspend fun poblarSiEstaVacia() {
        if (contenidoDao.contar() == 0) contenidoDao.insertarTodos(DatosSemilla.contenidos)
    }
}