package com.example.master_martini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.master_martini.data.remote.RecetaExterna
import com.example.master_martini.data.repository.RecetasExternasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InspiracionUiState(
    val cargando: Boolean = true,
    val recetas: List<RecetaExterna> = emptyList(),
    val error: String? = null
)

// Ideas de postres desde la API externa TheMealDB.
class InspiracionViewModel(private val repositorio: RecetasExternasRepository) : ViewModel() {

    private val _estado = MutableStateFlow(InspiracionUiState())
    val estado: StateFlow<InspiracionUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repositorio.postres()
                .onSuccess { lista -> _estado.update { it.copy(cargando = false, recetas = lista) } }
                .onFailure {
                    _estado.update { it.copy(cargando = false, error = "No se pudieron cargar las ideas. Revisa tu conexión a internet.") }
                }
        }
    }
}
