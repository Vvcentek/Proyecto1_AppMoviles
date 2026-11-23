package com.example.master_martini.viewmodel

import com.example.master_martini.MainDispatcherRule
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoNegocio
import com.example.master_martini.data.repository.MasterMartiniRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class RegistroViewModelTest {

    @get:Rule
    val reglaMain = MainDispatcherRule()

    private val repositorio = mockk<MasterMartiniRepository>()

    private fun RegistroViewModel.llenar() {
        onNombreChange("Ana Pérez")
        onCorreoChange("Ana@Correo.cl")
        onTelefonoChange("+56 9 1234 5678")
        onDireccionChange("Av. Matta 123")
        onRutChange("76.123.456-0")
        onTipoNegocioChange(TipoNegocio.PASTELERIA)
        onTamanoNegocioChange(TamanoNegocio.PEQUENO)
    }

    @Test
    fun `registro vacio muestra errores y no guarda`() {
        val vm = RegistroViewModel(repositorio)
        vm.registrar()
        assertEquals(CampoRegistro.entries.toSet(), vm.estado.value.errores.keys)
        coVerify(exactly = 0) { repositorio.registrarUsuario(any()) }
    }

    @Test
    fun `registro valido guarda el usuario`() {
        coEvery { repositorio.correoYaRegistrado("ana@correo.cl") } returns false
        coEvery { repositorio.registrarUsuario(any()) } returns 3L
        val vm = RegistroViewModel(repositorio)
        vm.llenar()
        vm.registrar()
        assertEquals(3L, vm.estado.value.usuarioRegistradoId)
    }

    @Test
    fun `correo repetido muestra error`() {
        coEvery { repositorio.correoYaRegistrado(any()) } returns true
        val vm = RegistroViewModel(repositorio)
        vm.llenar()
        vm.registrar()
        assertNull(vm.estado.value.usuarioRegistradoId)
        assertEquals("Este correo ya está registrado", vm.estado.value.errorDe(CampoRegistro.CORREO))
    }
}
