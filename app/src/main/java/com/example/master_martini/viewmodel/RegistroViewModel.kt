package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.TamanoNegocio
import com.example.master_martini.data.local.model.TipoNegocio
import com.example.master_martini.data.repository.MasterMartiniRepository
import com.example.master_martini.validation.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CampoRegistro { NOMBRE, CORREO, TELEFONO, DIRECCION, RUT, TIPO_NEGOCIO, TAMANO_NEGOCIO }

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val rut: String = "",
    val tipoNegocio: TipoNegocio? = null,
    val tamanoNegocio: TamanoNegocio? = null,
    val errores: Map<CampoRegistro, String> = emptyMap(),
    val tocados: Set<CampoRegistro> = emptySet(),
    val intentoEnviar: Boolean = false,
    val guardando: Boolean = false,
    val usuarioRegistradoId: Long? = null
) {
    // El error de un campo se muestra solo si el usuario ya lo tocó o intentó enviar.
    fun errorDe(campo: CampoRegistro): String? =
        if (intentoEnviar || campo in tocados) errores[campo] else null
}

class RegistroViewModel(private val repositorio: MasterMartiniRepository) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) = actualizar(CampoRegistro.NOMBRE) { it.copy(nombre = valor) }
    fun onCorreoChange(valor: String) = actualizar(CampoRegistro.CORREO) { it.copy(correo = valor) }
    fun onTelefonoChange(valor: String) = actualizar(CampoRegistro.TELEFONO) { it.copy(telefono = valor) }
    fun onDireccionChange(valor: String) = actualizar(CampoRegistro.DIRECCION) { it.copy(direccion = valor) }
    fun onRutChange(valor: String) = actualizar(CampoRegistro.RUT) { it.copy(rut = valor) }
    fun onTipoNegocioChange(valor: TipoNegocio) =
        actualizar(CampoRegistro.TIPO_NEGOCIO) { it.copy(tipoNegocio = valor) }
    fun onTamanoNegocioChange(valor: TamanoNegocio) =
        actualizar(CampoRegistro.TAMANO_NEGOCIO) { it.copy(tamanoNegocio = valor) }

    fun registrar() {
        val actual = _estado.value
        if (actual.guardando) return
        val errores = validar(actual)
        _estado.update { it.copy(intentoEnviar = true, errores = errores) }
        if (errores.isNotEmpty()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val correo = actual.correo.trim().lowercase()
            if (repositorio.correoYaRegistrado(correo)) {
                _estado.update {
                    it.copy(
                        guardando = false,
                        errores = it.errores + (CampoRegistro.CORREO to "Este correo ya está registrado")
                    )
                }
                return@launch
            }
            val id = repositorio.registrarUsuario(
                Usuario(
                    nombre = actual.nombre.trim(),
                    correo = correo,
                    telefono = Validadores.soloDigitosTelefono(actual.telefono),
                    direccion = actual.direccion.trim(),
                    rutEmpresa = Validadores.formatearRut(actual.rut),
                    tipoNegocio = requireNotNull(actual.tipoNegocio),
                    tamanoNegocio = requireNotNull(actual.tamanoNegocio)
                )
            )
            _estado.update { it.copy(guardando = false, usuarioRegistradoId = id) }
        }
    }

    private fun actualizar(campo: CampoRegistro, cambio: (RegistroUiState) -> RegistroUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            nuevo.copy(tocados = actual.tocados + campo, errores = validar(nuevo))
        }
    }

    private fun validar(s: RegistroUiState): Map<CampoRegistro, String> = buildMap {
        Validadores.nombre(s.nombre)?.let { put(CampoRegistro.NOMBRE, it) }
        Validadores.correo(s.correo)?.let { put(CampoRegistro.CORREO, it) }
        Validadores.telefono(s.telefono)?.let { put(CampoRegistro.TELEFONO, it) }
        Validadores.direccion(s.direccion)?.let { put(CampoRegistro.DIRECCION, it) }
        Validadores.rut(s.rut)?.let { put(CampoRegistro.RUT, it) }
        Validadores.seleccion(s.tipoNegocio, "Elige el tipo de negocio")
            ?.let { put(CampoRegistro.TIPO_NEGOCIO, it) }
        Validadores.seleccion(s.tamanoNegocio, "Elige el tamaño del negocio")
            ?.let { put(CampoRegistro.TAMANO_NEGOCIO, it) }
    }
}