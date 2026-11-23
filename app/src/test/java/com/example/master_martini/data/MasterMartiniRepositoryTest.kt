package com.example.master_martini.data

import com.example.master_martini.data.local.dao.ContenidoDao
import com.example.master_martini.data.local.dao.FavoritoDao
import com.example.master_martini.data.local.dao.InteresDao
import com.example.master_martini.data.local.dao.UsuarioDao
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.entity.InteresUsuario
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.data.local.model.TipoNegocio
import com.example.master_martini.data.local.seed.DatosSemilla
import com.example.master_martini.data.repository.MasterMartiniRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Comprueba que el repositorio llama al DAO correcto y transforma bien los datos.
class MasterMartiniRepositoryTest {

    private val usuarioDao = mockk<UsuarioDao>(relaxed = true)
    private val interesDao = mockk<InteresDao>(relaxed = true)
    private val contenidoDao = mockk<ContenidoDao>(relaxed = true)
    private val favoritoDao = mockk<FavoritoDao>(relaxed = true)

    private val usuario = Usuario(
        id = 1, nombre = "Ana Pérez", correo = "ana@correo.cl", telefono = "912345678",
        direccion = "Av. Matta 123", rutEmpresa = "76.123.456-0",
        tipoNegocio = TipoNegocio.PASTELERIA, tamanoNegocio = TamanoNegocio.PEQUENO
    )
    private val contenido = Contenido(
        id = 4, titulo = "Bombones", descripcion = "Receta", categoria = Categoria.CHOCOLATERIA,
        tipo = TipoContenido.RECETA, nivel = NivelDificultad.BASICO, duracionMinutos = 20,
        chef = "Chef ficticio", pasos = "Paso 1"
    )

    private fun repositorio(): MasterMartiniRepository {
        every { usuarioDao.observarUsuarioActual() } returns flowOf(usuario)
        return MasterMartiniRepository(usuarioDao, interesDao, contenidoDao, favoritoDao)
    }

    @Test
    fun `usuario actual viene del dao`() = runTest {
        coEvery { usuarioDao.obtenerUsuarioActual() } returns usuario
        val repo = repositorio()
        assertEquals(usuario, repo.usuarioActual.first())
        assertEquals(usuario, repo.obtenerUsuarioActual())
    }

    @Test
    fun `correo se normaliza antes de buscarlo`() = runTest {
        coEvery { usuarioDao.contarPorCorreo("ana@correo.cl") } returns 1
        coEvery { usuarioDao.contarPorCorreo("otro@correo.cl") } returns 0
        val repo = repositorio()
        assertTrue(repo.correoYaRegistrado("  Ana@Correo.CL "))
        assertFalse(repo.correoYaRegistrado("otro@correo.cl"))
    }

    @Test
    fun `registrar y actualizar usuario`() = runTest {
        coEvery { usuarioDao.insertar(usuario) } returns 7L
        val repo = repositorio()
        assertEquals(7L, repo.registrarUsuario(usuario))
        repo.actualizarUsuario(usuario)
        coVerify { usuarioDao.actualizar(usuario) }
    }

    @Test
    fun `intereses se transforman a conjunto de categorias`() = runTest {
        every { interesDao.observarPorUsuario(1) } returns flowOf(
            listOf(InteresUsuario(1, Categoria.GALLETAS), InteresUsuario(1, Categoria.HELADERIA))
        )
        val repo = repositorio()
        assertEquals(setOf(Categoria.GALLETAS, Categoria.HELADERIA), repo.interesesDe(1).first())
        repo.guardarIntereses(1, setOf(Categoria.BOLLERIA))
        coVerify { interesDao.reemplazar(1, listOf(InteresUsuario(1, Categoria.BOLLERIA))) }
    }

    @Test
    fun `contenidos y favoritos vienen de sus daos`() = runTest {
        every { contenidoDao.observarTodos() } returns flowOf(listOf(contenido))
        every { contenidoDao.observarPorId(4) } returns flowOf(contenido)
        every { favoritoDao.observarContenidosFavoritos(1) } returns flowOf(listOf(contenido))
        every { favoritoDao.observarIdsFavoritos(1) } returns flowOf(listOf(4L, 4L))
        val repo = repositorio()
        assertEquals(listOf(contenido), repo.contenidos().first())
        assertEquals(contenido, repo.contenido(4).first())
        assertEquals(listOf(contenido), repo.favoritosDe(1).first())
        assertEquals(setOf(4L), repo.idsFavoritosDe(1).first())
    }

    @Test
    fun `alternar favorito agrega o quita`() = runTest {
        val repo = repositorio()
        repo.alternarFavorito(1, 4, esFavoritoAhora = true)
        coVerify { favoritoDao.quitar(1, 4) }
        repo.alternarFavorito(1, 5, esFavoritoAhora = false)
        coVerify { favoritoDao.agregar(match { it.usuarioId == 1L && it.contenidoId == 5L }) }
    }

    @Test
    fun `datos semilla solo se cargan si la tabla esta vacia`() = runTest {
        val repo = repositorio()
        coEvery { contenidoDao.contar() } returns 0
        repo.poblarSiEstaVacia()
        coVerify(exactly = 1) { contenidoDao.insertarTodos(DatosSemilla.contenidos) }

        coEvery { contenidoDao.contar() } returns 10
        repo.poblarSiEstaVacia()
        coVerify(exactly = 1) { contenidoDao.insertarTodos(any()) }
    }
}
