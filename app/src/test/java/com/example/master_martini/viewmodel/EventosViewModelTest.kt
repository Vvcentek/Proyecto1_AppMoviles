package com.example.master_martini.viewmodel

import com.example.master_martini.MainDispatcherRule
import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.remote.RecetaExterna
import com.example.master_martini.data.repository.EventosRepository
import com.example.master_martini.data.repository.RecetasExternasRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class EventosViewModelTest {

    @get:Rule
    val reglaMain = MainDispatcherRule()

    private val repositorio = mockk<EventosRepository>()
    private val e1 = EventoDto(1, "Taller uno", "Descripción uno", "2030-01-01", "Sala Santiago", "GALLETAS", 10)
    private val e2 = EventoDto(2, "Taller dos", "Descripción dos", "2030-02-01", "Sala Santiago", "HELADERIA", 20)

    @Test
    fun `cargar muestra los eventos`() {
        coEvery { repositorio.listar() } returns Result.success(listOf(e1, e2))
        val vm = EventosViewModel(repositorio)
        vm.cargar()
        val estado = vm.estado.value
        assertFalse(estado.cargando)
        assertEquals(2, estado.eventos.size)
        assertNull(estado.error)
    }

    @Test
    fun `cargar sin conexion muestra error`() {
        coEvery { repositorio.listar() } returns Result.failure(RuntimeException())
        val vm = EventosViewModel(repositorio)
        vm.cargar()
        assertEquals(EventosViewModel.MENSAJE_SIN_CONEXION, vm.estado.value.error)
    }

    @Test
    fun `eliminar quita el evento de la lista`() {
        coEvery { repositorio.listar() } returns Result.success(listOf(e1, e2))
        coEvery { repositorio.eliminar(1) } returns Result.success(Unit)
        val vm = EventosViewModel(repositorio)
        vm.cargar()
        vm.eliminar(1)
        assertEquals(listOf(e2), vm.estado.value.eventos)
        assertEquals("Evento eliminado", vm.estado.value.mensaje)
        vm.mensajeMostrado()
        assertNull(vm.estado.value.mensaje)
    }

    @Test
    fun `eliminar con error mantiene la lista y avisa`() {
        coEvery { repositorio.listar() } returns Result.success(listOf(e1))
        coEvery { repositorio.eliminar(1) } returns Result.failure(RuntimeException())
        val vm = EventosViewModel(repositorio)
        vm.cargar()
        vm.eliminar(1)
        assertEquals(1, vm.estado.value.eventos.size)
        assertNotNull(vm.estado.value.mensaje)
    }

    @Test
    fun `inspiracion carga recetas de la API externa`() {
        val recetas = mockk<RecetasExternasRepository>()
        coEvery { recetas.postres() } returns Result.success(listOf(RecetaExterna("1", "Tarta", "url")))
        val vm = InspiracionViewModel(recetas)
        assertEquals(1, vm.estado.value.recetas.size)
        assertFalse(vm.estado.value.cargando)
    }

    @Test
    fun `inspiracion sin internet muestra error y permite reintentar`() {
        val recetas = mockk<RecetasExternasRepository>()
        coEvery { recetas.postres() } returns Result.failure(RuntimeException())
        val vm = InspiracionViewModel(recetas)
        assertNotNull(vm.estado.value.error)
        coEvery { recetas.postres() } returns Result.success(emptyList())
        vm.cargar()
        assertNull(vm.estado.value.error)
    }
}
