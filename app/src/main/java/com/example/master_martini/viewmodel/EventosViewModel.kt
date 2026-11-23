package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.remote.EventoDto
import com.example.master_martini.data.repository.EventosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventosUiState(
    val cargando: Boolean = true,
    val eventos: List<EventoDto> = emptyList(),
    val error: String? = null,
    val mensaje: String? = null
)

// Lista de eventos que viene del microservicio (Read + Delete del CRUD).
class EventosViewModel(private val repositorio: EventosRepository) : ViewModel() {

    private val _estado = MutableStateFlow(EventosUiState())
    val estado: StateFlow<EventosUiState> = _estado.asStateFlow()

    fun cargar() {
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repositorio.listar()
                .onSuccess { lista -> _estado.update { it.copy(cargando = false, eventos = lista) } }
                .onFailure { _estado.update { it.copy(cargando = false, error = MENSAJE_SIN_CONEXION) } }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repositorio.eliminar(id)
                .onSuccess {
                    _estado.update { s ->
                        s.copy(eventos = s.eventos.filterNot { it.id == id }, mensaje = "Evento eliminado")
                    }
                }
                .onFailure { _estado.update { it.copy(mensaje = "No se pudo eliminar el evento") } }
        }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }

    companion object {
        const val MENSAJE_SIN_CONEXION =
            "No se pudo conectar con el servidor de eventos. Revisa que el microservicio esté encendido."
    }
}
