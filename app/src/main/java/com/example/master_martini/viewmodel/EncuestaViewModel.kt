package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.entity.Usuario
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.local.model.NivelDificultad
import com.example.master_martini.data.local.model.TipoContenido
import com.example.master_martini.data.repository.MasterMartiniRepository
import com.example.master_martini.validation.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CampoEncuesta { INTERESES, FORMATO, NIVEL }

data class EncuestaUiState(
    val nombreUsuario: String = "",
    val intereses: Set<Categoria> = emptySet(),
    val formatoPreferido: TipoContenido? = null,
    val nivel: NivelDificultad? = null,
    val aceptaNotificaciones: Boolean = false,
    val errores: Map<CampoEncuesta, String> = emptyMap(),
    val intentoEnviar: Boolean = false,
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val guardado: Boolean = false
) {
    fun errorDe(campo: CampoEncuesta): String? = if (intentoEnviar) errores[campo] else null
}

class EncuestaViewModel(private val repositorio: MasterMartiniRepository) : ViewModel() {

    private val _estado = MutableStateFlow(EncuestaUiState())
    val estado: StateFlow<EncuestaUiState> = _estado.asStateFlow()
    private var usuario: Usuario? = null

    init {
        // Carga lo que el usuario ya respondió (sirve también para editar la encuesta desde el perfil).
        viewModelScope.launch {
            val u = repositorio.obtenerUsuarioActual()
            usuario = u
            if (u == null) {
                _estado.update { it.copy(cargando = false) }
                return@launch
            }
            val intereses = repositorio.interesesDe(u.id).first()
            _estado.update {
                it.copy(
                    nombreUsuario = u.nombre.substringBefore(" "),
                    intereses = intereses,
                    formatoPreferido = u.formatoPreferido,
                    nivel = u.nivelExperiencia,
                    aceptaNotificaciones = u.aceptaNotificaciones,
                    cargando = false
                )
            }
        }
    }

    fun onAlternarInteres(categoria: Categoria) = actualizar { s ->
        s.copy(intereses = if (categoria in s.intereses) s.intereses - categoria else s.intereses + categoria)
    }

    fun onFormatoChange(formato: TipoContenido) = actualizar { it.copy(formatoPreferido = formato) }

    fun onNivelChange(nivel: NivelDificultad) = actualizar { it.copy(nivel = nivel) }

    fun onNotificacionesChange(acepta: Boolean) = _estado.update { it.copy(aceptaNotificaciones = acepta) }

    fun guardar() {
        val actual = _estado.value
        val u = usuario ?: return
        if (actual.guardando) return
        val errores = validar(actual)
        _estado.update { it.copy(intentoEnviar = true, errores = errores) }
        if (errores.isNotEmpty()) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            repositorio.guardarIntereses(u.id, actual.intereses)
            val actualizado = u.copy(
                formatoPreferido = actual.formatoPreferido,
                nivelExperiencia = actual.nivel,
                aceptaNotificaciones = actual.aceptaNotificaciones
            )
            repositorio.actualizarUsuario(actualizado)
            usuario = actualizado
            _estado.update { it.copy(guardando = false, guardado = true) }
        }
    }

    private fun actualizar(cambio: (EncuestaUiState) -> EncuestaUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            nuevo.copy(errores = validar(nuevo))
        }
    }

    private fun validar(s: EncuestaUiState): Map<CampoEncuesta, String> = buildMap {
        Validadores.intereses(s.intereses)?.let { put(CampoEncuesta.INTERESES, it) }
        Validadores.seleccion(s.formatoPreferido, "Elige el formato que prefieres")
            ?.let { put(CampoEncuesta.FORMATO, it) }
        Validadores.seleccion(s.nivel, "Elige tu nivel de experiencia")
            ?.let { put(CampoEncuesta.NIVEL, it) }
    }
}