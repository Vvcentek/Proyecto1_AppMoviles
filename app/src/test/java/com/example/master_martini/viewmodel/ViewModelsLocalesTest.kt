package com.example.master_martini.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.master_martini.MainDispatcherRule
import com.example.master_martini.data.local.AlmacenFotos
import com.example.master_martini.data.local.entity.Contenido
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.data.local.model.TipoNegocio
import com.example.master_martini.data.repository.MasterMartiniRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.StateFlow

// Tests de los ViewModels que trabajan con la base local (Room), usando un repositorio simulado con MockK.
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsLocalesTest {

    @get:Rule
    val reglaMain = MainDispatcherRule()

    private val repo = mockk<MasterMartiniRepository>(relaxed = true)

    private val usuario = Usuario(
        id = 1, nombre = "Ana Pérez", correo = "ana@correo.cl", telefono = "912345678",
        direccion = "Av. Matta 123", rutEmpresa = "76.123.456-0",
        tipoNegocio = TipoNegocio.PASTELERIA, tamanoNegocio = TamanoNegocio.PEQUENO,
        formatoPreferido = TipoContenido.VIDEO
    )

    private fun contenido(id: Long, categoria: Categoria, tipo: TipoContenido) = Contenido(
        id = id, titulo = "Contenido $id", descripcion = "Descripción", categoria = categoria, tipo = tipo,
        nivel = NivelDificultad.BASICO, duracionMinutos = 10, chef = "Chef ficticio", pasos = "Paso 1\nPaso 2"
    )

    private val c1 = contenido(1, Categoria.CHOCOLATERIA, TipoContenido.RECETA)
    private val c2 = contenido(2, Categoria.CHOCOLATERIA, TipoContenido.VIDEO)
    private val c3 = contenido(3, Categoria.HELADERIA, TipoContenido.CURSO)

    private fun conUsuario(u: Usuario?) {
        every { repo.usuarioActual } returns flowOf(u)
    }

    // Los StateFlow con WhileSubscribed solo trabajan si alguien los observa (como la pantalla).
    private fun <T> TestScope.observar(flujo: StateFlow<T>) {
        backgroundScope.launch { flujo.collect {} }
        advanceUntilIdle()
    }

    // ---------- Sesión ----------

    @Test
    fun `sesion reconoce al usuario guardado`() = runTest {
        conUsuario(usuario)
        val vm = SesionViewModel(repo)
        assertEquals(EstadoSesion.Cargando, vm.estado.value)
        observar(vm.estado)
        assertEquals(EstadoSesion.Listo(usuario), vm.estado.value)
    }

    @Test
    fun `sesion sin usuario queda lista con null`() = runTest {
        conUsuario(null)
        val vm = SesionViewModel(repo)
        observar(vm.estado)
        assertEquals(EstadoSesion.Listo(null), vm.estado.value)
    }

    // ---------- Catálogo ----------

    private fun prepararCatalogo() {
        every { repo.contenidos() } returns flowOf(listOf(c1, c2, c3))
        every { repo.interesesDe(any()) } returns flowOf(setOf(Categoria.CHOCOLATERIA))
        every { repo.idsFavoritosDe(any()) } returns flowOf(setOf(3L))
    }

    @Test
    fun `catalogo recomienda segun intereses y formato preferido`() = runTest {
        conUsuario(usuario)
        prepararCatalogo()
        val vm = CatalogoViewModel(repo)
        observar(vm.estado)
        val s = vm.estado.value
        assertFalse(s.cargando)
        assertEquals("Ana", s.nombre)
        assertEquals(listOf(c2, c1), s.recomendados)
        assertEquals(3, s.contenidos.size)
        assertEquals(setOf(3L), s.idsFavoritos)
    }

    @Test
    fun `catalogo filtra por categoria y se desmarca al repetir`() = runTest {
        conUsuario(usuario)
        prepararCatalogo()
        val vm = CatalogoViewModel(repo)
        observar(vm.estado)
        vm.seleccionarCategoria(Categoria.HELADERIA)
        advanceUntilIdle()
        assertEquals(listOf(c3), vm.estado.value.contenidos)
        vm.seleccionarCategoria(Categoria.HELADERIA)
        advanceUntilIdle()
        assertNull(vm.estado.value.categoriaSeleccionada)
        assertEquals(3, vm.estado.value.contenidos.size)
    }

    @Test
    fun `catalogo alterna favoritos`() = runTest {
        conUsuario(usuario)
        prepararCatalogo()
        val vm = CatalogoViewModel(repo)
        observar(vm.estado)
        vm.alternarFavorito(3)
        vm.alternarFavorito(1)
        advanceUntilIdle()
        coVerify { repo.alternarFavorito(1, 3, true) }
        coVerify { repo.alternarFavorito(1, 1, false) }
    }

    @Test
    fun `catalogo sin usuario no guarda favoritos`() = runTest {
        conUsuario(null)
        prepararCatalogo()
        val vm = CatalogoViewModel(repo)
        observar(vm.estado)
        assertNull(vm.estado.value.usuarioId)
        vm.alternarFavorito(1)
        advanceUntilIdle()
        coVerify(exactly = 0) { repo.alternarFavorito(any(), any(), any()) }
    }

    // ---------- Detalle ----------

    @Test
    fun `detalle muestra el contenido y si es favorito`() = runTest {
        conUsuario(usuario)
        every { repo.contenido(2) } returns flowOf(c2)
        every { repo.idsFavoritosDe(any()) } returns flowOf(setOf(2L))
        val vm = DetalleViewModel(SavedStateHandle(mapOf(DetalleViewModel.ARG_ID to 2L)), repo)
        observar(vm.estado)
        assertEquals(c2, vm.estado.value.contenido)
        assertTrue(vm.estado.value.esFavorito)
        vm.alternarFavorito()
        advanceUntilIdle()
        coVerify { repo.alternarFavorito(1, 2, true) }
    }

    @Test
    fun `detalle sin usuario no alterna favorito`() = runTest {
        conUsuario(null)
        every { repo.contenido(1) } returns flowOf(c1)
        every { repo.idsFavoritosDe(any()) } returns flowOf(emptySet())
        val vm = DetalleViewModel(SavedStateHandle(mapOf(DetalleViewModel.ARG_ID to 1L)), repo)
        observar(vm.estado)
        assertFalse(vm.estado.value.esFavorito)
        vm.alternarFavorito()
        advanceUntilIdle()
        coVerify(exactly = 0) { repo.alternarFavorito(any(), any(), any()) }
    }

    // ---------- Favoritos ----------

    @Test
    fun `favoritos lista y quita contenidos`() = runTest {
        conUsuario(usuario)
        every { repo.favoritosDe(1) } returns flowOf(listOf(c1))
        val vm = FavoritosViewModel(repo)
        observar(vm.estado)
        assertEquals(listOf(c1), vm.estado.value.favoritos)
        vm.quitar(1)
        advanceUntilIdle()
        coVerify { repo.alternarFavorito(1, 1, true) }
    }

    @Test
    fun `favoritos sin usuario queda vacio`() = runTest {
        conUsuario(null)
        val vm = FavoritosViewModel(repo)
        observar(vm.estado)
        assertFalse(vm.estado.value.cargando)
        assertTrue(vm.estado.value.favoritos.isEmpty())
        vm.quitar(1)
        advanceUntilIdle()
        coVerify(exactly = 0) { repo.alternarFavorito(any(), any(), any()) }
    }

    // ---------- Encuesta ----------

    @Test
    fun `encuesta carga respuestas previas`() = runTest {
        coEvery { repo.obtenerUsuarioActual() } returns usuario.copy(nivelExperiencia = NivelDificultad.AVANZADO)
        every { repo.interesesDe(1) } returns flowOf(setOf(Categoria.HELADERIA))
        val vm = EncuestaViewModel(repo)
        advanceUntilIdle()
        val s = vm.estado.value
        assertFalse(s.cargando)
        assertEquals("Ana", s.nombreUsuario)
        assertEquals(setOf(Categoria.HELADERIA), s.intereses)
        assertEquals(NivelDificultad.AVANZADO, s.nivel)
    }

    @Test
    fun `encuesta vacia muestra errores y luego guarda`() = runTest {
        coEvery { repo.obtenerUsuarioActual() } returns usuario.copy(formatoPreferido = null)
        every { repo.interesesDe(1) } returns flowOf(emptySet())
        val vm = EncuestaViewModel(repo)
        advanceUntilIdle()

        vm.guardar()
        assertEquals(CampoEncuesta.entries.toSet(), vm.estado.value.errores.keys)
        assertNotNull(vm.estado.value.errorDe(CampoEncuesta.INTERESES))

        vm.onAlternarInteres(Categoria.GALLETAS)
        vm.onAlternarInteres(Categoria.BOLLERIA)
        vm.onAlternarInteres(Categoria.BOLLERIA) // se desmarca
        vm.onFormatoChange(TipoContenido.RECETA)
        vm.onNivelChange(NivelDificultad.BASICO)
        vm.onNotificacionesChange(true)
        assertTrue(vm.estado.value.errores.isEmpty())

        vm.guardar()
        advanceUntilIdle()
        assertTrue(vm.estado.value.guardado)
        coVerify { repo.guardarIntereses(1, setOf(Categoria.GALLETAS)) }
        coVerify {
            repo.actualizarUsuario(match {
                it.formatoPreferido == TipoContenido.RECETA && it.nivelExperiencia == NivelDificultad.BASICO && it.aceptaNotificaciones
            })
        }
    }

    @Test
    fun `encuesta sin usuario no guarda`() = runTest {
        coEvery { repo.obtenerUsuarioActual() } returns null
        val vm = EncuestaViewModel(repo)
        advanceUntilIdle()
        assertFalse(vm.estado.value.cargando)
        vm.guardar()
        advanceUntilIdle()
        coVerify(exactly = 0) { repo.guardarIntereses(any(), any()) }
    }

    // ---------- Perfil ----------

    private val almacen = mockk<AlmacenFotos>(relaxed = true)

    @Test
    fun `perfil muestra datos intereses y total de favoritos`() = runTest {
        conUsuario(usuario)
        every { repo.interesesDe(1) } returns flowOf(setOf(Categoria.CHOCOLATERIA))
        every { repo.idsFavoritosDe(1) } returns flowOf(setOf(1L, 2L))
        val vm = PerfilViewModel(repo, almacen)
        observar(vm.estado)
        val s = vm.estado.value
        assertEquals(usuario, s.usuario)
        assertEquals(setOf(Categoria.CHOCOLATERIA), s.intereses)
        assertEquals(2, s.totalFavoritos)
    }

    @Test
    fun `perfil muestra mensajes de foto y permisos`() = runTest {
        conUsuario(null)
        val vm = PerfilViewModel(repo, almacen)
        observar(vm.estado)
        assertNull(vm.estado.value.usuario)

        vm.onFotoTomada(null)
        advanceUntilIdle()
        assertEquals("No se tomó ninguna foto", vm.estado.value.mensaje)
        vm.mensajeMostrado()
        advanceUntilIdle()
        assertNull(vm.estado.value.mensaje)

        vm.onPermisoCamaraDenegado()
        advanceUntilIdle()
        assertNotNull(vm.estado.value.mensaje)
        vm.onPermisoGaleriaDenegado()
        advanceUntilIdle()
        assertNotNull(vm.estado.value.mensaje)

        vm.onFotoElegida(null) // sin foto elegida no hace nada
        advanceUntilIdle()
        assertFalse(vm.estado.value.guardandoFoto)
    }
}
