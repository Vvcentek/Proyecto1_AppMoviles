package com.example.master_martini.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.local.model.Categoria
import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.repository.EventosRepository
import com.example.master_martini.validation.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CampoEvento { TITULO, DESCRIPCION, FECHA, LUGAR, CATEGORIA, CUPOS }

data class EventoFormUiState(
    val id: Long? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val lugar: String = "",
    val categoria: Categoria? = null,
    val cupos: String = "",
    val errores: Map<CampoEvento, String> = emptyMap(),
    val tocados: Set<CampoEvento> = emptySet(),
    val intentoEnviar: Boolean = false,
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val errorServidor: String? = null
) {
    val esEdicion: Boolean get() = id != null

    fun errorDe(campo: CampoEvento): String? =
        if (intentoEnviar || campo in tocados) errores[campo] else null
}

// Formulario para crear (Create) y editar (Update) un evento.
// eventoId = -1 significa "nuevo"; otro valor carga el evento desde el servidor.
class EventoFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: EventosRepository,
    private val hoy: () -> String = { Validadores.hoyIso() }
) : ViewModel() {

    companion object {
        const val ARG_ID = "eventoId"
        const val NUEVO = -1L
    }

    private val _estado = MutableStateFlow(EventoFormUiState())
    val estado: StateFlow<EventoFormUiState> = _estado.asStateFlow()

    init {
        val id = savedStateHandle.get<Long>(ARG_ID) ?: NUEVO
        if (id != NUEVO) cargar(id)
    }

    private fun cargar(id: Long) {
        _estado.update { it.copy(cargando = true) }
        viewModelScope.launch {
            repositorio.obtener(id)
                .onSuccess { e ->
                    _estado.update {
                        val nuevo = it.copy(
                            id = e.id, titulo = e.titulo, descripcion = e.descripcion, fecha = e.fecha,
                            lugar = e.lugar, cupos = e.cupos.toString(), cargando = false,
                            categoria = Categoria.entries.firstOrNull { c -> c.name == e.categoria }
                        )
                        nuevo.copy(errores = validar(nuevo))
                    }
                }
                .onFailure {
                    _estado.update { it.copy(cargando = false, errorServidor = "No se pudo cargar el evento") }
                }
        }
    }

    fun onTituloChange(v: String) = actualizar(CampoEvento.TITULO) { it.copy(titulo = v) }
    fun onDescripcionChange(v: String) = actualizar(CampoEvento.DESCRIPCION) { it.copy(descripcion = v) }
    fun onFechaChange(v: String) = actualizar(CampoEvento.FECHA) { it.copy(fecha = v) }
    fun onLugarChange(v: String) = actualizar(CampoEvento.LUGAR) { it.copy(lugar = v) }
    fun onCategoriaChange(v: Categoria) = actualizar(CampoEvento.CATEGORIA) { it.copy(categoria = v) }
    fun onCuposChange(v: String) = actualizar(CampoEvento.CUPOS) { it.copy(cupos = v.filter { c -> c.isDigit() }.take(3)) }

    fun guardar() {
        val actual = _estado.value
        if (actual.guardando) return
        val errores = validar(actual)
        _estado.update { it.copy(intentoEnviar = true, errores = errores, errorServidor = null) }
        if (errores.isNotEmpty()) return

        val dto = EventoDto(
            id = actual.id,
            titulo = actual.titulo.trim(),
            descripcion = actual.descripcion.trim(),
            fecha = actual.fecha.trim(),
            lugar = actual.lugar.trim(),
            categoria = requireNotNull(actual.categoria).name,
            cupos = actual.cupos.trim().toInt()
        )
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            repositorio.guardar(dto)
                .onSuccess { _estado.update { it.copy(guardando = false, guardado = true) } }
                .onFailure {
                    _estado.update {
                        it.copy(guardando = false, errorServidor = "No se pudo guardar. Revisa la conexión con el servidor.")
                    }
                }
        }
    }

    private fun actualizar(campo: CampoEvento, cambio: (EventoFormUiState) -> EventoFormUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            nuevo.copy(tocados = actual.tocados + campo, errores = validar(nuevo))
        }
    }

    private fun validar(s: EventoFormUiState): Map<CampoEvento, String> = buildMap {
        Validadores.tituloEvento(s.titulo)?.let { put(CampoEvento.TITULO, it) }
        Validadores.descripcionEvento(s.descripcion)?.let { put(CampoEvento.DESCRIPCION, it) }
        Validadores.fechaEvento(s.fecha, hoy())?.let { put(CampoEvento.FECHA, it) }
        Validadores.lugarEvento(s.lugar)?.let { put(CampoEvento.LUGAR, it) }
        Validadores.seleccion(s.categoria, "Elige una especialidad")?.let { put(CampoEvento.CATEGORIA, it) }
        Validadores.cuposEvento(s.cupos)?.let { put(CampoEvento.CUPOS, it) }
    }
}
