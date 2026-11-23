package com.example.master_martini.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.master_martini.MainDispatcherRule
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.repository.EventosRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EventoFormViewModelTest {

    @get:Rule
    val reglaMain = MainDispatcherRule()

    private val repositorio = mockk<EventosRepository>()
    private val hoy = { "2026-11-23" }

    private fun vmNuevo() = EventoFormViewModel(
        SavedStateHandle(mapOf(EventoFormViewModel.ARG_ID to EventoFormViewModel.NUEVO)), repositorio, hoy
    )

    private fun EventoFormViewModel.llenarValido() {
        onTituloChange("Taller de galletas")
        onDescripcionChange("Galletas decoradas para vitrina")
        onFechaChange("2026-12-15")
        onLugarChange("Sala Master Martini")
        onCategoriaChange(Categoria.GALLETAS)
        onCuposChange("25")
    }

    @Test
    fun `formulario nuevo parte vacio y sin errores visibles`() {
        val vm = vmNuevo()
        val s = vm.estado.value
        assertFalse(s.esEdicion)
        assertNull(s.errorDe(CampoEvento.TITULO))
    }

    @Test
    fun `guardar vacio marca todos los errores y no llama al servidor`() {
        val vm = vmNuevo()
        vm.guardar()
        val s = vm.estado.value
        assertEquals(CampoEvento.entries.toSet(), s.errores.keys)
        assertNotNull(s.errorDe(CampoEvento.FECHA))
        coVerify(exactly = 0) { repositorio.guardar(any()) }
    }

    @Test
    fun `solo se muestra el error del campo tocado`() {
        val vm = vmNuevo()
        vm.onTituloChange("abc")
        assertNotNull(vm.estado.value.errorDe(CampoEvento.TITULO))
        assertNull(vm.estado.value.errorDe(CampoEvento.LUGAR))
    }

    @Test
    fun `cupos solo acepta digitos`() {
        val vm = vmNuevo()
        vm.onCuposChange("1a2b")
        assertEquals("12", vm.estado.value.cupos)
    }

    @Test
    fun `guardar valido envia el evento y marca guardado`() {
        val enviado = slot<EventoDto>()
        coEvery { repositorio.guardar(capture(enviado)) } answers { Result.success(enviado.captured.copy(id = 5)) }
        val vm = vmNuevo()
        vm.llenarValido()
        vm.guardar()
        assertTrue(vm.estado.value.guardado)
        assertNull(enviado.captured.id)
        assertEquals("GALLETAS", enviado.captured.categoria)
        assertEquals(25, enviado.captured.cupos)
    }

    @Test
    fun `error del servidor al guardar se informa`() {
        coEvery { repositorio.guardar(any()) } returns Result.failure(RuntimeException())
        val vm = vmNuevo()
        vm.llenarValido()
        vm.guardar()
        assertFalse(vm.estado.value.guardado)
        assertNotNull(vm.estado.value.errorServidor)
    }

    @Test
    fun `editar carga los datos del evento`() {
        val evento = EventoDto(7, "Taller de helados", "Bases para helado artesanal", "2026-12-20", "Sala Providencia", "HELADERIA", 30)
        coEvery { repositorio.obtener(7) } returns Result.success(evento)
        val vm = EventoFormViewModel(SavedStateHandle(mapOf(EventoFormViewModel.ARG_ID to 7L)), repositorio, hoy)
        val s = vm.estado.value
        assertTrue(s.esEdicion)
        assertEquals("Taller de helados", s.titulo)
        assertEquals(Categoria.HELADERIA, s.categoria)
        assertEquals("30", s.cupos)
        assertTrue(s.errores.isEmpty())
    }

    @Test
    fun `editar con error de carga lo informa`() {
        coEvery { repositorio.obtener(8) } returns Result.failure(RuntimeException())
        val vm = EventoFormViewModel(SavedStateHandle(mapOf(EventoFormViewModel.ARG_ID to 8L)), repositorio, hoy)
        assertFalse(vm.estado.value.cargando)
        assertNotNull(vm.estado.value.errorServidor)
    }
}
