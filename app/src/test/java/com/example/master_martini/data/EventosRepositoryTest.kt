package com.example.master_martini.data

import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.remote.EventosApi
import com.example.master_martini.data.remote.RecetaExterna
import com.example.master_martini.data.remote.RecetasApi
import com.example.master_martini.data.remote.RespuestaRecetas
import com.example.master_martini.data.repository.EventosRepository
import com.example.master_martini.data.repository.RecetasExternasRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class EventosRepositoryTest {

    private val api = mockk<EventosApi>()
    private val repositorio = EventosRepository(api)
    private val evento = EventoDto(1, "Taller de galletas", "Descripción del taller", "2030-01-10", "Sala Santiago", "GALLETAS", 10)

    @Test
    fun `listar devuelve los eventos del servidor`() = runTest {
        coEvery { api.listar() } returns listOf(evento)
        assertEquals(listOf(evento), repositorio.listar().getOrThrow())
    }

    @Test
    fun `listar devuelve fallo si no hay conexion`() = runTest {
        coEvery { api.listar() } throws IOException("sin red")
        assertTrue(repositorio.listar().isFailure)
    }

    @Test
    fun `obtener pide el evento por id`() = runTest {
        coEvery { api.obtener(1) } returns evento
        assertEquals(evento, repositorio.obtener(1).getOrThrow())
    }

    @Test
    fun `guardar sin id crea con POST`() = runTest {
        val nuevo = evento.copy(id = null)
        coEvery { api.crear(nuevo) } returns evento
        repositorio.guardar(nuevo)
        coVerify(exactly = 1) { api.crear(nuevo) }
        coVerify(exactly = 0) { api.actualizar(any(), any()) }
    }

    @Test
    fun `guardar con id actualiza con PUT`() = runTest {
        coEvery { api.actualizar(1, evento) } returns evento
        repositorio.guardar(evento)
        coVerify(exactly = 1) { api.actualizar(1, evento) }
    }

    @Test
    fun `eliminar exitoso devuelve exito`() = runTest {
        coEvery { api.eliminar(1) } returns Response.success(204, Unit)
        assertTrue(repositorio.eliminar(1).isSuccess)
    }

    @Test
    fun `eliminar con error del servidor devuelve fallo`() = runTest {
        coEvery { api.eliminar(9) } returns Response.error(404, ResponseBody.create(null, ""))
        assertTrue(repositorio.eliminar(9).isFailure)
    }

    @Test
    fun `postres devuelve la lista de la API externa`() = runTest {
        val recetasApi = mockk<RecetasApi>()
        val receta = RecetaExterna("1", "Tarta", "https://img/1.jpg")
        coEvery { recetasApi.porCategoria("Dessert") } returns RespuestaRecetas(listOf(receta))
        assertEquals(listOf(receta), RecetasExternasRepository(recetasApi).postres().getOrThrow())
    }

    @Test
    fun `postres sin resultados devuelve lista vacia`() = runTest {
        val recetasApi = mockk<RecetasApi>()
        coEvery { recetasApi.porCategoria(any()) } returns RespuestaRecetas(null)
        assertEquals(emptyList<RecetaExterna>(), RecetasExternasRepository(recetasApi).postres().getOrThrow())
    }
}
